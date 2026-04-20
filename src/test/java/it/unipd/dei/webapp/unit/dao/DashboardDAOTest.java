package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.DashboardDAO;
import it.unipd.dei.webapp.dao.TransactionDAO;
import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.DashboardStats;
import it.unipd.dei.webapp.model.TransactionType;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletSummary;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardDAOTest {

    private DashboardDAO dashboardDAO;
    private TransactionDAO transactionDAO;
    private WalletDAO walletDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        dashboardDAO = new DashboardDAO();
        transactionDAO = new TransactionDAO();
        walletDAO = new WalletDAO();
        userDAO = new UserDAO();
    }

    private User createTestUser() throws Exception {
        User user = new User();
        user.setName("Dashboard");
        user.setSurname("DAOTest");
        user.setEmail("junit-ddao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        return userDAO.createUser(user);
    }

    @Test
    void getWalletSummariesReturnsEmptyForUserWithNoWallets() throws Exception {
        User user = createTestUser();

        List<WalletSummary> summaries = dashboardDAO.getWalletSummariesByUserId(user.getId());

        assertTrue(summaries.isEmpty());
    }

    @Test
    void getWalletSummariesReturnsZeroBalanceForWalletWithNoTransactions() throws Exception {
        User user = createTestUser();
        walletDAO.create(user.getId(), "Empty Wallet", WalletType.CASH, "EUR");

        List<WalletSummary> summaries = dashboardDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(1, summaries.size());
        assertEquals(0, summaries.get(0).getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void getWalletSummariesComputesBalanceCorrectly() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.BANK_ACCOUNT, "EUR");

        transactionDAO.create(wallet.getId(), TransactionType.INCOME,
                new BigDecimal("500.00"), "Salary", LocalDate.now(), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("150.00"), "Rent", LocalDate.now(), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("50.00"), "Groceries", LocalDate.now(), null);

        List<WalletSummary> summaries = dashboardDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(1, summaries.size());
        assertEquals(0, summaries.get(0).getBalance().compareTo(new BigDecimal("300.00")));
    }

    @Test
    void getWalletSummariesReturnsCorrectBalancePerWallet() throws Exception {
        User user = createTestUser();
        Wallet walletA = walletDAO.create(user.getId(), "Wallet A", WalletType.CASH, "EUR");
        Wallet walletB = walletDAO.create(user.getId(), "Wallet B", WalletType.BANK_ACCOUNT, "EUR");

        transactionDAO.create(walletA.getId(), TransactionType.INCOME,
                new BigDecimal("200.00"), "Income A", LocalDate.now(), null);
        transactionDAO.create(walletB.getId(), TransactionType.INCOME,
                new BigDecimal("100.00"), "Income B", LocalDate.now(), null);
        transactionDAO.create(walletB.getId(), TransactionType.EXPENSE,
                new BigDecimal("40.00"), "Expense B", LocalDate.now(), null);

        List<WalletSummary> summaries = dashboardDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(2, summaries.size());
        WalletSummary summaryA = summaries.stream()
                .filter(summary -> summary.getName().equals("Wallet A"))
                .findFirst()
                .orElseThrow();
        WalletSummary summaryB = summaries.stream()
                .filter(summary -> summary.getName().equals("Wallet B"))
                .findFirst()
                .orElseThrow();

        assertEquals(0, summaryA.getBalance().compareTo(new BigDecimal("200.00")));
        assertEquals(0, summaryB.getBalance().compareTo(new BigDecimal("60.00")));
    }

    @Test
    void getWalletSummariesPopulatesWalletFields() throws Exception {
        User user = createTestUser();
        walletDAO.create(user.getId(), "My Cash", WalletType.CASH, "USD");

        List<WalletSummary> summaries = dashboardDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(1, summaries.size());
        WalletSummary summary = summaries.get(0);
        assertNotNull(summary.getId());
        assertEquals("My Cash", summary.getName());
        assertEquals(WalletType.CASH, summary.getType());
        assertEquals("USD", summary.getCurrency());
    }

    @Test
    void getUserDashboardStatsByCurrencyReturnsEmptyForUserWithNoWallets() throws Exception {
        User user = createTestUser();

        List<DashboardStats> stats = dashboardDAO.getUserDashboardStatsByCurrency(user.getId());

        assertTrue(stats.isEmpty());
    }

    @Test
    void getUserDashboardStatsByCurrencyReturnsZerosForWalletWithNoTransactions() throws Exception {
        User user = createTestUser();
        walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        List<DashboardStats> statsByCurrency = dashboardDAO.getUserDashboardStatsByCurrency(user.getId());

        assertEquals(1, statsByCurrency.size());
        DashboardStats stats = statsByCurrency.get(0);
        assertEquals("EUR", stats.currency());
        assertEquals(0, stats.totalEquity().compareTo(BigDecimal.ZERO));
        assertEquals(0, stats.ytdExpenses().compareTo(BigDecimal.ZERO));
        assertEquals(0, stats.ytdSavings().compareTo(BigDecimal.ZERO));
    }

    @Test
    void getUserDashboardStatsByCurrencyComputesTotalEquity() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        transactionDAO.create(wallet.getId(), TransactionType.INCOME,
                new BigDecimal("1000.00"), "Salary", LocalDate.now(), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("300.00"), "Bills", LocalDate.now(), null);

        List<DashboardStats> statsByCurrency = dashboardDAO.getUserDashboardStatsByCurrency(user.getId());

        assertEquals(1, statsByCurrency.size());
        DashboardStats stats = statsByCurrency.get(0);
        assertEquals("EUR", stats.currency());
        assertEquals(0, stats.totalEquity().compareTo(new BigDecimal("700.00")));
    }

    @Test
    void getUserDashboardStatsByCurrencyYtdExcludesPriorYearExpenses() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        LocalDate currentYear = LocalDate.of(LocalDate.now().getYear(), 6, 1);
        LocalDate priorYear = LocalDate.of(LocalDate.now().getYear() - 1, 6, 1);

        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("200.00"), "Current Expense", currentYear, null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("500.00"), "Old Expense", priorYear, null);

        List<DashboardStats> statsByCurrency = dashboardDAO.getUserDashboardStatsByCurrency(user.getId());

        assertEquals(1, statsByCurrency.size());
        DashboardStats stats = statsByCurrency.get(0);
        assertEquals(0, stats.ytdExpenses().compareTo(new BigDecimal("200.00")));
        assertEquals(0, stats.ytdSavings().compareTo(new BigDecimal("-200.00")));
    }

    @Test
    void getUserDashboardStatsByCurrencyYtdSavingsEqualsCurrentYearIncomeMinusExpense() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        transactionDAO.create(wallet.getId(), TransactionType.INCOME,
                new BigDecimal("800.00"), "Salary", LocalDate.now(), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("250.00"), "Rent", LocalDate.now(), null);

        List<DashboardStats> statsByCurrency = dashboardDAO.getUserDashboardStatsByCurrency(user.getId());

        assertEquals(1, statsByCurrency.size());
        DashboardStats stats = statsByCurrency.get(0);
        assertEquals(0, stats.ytdSavings().compareTo(new BigDecimal("550.00")));
    }

    @Test
    void getUserDashboardStatsByCurrencySeparatesDifferentCurrencies() throws Exception {
        User user = createTestUser();
        Wallet eurWallet = walletDAO.create(user.getId(), "EUR Wallet", WalletType.CASH, "EUR");
        Wallet usdWallet = walletDAO.create(user.getId(), "USD Wallet", WalletType.BANK_ACCOUNT, "USD");

        transactionDAO.create(eurWallet.getId(), TransactionType.INCOME,
                new BigDecimal("500.00"), "EUR Income", LocalDate.now(), null);
        transactionDAO.create(usdWallet.getId(), TransactionType.INCOME,
                new BigDecimal("200.00"), "USD Income", LocalDate.now(), null);
        transactionDAO.create(usdWallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("50.00"), "USD Expense", LocalDate.now(), null);

        List<DashboardStats> statsByCurrency = dashboardDAO.getUserDashboardStatsByCurrency(user.getId());

        assertEquals(2, statsByCurrency.size());
        DashboardStats eurStats = statsByCurrency.stream()
                .filter(stats -> "EUR".equals(stats.currency()))
                .findFirst()
                .orElseThrow();
        DashboardStats usdStats = statsByCurrency.stream()
                .filter(stats -> "USD".equals(stats.currency()))
                .findFirst()
                .orElseThrow();

        assertEquals(0, eurStats.totalEquity().compareTo(new BigDecimal("500.00")));
        assertEquals(0, usdStats.totalEquity().compareTo(new BigDecimal("150.00")));
    }
}
