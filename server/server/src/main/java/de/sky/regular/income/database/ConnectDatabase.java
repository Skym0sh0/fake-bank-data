package de.sky.regular.income.database;

import de.sky.common.database.DatabaseConnection;
import org.flywaydb.core.Flyway;

public class ConnectDatabase {
    public static DatabaseConnection connect() {
        return DatabaseConnection.connect(
                "jdbc:postgresql://localhost:5432/scott",
                "scott",
                "tiger",
                "REGULAR_INCOME",
                "public"
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
