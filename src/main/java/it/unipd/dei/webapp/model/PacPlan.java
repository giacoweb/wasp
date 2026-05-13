package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class PacPlan extends AbstractResource {

    private UUID userId;
    private String assetName;
    private BigDecimal monthlyAmount;
    private BigDecimal targetAmount;
    private LocalDate startDate;
    private boolean active;

    public PacPlan() {
    }

    public PacPlan(UUID id, UUID userId, String assetName, BigDecimal monthlyAmount, BigDecimal targetAmount,
                   LocalDate startDate, boolean active, LocalDateTime createdAt) {
        super(id, createdAt);
        this.userId = userId;
        this.assetName = assetName;
        this.monthlyAmount = monthlyAmount;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.active = active;
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

}
