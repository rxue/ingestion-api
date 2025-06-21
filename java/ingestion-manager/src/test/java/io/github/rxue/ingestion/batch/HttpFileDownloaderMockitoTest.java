package io.github.rxue.ingestion.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;

import static io.github.rxue.ingestion.batch.HttpFileDownloaderIT.TEST_DATA_DIR_STR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HttpFileDownloaderMockitoTest {
    @Test
    void process_when_file_is_already_downloaded(@Mock HttpClient mockedHttpClient) throws IOException, InterruptedException {
        String fileName = "sample.tar.gz";
        URI downloadURI = URI.create("http://dummyhost/" + fileName);
        Path downloadToDirectory = Path.of(TEST_DATA_DIR_STR);
        HttpFileDownloader httpFileDownloader = new HttpFileDownloader(downloadURI,
                downloadToDirectory, null, mockedHttpClient);
        httpFileDownloader.process();
        verify(mockedHttpClient, never()).send(any(), any());
    }
}
