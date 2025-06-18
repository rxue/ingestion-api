package io.github.rxue;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MonitorTest {
    public static final String TEST_DATA_DIR = "src/test/resources";

    @Test
    void getStatus_when_status_file_does_not_exists() throws IOException {
        //ARRANGE
        Monitor monitor = new Monitor(TEST_DATA_DIR + "/nostatus.log");
        //ACT
        final Optional<String> optionalStatus = monitor.getStatus();
        //ASSERT
        assertThat(optionalStatus)
                .isEmpty();

    }
    @Test
    void getStatus_when_status_file_exists() throws IOException {
        //ARRANGE
        Monitor monitor = new Monitor(TEST_DATA_DIR + "/status.txt");
        //ACT
        final Optional<String> optionalStatus = monitor.getStatus();
        //ASSERT
        assertThat(optionalStatus).isPresent();
        assertThat(optionalStatus.orElse(""))
                .isEqualTo("Download Files");
    }
  
}