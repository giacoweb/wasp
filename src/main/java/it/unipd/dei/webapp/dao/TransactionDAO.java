package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.Transaction;
import it.unipd.dei.webapp.model.TransactionType;
import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionDAO {

    private static final String INSERT_TRANSACTION_SQL =
            "INSERT INTO transactions (wallet_id, type, amount, category, transaction_date, note) " +
                    "VALUES (?, ?::transaction_type, ?, ?, ?, ?) " +
                    "RETURNING id, wallet_id, type, amount, category, transaction_date, note, created_at";

    private static final String FIND_BY_WALLET_ID_SQL =
            "SELECT id, wallet_id, type, amount, category, transaction_date, note, created_at " +
                    "FROM transactions WHERE wallet_id = ? ORDER BY transaction_date DESC, created_at DESC";

    private static final String FIND_BY_USER_ID_SQL =
            "SELECT t.id, t.wallet_id, t.type, t.amount, t.category, t.transaction_date, t.note, t.created_at " +
                    "FROM transactions t " +
                    "JOIN wallets w ON t.wallet_id = w.id " +
                    "WHERE w.user_id = ? " +
                    "ORDER BY t.transaction_date DESC, t.created_at DESC";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, wallet_id, type, amount, category, transaction_date, note, created_at " +
                    "FROM transactions WHERE id = ?";

    private static final String UPDATE_SQL =
            "UPDATE transactions SET type = ?::transaction_type, amount = ?, category = ?, " +
                    "transaction_date = ?, note = ? WHERE id = ? " +
                    "RETURNING id, wallet_id, type, amount, category, transaction_date, note, created_at";

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM transactions WHERE id = ?";

    public Transaction create(final UUID walletId,
                               final TransactionType type,
                               final java.math.BigDecimal amount,
                               final String category,
                               final LocalDate transactionDate,
                               final String note) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_TRANSACTION_SQL)) {

            statement.setObject(1, walletId);
            statement.setString(2, type.name());
            statement.setBigDecimal(3, amount);
            statement.setString(4, category);
            statement.setDate(5, Date.valueOf(transactionDate));
            statement.setString(6, note);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        }

        throw new SQLException("Failed to create transaction.");
    }

    public List<Transaction> findByWalletId(final UUID walletId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_WALLET_ID_SQL)) {

            statement.setObject(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    public List<Transaction> findByUserId(final UUID userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    public Transaction findById(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        }

        return null;
    }

    public Transaction update(final Transaction transaction) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, transaction.getType().name());
            statement.setBigDecimal(2, transaction.getAmount());
            statement.setString(3, transaction.getCategory());
            statement.setDate(4, Date.valueOf(transaction.getTransactionDate()));
            statement.setString(5, transaction.getNote());
            statement.setObject(6, transaction.getId());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        }

        throw new SQLException("Failed to update transaction.");
    }

    public boolean delete(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Filters a user's transactions by type, category, and/or date range.
     * All parameters are optional (null = ignored).
     */
    public List<Transaction> findByUserIdAndFilters(final UUID userId,
                                                     final TransactionType type,
                                                     final String category,
                                                     final LocalDate dateFrom,
                                                     final LocalDate dateTo) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT t.id, t.wallet_id, t.type, t.amount, t.category, t.transaction_date, t.note, t.created_at " +
                        "FROM transactions t " +
                        "JOIN wallets w ON t.wallet_id = w.id " +
                        "WHERE w.user_id = ? "
        );

        if (type != null)     sql.append("AND t.type = ?::transaction_type ");
        if (category != null) sql.append("AND LOWER(t.category) = LOWER(?) ");
        if (dateFrom != null) sql.append("AND t.transaction_date >= ? ");
        if (dateTo != null)   sql.append("AND t.transaction_date <= ? ");

        sql.append("ORDER BY t.transaction_date DESC, t.created_at DESC");

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            statement.setObject(paramIndex++, userId);
            if (type != null)     statement.setString(paramIndex++, type.name());
            if (category != null) statement.setString(paramIndex++, category);
            if (dateFrom != null) statement.setDate(paramIndex++, Date.valueOf(dateFrom));
            if (dateTo != null)   statement.setDate(paramIndex, Date.valueOf(dateTo));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    private Transaction mapRowToTransaction(final ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getObject("id", UUID.class));
        t.setWalletId(rs.getObject("wallet_id", UUID.class));
        t.setType(TransactionType.valueOf(rs.getString("type")));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setCategory(rs.getString("category"));
        t.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        t.setNote(rs.getString("note"));
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return t;
    }
}
