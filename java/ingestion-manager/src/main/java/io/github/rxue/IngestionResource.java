package io.github.rxue;

import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        if (hasNoRunningJob()) {
            LOGGER.info("received urlx: " + downloadURL);
            Properties properties = new Properties();
            properties.setProperty(DOWNLOAD_URL_PROPERTY, downloadURL);
            jobOperator.start(JOB_NAME, properties);
            LOGGER.info("job started by JobOperator");
            return Response.ok().build();
        } else {
            LOGGER.info("running already");
            return Response.status(Response.Status.CONFLICT)
                    .entity("task started already")
                    .build();
        }
    }
    private boolean hasNoRunningJob() {
        Set<String> jobNames = jobOperator.getJobNames();
        return jobNames.isEmpty();
    }

    @Path("status")
    @GET
    public Response getStatue() {
        StringBuilder statusStringBuilder = new StringBuilder();
        if (hasNoRunningJob())
            return Response.noContent().build();
        List<JobInstance> jobInstances = jobOperator.getJobInstances(JOB_NAME, 0, 1);
        JobInstance jobInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = jobOperator.getJobExecutions(jobInstance);
        LOGGER.info("job executions {}", jobExecutions);
        for (JobExecution jobExecution : jobExecutions) {
            BatchStatus batchStatus = jobExecution.getBatchStatus();
            String status = batchStatus.name();
            statusStringBuilder.append(status);
            statusStringBuilder.append("\n");
            List<StepExecution> stepExecutions = jobOperator.getStepExecutions(jobExecution.getExecutionId());
            StepExecution lastStepExecution = stepExecutions.get(stepExecutions.size() - 1);
            Metric[] metrics = lastStepExecution.getMetrics();
            StringBuilder metricsBuilder = new StringBuilder();
            for (Metric metric : metrics) {
                metricsBuilder.append(metric.toString()).append(",");
            }
            statusStringBuilder.append(metricsBuilder);
            return Response.ok(statusStringBuilder.toString()).build();
        }
        throw new IllegalStateException("Never expected to come here");
    }

}
