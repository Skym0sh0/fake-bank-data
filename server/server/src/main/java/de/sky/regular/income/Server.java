package de.sky.regular.income;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.database.ConnectDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {
    public static void main(String[] args) throws Exception {
        try (DatabaseConnection db = ConnectDatabase.connect()) {
            ConnectDatabase.migrateSchema(db);
        }

        SpringApplication.run(Server.class, args);
    }
}
