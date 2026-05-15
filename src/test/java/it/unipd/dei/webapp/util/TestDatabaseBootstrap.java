package it.unipd.dei.webapp.util;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class TestDatabaseBootstrap implements BeforeAllCallback {

    private static final String CONTEXT_XML = "src/main/webapp/META-INF/context.xml";
    private static volatile boolean initialized;

    @Override
    public void beforeAll(ExtensionContext context) {
        initialize();
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        DatabaseConfig config = DatabaseConfig.load();
        DBConnection.setDataSource(new DriverManagerDataSource(config.url, config.username, config.password));
        initialized = true;
    }

    public static synchronized void resetForTest(DataSource dataSource) {
        DBConnection.setDataSource(dataSource);
    }

    public static synchronized void restoreAfterTest() {
        initialized = false;
        initialize();
    }

    private record DatabaseConfig(String url, String username, String password) {

        private static DatabaseConfig load() {
            ContextConfig contextConfig = ContextConfig.load();

            String url = firstPresent(
                    System.getProperty("test.db.url"),
                    System.getenv("TEST_DB_URL"),
                    contextConfig.url(),
                    "jdbc:postgresql://localhost:5432/fintrack_db"
            );
            String username = firstPresent(
                    System.getProperty("test.db.username"),
                    System.getenv("TEST_DB_USERNAME"),
                    contextConfig.username(),
                    "postgres"
            );
            String password = firstPresent(
                    System.getProperty("test.db.password"),
                    System.getenv("TEST_DB_PASSWORD"),
                    contextConfig.password(),
                    ""
            );

            return new DatabaseConfig(url, username, password);
        }

        private static String firstPresent(String... values) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
            return "";
        }
    }

    private record ContextConfig(String url, String username, String password) {

        private static ContextConfig load() {
            File contextXml = new File(CONTEXT_XML);
            if (!contextXml.isFile()) {
                return new ContextConfig(null, null, null);
            }

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

                Document document = factory.newDocumentBuilder().parse(contextXml);
                NodeList resources = document.getElementsByTagName("Resource");
                for (int i = 0; i < resources.getLength(); i++) {
                    Element resource = (Element) resources.item(i);
                    if ("jdbc/fintrack".equals(resource.getAttribute("name"))) {
                        return new ContextConfig(
                                resource.getAttribute("url"),
                                resource.getAttribute("username"),
                                resource.getAttribute("password")
                        );
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to read test database settings from " + CONTEXT_XML, e);
            }

            return new ContextConfig(null, null, null);
        }
    }

    private static final class DriverManagerDataSource implements DataSource {

        private final String url;
        private final String username;
        private final String password;
        private PrintWriter logWriter;
        private int loginTimeout;

        private DriverManagerDataSource(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }

        @Override
        public PrintWriter getLogWriter() {
            return logWriter;
        }

        @Override
        public void setLogWriter(PrintWriter out) {
            this.logWriter = out;
        }

        @Override
        public void setLoginTimeout(int seconds) {
            this.loginTimeout = seconds;
        }

        @Override
        public int getLoginTimeout() {
            return loginTimeout;
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
