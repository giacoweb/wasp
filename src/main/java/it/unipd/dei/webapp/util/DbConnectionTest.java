package it.unipd.dei.webapp.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DbConnectionTest {

    public static void main(String[] args) {
        Properties properties = new Properties();

        try (InputStream input = DbConnectionTest.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found in src/main/resources");
            }

            properties.load(input);

            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String driver = properties.getProperty("db.driver");

            Class.forName(driver);

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Database connection successful!");

                try (Statement statement = connection.createStatement();
                     ResultSet rs = statement.executeQuery("SELECT current_database(), current_user")) {

                    while (rs.next()) {
                        System.out.println("DB: " + rs.getString(1));
                        System.out.println("User: " + rs.getString(2));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}