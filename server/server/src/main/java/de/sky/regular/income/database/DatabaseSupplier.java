package de.sky.regular.income.database;

import de.sky.common.database.DatabaseConnection;

public interface DatabaseSupplier {
    DatabaseConnection getDatabase();
}
