package de.sky.regular.income.database;

import de.sky.common.database.DatabaseConnection;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Slf4j
@Service
public class DatabaseService implements DatabaseSupplier, InitializingBean, DisposableBean {
    private DatabaseConnection connect;

    @Value("${config.jdbc.url}")
    private String url;
    @Value("${config.jdbc.user}")
    private String user;
    @Value("${config.jdbc.password}")
    private String password;
    @Value("${config.jdbc.schema}")
    private String schema;

    @Override
    public DatabaseConnection getDatabase() {
        return connect;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Connecting to Database");

        connect = ConnectDatabase.connect(url, user, password, schema);

        log.info("Database connected, now migrating Schema...");

        ConnectDatabase.migrateSchema(connect);

        log.info("Database connected and schema migrated successfully");
    }

    @Override
    public void destroy() throws Exception {
        log.info("Closing Database Connection...");

        if (connect != null)
            connect.close();

        log.info("Database Connection successfully closed");
    }
}
