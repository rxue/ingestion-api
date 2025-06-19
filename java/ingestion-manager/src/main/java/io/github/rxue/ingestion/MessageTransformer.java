package io.github.rxue.ingestion;

import io.github.rxue.ingestion.log.Log;
import jakarta.enterprise.context.Dependent;

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

@Dependent
public class MessageTransformer implements StateDescriber {
    @Log
    public Map<String,Long> toFromEmailsWithMessageCount(Path inputDirectory) {
        try (Stream<Path> paths = Files.walk(inputDirectory)) {
            return paths.parallel().filter(MessageTransformer::isRegularFile)
                    .map(MessageTransformer::readSenderEmails)
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

    static List<Sender> getTopSenders(Map<Sender,Long> sendersWithMessageCount, int limit) {
        return sendersWithMessageCount.keySet().stream()
                .sorted(comparing(sendersWithMessageCount::get).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public String description() {
        return "Transform message to what is needed";
    }
}
