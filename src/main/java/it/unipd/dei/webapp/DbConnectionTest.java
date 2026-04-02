package it.unipd.dei.webapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/fintrack_db";
        String username = "postgres";
        String password = "huesos228";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connection successful!");

            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery("SELECT current_database(), current_user")) {

                while (rs.next()) {
                    System.out.println("DB: " + rs.getString(1));
                    System.out.println("User: " + rs.getString(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}