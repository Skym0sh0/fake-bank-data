package de.sky.regular.income.database;

import de.sky.common.database.DatabaseConnection;
import org.flywaydb.core.Flyway;

public class ConnectDatabase {
    public static DatabaseConnection connect(String url, String user, String password, String schema) {
        return DatabaseConnection.connect(
                url,
                user,
                password,
                "REGULAR_INCOME",
                schema
        );
    }

    public static void migrateSchema(DatabaseConnection db) {
        String descriptor = "db/migration";

        Flyway flyway = Flyway.configure()
                .dataSource(db.getDatasource())
                .locations(descriptor)
                .load();

        flyway.migrate();
    }
}
