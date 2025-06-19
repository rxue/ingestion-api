package io.github.rxue.jpaentity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class StatusLog {
    @Id
    private Long id;
    private String description;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
