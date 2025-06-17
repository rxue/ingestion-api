package io.github.rxue.ingestion;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MessageCounterTest {

    @Test
    public void getSendersWithMessageCount_when_there_are_2_mails() {
        MessageCounter messageCounter = new MessageCounter(Path.of(HttpFileDownloaderIT.TEST_DATA_DIR_STR, "testmail", "campbell-l", "north_crawar_remediation"));
        Map<Sender,Long> result = messageCounter.getSendersWithMessageCount();
        assertThat(result)
                .hasSize(1);
        assertThat(result.get(new Sender("george.robinson@enron.com", "George Robinson")))
                .isEqualTo(2);
    }

    @Test
    public void writeProcessedMessageCount_when_there_are_2_mails() throws IOException {
        MessageCounter messageCounter = new MessageCounter(Path.of(HttpFileDownloaderIT.TEST_DATA_DIR_STR, "testmail", "campbell-l", "north_crawar_remediation"));
        Path outputFilePath = Path.of(HttpFileDownloaderIT.TEST_DATA_DIR_STR, "status.txt");
        messageCounter.writeProcessedMessageCount(outputFilePath);
        List<String> outputContent = Files.readAllLines(outputFilePath);
        assertThat(outputContent).hasSize(1);
        assertThat(outputContent.get(0)).isEqualTo("2 message(s) processed in total");
        Files.delete(outputFilePath);
    }

    @Test
    public void getTopSenders() {
        Map<Sender,Long> sendersWithMessageCount = new HashMap<>();
        sendersWithMessageCount.put(new Sender("A", "A"), 1L);
        sendersWithMessageCount.put(new Sender("B", "B"), 2L);
        sendersWithMessageCount.put(new Sender("C", "C"), 3L);
        sendersWithMessageCount.put(new Sender("D", "D"), 4L);
        sendersWithMessageCount.put(new Sender("E", "D"), 5L);
        List<Sender> topTenSenders = MessageCounter.getTopSenders(sendersWithMessageCount,3);
        assertThat(topTenSenders.stream().map(Sender::email)).isEqualTo(List.of("E","D","C"));

    }



}