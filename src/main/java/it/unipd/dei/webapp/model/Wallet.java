package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Wallet {

    private UUID id;
    private UUID userId;
    private String name;
    private WalletType type;
    private String currency;
    private LocalDateTime createdAt;

    public Wallet() {
    }

    public Wallet(UUID id, UUID userId, String name, WalletType type, String currency, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.currency = currency;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}