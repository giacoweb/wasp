package it.unipd.dei.webapp.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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
            synchronized (DBConnection.class) {
                if (dataSource == null) {
                    try {
                        dataSource = (DataSource) new InitialContext()
                                .lookup("java:comp/env/jdbc/fintrack");
                    } catch (NamingException e) {
                        throw new RuntimeException(
                                "Failed to look up JNDI DataSource jdbc/fintrack", e);
                    }
                }
            }
        }
        return dataSource.getConnection();
    }
}
