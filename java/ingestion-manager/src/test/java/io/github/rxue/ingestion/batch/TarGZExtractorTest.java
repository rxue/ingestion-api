package io.github.rxue.ingestion.batch;

import org.junit.jupiter.api.*;

import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TarGZExtractorTest {
    @Test
    void getDownloadedFilePath() {
        String fileName = "enron_mail_20150507.tar.gz";
        String downloadURL = "https://www.cs.cmu.edu/~enron/" + fileName;
        String downloadToDirectory = "/home/default";
        assertThat(TarGZExtractor.getDownloadedFilePath(downloadURL, downloadToDirectory))
                .isEqualTo(Path.of(downloadToDirectory, fileName));

    }

}