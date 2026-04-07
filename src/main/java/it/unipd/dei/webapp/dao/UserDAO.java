package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAO {

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (name, surname, email, password_hash, role) " +
                    "VALUES (?, ?, ?, ?, ?::user_role) " +
                    "RETURNING id, name, surname, email, password_hash, role, created_at";

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT id, name, surname, email, password_hash, role, created_at " +
                    "FROM users WHERE email = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, name, surname, email, password_hash, role, created_at " +
                    "FROM users WHERE id = ?";

    public User createUser(final User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER_SQL)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getRole().name());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }

        throw new SQLException("Failed to create user.");
    }

    public User findByEmail(final String email) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {

            statement.setString(1, email);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }

        return null;
    }

    public User findById(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }

        return null;
    }

    private User mapRowToUser(final ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getObject("id", UUID.class));
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}