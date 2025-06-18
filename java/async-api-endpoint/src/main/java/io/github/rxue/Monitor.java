package io.github.rxue;

import io.github.rxue.jpaentity.State;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class Monitor {
    private final EntityManager entityManager;
    public Monitor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<String> getStatus() throws IOException {
        List<State> stateList = entityManager.createQuery("select s from State s", State.class)
                .getResultList();
        if (stateList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(stateList.get(0).getDescription());
    }
}
