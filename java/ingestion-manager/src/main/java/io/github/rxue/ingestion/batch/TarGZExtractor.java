package io.github.rxue.ingestion.batch;

import jakarta.batch.api.Batchlet;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

@Named
@Dependent
public class TarGZExtractor implements Batchlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(TarGZExtractor.class);
    private final String tarGZFilePath;
    private final String extractTargetDirectory;
    public TarGZExtractor(@ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadDirectory) {
        String fileName = "test.tar.gz";
        this.tarGZFilePath = Path.of(downloadDirectory).resolve(fileName).toString();
        this.extractTargetDirectory = formInputDirectory(downloadDirectory).toString();
    }

    static Path formInputDirectory(String downloadDirectory) {
        return Path.of(downloadDirectory).resolve("input");
    }

    @Override
    public String process() throws Exception {
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
        return "DATA_EXTRACTED";
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stop extracting");
    }
}
