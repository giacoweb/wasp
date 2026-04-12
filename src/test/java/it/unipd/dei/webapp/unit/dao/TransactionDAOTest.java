package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.TransactionDAO;
import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Transaction;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionDAOTest {

    private TransactionDAO transactionDAO;
    private WalletDAO walletDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        transactionDAO = new TransactionDAO();
        walletDAO = new WalletDAO();
        userDAO = new UserDAO();
    }

    private User createTestUser() throws Exception {
        User user = new User();
        user.setName("Txn");
        user.setSurname("DAOTest");
        user.setEmail("junit-tdao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        return userDAO.createUser(user);
    }

    private Wallet createTestWallet() throws Exception {
        User created = createTestUser();
        return walletDAO.create(created.getId(), "Test Wallet", WalletType.CASH, "EUR");
    }

    @Test
    void createReturnsTransactionWithGeneratedId() throws Exception {
        Wallet wallet = createTestWallet();

        Transaction txn = transactionDAO.create(
                wallet.getId(), TransactionType.INCOME, new BigDecimal("100.50"),
                "Salary", LocalDate.of(2026, 1, 15), "Monthly pay");

        assertNotNull(txn.getId());
        assertEquals(wallet.getId(), txn.getWalletId());
        assertEquals(TransactionType.INCOME, txn.getType());
        assertEquals(0, txn.getAmount().compareTo(new BigDecimal("100.50")));
        assertEquals("Salary", txn.getCategory());
        assertEquals(LocalDate.of(2026, 1, 15), txn.getTransactionDate());
        assertEquals("Monthly pay", txn.getNote());
        assertNotNull(txn.getCreatedAt());
    }

    @Test
    void createWithNullNoteSucceeds() throws Exception {
        Wallet wallet = createTestWallet();

        Transaction txn = transactionDAO.create(
                wallet.getId(), TransactionType.EXPENSE, new BigDecimal("25.00"),
                "Lunch", LocalDate.of(2026, 1, 16), null);

        assertNotNull(txn.getId());
        assertNull(txn.getNote());
    }

    @Test
    void findByWalletIdReturnsAllTransactions() throws Exception {
        Wallet wallet = createTestWallet();

        transactionDAO.create(wallet.getId(), TransactionType.INCOME,
                new BigDecimal("100"), "Salary", LocalDate.of(2026, 1, 15), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("30"), "Lunch", LocalDate.of(2026, 1, 16), null);

        List<Transaction> transactions = transactionDAO.findByWalletId(wallet.getId());

        assertEquals(2, transactions.size());
    }

    @Test
    void findByWalletIdReturnsEmptyForUnknownWallet() throws Exception {
        List<Transaction> transactions = transactionDAO.findByWalletId(UUID.randomUUID());

        assertTrue(transactions.isEmpty());
    }

    @Test
    void findByIdReturnsCorrectTransaction() throws Exception {
        Wallet wallet = createTestWallet();

        Transaction created = transactionDAO.create(
                wallet.getId(), TransactionType.INCOME, new BigDecimal("200"),
                "Bonus", LocalDate.of(2026, 3, 1), null);

        Transaction found = transactionDAO.findById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Bonus", found.getCategory());
    }

    @Test
    void findByIdReturnsNullForNonexistent() throws Exception {
        Transaction found = transactionDAO.findById(UUID.randomUUID());

        assertNull(found);
    }

    @Test
    void updateModifiesTransactionFields() throws Exception {
        Wallet wallet = createTestWallet();

        Transaction created = transactionDAO.create(
                wallet.getId(), TransactionType.INCOME, new BigDecimal("100"),
                "Salary", LocalDate.of(2026, 1, 15), null);

        created.setType(TransactionType.EXPENSE);
        created.setAmount(new BigDecimal("50.00"));
        created.setCategory("Groceries");
        created.setTransactionDate(LocalDate.of(2026, 2, 1));
        created.setNote("Weekly shop");

        Transaction updated = transactionDAO.update(created);

        assertEquals(TransactionType.EXPENSE, updated.getType());
        assertEquals(0, updated.getAmount().compareTo(new BigDecimal("50.00")));
        assertEquals("Groceries", updated.getCategory());
        assertEquals(LocalDate.of(2026, 2, 1), updated.getTransactionDate());
        assertEquals("Weekly shop", updated.getNote());
    }

    @Test
    void deleteRemovesTransaction() throws Exception {
        Wallet wallet = createTestWallet();

        Transaction created = transactionDAO.create(
                wallet.getId(), TransactionType.INCOME, new BigDecimal("100"),
                "Salary", LocalDate.of(2026, 1, 15), null);

        boolean deleted = transactionDAO.delete(created.getId());

        assertTrue(deleted);
        assertNull(transactionDAO.findById(created.getId()));
    }

    @Test
    void deleteReturnsFalseForNonexistent() throws Exception {
        boolean deleted = transactionDAO.delete(UUID.randomUUID());

        assertFalse(deleted);
    }

    // --- getWalletSummariesByUserId ---

    @Test
    void getWalletSummariesReturnsEmptyForUserWithNoWallets() throws Exception {
        User user = createTestUser();

        List<WalletSummary> summaries = transactionDAO.getWalletSummariesByUserId(user.getId());

        assertTrue(summaries.isEmpty());
    }

    @Test
    void getWalletSummariesReturnsZeroBalanceForWalletWithNoTransactions() throws Exception {
        User user = createTestUser();
        walletDAO.create(user.getId(), "Empty Wallet", WalletType.CASH, "EUR");

        List<WalletSummary> summaries = transactionDAO.getWalletSummariesByUserId(user.getId());

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

        List<WalletSummary> summaries = transactionDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(1, summaries.size());
        // 500 - 150 - 50 = 300
        assertEquals(0, summaries.get(0).getBalance().compareTo(new BigDecimal("300.00")));
    }

    @Test
    void getWalletSummariesReturnsCorrectBalancePerWallet() throws Exception {
        User user = createTestUser();
        Wallet w1 = walletDAO.create(user.getId(), "Wallet A", WalletType.CASH, "EUR");
        Wallet w2 = walletDAO.create(user.getId(), "Wallet B", WalletType.BANK_ACCOUNT, "EUR");

        transactionDAO.create(w1.getId(), TransactionType.INCOME,
                new BigDecimal("200.00"), "Income A", LocalDate.now(), null);
        transactionDAO.create(w2.getId(), TransactionType.INCOME,
                new BigDecimal("100.00"), "Income B", LocalDate.now(), null);
        transactionDAO.create(w2.getId(), TransactionType.EXPENSE,
                new BigDecimal("40.00"), "Expense B", LocalDate.now(), null);

        List<WalletSummary> summaries = transactionDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(2, summaries.size());
        WalletSummary s1 = summaries.stream().filter(s -> s.getName().equals("Wallet A")).findFirst().orElseThrow();
        WalletSummary s2 = summaries.stream().filter(s -> s.getName().equals("Wallet B")).findFirst().orElseThrow();
        assertEquals(0, s1.getBalance().compareTo(new BigDecimal("200.00")));
        assertEquals(0, s2.getBalance().compareTo(new BigDecimal("60.00")));
    }

    @Test
    void getWalletSummariesPopulatesWalletFields() throws Exception {
        User user = createTestUser();
        walletDAO.create(user.getId(), "My Cash", WalletType.CASH, "USD");

        List<WalletSummary> summaries = transactionDAO.getWalletSummariesByUserId(user.getId());

        assertEquals(1, summaries.size());
        WalletSummary s = summaries.get(0);
        assertNotNull(s.getId());
        assertEquals("My Cash", s.getName());
        assertEquals(WalletType.CASH, s.getType());
        assertEquals("USD", s.getCurrency());
    }

    // --- getUserDashboardStats ---

    @Test
    void getUserDashboardStatsReturnsZerosForUserWithNoTransactions() throws Exception {
        User user = createTestUser();

        BigDecimal[] stats = transactionDAO.getUserDashboardStats(user.getId());

        assertEquals(0, stats[0].compareTo(BigDecimal.ZERO)); // totalEquity
        assertEquals(0, stats[1].compareTo(BigDecimal.ZERO)); // ytdExpenses
        assertEquals(0, stats[2].compareTo(BigDecimal.ZERO)); // totalSavings
    }

    @Test
    void getUserDashboardStatsComputesTotalEquity() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        transactionDAO.create(wallet.getId(), TransactionType.INCOME,
                new BigDecimal("1000.00"), "Salary", LocalDate.now(), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("300.00"), "Bills", LocalDate.now(), null);

        BigDecimal[] stats = transactionDAO.getUserDashboardStats(user.getId());

        assertEquals(0, stats[0].compareTo(new BigDecimal("700.00"))); // totalEquity
    }

    @Test
    void getUserDashboardStatsYtdExcludesPriorYearExpenses() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        LocalDate currentYear = LocalDate.of(LocalDate.now().getYear(), 6, 1);
        LocalDate priorYear  = LocalDate.of(LocalDate.now().getYear() - 1, 6, 1);

        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("200.00"), "Current Expense", currentYear, null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("500.00"), "Old Expense", priorYear, null);

        BigDecimal[] stats = transactionDAO.getUserDashboardStats(user.getId());

        // Only the current-year expense counts towards YTD
        assertEquals(0, stats[1].compareTo(new BigDecimal("200.00"))); // ytdExpenses
    }

    @Test
    void getUserDashboardStatsTotalSavingsEqualsIncomeMinusExpense() throws Exception {
        User user = createTestUser();
        Wallet wallet = walletDAO.create(user.getId(), "Main", WalletType.CASH, "EUR");

        transactionDAO.create(wallet.getId(), TransactionType.INCOME,
                new BigDecimal("800.00"), "Salary", LocalDate.now(), null);
        transactionDAO.create(wallet.getId(), TransactionType.EXPENSE,
                new BigDecimal("250.00"), "Rent", LocalDate.now(), null);

        BigDecimal[] stats = transactionDAO.getUserDashboardStats(user.getId());

        assertEquals(0, stats[2].compareTo(new BigDecimal("550.00"))); // totalSavings
    }
}
