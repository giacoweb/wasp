package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class PacPlan {

    private UUID id;
    private UUID userId;
    private String assetName;
    private BigDecimal monthlyAmount;
    private BigDecimal targetAmount;
    private LocalDate startDate;
    private boolean active;
    private LocalDateTime createdAt;

    public PacPlan() {
    }

    public PacPlan(UUID id, UUID userId, String assetName, BigDecimal monthlyAmount, BigDecimal targetAmount,
                   LocalDate startDate, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.assetName = assetName;
        this.monthlyAmount = monthlyAmount;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.active = active;
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

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(BigDecimal monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}