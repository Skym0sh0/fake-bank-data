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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatabaseConnection implements AutoCloseable {
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
                        .set(new Settings().withRenderMapping(
                                new RenderMapping().withSchemata(
                                        new MappedSchema()
                                                .withInput(inSchema)
                                                .withOutput(outSchema)
                                ))));
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
        try (DSLContext outerCtx = getContext()) {
            return outerCtx.transactionResult(configuration -> {
                try (DSLContext ctx = DSL.using(configuration)) {
                    return func.apply(ctx);
                }
            });
        }
    }

    public void transactionWithoutResult(Consumer<DSLContext> func) {
        try (DSLContext outerCtx = getContext()) {
            outerCtx.transaction(configuration -> {
                try (DSLContext ctx = DSL.using(configuration)) {
                    func.accept(ctx);
                }
            });
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
