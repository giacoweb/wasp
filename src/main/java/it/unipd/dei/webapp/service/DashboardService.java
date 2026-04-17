package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.DashboardDAO;
import it.unipd.dei.webapp.model.DashboardStats;
import it.unipd.dei.webapp.model.DashboardSummary;
import it.unipd.dei.webapp.model.WalletSummary;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAO();
    }

    public DashboardService(final DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    public DashboardSummary getSummary(final UUID userId) throws SQLException {
        List<WalletSummary> wallets = dashboardDAO.getWalletSummariesByUserId(userId);
        DashboardStats stats = dashboardDAO.getUserDashboardStats(userId);

        return new DashboardSummary(stats.totalEquity(), stats.ytdExpenses(), stats.ytdSavings(), wallets);
    }
}
