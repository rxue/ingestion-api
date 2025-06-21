package io.github.rxue.ingestion;

import io.github.rxue.ingestion.log.Log;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static io.github.rxue.ingestion.HttpFileDownloader.MB;

@Dependent
public class IngestionRunner implements StateDescriber {
    private final Path downloadDirectoryPath;
    private final HttpFileDownloader httpFileDownloader;
    private final MessageTransformer messageTransformer;
    private final Completion completion;
    @Inject
    public IngestionRunner(@ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadDirectory,
                           HttpFileDownloader httpFileDownloader,
                           MessageTransformer messageTransformer,
                           Completion completion) {
        this.downloadDirectoryPath = Path.of(downloadDirectory);
        this.httpFileDownloader = httpFileDownloader;
        this.messageTransformer = messageTransformer;
        this.completion = completion;
    }
    @Log
    public void run(String dataSourceURL) {
        System.out.println("INSIDE RUNNER with data source URL " + dataSourceURL);

        Optional<Path> optionalTarGZFilePath = httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
        if (optionalTarGZFilePath.isEmpty()) {
            return;
        }
        final Path ingestionInputDirectory = downloadDirectoryPath.resolve("input");
        Map<String,Long> transformedData = messageTransformer.toFromEmailsWithMessageCount(ingestionInputDirectory);
        final Long totalMessagesProcessed = transformedData.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        System.out.println("Total number of messages processed : " + totalMessagesProcessed);
        completion.passToInterceptor(totalMessagesProcessed);
    }

    @Override
    public String description() {
        return "Ingest transformed data to data store";
    }
}
