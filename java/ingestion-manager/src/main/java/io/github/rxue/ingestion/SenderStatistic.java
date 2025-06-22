package io.github.rxue.ingestion;

public record SenderStatistic(String email, long sentMails) {
    @Override
    public String toString() {
        return email + " sent " + sentMails;
    }
}
