package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletSummary {

    private UUID id;
    private String name;
    private WalletType type;
    private String currency;
    private BigDecimal balance;

    public WalletSummary() {
    }

    public WalletSummary(UUID id, String name, WalletType type, String currency, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
