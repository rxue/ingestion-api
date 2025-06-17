package io.github.rxue.ingestion;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpFileDownloaderIT {
    public static final String TEST_DATA_DIR_STR = "src/test/resources";
    public static final String DOWNLOAD_URL = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/2.1.21/kotlin-stdlib-2.1.21.jar";
    private static final Path DOWNLOAD_DIR = Path.of(TEST_DATA_DIR_STR, "target");

    @BeforeEach
    void initDownloadDir() {
        if (Files.exists(DOWNLOAD_DIR)) {
            removeDownloadDir();
        }
        try {
            Files.createDirectory(DOWNLOAD_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void removeDownloadDir() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DOWNLOAD_DIR)) {
            for (Path file : stream) {
                Files.delete(file); // Delete each file
                System.out.println("Deleted file: " + file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Delete the directory itself
        try {
            Files.delete(DOWNLOAD_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void download_with_half_M() throws IOException, ExecutionException, InterruptedException {
        HttpFileDownloader downloader = new HttpFileDownloader();
        final String downloadURL = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/2.1.21/kotlin-stdlib-2.1.21.jar";
        downloader.download(downloadURL, HttpFileDownloader.KB * HttpFileDownloader.KB /2, DOWNLOAD_DIR);
        singleThreadDownload(downloadURL, DOWNLOAD_DIR);
        final Path downloadedFilePath = DOWNLOAD_DIR.resolve(HttpFileDownloader.getBaseName(downloadURL));
        assertTrue(Files.exists(downloadedFilePath));
        final Path singleThreadDownloadedFilePath = getSingleThreadDownloadFilePath(downloadURL, DOWNLOAD_DIR);
        assertTrue(Files.exists(singleThreadDownloadedFilePath));
        assertFalse(diff(downloadedFilePath, singleThreadDownloadedFilePath));
    }

    @Test
    void download_with_1_M() throws IOException, ExecutionException, InterruptedException {
        HttpFileDownloader downloader = new HttpFileDownloader();
        downloader.download(DOWNLOAD_URL, HttpFileDownloader.KB * HttpFileDownloader.KB, DOWNLOAD_DIR);
        singleThreadDownload(DOWNLOAD_URL, DOWNLOAD_DIR);
        final Path downloadedFilePath = DOWNLOAD_DIR.resolve(HttpFileDownloader.getBaseName(DOWNLOAD_URL));
        assertTrue(Files.exists(downloadedFilePath));
        final Path singleThreadDownloadedFilePath = getSingleThreadDownloadFilePath(DOWNLOAD_URL, DOWNLOAD_DIR);
        assertTrue(Files.exists(singleThreadDownloadedFilePath));
        assertFalse(diff(downloadedFilePath, singleThreadDownloadedFilePath));
    }

    @Test
    void download_with_5_K() throws IOException, ExecutionException, InterruptedException {
        HttpFileDownloader downloader = new HttpFileDownloader();
        final String downloadURL = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/2.1.21/kotlin-stdlib-2.1.21.jar";
        downloader.download(downloadURL, 5* HttpFileDownloader.KB, DOWNLOAD_DIR);
        singleThreadDownload(downloadURL, DOWNLOAD_DIR);
        final Path downloadedFilePath = DOWNLOAD_DIR.resolve(HttpFileDownloader.getBaseName(downloadURL));
        assertTrue(Files.exists(downloadedFilePath));
        final Path singleThreadDownloadedFilePath = getSingleThreadDownloadFilePath(downloadURL, DOWNLOAD_DIR);
        assertTrue(Files.exists(singleThreadDownloadedFilePath));
        assertFalse(diff(downloadedFilePath, singleThreadDownloadedFilePath));
    }

    private static void singleThreadDownload(String url, Path downloadDirectory) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url) )
                .GET()
                .build();
        HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofFile(getSingleThreadDownloadFilePath(url, downloadDirectory)));
    }
    private static Path getSingleThreadDownloadFilePath(String url, Path downloadDirectory) {
        return downloadDirectory.resolve(HttpFileDownloader.getBaseName(url) + ".original");
    }

    public static boolean diff(Path file1, Path file2) throws IOException {
        byte[] bytes1 = Files.readAllBytes(file1);
        byte[] bytes2 = Files.readAllBytes(file2);

        int minLength = Math.min(bytes1.length, bytes2.length);
        boolean differenceFound = false;

        for (int i = 0; i < minLength; i++) {
            if (bytes1[i] != bytes2[i]) {
                differenceFound = true;
                break; // remove this `break` to print all differences
            }
        }

        if (!differenceFound && bytes1.length != bytes2.length) {
            return true;
        }
        return differenceFound;
    }

}
