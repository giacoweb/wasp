package it.unipd.dei.webapp.model;

import java.util.List;

public class DashboardSummary {

    private List<DashboardStats> statsByCurrency;
    private List<WalletSummary> wallets;

    public DashboardSummary() {
    }

    public DashboardSummary(final List<DashboardStats> statsByCurrency, final List<WalletSummary> wallets) {
        this.statsByCurrency = statsByCurrency;
        this.wallets = wallets;
    }

    public List<DashboardStats> getStatsByCurrency() {
        return statsByCurrency;
    }

    public void setStatsByCurrency(final List<DashboardStats> statsByCurrency) {
        this.statsByCurrency = statsByCurrency;
    }

    public List<WalletSummary> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletSummary> wallets) {
        this.wallets = wallets;
    }
}
