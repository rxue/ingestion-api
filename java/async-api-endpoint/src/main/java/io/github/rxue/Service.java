package io.github.rxue;

import io.github.rxue.jpaentity.SenderStatistic;
import io.github.rxue.jpaentity.StatusLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
public class Service {
    private final EntityManager entityManager;
    public Service(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<String> getStatus() {
        List<StatusLog> statusLogList = entityManager.createQuery("select s from StatusLog s", StatusLog.class)
                .getResultList();
        if (statusLogList.isEmpty()) {
            return Optional.empty();
        }
        String statusLineByLine = statusLogList.stream()
                .map(StatusLog::getDescription)
                .collect(joining("\n"));
        return Optional.of(statusLineByLine);
    }
    public Optional<String> getTopTenSenders() {
        List<SenderStatistic> senderStatisticList = entityManager.createQuery("select s from SenderStatistic s order by sentMails desc limit 10", SenderStatistic.class)
                .getResultList();
        if (senderStatisticList.isEmpty()) {
            return Optional.empty();
        }
        String statisticLineByLine = senderStatisticList.stream()
                .map(senderStatistic -> "email: " + senderStatistic.getEmail() + " sent " + senderStatistic.getSentMails() + " times")
                .collect(joining("\n"));
        return Optional.of(statisticLineByLine);
    }
}
