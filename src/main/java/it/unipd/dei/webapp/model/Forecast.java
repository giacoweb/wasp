package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Forecast extends AbstractResource {

    private UUID userId;
    private ForecastScenario scenario;
    private String projectionJson;

    public Forecast() {
    }

    public Forecast(UUID id, UUID userId, ForecastScenario scenario, String projectionJson, LocalDateTime createdAt) {
        super(id, createdAt);
        this.userId = userId;
        this.scenario = scenario;
        this.projectionJson = projectionJson;
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

}
