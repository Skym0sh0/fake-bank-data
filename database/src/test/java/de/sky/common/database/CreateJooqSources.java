package de.sky.common.database;

import java.nio.file.Path;

public class CreateJooqSources {
    public static void main(String[] args) throws Exception {
        new JooqBindingGenerator(
                "REGULAR_INCOME",
                "generated.sky.regular.income",
                Path.of("./server/src/main/java"),
                Path.of("./server/src/main/resources/db/migration")
        ).run();
    }
}
