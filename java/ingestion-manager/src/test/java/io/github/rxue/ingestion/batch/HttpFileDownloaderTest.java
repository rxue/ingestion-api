package io.github.rxue.ingestion.batch;

import org.junit.jupiter.api.*;

import java.net.URI;
import java.util.List;
import io.github.rxue.ingestion.batch.HttpFileDownloader.Range;
import static io.github.rxue.ingestion.batch.HttpFileDownloader.divide;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpFileDownloaderTest {

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