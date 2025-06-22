package io.github.rxue.ingestion.batch;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import io.github.rxue.ingestion.batch.HttpFileDownloader.Range;
import static io.github.rxue.ingestion.batch.HttpFileDownloader.divide;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpFileDownloaderTest {
    @Test
    void getChunkSize_when_chunk_size_is_null() {
        assertThat(HttpFileDownloader.getChunkSize(null))
                .isEqualTo(HttpFileDownloader.MB * 100);
    }

    @Test
    void getChunkSize_when_chunk_size_is_defined() {
        assertThat(HttpFileDownloader.getChunkSize("1000"))
                .isEqualTo(1000);
    }

    @Test
    void getDownloadedFilePath() {
        String fileName = "enron_mail_20150507.tar.gz";
        String downloadURL = "https://www.cs.cmu.edu/~enron/" + fileName;
        String downloadToDirectory = "/home/default";
        AssertionsForClassTypes.assertThat(HttpFileDownloader.getDownloadedFilePath(downloadURL, downloadToDirectory))
                .isEqualTo(Path.of(downloadToDirectory, fileName));

    }

    @Test
    void getBaseName() {
        String fileName = "enron_mail_20150507.tar.gz";
        URI uri = URI.create("https://www.cs.cmu.edu/~enron/" + fileName);
        assertThat(HttpFileDownloader.getBaseName(uri))
                .isEqualTo(fileName);
    }

    @Test
    void divide_1_chunk() {
        List<Range> result = divide(100, 100);
        assertThat(result)
                .isEqualTo(List.of(new Range(0,99)));
    }

    @Test
    void divide_2_chunks() {
        List<Range> result = divide(100, 50);
        assertThat(result)
                .isEqualTo(List.of(new Range(0,49), new Range(50,99)));
    }

    @Test
    void divide_3_chunks() {
        List<Range> result = divide(101, 50);
        assertThat(result)
                .isEqualTo(List.of(new Range(0,49), new Range(50,99), new Range(100,100)));
    }


}