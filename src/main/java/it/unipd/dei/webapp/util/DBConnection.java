package it.unipd.dei.webapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new RuntimeException(CONFIG_FILE + " not found in src/main/resources");
            }

            properties.load(input);

            String driver = properties.getProperty("db.driver");
            if (driver == null || driver.isBlank()) {
                throw new RuntimeException("Missing property: db.driver");
            }

            Class.forName(driver);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        if (url == null || url.isBlank()) {
            throw new RuntimeException("Missing property: db.url");
        }
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Missing property: db.username");
        }
        if (password == null) {
            throw new RuntimeException("Missing property: db.password");
        }

        return DriverManager.getConnection(url, username, password);
    }
}