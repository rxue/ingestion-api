package io.github.rxue.ingestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class MessageCounter {
    private final Path inputDirectory;
    public MessageCounter(Path inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public Map<Sender,Long> getSendersWithMessageCount() {
        try (Stream<Path> paths = Files.walk(inputDirectory)) {
            return paths.parallel().filter(MessageCounter::isRegularFile)
                    .map(MessageCounter::readSender)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void test() {
        System.out.println("test");
    }

    public Map<String,Long> getSenderEmailsWithMessageCount() {
        try (Stream<Path> paths = Files.walk(inputDirectory)) {
            return paths.parallel().filter(MessageCounter::isRegularFile)
                    .map(MessageCounter::readSenderEmails)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRegularFile(Path path) {
        try {
            return Files.isRegularFile(path) && !Files.isHidden(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Sender readSender(Path filePath) {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.ISO_8859_1)) {
            String line;
            String email = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("From: ")) {
                    email = line.split(" ")[1];
                } else if (line.startsWith("X-From: ")) {
                    return new Sender(email, getName(line));
                }
            }
        } catch (Exception e) {
            System.out.println("Erratic File: " + filePath);
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    private static String readSenderEmails(Path filePath) {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.ISO_8859_1)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("From: ")) {
                    return line.split(" ")[1];
                }
            }
        } catch (Exception e) {
            System.out.println("Erratic File: " + filePath);
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    private static String getName(String nameLine) {
        return nameLine.substring(nameLine.indexOf(" ") + 1);
    }

    public void writeProcessedMessageCount(Path outputFilePath) throws IOException {
        Long totalMessages = getSendersWithMessageCount()
                .values().stream().mapToLong(Long::longValue)
                .sum();

        Files.writeString(outputFilePath, totalMessages + " message(s) processed in total");
    }

    static List<Sender> getTopSenders(Map<Sender,Long> sendersWithMessageCount, int limit) {
        return sendersWithMessageCount.keySet().stream()
                .sorted(comparing(sendersWithMessageCount::get).reversed())
                .limit(limit)
                .toList();
    }

    public static void main(String[] args) {
        MessageCounter messageCounter = new MessageCounter(Path.of("/","Users", "ruixue", "Downloads", "target", "maildir"));
        Map<String,Long> emailsWithMessageCount = messageCounter.getSenderEmailsWithMessageCount();
        String mail = "feedback@intcx.com";
        System.out.println(mail + " has " + emailsWithMessageCount.get(mail) + " mails");
    }
}
