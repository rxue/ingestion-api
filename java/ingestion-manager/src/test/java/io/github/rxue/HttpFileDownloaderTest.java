package io.github.rxue;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpFileDownloaderTest {
    @Test
    void getBaseName() {
        String url = "https://www.cs.cmu.edu/~enron/enron_mail_20150507.tar.gz";
        assertThat(HttpFileDownloader.getBaseName(url))
                .isEqualTo("enron_mail_20150507.tar.gz");
    }

}