package io.github.rxue.ingestion.log;

import io.github.rxue.ingestion.Completion;
import io.github.rxue.ingestion.StateDescriber;
import io.github.rxue.ingestion.jpaentity.StatusLog;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Interceptor
@io.github.rxue.ingestion.log.Log
public class StatusLogger {
    private static final Logger LOG = LoggerFactory.getLogger(StatusLogger.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    @AroundInvoke
    public Object log(InvocationContext context) {
        Object target = context.getTarget();
        try {
            if (target instanceof StateDescriber stateDescriber) {
                LOG.info("Intercepting " + target);
                userTransaction.begin();
                StatusLog state = getState();
                state.setDescription(stateDescriber.description());
                entityManager.merge(state);

                if (target instanceof Completion) {
                    StatusLog processedNumberLog = new StatusLog();
                    Long totalNumber = (Long) context.getParameters()[0];
                    processedNumberLog.setDescription("Total number of processed messages is " + totalNumber);
                    entityManager.persist(processedNumberLog);
                }
                userTransaction.commit();
            }

            return context.proceed();
        } catch(Exception e) {
            LOG.error("When you see this FATAL message, the exception thrown caused the app kinda crashed :< " + e);
            throw new RuntimeException(e);
        }

    }

    private StatusLog getState() {
        List<StatusLog> stateList = entityManager.createQuery("select s from " + StatusLog.class.getSimpleName() + " s", StatusLog.class).getResultList();
        if (stateList.isEmpty()) {
            return new StatusLog();
        } else {
            return stateList.get(0);
        }
    }

}
