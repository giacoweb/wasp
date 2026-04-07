package it.unipd.dei.webapp.manual;

import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionTest {

    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
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