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

    public static DatabaseConnection connect(String url, String user, String password, String inSchema, String outSchema) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        //config.setSchema(outSchema);

        return new DatabaseConnection(new HikariDataSource(config), inSchema, outSchema);
    }
}
