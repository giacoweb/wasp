package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.Forecast;
import it.unipd.dei.webapp.model.ForecastScenario;
import it.unipd.dei.webapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ForecastDAO {

    private static final String INSERT_SQL =
            "INSERT INTO forecasts (user_id, scenario, projection_json) " +
                    "VALUES (?, ?::forecast_scenario, ?::jsonb) " +
                    "RETURNING id, user_id, scenario, projection_json, created_at";

    private static final String FIND_LATEST_BY_USER_AND_SCENARIO_SQL =
            "SELECT id, user_id, scenario, projection_json, created_at " +
                    "FROM forecasts WHERE user_id = ? AND scenario = ?::forecast_scenario " +
                    "ORDER BY created_at DESC LIMIT 1";

    public Forecast save(final Forecast forecast) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

            statement.setObject(1, forecast.getUserId());
            statement.setString(2, forecast.getScenario().name());
            statement.setString(3, forecast.getProjectionJson());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToForecast(rs);
                }
            }
        }

        throw new SQLException("Failed to save forecast.");
    }

    public Forecast findLatestByUserAndScenario(final UUID userId,
                                                final ForecastScenario scenario) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_LATEST_BY_USER_AND_SCENARIO_SQL)) {

            statement.setObject(1, userId);
            statement.setString(2, scenario.name());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToForecast(rs);
                }
            }
        }

        return null;
    }

    private Forecast mapRowToForecast(final ResultSet rs) throws SQLException {
        Forecast forecast = new Forecast();
        forecast.setId(rs.getObject("id", UUID.class));
        forecast.setUserId(rs.getObject("user_id", UUID.class));
        forecast.setScenario(ForecastScenario.valueOf(rs.getString("scenario")));
        forecast.setProjectionJson(rs.getString("projection_json"));
        forecast.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return forecast;
    }
}
