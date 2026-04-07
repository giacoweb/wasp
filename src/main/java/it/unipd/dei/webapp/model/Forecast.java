package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Forecast {

    private UUID id;
    private UUID userId;
    private ForecastScenario scenario;
    private String projectionJson;
    private LocalDateTime createdAt;

    public Forecast() {
    }

    public Forecast(UUID id, UUID userId, ForecastScenario scenario, String projectionJson, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.scenario = scenario;
        this.projectionJson = projectionJson;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ForecastScenario getScenario() {
        return scenario;
    }

    public void setScenario(ForecastScenario scenario) {
        this.scenario = scenario;
    }

    public String getProjectionJson() {
        return projectionJson;
    }

    public void setProjectionJson(String projectionJson) {
        this.projectionJson = projectionJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}