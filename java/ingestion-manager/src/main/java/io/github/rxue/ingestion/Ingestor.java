package io.github.rxue.ingestion;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@Dependent
public class Ingestor implements StateDescriber {

    public void ingest(Map<String,Long> sendersWithMailCount) {
        System.out.println("INSIDE Ingestor :)");
        String jdbcURL = System.getenv("QUARKUS_DATASOURCE_JDBC_URL");
        String username = System.getenv("QUARKUS_DATASOURCE_USERNAME");
        String password = System.getenv("QUARKUS_DATASOURCE_PASSWORD");
        try (Connection conn = DriverManager.getConnection(jdbcURL, username, password);
             Statement statement = conn.createStatement()) {
            for (String email : sendersWithMailCount.keySet()) {
                String emailStringToInsert = email;
                if (email.contains("'")) {
                    emailStringToInsert = email.replace("'", "''");
                }
                String insertSQL = String.format("INSERT INTO senderstatistic (email, sentmails) VALUES('%s', %d);", emailStringToInsert, sendersWithMailCount.get(email));
                statement.addBatch(insertSQL);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public String description() {
        return "Ingest data to database";
    }
}
