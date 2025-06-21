package io.github.rxue.ingestion.batch;

import jakarta.batch.api.AbstractBatchlet;
import jakarta.batch.api.BatchProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Named
@Dependent
public class HttpFileDownloader extends AbstractBatchlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileDownloader.class);
    static final long KB = 1024;
    static final long MB = KB * KB;
    public static final String COMPLETE = "COMPLETE";
    public static final String DOWNLOAD_URL = "download.url";
    private final URI downloadURI;
    private final Path downloadToDirectory;
    private final HttpClient httpClient;
    private final long chunkSizeValue;
    public HttpFileDownloader(@BatchProperty(name= DOWNLOAD_URL) String downloadURL,
                              @ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadToDirectory,
                              @BatchProperty(name= "download.chunk.size") String chunkSizeValue) {
        this(URI.create(downloadURL),
                Path.of(downloadToDirectory),
                chunkSizeValue,
                HttpClient.newHttpClient());
    }

    HttpFileDownloader(URI downloadURI, Path downloadToDirectory, String chunkSize, HttpClient httpClient) {
        this.downloadURI = downloadURI;
        this.downloadToDirectory = downloadToDirectory;
        this.chunkSizeValue = getChunkSize(chunkSize);
        this.httpClient = httpClient;
    }
    static long getChunkSize(String chunkSize) {
        return chunkSize == null ? MB * 100 : Long.valueOf(chunkSize);
    }


    static Path getDownloadedFilePath(String downloadURL, String downloadToDirectory) {
        String fileName = Path.of(downloadURL).getFileName().toString();
        return Path.of(downloadToDirectory, fileName);
    }

    @Override
    public String process() {
        LOGGER.info("download from {} to {}", downloadURI, downloadToDirectory);
        if (!Files.exists(getDownloadedFilePath(downloadURI.toString(), downloadToDirectory.toString())))
            download();
        return COMPLETE;
    }
    private void download() {
        final long byteLength;
        try {
            byteLength = getByteLength(downloadURI);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        final List<Range> multiPartRanges = divide(byteLength, chunkSizeValue);
        final List<CompletableFuture<byte[]>> allBytes = multiPartRanges.stream()
                .map(range -> downloadRange(downloadURI, range))
                .toList();
        final Path targetFilePath = downloadToDirectory.resolve(getBaseName(downloadURI));
        try {
            Files.write(targetFilePath, combineMultiParts(allBytes));
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static String getBaseName(URI uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }
    long getByteLength(URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        Map<String, List<String>> map = response.headers().map();
        return map.keySet().stream()
                .filter(HttpHeaders.CONTENT_LENGTH::equalsIgnoreCase)
                .map(key -> map.get(key).get(0))
                .mapToLong(Long::parseLong)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     *
     * @param length
     * @param chunkSize size of each chunk to download separately
     * @return
     */
    static List<Range> divide(long length, long chunkSize) {
        List<Range> ranges = new ArrayList<>();
        for (int startIndex = 0; startIndex < length; startIndex += chunkSize) {
            long endIndex = startIndex + chunkSize -1;
            if (endIndex > length) {
                endIndex = length-1;
            }
            ranges.add(new Range(startIndex, endIndex));
        }
        return ranges;
    }

    private CompletableFuture<byte[]> downloadRange(URI uri, Range range) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Range", range.toRequestHeaderValue())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body);
    }

    private static byte[] combineMultiParts(List<CompletableFuture<byte[]>> parts) throws ExecutionException, InterruptedException {
        List<byte[]> results = CompletableFuture.allOf(parts.toArray(new CompletableFuture[0]))
                .thenApply(v -> parts.stream().map(CompletableFuture::join).toList())
                .get();

        int totalSize = results.stream().mapToInt(part -> part.length).sum();
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        for (byte[] part : results) {
            buffer.put(part);
        }

        return buffer.array();
    }

    record Range(long start, long end) {
        public String toRequestHeaderValue() {
            return "bytes=" + start + "-" + end;
        }
    }
}
