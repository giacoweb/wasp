package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.Periodicity;
import it.unipd.dei.webapp.model.RecurringExpense;
import it.unipd.dei.webapp.util.DBConnection;

<<<<<<< HEAD
import java.sql.*;
=======
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
>>>>>>> master
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecurringExpenseDAO {

    private static final String INSERT_SQL =
            "INSERT INTO recurring_expenses (wallet_id, label, amount, periodicity, next_due) " +
                    "VALUES (?, ?, ?, ?::periodicity_type, ?) " +
                    "RETURNING id, wallet_id, label, amount, periodicity, next_due, active, created_at";

    private static final String FIND_BY_WALLET_ID_SQL =
            "SELECT id, wallet_id, label, amount, periodicity, next_due, active, created_at " +
                    "FROM recurring_expenses WHERE wallet_id = ? ORDER BY next_due ASC";

    private static final String FIND_BY_USER_ID_SQL =
            "SELECT re.id, re.wallet_id, re.label, re.amount, re.periodicity, re.next_due, re.active, re.created_at " +
                    "FROM recurring_expenses re " +
                    "JOIN wallets w ON re.wallet_id = w.id " +
                    "WHERE w.user_id = ? ORDER BY re.next_due ASC";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, wallet_id, label, amount, periodicity, next_due, active, created_at " +
                    "FROM recurring_expenses WHERE id = ?";

<<<<<<< HEAD
    private static final String UPDATE_SQL =
            "UPDATE recurring_expenses SET label = ?, amount = ?, periodicity = ?::periodicity_type, next_due = ? " +
                    "WHERE id = ? " +
                    "RETURNING id, wallet_id, label, amount, periodicity, next_due, active, created_at";

    private static final String SET_ACTIVE_SQL =
            "UPDATE recurring_expenses SET active = ? WHERE id = ?";

    private static final String TOGGLE_ACTIVE_SQL =
            "UPDATE recurring_expenses SET active = NOT active WHERE id = ?";

=======
    private static final String SET_ACTIVE_SQL =
            "UPDATE recurring_expenses SET active = ? WHERE id = ?";

>>>>>>> master
    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM recurring_expenses WHERE id = ?";

    public RecurringExpense create(final UUID walletId,
                                    final String label,
<<<<<<< HEAD
                                    final java.math.BigDecimal amount,
=======
                                    final BigDecimal amount,
>>>>>>> master
                                    final Periodicity periodicity,
                                    final LocalDate nextDue) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

            statement.setObject(1, walletId);
            statement.setString(2, label);
            statement.setBigDecimal(3, amount);
            statement.setString(4, periodicity.name());
            statement.setDate(5, Date.valueOf(nextDue));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRecurringExpense(rs);
                }
            }
        }

        throw new SQLException("Failed to create recurring expense.");
    }

    public List<RecurringExpense> findByWalletId(final UUID walletId) throws SQLException {
        List<RecurringExpense> list = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_WALLET_ID_SQL)) {

            statement.setObject(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToRecurringExpense(rs));
                }
            }
        }

        return list;
    }

    public List<RecurringExpense> findByUserId(final UUID userId) throws SQLException {
        List<RecurringExpense> list = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToRecurringExpense(rs));
                }
            }
        }

        return list;
    }

    public RecurringExpense findById(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRecurringExpense(rs);
                }
            }
        }

        return null;
    }

<<<<<<< HEAD
    public RecurringExpense update(final UUID id,
                                    final String label,
                                    final java.math.BigDecimal amount,
                                    final Periodicity periodicity,
                                    final LocalDate nextDue) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, label);
            statement.setBigDecimal(2, amount);
            statement.setString(3, periodicity.name());
            statement.setDate(4, Date.valueOf(nextDue));
            statement.setObject(5, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRecurringExpense(rs);
                }
            }
        }

        throw new SQLException("Failed to update recurring expense.");
    }

    public boolean toggleActive(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(TOGGLE_ACTIVE_SQL)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

=======
>>>>>>> master
    public boolean setActive(final UUID id, final boolean active) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SET_ACTIVE_SQL)) {

            statement.setBoolean(1, active);
            statement.setObject(2, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private RecurringExpense mapRowToRecurringExpense(final ResultSet rs) throws SQLException {
        RecurringExpense re = new RecurringExpense();
        re.setId(rs.getObject("id", UUID.class));
        re.setWalletId(rs.getObject("wallet_id", UUID.class));
        re.setLabel(rs.getString("label"));
        re.setAmount(rs.getBigDecimal("amount"));
        re.setPeriodicity(Periodicity.valueOf(rs.getString("periodicity")));
        re.setNextDue(rs.getDate("next_due").toLocalDate());
        re.setActive(rs.getBoolean("active"));
        re.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return re;
    }
}
