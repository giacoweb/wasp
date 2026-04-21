package it.unipd.dei.webapp.unit.util;

import it.unipd.dei.webapp.util.DBConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBConnectionTest {

    @Test
    void getConnectionReturnsOpenConnectionToConfiguredDatabase() throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());

            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery("SELECT current_database(), current_user")) {

                assertTrue(rs.next());
                assertNotNull(rs.getString(1));
                assertNotNull(rs.getString(2));
            }
        }
    }
}
