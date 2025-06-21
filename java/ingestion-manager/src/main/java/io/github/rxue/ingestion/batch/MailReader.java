package io.github.rxue.ingestion.batch;

import io.github.rxue.ingestion.jpaentity.Mail;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

@Named
@Dependent
public class MailReader extends AbstractItemReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailReader.class);
    private Iterator<Path> mails;

    public MailReader(@ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadDirectory) {
        Path inputDirectory = TarGZExtractor.formInputDirectory(downloadDirectory);
        mails = getAllFiles(inputDirectory);
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        LOGGER.info("Opened checkpoint is " + checkpoint);
    }

    static Iterator<Path> getAllFiles(Path directory) {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(MailReader::isRegularFile)
                    .toList()
                    .iterator();
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

    @Override
    public Mail readItem() throws Exception {
        if (mails.hasNext()) {
            Path filePath = mails.next();
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.ISO_8859_1)) {
                Mail mail = new Mail();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Message-ID: ")) {
                        mail.setMessageId(line.split(" ")[1]);
                    } if (line.startsWith("From: ")) {
                        mail.setFromEmail(line.split(" ")[1]);
                        return mail;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erratic File: " + filePath);
                e.printStackTrace();
            }
            throw new IllegalStateException();
        } else {
            return null;
        }
    }

}
