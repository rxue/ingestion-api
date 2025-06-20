package io.github.rxue;

import io.github.rxue.ingestion.Completion;
import io.github.rxue.ingestion.IngestionRunner;
import io.quarkiverse.jberet.runtime.QuarkusJobOperator;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jberet.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class Consumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private final ConnectionFactory connectionFactory;
    private final String queueName;
    private final ExecutorService executor;
    //private final IngestionRunner ingestionRunner;
    private final JobOperator jobOperator;
    private final JobRepository jobRepository;

    @Inject
    public Consumer(ConnectionFactory connectionFactory,
                    @ConfigProperty(name = "QUEUE_NAME") String queueName, JobOperator jobOperator, JobRepository jobRepository) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        //this.ingestionRunner = ingestionRunner;
        this.jobOperator = jobOperator;
        this.jobRepository = jobRepository;
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
                QuarkusJobOperator job;
                final String downloadURL = message.getBody(String.class);
                LOGGER.info("received url: " + downloadURL);
                long executionId = jobOperator.start("ingestion", null);
                LOGGER.info("job started by JobOperator");
                JobExecution jobExecution = jobOperator.getJobExecution(executionId);
                LOGGER.info("JobExecution: " + jobExecution.getJobName());
                BatchStatus batchStatus = jobExecution.getBatchStatus();
                //ingestionRunner.run(receivedString);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


}
