package io.github.rxue;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.rxue.HttpFileDownloader.Range;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpFileDownloaderTest {

    @Test
    void getBaseName() {
        String url = "https://www.cs.cmu.edu/~enron/enron_mail_20150507.tar.gz";
        assertThat(HttpFileDownloader.getBaseName(url))
                .isEqualTo("enron_mail_20150507.tar.gz");
    }

    @Test
    void divide_1_chunk() {
        List<Range> result = HttpFileDownloader.divide(100, 100);
        assertThat(result)
                .isEqualTo(List.of(new Range(0,99)));
    }

    @Test
    void divide_2_chunks() {
        List<Range> result = HttpFileDownloader.divide(100, 50);
        assertThat(result)
                .isEqualTo(List.of(new Range(0,49), new Range(50,99)));
    }

    @Test
    void divide_3_chunks() {
        List<Range> result = HttpFileDownloader.divide(101, 50);
        assertThat(result)
                .isEqualTo(List.of(new Range(0,49), new Range(50,99), new Range(100,100)));
    }

}