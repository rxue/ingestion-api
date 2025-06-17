package io.github.rxue.ingestion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import jakarta.ws.rs.core.HttpHeaders;

public class HttpFileDownloader {
    public static final long KB = 1024;
    public static final long MB = KB * KB;
    private final HttpClient httpClient;

    public HttpFileDownloader() {
        this.httpClient = HttpClient.newHttpClient();
    }
    public void download(String urlString, long chunkSize, Path targetDirectory) {
        System.out.println("DOWNLOAD!!!!");
        if (!urlString.startsWith("http")) {
            System.out.println("This is a test, no need to continue");
            return;
        }
        final URI uri =URI.create(urlString);
        final long byteLength;
        try {
            byteLength = getByteLength(uri);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        final List<Range> multiPartRanges = divide(byteLength, chunkSize);
        final List<CompletableFuture<byte[]>> allBytes = multiPartRanges.stream()
                .map(range -> downloadRange(uri, range))
                .toList();
        Path targetFilePath = targetDirectory.resolve(getBaseName(urlString));
        try {
            Files.write(targetFilePath, combineMultiParts(allBytes));
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    static long getByteLength(URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
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

    static String getBaseName(String url) {
        return Path.of(url).getFileName().toString();
    }

    record Range(long start, long end) {
        public String toRequestHeaderValue() {
            return "bytes=" + start + "-" + end;
        }
    }
}
