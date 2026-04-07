package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.ReportSession;

import java.time.LocalDateTime;
import java.util.UUID;

public class GuestSessionService {

    private static final int DEFAULT_EXPIRY_HOURS = 24;

    private final ReportSessionDAO reportSessionDAO;

    public GuestSessionService() {
        this.reportSessionDAO = new ReportSessionDAO();
    }

    public GuestSessionService(final ReportSessionDAO reportSessionDAO) {
        this.reportSessionDAO = reportSessionDAO;
    }

    public UUID ensureGuestSessionExists(final UUID existingToken) throws Exception {
        if (existingToken != null) {
            ReportSession existingSession = reportSessionDAO.findBySessionToken(existingToken);
            if (existingSession != null && existingSession.getExpiresAt().isAfter(LocalDateTime.now())) {
                return existingToken;
            }
        }

        UUID newToken = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(DEFAULT_EXPIRY_HOURS);

        reportSessionDAO.createReportSession(newToken, "{}", expiresAt);

        return newToken;
    }
}