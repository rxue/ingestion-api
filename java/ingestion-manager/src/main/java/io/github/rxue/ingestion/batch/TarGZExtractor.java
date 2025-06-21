package io.github.rxue.ingestion.batch;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.Batchlet;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static io.github.rxue.ingestion.batch.HttpFileDownloader.DOWNLOAD_URL;

@Named
@Dependent
public class TarGZExtractor implements Batchlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(TarGZExtractor.class);
    private final Path downloadedFilePath;
    private final String extractTargetDirectory;

    @Inject
    public TarGZExtractor(@BatchProperty(name= DOWNLOAD_URL) String downloadURL, @ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadToDirectory) {
        this.downloadedFilePath = HttpFileDownloader.getDownloadedFilePath(downloadURL, downloadToDirectory);
        LOGGER.info("Downloaded file path: {}", this.downloadedFilePath);
        this.extractTargetDirectory = null; //formInputDirectory(downloadDirectory).toString();
    }

    static Path formInputDirectory(String downloadDirectory) {
        return Path.of(downloadDirectory).resolve("input");
    }

    @Override
    public String process() throws Exception {

        /*
        ProcessBuilder pb = new ProcessBuilder("tar", "-xzf", tarGZFilePath.toString(), "-C", extractTargetDirectory);
        pb.inheritIO();
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exitCode != 0) {
            throw new RuntimeException("extraction failed");
        }
        System.out.println("Completed process");
         */
        LOGGER.info("Downloaded file path is " + downloadedFilePath);
        return "DATA_EXTRACTED TERMINATE ON HALF WAY";
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stop extracting");
    }
}
