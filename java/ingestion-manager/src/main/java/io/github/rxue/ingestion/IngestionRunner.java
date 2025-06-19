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
    private final TarGZExtractor tarGZExtractor;
    private final MessageTransformer messageTransformer;
    private final Ingestor ingestor;
    @Inject
    public IngestionRunner(@ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadDirectory,
                           HttpFileDownloader httpFileDownloader,
                           TarGZExtractor tarGZExtractor,
                           MessageTransformer messageTransformer,
                           Ingestor ingestor) {
        this.downloadDirectoryPath = Path.of(downloadDirectory);
        this.httpFileDownloader = httpFileDownloader;
        this.tarGZExtractor = tarGZExtractor;
        this.messageTransformer = messageTransformer;
        this.ingestor = ingestor;
    }
    @Log
    public void run(String dataSourceURL) {
        System.out.println("INSIDE RUNNER with data source URL " + dataSourceURL);

        Optional<Path> optionalTarGZFilePath = httpFileDownloader.download(dataSourceURL, MB * 100, downloadDirectoryPath);
        if (optionalTarGZFilePath.isEmpty()) {
            return;
        }
        final Path ingestionInputDirectory = downloadDirectoryPath.resolve("input");
        tarGZExtractor.extract(optionalTarGZFilePath.get(), ingestionInputDirectory);
        Map<String,Long> transformedData = messageTransformer.toFromEmailsWithMessageCount(ingestionInputDirectory);
        Long totalMessagesProcessed = transformedData.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        System.out.println("Total number of messages processed : " + totalMessagesProcessed);
        ingestor.ingest(transformedData);
    }

    @Override
    public String description() {
        return "Ingest transformed data to data store";
    }
}
