package de.sky.regular.income.database;

import de.sky.common.database.DatabaseConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        connect = ConnectDatabase.connect(url, user, password, schema);

        ConnectDatabase.migrateSchema(connect);
    }

    @Override
    public void destroy() throws Exception {
        if (connect != null)
            connect.close();
    }
}
