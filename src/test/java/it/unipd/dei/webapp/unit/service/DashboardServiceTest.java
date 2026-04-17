package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.model.DashboardSummary;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletSummary;
import it.unipd.dei.webapp.service.DashboardService;
import it.unipd.dei.webapp.service.TransactionService;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardServiceTest {

    private DashboardService dashboardService;
    private UserService userService;
    private WalletService walletService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService();
        userService = new UserService();
        walletService = new WalletService();
        transactionService = new TransactionService();
    }

    private User createTestUser() throws Exception {
        String email = "junit-dash-" + System.currentTimeMillis() + "@test.com";
        return userService.register("Dash", "Tester", email, "secret123");
    }

    private Wallet createTestWallet(java.util.UUID userId) throws Exception {
        return walletService.createWallet(userId, "Test Wallet", "CASH", "EUR");
    }

    // --- getSummary ---

    @Test
    void getSummaryReturnsNotNullForNewUser() throws Exception {
        User user = createTestUser();

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        assertNotNull(summary);
    }

    @Test
    void getSummaryReturnsZerosAndEmptyWalletsForNewUser() throws Exception {
        User user = createTestUser();

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        assertEquals(0, summary.getTotalEquity().compareTo(BigDecimal.ZERO));
        assertEquals(0, summary.getYtdExpenses().compareTo(BigDecimal.ZERO));
        assertEquals(0, summary.getYtdSavings().compareTo(BigDecimal.ZERO));
        assertTrue(summary.getWallets().isEmpty());
    }

    @Test
    void getSummaryReturnsZeroBalanceForWalletWithNoTransactions() throws Exception {
        User user = createTestUser();
        createTestWallet(user.getId());

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        assertEquals(1, summary.getWallets().size());
        assertEquals(0, summary.getWallets().get(0).getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, summary.getTotalEquity().compareTo(BigDecimal.ZERO));
    }

    @Test
    void getSummaryComputesCorrectTotals() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        transactionService.createTransaction(
                wallet.getId(), user.getId(), "1000.00", "Salary", "2026-03-01", null);
        transactionService.createTransaction(
                wallet.getId(), user.getId(), "-200.00", "Rent", "2026-03-05", null);

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        // totalEquity = 1000 - 200 = 800
        assertEquals(0, summary.getTotalEquity().compareTo(new BigDecimal("800.00")));
        // ytdSavings = same for current-year transactions
        assertEquals(0, summary.getYtdSavings().compareTo(new BigDecimal("800.00")));
    }

    @Test
    void getSummaryIncludesPerWalletBalance() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        transactionService.createTransaction(
                wallet.getId(), user.getId(), "500.00", "Income", "2026-01-10", null);
        transactionService.createTransaction(
                wallet.getId(), user.getId(), "-100.00", "Expense", "2026-01-15", null);

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        List<WalletSummary> wallets = summary.getWallets();
        assertEquals(1, wallets.size());
        assertEquals(0, wallets.get(0).getBalance().compareTo(new BigDecimal("400.00")));
    }

    @Test
    void getSummaryYtdExpensesOnlyCountsCurrentYear() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        String currentYear = LocalDate.now().getYear() + "-06-01";
        String priorYear   = (LocalDate.now().getYear() - 1) + "-06-01";

        transactionService.createTransaction(
                wallet.getId(), user.getId(), "-300.00", "Current Expense", currentYear, null);
        transactionService.createTransaction(
                wallet.getId(), user.getId(), "-700.00", "Old Expense", priorYear, null);

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        // Only current-year expense in YTD
        assertEquals(0, summary.getYtdExpenses().compareTo(new BigDecimal("300.00")));
        // Total equity still includes all-time transactions
        assertEquals(0, summary.getTotalEquity().compareTo(new BigDecimal("-1000.00")));
    }

    @Test
    void getSummaryAggregatesAcrossMultipleWallets() throws Exception {
        User user = createTestUser();
        Wallet w1 = walletService.createWallet(user.getId(), "Wallet 1", "CASH", "EUR");
        Wallet w2 = walletService.createWallet(user.getId(), "Wallet 2", "BANK_ACCOUNT", "EUR");

        transactionService.createTransaction(
                w1.getId(), user.getId(), "400.00", "Income 1", "2026-02-01", null);
        transactionService.createTransaction(
                w2.getId(), user.getId(), "600.00", "Income 2", "2026-02-01", null);
        transactionService.createTransaction(
                w2.getId(), user.getId(), "-100.00", "Expense 2", "2026-02-10", null);

        DashboardSummary summary = dashboardService.getSummary(user.getId());

        // Total equity across both wallets: 400 + 600 - 100 = 900
        assertEquals(0, summary.getTotalEquity().compareTo(new BigDecimal("900.00")));
        assertEquals(2, summary.getWallets().size());
    }
}
