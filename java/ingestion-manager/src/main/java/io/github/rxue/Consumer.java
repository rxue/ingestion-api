package io.github.rxue;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.JobExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.rxue.ingestion.batch.HttpFileDownloader.DOWNLOAD_URL_PROPERTY;

@ApplicationScoped
public class Consumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private final ConnectionFactory connectionFactory;
    private final String queueName;
    private final ExecutorService executor;
    private final JobOperator jobOperator;

    @Inject
    public Consumer(ConnectionFactory connectionFactory,
                    @ConfigProperty(name = "QUEUE_NAME") String queueName, JobOperator jobOperator) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        this.jobOperator = jobOperator;
        this.executor = Executors.newSingleThreadExecutor();
    }

    void onStart(@Observes StartupEvent ev) {
        executor.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        executor.shutdown();
    }

    @Override
    public void run() {
        System.out.println("Dispatcher with THREAD ID: " + Thread.currentThread().getId());
        try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue(queueName));
            while (true) {
                Message message = consumer.receive();
                if (message == null) {
                    return;
                }
                final String downloadURL = message.getBody(String.class);
                LOGGER.info("received url: " + downloadURL);
                Properties properties = new Properties();
                properties.setProperty(DOWNLOAD_URL_PROPERTY, downloadURL);
                long executionId = jobOperator.start("ingestion", properties);
                LOGGER.info("job started by JobOperator");
                JobExecution jobExecution = jobOperator.getJobExecution(executionId);
                LOGGER.info("JobExecution: " + jobExecution.getJobName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


}
