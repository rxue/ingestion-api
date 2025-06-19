package io.github.rxue.jpaentity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SenderStatistic {
    @Id
    private Long id;
    private String email;
    private Long sentMails;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSentMails() {
        return sentMails;
    }

    public void setSentMails(Long sentMails) {
        this.sentMails = sentMails;
    }
}
