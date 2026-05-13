package it.unipd.dei.webapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Wallet extends AbstractResource implements Resource {

    private UUID userId;
    private String name;
    private WalletType type;
    private String currency;

    public Wallet() {
    }

    public Wallet(UUID id, UUID userId, String name, WalletType type, String currency, LocalDateTime createdAt) {
        super(id, createdAt);
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.currency = currency;
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

}
