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
import jakarta.transaction.UserTransaction;

import java.util.List;

@Interceptor
@Log
public class StatusLogger {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        Object target = context.getTarget();
        State state = getState();
        System.out.println("INTERCEPTED");
        //if (target instanceof StateDescriber stateDescriber) {
          //  state.setDescription(stateDescriber.description());
          //  System.out.println("is statedescriber");
            //userTransaction.begin();
            //entityManager.merge(state);
            //userTransaction.commit();
            //System.out.println("entityManager is " + entityManager);
        //}
        return context.proceed();
    }

    private State getState() {
        System.out.println("going to get state!!!!!");
        try {
            List<State> stateList = entityManager.createQuery("select s from State s", State.class)
                    .getResultList();
            System.out.println("state result " + stateList);
            if (stateList.isEmpty()) {
                return new State();
            } else {
                return stateList.get(0);
            }
        } catch (Throwable e) {
            System.out.println("got example" + e);
        }
        throw new IllegalStateException();

    }

}
