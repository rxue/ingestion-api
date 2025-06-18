package io.github.rxue.ingestion.log;

import io.github.rxue.ingestion.HttpFileDownloader;
import io.github.rxue.ingestion.jpaentity.State;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

@Interceptor
@Log
public class StatusLogger {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        System.out.println("Interceptor working:D !!!!!!!!");
        Object target = context.getTarget();
        if (target instanceof HttpFileDownloader httpFileDownloader) {
            System.out.println("description: " + httpFileDownloader.description());
            State state = new State();
            state.setDescription(httpFileDownloader.description());
            userTransaction.begin();
            System.out.println("try to merge result!!!!!!!!!");
            entityManager.merge(state);
            userTransaction.commit();
            System.out.println("entityManager is " + entityManager);
        }

        return context.proceed();
    }

}
