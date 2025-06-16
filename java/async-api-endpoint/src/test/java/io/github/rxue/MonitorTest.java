package io.github.rxue;

import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MonitorTest {
    public static final String TEST_DATA_DIR = "src/test/resources";

    @Test
    void getStatus_when_status_file_does_not_exists() throws IOException {
        Monitor monitor = new Monitor(TEST_DATA_DIR + "/nostatus.txt");
        assertThat(monitor.getStatus())
                .isEqualTo("not started");
    }
    @Test
    void getStatus_when_status_file_exists() throws IOException {
        Monitor monitor = new Monitor(TEST_DATA_DIR + "/status.txt");
        assertThat(monitor.getStatus()).isNotEmpty();
    }
  
}