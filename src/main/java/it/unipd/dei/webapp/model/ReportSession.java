package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReportSession {

    private UUID id;
    private UUID sessionToken;
    private String reportJson;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public ReportSession() {
    }

    public ReportSession(UUID id, UUID sessionToken, String reportJson, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.sessionToken = sessionToken;
        this.reportJson = reportJson;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}