package io.github.rxue.ingestion.jpaentity;

import jakarta.persistence.*;

@Entity
@Table(indexes = @Index(name = "IDX_FROM_EMAIL", columnList = "fromEmail"))
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fromEmail;
    private String messageId;

    public Long getId() {
        return id;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
