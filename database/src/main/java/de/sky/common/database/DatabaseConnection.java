package de.sky.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

public class DatabaseConnection implements AutoCloseable {
    private static final Logger logger = getLogger(DatabaseConnection.class);

    private final HikariDataSource datasource;

    private final ThreadLocal<Configuration> configuration;

    private DatabaseConnection(HikariDataSource datasource, String inSchema, String outSchema) {
        this.datasource = Objects.requireNonNull(datasource);

        Objects.requireNonNull(inSchema);
        Objects.requireNonNull(outSchema);

        this.configuration = ThreadLocal.withInitial(() ->
                new DefaultConfiguration()
                        .set(datasource)
                        .set(SQLDialect.POSTGRES)
                        .set(new Settings()
                                .withRenderMapping(
                                        new RenderMapping()
                                                .withSchemata(
                                                        new MappedSchema()
                                                                .withInput(inSchema)
                                                                .withOutput(outSchema)
                                                )
                                )
                        )
        );
    }

    @Override
    public void close() throws Exception {
        datasource.close();
    }

    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public DSLContext getContext() {
        return DSL.using(getConfiguration());
    }

    public Configuration getConfiguration() {
        return configuration.get();
    }

    public <T> T transactionWithResult(Function<DSLContext, T> func) {
        Instant t0 = Instant.now();

        try {
            var outerCtx = getContext();
            return outerCtx.transactionResult(configuration -> {
                var ctx = DSL.using(configuration);
                return func.apply(ctx);
            });
        } finally {
            Instant t1 = Instant.now();

            logger.info("Database Result-Transaction took {} ms", Duration.between(t0, t1).toMillis());
        }
    }

    public void transactionWithoutResult(Consumer<DSLContext> func) {
        Instant t0 = Instant.now();

        try {
            var outerCtx = getContext();

            outerCtx.transaction(configuration -> {
                var ctx = DSL.using(configuration);
                func.accept(ctx);
            });
        } finally {
            Instant t1 = Instant.now();

            logger.info("Database Void-Transaction took {} ms", Duration.between(t0, t1).toMillis());
        }
    }

    public static DatabaseConnection connect(String url, String user, String password, String inSchema, String outSchema) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        //config.setSchema(outSchema);

        return new DatabaseConnection(new HikariDataSource(config), inSchema, outSchema);
    }
}
