package io.github.rxue;

import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.JobExecution;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static io.github.rxue.ingestion.batch.HttpFileDownloader.DOWNLOAD_URL_PROPERTY;

@Path("/")
public class IngestionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionResource.class);
    private final JobOperator jobOperator;

    @Inject
    public IngestionResource(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @Path("start")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void start(String downloadURL) {
        System.out.println("Dispatcher with THREAD ID: " + Thread.currentThread().getId());
        LOGGER.info("received url: " + downloadURL);
        Properties properties = new Properties();
        properties.setProperty(DOWNLOAD_URL_PROPERTY, downloadURL);
        long executionId = jobOperator.start("ingestion", properties);
        LOGGER.info("job started by JobOperator");
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        LOGGER.info("Final batch Status: " + jobExecution.getBatchStatus().name());
    }


}
