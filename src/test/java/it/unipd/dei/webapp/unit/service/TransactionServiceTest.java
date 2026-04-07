package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.model.Transaction;
import it.unipd.dei.webapp.model.TransactionType;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.service.TransactionService;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionServiceTest {

    private TransactionService transactionService;
    private WalletService walletService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        walletService = new WalletService();
        userService = new UserService();
    }

    private User createTestUser() throws Exception {
        String email = "junit-txn-" + System.currentTimeMillis() + "@test.com";
        return userService.register("Txn", "Tester", email, "secret123");
    }

    private Wallet createTestWallet(UUID userId) throws Exception {
        return walletService.createWallet(userId, "Test Wallet", "CASH", "EUR");
    }

    @Test
    void createTransactionWithPositiveAmountSetsIncome() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        Transaction txn = transactionService.createTransaction(
                wallet.getId(), user.getId(), "100.50", "Salary", "2026-01-15", "Monthly pay");

        assertNotNull(txn.getId());
        assertEquals(TransactionType.INCOME, txn.getType());
        assertEquals("Salary", txn.getCategory());
    }

    @Test
    void createTransactionWithNegativeAmountSetsExpense() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        Transaction txn = transactionService.createTransaction(
                wallet.getId(), user.getId(), "-50.00", "Groceries", "2026-01-15", null);

        assertEquals(TransactionType.EXPENSE, txn.getType());
    }

    @Test
    void createTransactionStoresAbsoluteAmount() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        Transaction txn = transactionService.createTransaction(
                wallet.getId(), user.getId(), "-75.25", "Food", "2026-01-15", null);

        assertEquals(0, txn.getAmount().compareTo(new java.math.BigDecimal("75.25")));
    }

    @Test
    void createTransactionWithZeroAmountThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), user.getId(), "0", "Test", "2026-01-15", null));
    }

    @Test
    void createTransactionWithNullAmountThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), user.getId(), null, "Test", "2026-01-15", null));
    }

    @Test
    void createTransactionWithInvalidAmountThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), user.getId(), "abc", "Test", "2026-01-15", null));
    }

    @Test
    void createTransactionWithBlankCategoryThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), user.getId(), "100", "", "2026-01-15", null));
    }

    @Test
    void createTransactionWithNullDateThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), user.getId(), "100", "Test", null, null));
    }

    @Test
    void createTransactionWithInvalidDateThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), user.getId(), "100", "Test", "not-a-date", null));
    }

    @Test
    void createTransactionOnOtherUsersWalletThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());
        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                transactionService.createTransaction(
                        wallet.getId(), other.getId(), "100", "Test", "2026-01-15", null));
    }

    @Test
    void createTransactionOnNonexistentWalletThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(
                        UUID.randomUUID(), UUID.randomUUID(), "100", "Test", "2026-01-15", null));
    }

    @Test
    void getTransactionsByWalletReturnsCreated() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        transactionService.createTransaction(
                wallet.getId(), user.getId(), "100", "Salary", "2026-01-15", null);
        transactionService.createTransaction(
                wallet.getId(), user.getId(), "-30", "Lunch", "2026-01-16", null);

        List<Transaction> transactions = transactionService.getTransactionsByWallet(
                wallet.getId(), user.getId());

        assertEquals(2, transactions.size());
    }

    @Test
    void getTransactionsByWalletForOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());
        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                transactionService.getTransactionsByWallet(wallet.getId(), other.getId()));
    }

    @Test
    void updateTransactionSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        Transaction created = transactionService.createTransaction(
                wallet.getId(), user.getId(), "100", "Salary", "2026-01-15", null);

        Transaction updated = transactionService.updateTransaction(
                created.getId(), user.getId(), "-50", "Groceries", "2026-02-01", "Weekly shop");

        assertEquals(TransactionType.EXPENSE, updated.getType());
        assertEquals("Groceries", updated.getCategory());
        assertEquals("Weekly shop", updated.getNote());
    }

    @Test
    void updateTransactionByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());

        Transaction created = transactionService.createTransaction(
                wallet.getId(), owner.getId(), "100", "Salary", "2026-01-15", null);

        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                transactionService.updateTransaction(
                        created.getId(), other.getId(), "200", "Hack", "2026-01-15", null));
    }

    @Test
    void updateNonexistentTransactionThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                transactionService.updateTransaction(
                        UUID.randomUUID(), UUID.randomUUID(), "100", "Test", "2026-01-15", null));
    }

    @Test
    void deleteTransactionSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        Transaction created = transactionService.createTransaction(
                wallet.getId(), user.getId(), "100", "Salary", "2026-01-15", null);

        transactionService.deleteTransaction(created.getId(), user.getId());

        List<Transaction> transactions = transactionService.getTransactionsByWallet(
                wallet.getId(), user.getId());
        assertTrue(transactions.isEmpty());
    }

    @Test
    void deleteTransactionByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());

        Transaction created = transactionService.createTransaction(
                wallet.getId(), owner.getId(), "100", "Salary", "2026-01-15", null);

        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                transactionService.deleteTransaction(created.getId(), other.getId()));
    }

    @Test
    void deleteNonexistentTransactionThrowsValidation() {
        assertThrows(ValidationException.class, () ->
                transactionService.deleteTransaction(UUID.randomUUID(), UUID.randomUUID()));
    }
}
