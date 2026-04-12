package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.TransactionDAO;
import it.unipd.dei.webapp.model.DashboardSummary;
import it.unipd.dei.webapp.model.WalletSummary;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DashboardService {

    private final TransactionDAO transactionDAO;

    public DashboardService() {
        this.transactionDAO = new TransactionDAO();
    }

    public DashboardService(final TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public DashboardSummary getSummary(final UUID userId) throws SQLException {
        List<WalletSummary> wallets = transactionDAO.getWalletSummariesByUserId(userId);
        BigDecimal[] stats = transactionDAO.getUserDashboardStats(userId);

        // stats[0] = totalEquity, stats[1] = ytdExpenses, stats[2] = totalSavings
        return new DashboardSummary(stats[0], stats[1], stats[2], wallets);
    }
}
