package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WalletDAO {

    private static final String INSERT_WALLET_SQL =
            "INSERT INTO wallets (user_id, name, type, currency) " +
                    "VALUES (?, ?, ?::wallet_type, ?) " +
                    "RETURNING id, user_id, name, type, currency, created_at";

    private static final String FIND_BY_USER_ID_SQL =
            "SELECT id, user_id, name, type, currency, created_at " +
                    "FROM wallets WHERE user_id = ? ORDER BY created_at ASC";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, user_id, name, type, currency, created_at " +
                    "FROM wallets WHERE id = ?";

    private static final String UPDATE_SQL =
            "UPDATE wallets SET name = ?, type = ?::wallet_type, currency = ? " +
                    "WHERE id = ? " +
                    "RETURNING id, user_id, name, type, currency, created_at";

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM wallets WHERE id = ?";

    private static final String COUNT_TRANSACTIONS_SQL =
            "SELECT COUNT(*) FROM transactions WHERE wallet_id = ?";

    public Wallet create(final UUID userId,
                         final String name,
                         final WalletType type,
                         final String currency) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_WALLET_SQL)) {

            statement.setObject(1, userId);
            statement.setString(2, name);
            statement.setString(3, type.name());
            statement.setString(4, currency);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWallet(rs);
                }
            }
        }

        throw new SQLException("Failed to create wallet.");
    }

    public List<Wallet> findByUserId(final UUID userId) throws SQLException {
        List<Wallet> wallets = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    wallets.add(mapRowToWallet(rs));
                }
            }
        }

        return wallets;
    }

    public Wallet findById(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWallet(rs);
                }
            }
        }

        return null;
    }

    public Wallet update(final Wallet wallet) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, wallet.getName());
            statement.setString(2, wallet.getType().name());
            statement.setString(3, wallet.getCurrency());
            statement.setObject(4, wallet.getId());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToWallet(rs);
                }
            }
        }

        throw new SQLException("Failed to update wallet.");
    }

    public boolean delete(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public int countTransactions(final UUID walletId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_TRANSACTIONS_SQL)) {

            statement.setObject(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private Wallet mapRowToWallet(final ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setId(rs.getObject("id", UUID.class));
        wallet.setUserId(rs.getObject("user_id", UUID.class));
        wallet.setName(rs.getString("name"));
        wallet.setType(WalletType.valueOf(rs.getString("type")));
        wallet.setCurrency(rs.getString("currency"));
        wallet.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return wallet;
    }
}
