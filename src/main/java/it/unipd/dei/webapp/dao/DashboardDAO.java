package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.DashboardStats;
import it.unipd.dei.webapp.model.WalletSummary;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DashboardDAO {

    private static final String WALLET_SUMMARIES_BY_USER_SQL =
            "SELECT w.id AS wallet_id, w.name AS wallet_name, w.type AS wallet_type, " +
            "w.currency AS currency, " +
            "COALESCE(" +
            "    SUM(CASE WHEN t.type = 'INCOME'  THEN t.amount ELSE 0 END) - " +
            "    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS balance " +
            "FROM wallets w " +
            "LEFT JOIN transactions t ON t.wallet_id = w.id " +
            "WHERE w.user_id = ? " +
            "GROUP BY w.id, w.name, w.type, w.currency, w.created_at " +
            "ORDER BY w.created_at ASC";

    private static final String DASHBOARD_STATS_BY_USER_SQL =
            "SELECT " +
            "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0) AS total_equity, " +
            "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' " +
            "    AND EXTRACT(YEAR FROM t.transaction_date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "    THEN t.amount ELSE 0 END), 0) AS ytd_expenses, " +
            "COALESCE(" +
            "    SUM(CASE WHEN t.type = 'INCOME' " +
            "        AND EXTRACT(YEAR FROM t.transaction_date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "        THEN t.amount ELSE 0 END) - " +
            "    SUM(CASE WHEN t.type = 'EXPENSE' " +
            "        AND EXTRACT(YEAR FROM t.transaction_date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "        THEN t.amount ELSE 0 END), 0) AS ytd_savings " +
            "FROM wallets w LEFT JOIN transactions t ON t.wallet_id = w.id " +
            "WHERE w.user_id = ?";

    public List<WalletSummary> getWalletSummariesByUserId(final UUID userId) throws SQLException {
        List<WalletSummary> summaries = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(WALLET_SUMMARIES_BY_USER_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    summaries.add(mapRowToWalletSummary(rs));
                }
            }
        }

        return summaries;
    }

    public DashboardStats getUserDashboardStats(final UUID userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DASHBOARD_STATS_BY_USER_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Failed to retrieve dashboard stats.");
                }
                return new DashboardStats(
                        rs.getBigDecimal("total_equity"),
                        rs.getBigDecimal("ytd_expenses"),
                        rs.getBigDecimal("ytd_savings")
                );
            }
        }
    }

    private WalletSummary mapRowToWalletSummary(final ResultSet rs) throws SQLException {
        WalletSummary ws = new WalletSummary();
        ws.setId(rs.getObject("wallet_id", UUID.class));
        ws.setName(rs.getString("wallet_name"));
        ws.setType(WalletType.valueOf(rs.getString("wallet_type")));
        ws.setCurrency(rs.getString("currency"));
        ws.setBalance(rs.getBigDecimal("balance"));
        return ws;
    }
}
