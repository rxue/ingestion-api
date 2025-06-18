package io.github.rxue.ingestion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;

import static io.github.rxue.ingestion.HttpFileDownloader.MB;

@ApplicationScoped
public class IngestionRunner {
    private final Path downloadDirectoryPath;
    private HttpFileDownloader httpFileDownloader;
    @Inject
    public IngestionRunner(@ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadDirectory, HttpFileDownloader httpFileDownloader) {
        this.downloadDirectoryPath = Path.of(downloadDirectory);
        this.httpFileDownloader = httpFileDownloader;
    }

    public void run(String dataSourceURL) {
        System.out.println("Execute the executor");
        httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
        //MessageCounter mc = messageCounter(statusLogger);
        //System.out.println("going to call test");
        //mc.test();
    }

}
