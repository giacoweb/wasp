package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction extends AbstractResource {

    private UUID walletId;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private LocalDate transactionDate;
    private String note;

    public Transaction() {
    }

    public Transaction(UUID id, UUID walletId, TransactionType type, BigDecimal amount, String category,
                       LocalDate transactionDate, String note, LocalDateTime createdAt) {
        super(id, createdAt);
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.transactionDate = transactionDate;
        this.note = note;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
