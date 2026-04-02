package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class RecurringExpense {

    private UUID id;
    private UUID walletId;
    private String label;
    private BigDecimal amount;
    private Periodicity periodicity;
    private LocalDate nextDue;
    private boolean active;
    private LocalDateTime createdAt;

    public RecurringExpense() {
    }

    public RecurringExpense(UUID id, UUID walletId, String label, BigDecimal amount, Periodicity periodicity,
                            LocalDate nextDue, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.label = label;
        this.amount = amount;
        this.periodicity = periodicity;
        this.nextDue = nextDue;
        this.active = active;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Periodicity getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Periodicity periodicity) {
        this.periodicity = periodicity;
    }

    public LocalDate getNextDue() {
        return nextDue;
    }

    public void setNextDue(LocalDate nextDue) {
        this.nextDue = nextDue;
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