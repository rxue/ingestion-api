package io.github.rxue.ingestion.log;

import io.github.rxue.ingestion.HttpFileDownloader;
import io.github.rxue.ingestion.StateDescriber;
import io.github.rxue.ingestion.TarGZExtractor;
import io.github.rxue.ingestion.jpaentity.State;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;

import java.util.List;

@Interceptor
@Log
public class StatusLogger {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    @AroundInvoke
    public Object log(InvocationContext context) {
        Object target = context.getTarget();
        try {
            State state = getState();
            if (target instanceof StateDescriber stateDescriber) {
                state.setDescription(stateDescriber.description());
                userTransaction.begin();
                entityManager.merge(state);
                userTransaction.commit();
                System.out.println("entityManager is " + entityManager);
            }
        } catch (SystemException | NotSupportedException
                 | HeuristicRollbackException | HeuristicMixedException
                | RollbackException e) {
            throw new RuntimeException(e);
        }
        System.out.println("INTERCEPTED");

        try {
            return context.proceed();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private State getState() throws SystemException, NotSupportedException,
            HeuristicRollbackException, HeuristicMixedException, RollbackException {
        System.out.println("going to get state!!!!!");
        userTransaction.begin();
        List<State> stateList = entityManager.createQuery("select s from State s", State.class)
                .getResultList();
        userTransaction.commit();
        System.out.println("state result " + stateList);
        if (stateList.isEmpty()) {
                return new State();
        } else {
                return stateList.get(0);
        }
    }

}
