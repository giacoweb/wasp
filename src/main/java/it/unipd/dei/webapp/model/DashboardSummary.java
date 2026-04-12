package it.unipd.dei.webapp.model;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummary {

    private BigDecimal totalEquity;
    private BigDecimal ytdExpenses;
    private BigDecimal totalSavings;
    private List<WalletSummary> wallets;

    public DashboardSummary() {
    }

    public DashboardSummary(BigDecimal totalEquity, BigDecimal ytdExpenses,
                            BigDecimal totalSavings, List<WalletSummary> wallets) {
        this.totalEquity = totalEquity;
        this.ytdExpenses = ytdExpenses;
        this.totalSavings = totalSavings;
        this.wallets = wallets;
    }

    public BigDecimal getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(BigDecimal totalEquity) {
        this.totalEquity = totalEquity;
    }

    public BigDecimal getYtdExpenses() {
        return ytdExpenses;
    }

    public void setYtdExpenses(BigDecimal ytdExpenses) {
        this.ytdExpenses = ytdExpenses;
    }

    public BigDecimal getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(BigDecimal totalSavings) {
        this.totalSavings = totalSavings;
    }

    public List<WalletSummary> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletSummary> wallets) {
        this.wallets = wallets;
    }
}
