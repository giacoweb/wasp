package it.unipd.dei.webapp.unit.dao;

import it.unipd.dei.webapp.dao.TransactionDAO;
import it.unipd.dei.webapp.dao.UserDAO;
import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Transaction;
import it.unipd.dei.webapp.model.TransactionType;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.UserRole;
import it.unipd.dei.webapp.model.Wallet;
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

    private Wallet createTestWallet() throws Exception {
        User user = new User();
        user.setName("Txn");
        user.setSurname("DAOTest");
        user.setEmail("junit-tdao-" + System.currentTimeMillis() + "@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("secret123"));
        user.setRole(UserRole.REGISTERED);
        User created = userDAO.createUser(user);
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
}
