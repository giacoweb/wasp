package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReportSession extends AbstractResource {

    private UUID sessionToken;
    private String reportJson;
    private LocalDateTime expiresAt;

    public ReportSession() {
    }

    public ReportSession(UUID id, UUID sessionToken, String reportJson, LocalDateTime expiresAt, LocalDateTime createdAt) {
        super(id, createdAt);
        this.sessionToken = sessionToken;
        this.reportJson = reportJson;
        this.expiresAt = expiresAt;
    }

    public UUID getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(UUID sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getReportJson() {
        return reportJson;
    }

    public void setReportJson(String reportJson) {
        this.reportJson = reportJson;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

}
