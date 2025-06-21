package io.github.rxue.ingestion.batch;

import io.github.rxue.ingestion.jpaentity.Mail;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.batch.runtime.context.JobContext;
import jakarta.batch.runtime.context.StepContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Named
@Dependent
public class Ingestor extends AbstractItemWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ingestor.class);
    private final JobContext jobContext;
    private final StepContext stepContext;
    private final Path inputDirectory;
    private volatile long processedMails;
    @BatchProperty
    private String downloadURL;
    public Ingestor(JobContext jobContext, StepContext stepContext, @ConfigProperty(name = "CONTAINER_DOWNLOAD_DIR") String downloadDirectory) {
        this.jobContext = jobContext;
        this.stepContext = stepContext;
        this.inputDirectory = Path.of(downloadDirectory).resolve("input");
        this.processedMails = 0;
        LOGGER.info("Construct Ingestor");
    }

    @Override
    public void open(Serializable serializable) {
        LOGGER.info("Ingestor open!!!!!");
    }

    @Override
    public void close() {
        LOGGER.info("Close Ingestor!!!!!");
    }

    @Override
    public void writeItems(List<Object> mails) throws Exception {
        String jdbcURL = System.getenv("QUARKUS_DATASOURCE_JDBC_URL");
        String username = System.getenv("QUARKUS_DATASOURCE_USERNAME");
        String password = System.getenv("QUARKUS_DATASOURCE_PASSWORD");
        try (Connection conn = DriverManager.getConnection(jdbcURL, username, password);
             Statement statement = conn.createStatement()) {
            for (Object senderStatisticObj : mails) {
                Mail mail = (Mail) senderStatisticObj;
                String fromEmail = mail.getFromEmail();
                String messageId = mail.getMessageId();
                String fromEmailStringToInsert = fromEmail;
                if (fromEmail.contains("'")) {
                    fromEmailStringToInsert = fromEmail.replace("'", "''");
                }
                String insertSQL = String.format("INSERT INTO mail (fromemail, messageid) VALUES('%s', '%s');", fromEmailStringToInsert, messageId);
                statement.addBatch(insertSQL);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            LOGGER.error("IO ERROR when writting to database");
            e.printStackTrace();
        }
        processedMails += mails.size();
    }

    @Override
    public Serializable checkpointInfo() {
        LOGGER.info("Checkpoint info: total number of processed mails is " + processedMails);
        return null;
    }
}
