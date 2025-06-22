package io.github.rxue.ingestion.batch;

import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MailReaderTest {
    private static final String TEST_DATA_DIR_STR = "src/test/resources";

    @Test
    void getAllFiles() {
        Path rootDirectory = Path.of(TEST_DATA_DIR_STR, "testmail", "campbell-l");
        Iterator<Path> result = MailReader.getAllFiles(rootDirectory);
        List<String> listedPathName = new ArrayList<>();
        while (result.hasNext()) {
            Path currentFile = result.next();
            listedPathName.add(currentFile.getFileName().toString());
        }
        assertThat(listedPathName).hasSize(2);
        assertThat(listedPathName)
                .contains("1.", "2.");
    }

}