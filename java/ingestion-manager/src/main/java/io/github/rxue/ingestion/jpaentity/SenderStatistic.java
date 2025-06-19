package io.github.rxue.ingestion.jpaentity;

import jakarta.persistence.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(name = "UNQ_EMAIL", columnNames = { "email" })
        ,
        indexes = {
                @Index(name = "IDX_EMAIL", columnList = "email")
        }
)
public class SenderStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
