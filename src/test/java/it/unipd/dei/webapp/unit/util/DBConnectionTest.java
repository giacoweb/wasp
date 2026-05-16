package it.unipd.dei.webapp.unit.util;

import it.unipd.dei.webapp.util.DBConnection;
import it.unipd.dei.webapp.util.TestDatabaseBootstrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DBConnectionTest {

    @AfterEach
    void restoreTestDataSource() {
        TestDatabaseBootstrap.restoreAfterTest();
    }

    @Test
    void getConnectionReturnsConnectionFromInjectedDataSource() throws Exception {
        Connection expectedConnection = connectionProxy();
        FakeDataSource dataSource = new FakeDataSource(expectedConnection);

        TestDatabaseBootstrap.resetForTest(dataSource);

        try (Connection connection = DBConnection.getConnection()) {
            assertSame(expectedConnection, connection);
        }

        assertEquals(1, dataSource.connectionRequests);
    }

    @Test
    void getConnectionThrowsWhenDataSourceIsMissing() {
        TestDatabaseBootstrap.resetForTest(null);

        assertThrows(IllegalStateException.class, DBConnection::getConnection);
    }

    private static Connection connectionProxy() {
        return (Connection) Proxy.newProxyInstance(
                DBConnectionTest.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "close" -> null;
                    case "isClosed" -> false;
                    case "toString" -> "fake-test-connection";
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private static final class FakeDataSource implements DataSource {

        private final Connection connection;
        private int connectionRequests;

        private FakeDataSource(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() {
            connectionRequests++;
            return connection;
        }

        @Override
        public Connection getConnection(String username, String password) {
            connectionRequests++;
            return connection;
        }

        @Override
        public PrintWriter getLogWriter() {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) {
        }

        @Override
        public void setLoginTimeout(int seconds) {
        }

        @Override
        public int getLoginTimeout() {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (iface.isInstance(this)) {
                return iface.cast(this);
            }
            throw new SQLException("Not a wrapper for " + iface.getName());
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return iface.isInstance(this);
        }
    }
}
