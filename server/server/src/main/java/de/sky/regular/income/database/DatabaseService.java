package de.sky.regular.income.database;

import de.sky.common.database.DatabaseConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService implements DatabaseSupplier, InitializingBean, DisposableBean {
    private DatabaseConnection connect;

    @Override
    public DatabaseConnection getDatabase() {
        return connect;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        connect = ConnectDatabase.connect();

        ConnectDatabase.migrateSchema(connect);
    }

    @Override
    public void destroy() throws Exception {
        if (connect != null)
            connect.close();
    }
}
