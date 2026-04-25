package it.unipd.dei.webapp.dao;

import it.unipd.dei.webapp.model.ReportSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReportSessionDAO extends AbstractDAO implements DAO {

    private static final String INSERT_REPORT_SESSION_SQL =
            "INSERT INTO report_sessions (session_token, report_json, expires_at) " +
                    "VALUES (?, ?::jsonb, ?) " +
                    "RETURNING id, session_token, report_json, expires_at, created_at";

    private static final String FIND_BY_SESSION_TOKEN_SQL =
            "SELECT id, session_token, report_json, expires_at, created_at " +
                    "FROM report_sessions WHERE session_token = ?";

    private static final String UPDATE_REPORT_JSON_SQL =
            "UPDATE report_sessions " +
                    "SET report_json = ?::jsonb " +
                    "WHERE session_token = ?";

    public ReportSession createReportSession(final UUID sessionToken,
                                             final String reportJson,
                                             final LocalDateTime expiresAt) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_REPORT_SESSION_SQL)) {

            statement.setObject(1, sessionToken);
            statement.setString(2, reportJson);
            statement.setTimestamp(3, Timestamp.valueOf(expiresAt));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReportSession(rs);
                }
            }
        }

        throw new SQLException("Failed to create report session.");
    }

    public ReportSession findBySessionToken(final UUID sessionToken) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_SESSION_TOKEN_SQL)) {

            statement.setObject(1, sessionToken);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReportSession(rs);
                }
            }
        }

        return null;
    }

    public void updateReportSessionJson(UUID token, String json) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_REPORT_JSON_SQL)) {

            statement.setObject(1,json);
            statement.setObject(2,token);
            statement.executeUpdate();

        }
    }

    private ReportSession mapRowToReportSession(final ResultSet rs) throws SQLException {
        ReportSession reportSession = new ReportSession();
        reportSession.setId(rs.getObject("id", UUID.class));
        reportSession.setSessionToken(rs.getObject("session_token", UUID.class));
        reportSession.setReportJson(rs.getString("report_json"));
        reportSession.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        reportSession.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return reportSession;
    }
}
