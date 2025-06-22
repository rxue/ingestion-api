package io.github.rxue.ingestion;

import io.github.rxue.ingestion.batch.MailReader;
import io.github.rxue.ingestion.jpaentity.Mail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MailRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailRepository.class);
    private final EntityManager entityManager;
    @Inject
    public MailRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public List<SenderStatistic> getTopTenSenders() {
        List<Object[]> resultList = entityManager.createQuery("select m.fromEmail, count(m) as sentMails from Mail m group by m.fromEmail order by sentMails desc limit 10")
                .getResultList();
        List<SenderStatistic> senderStatisticList = new ArrayList<>();
        for (Object[] objects : resultList) {
            LOGGER.info("going to put senderStatisticList");
            senderStatisticList.add(new SenderStatistic((String)objects[0], (Long) objects[1]));
        }
        return senderStatisticList;
    }
}
