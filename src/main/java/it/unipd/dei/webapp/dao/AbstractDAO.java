package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Shared base class for all DAO implementations.
 */
public abstract class AbstractDAO implements DAO {

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
