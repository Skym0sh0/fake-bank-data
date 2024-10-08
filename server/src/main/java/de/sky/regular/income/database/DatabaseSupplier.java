package de.sky.regular.income.database;

import java.util.function.Supplier;

public interface DatabaseSupplier extends Supplier<DatabaseConnection> {
    @Override
    default DatabaseConnection get() {
        return this.getDatabase();
    }

    DatabaseConnection getDatabase();
}
