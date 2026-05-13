package it.unipd.dei.webapp.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DBConnection {

    private static volatile DataSource dataSource;

    // Package-private: allows tests to inject a DataSource without a JNDI context
    static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource has not been initialized");
        }
        return dataSource.getConnection();
    }
}
