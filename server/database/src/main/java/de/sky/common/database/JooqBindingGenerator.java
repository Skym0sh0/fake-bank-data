package de.sky.common.database;

import de.sky.common.database.converters.DateConverter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GenerationTool;
import org.jooq.codegen.JavaGenerator;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.postgres.PostgresDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JooqBindingGenerator {
    private static final Logger logger = LoggerFactory.getLogger(JooqBindingGenerator.class);

    private static final Set<String> DEFAULT_EXCLUDES = new HashSet<>(Arrays.asList("FLYWAY.*", "BIN.*"));

    private final String schemaName;

    private final String packageName;
    private final Path localBuildDir;
    private final Path changeset;

    public JooqBindingGenerator(String schemaName, String packageName, Path localBuildDir, Path changeset) {
        this.schemaName = Objects.requireNonNull(schemaName);
        this.packageName = Objects.requireNonNull(packageName);

        this.localBuildDir = Objects.requireNonNull(localBuildDir);

        this.changeset = Objects.requireNonNull(changeset);
    }

    public void run() throws Exception {
        try (var psql = new PostgreSQLContainer<>()
                .withDatabaseName("tmp-database")
                .withUsername("scott")
                .withPassword("tiger")) {
            psql.start();

            try {
                migrateSchema(psql);

                createJooqSources(psql);
            } catch (Exception e) {
                logger.error("Error during JooqBinding Generation", e);

                throw e;
            } finally {
                psql.stop();
            }
        }
    }

    private void migrateSchema(PostgreSQLContainer<?> psql) {
        logger.info("Migrating schema with Flyway...");

        String descriptor = Location.FILESYSTEM_PREFIX + changeset.toString();

        Flyway flyway = Flyway.configure()
                .dataSource(psql.getJdbcUrl(), psql.getUsername(), psql.getPassword())
                .locations(new Location(descriptor))
                .load();

        flyway.migrate();
    }

    private void createJooqSources(PostgreSQLContainer psql) throws Exception {
        logger.info("Jooq Connecting to: {} -> {}/{}", psql.getJdbcUrl(), psql.getUsername(), psql.getPassword());

        try (Connection connection = createConnection(psql)) {
            GenerationTool jooq = new GenerationTool();

            jooq.setConnection(connection);
            jooq.run(createConfiguration());
        }
    }

    private Configuration createConfiguration() {
        logger.info("Configuring jOOQ Generator...");

        return new Configuration()
                .withGenerator(
                        new Generator()
                                .withName(JavaGenerator.class.getName())
                                .withStrategy(
                                        new Strategy()
                                                .withName(DefaultGeneratorStrategy.class.getName())
                                ).withDatabase(
                                        new Database()
                                                .withName(PostgresDatabase.class.getName())
                                                .withInputSchema("public")
                                                .withOutputSchema(this.schemaName)
                                                .withExcludes(getExcludes())
                                                .withForcedTypes(
                                                /*new ForcedType()
                                                        .withUserType(ZonedDateTime.class.getName())
                                                        .withTypes("TIMESTAMP.*")
                                                        .withExpression(".*")
                                                        .withConverter(TimestampConverter.class.getName()),*/
                                                        new ForcedType()
                                                                .withUserType(LocalDate.class.getName())
                                                                .withTypes("DATE.*")
                                                                .withExpression(".*")
                                                                .withConverter(DateConverter.class.getName())
                                                )
                                ).withGenerate(
                                        new Generate()
                                                .withDeprecated(false)
                                                .withFluentSetters(true)
                                                .withRelations(true)
                                ).withTarget(
                                        new Target()
                                                .withEncoding(StandardCharsets.UTF_8.name())
                                                .withPackageName(this.packageName)
                                                .withDirectory(this.localBuildDir.toString())
                                                .withClean(true)
                                                .withEncoding(StandardCharsets.UTF_8.name())
                                )
                );
    }

    private String getExcludes() {
        return DEFAULT_EXCLUDES.stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining("|"));
    }

    private Connection createConnection(PostgreSQLContainer psql) throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("for " + getClass() + " to work properly, you have to manually include the H2 dependency, e.g. group: 'com.h2database', name: 'h2', version: '1.4.197'", e);
        }

        try {
            Class.forName(psql.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver missing: " + psql.getDriverClassName());
        }

        return DriverManager.getConnection(psql.getJdbcUrl(), psql.getUsername(), psql.getPassword());
    }

    private static final SimpleFileVisitor<Path> RECURSIVE_DELETER = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };
}
