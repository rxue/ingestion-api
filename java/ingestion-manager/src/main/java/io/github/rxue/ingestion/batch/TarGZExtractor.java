package io.github.rxue.ingestion.batch;

import jakarta.batch.api.Batchlet;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import static io.github.rxue.ingestion.batch.HttpFileDownloader.*;

@Named
@Dependent
public class TarGZExtractor implements Batchlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(TarGZExtractor.class);
    private final String downloadedTarGZPath;
    private final String extractToDirectory;

    @Inject
    public TarGZExtractor(JobContext jobContext, @ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadToDirectory) {
        this.downloadedTarGZPath = getDownloadedFilePath(getDownloadURL(jobContext), downloadToDirectory).toString();
        this.extractToDirectory = formExtractToDirectory(downloadToDirectory).toString();
    }

    static Path formExtractToDirectory(String downloadDirectory) {
        return Path.of(downloadDirectory).resolve("input");
    }

    @Override
    public String process() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("tar", "-xzf", downloadedTarGZPath.toString(), "-C", extractToDirectory);
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
        LOGGER.info("Downloaded file path is " + downloadedTarGZPath);
        return "DATA_EXTRACTED TERMINATE ON HALF WAY";
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stop extracting");
    }
}
