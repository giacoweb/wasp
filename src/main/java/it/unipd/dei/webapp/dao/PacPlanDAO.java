package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.PacPlan;
import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacPlanDAO {

    private static final String FIND_BY_USER_ID_SQL =
            "SELECT id, user_id, asset_name, monthly_amount, target_amount, start_date, active, created_at " +
                    "FROM pac_plans WHERE user_id = ? ORDER BY created_at ASC";

    private static final String FIND_BY_ID_SQL =
            "SELECT id, user_id, asset_name, monthly_amount, target_amount, start_date, active, created_at " +
                    "FROM pac_plans WHERE id = ?";

    private static final String FIND_ACTIVE_BY_USER_ID_SQL =
            "SELECT id, user_id, asset_name, monthly_amount, target_amount, start_date, active, created_at " +
                    "FROM pac_plans WHERE user_id = ? AND active = TRUE ORDER BY created_at ASC";

    private static final String INSERT_SQL =
            "INSERT INTO pac_plans (user_id, asset_name, monthly_amount, target_amount, start_date) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "RETURNING id, user_id, asset_name, monthly_amount, target_amount, start_date, active, created_at";

    private static final String UPDATE_SQL =
            "UPDATE pac_plans SET asset_name = ?, monthly_amount = ?, target_amount = ?, start_date = ?, active = ? " +
                    "WHERE id = ? " +
                    "RETURNING id, user_id, asset_name, monthly_amount, target_amount, start_date, active, created_at";

    private static final String DELETE_SQL =
            "DELETE FROM pac_plans WHERE id = ?";

    public List<PacPlan> findByUserId(final UUID userId) throws SQLException {
        List<PacPlan> plans = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapRowToPacPlan(rs));
                }
            }
        }

        return plans;
    }

    public PacPlan findById(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setObject(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPacPlan(rs);
                }
            }
        }

        return null;
    }

    public List<PacPlan> findActiveByUserId(final UUID userId) throws SQLException {
        List<PacPlan> plans = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_BY_USER_ID_SQL)) {

            statement.setObject(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapRowToPacPlan(rs));
                }
            }
        }

        return plans;
    }

    public PacPlan create(final PacPlan plan) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

            statement.setObject(1, plan.getUserId());
            statement.setString(2, plan.getAssetName());
            statement.setBigDecimal(3, plan.getMonthlyAmount());
            statement.setBigDecimal(4, plan.getTargetAmount());
            statement.setDate(5, Date.valueOf(plan.getStartDate()));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPacPlan(rs);
                }
            }
        }

        throw new SQLException("Failed to create PAC plan.");
    }

    public PacPlan update(final PacPlan plan) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(1, plan.getAssetName());
            statement.setBigDecimal(2, plan.getMonthlyAmount());
            statement.setBigDecimal(3, plan.getTargetAmount());
            statement.setDate(4, Date.valueOf(plan.getStartDate()));
            statement.setBoolean(5, plan.isActive());
            statement.setObject(6, plan.getId());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPacPlan(rs);
                }
            }
        }

        throw new SQLException("Failed to update PAC plan.");
    }

    public boolean delete(final UUID id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private PacPlan mapRowToPacPlan(final ResultSet rs) throws SQLException {
        PacPlan plan = new PacPlan();
        plan.setId(rs.getObject("id", UUID.class));
        plan.setUserId(rs.getObject("user_id", UUID.class));
        plan.setAssetName(rs.getString("asset_name"));
        plan.setMonthlyAmount(rs.getBigDecimal("monthly_amount"));
        plan.setTargetAmount(rs.getBigDecimal("target_amount"));
        plan.setStartDate(rs.getDate("start_date").toLocalDate());
        plan.setActive(rs.getBoolean("active"));
        plan.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return plan;
    }
}
