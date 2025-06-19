package io.github.rxue.ingestion;

import io.github.rxue.ingestion.log.Log;
import jakarta.enterprise.context.Dependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class Completion implements StateDescriber {
    private static final Logger LOG = LoggerFactory.getLogger(Completion.class);
    @Log
    public void passToInterceptor(Long totalNumberOfMessageProcessed) {
        LOG.info(description());
    }
    @Override
    public String description() {
        return "DONE";
    }
}
