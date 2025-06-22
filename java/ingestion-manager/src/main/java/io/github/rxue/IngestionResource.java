package io.github.rxue;

import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.JobExecution;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Set;

import static io.github.rxue.ingestion.batch.HttpFileDownloader.DOWNLOAD_URL_PROPERTY;

@Path("/")
public class IngestionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestionResource.class);
    public static final String JOB_NAME = "ingestion";
    private final JobOperator jobOperator;

    @Inject
    public IngestionResource(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @Path("start")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response start(String downloadURL) {
        Set<String> jobNames = jobOperator.getJobNames();
        if (jobNames.isEmpty()) {
            System.out.println("Dispatcher with THREAD ID: " + Thread.currentThread().getId());
            LOGGER.info("received url: " + downloadURL);
            Properties properties = new Properties();
            properties.setProperty(DOWNLOAD_URL_PROPERTY, downloadURL);
            long executionId = jobOperator.start(JOB_NAME, properties);
            LOGGER.info("job started by JobOperator");
            return Response.ok().build();
        } else {
            LOGGER.info("running already");
            return Response.status(Response.Status.CONFLICT)
                    .entity("task started already")
                    .build();
        }
    }




}
