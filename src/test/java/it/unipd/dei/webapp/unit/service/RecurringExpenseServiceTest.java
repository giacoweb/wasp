package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.model.Periodicity;
import it.unipd.dei.webapp.model.RecurringExpense;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.service.RecurringExpenseService;
import it.unipd.dei.webapp.service.UserService;
import it.unipd.dei.webapp.service.WalletService;
import it.unipd.dei.webapp.service.exception.RecurringExpenseNotFoundException;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecurringExpenseServiceTest {

    private RecurringExpenseService expenseService;
    private WalletService walletService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        expenseService = new RecurringExpenseService();
        walletService = new WalletService();
        userService = new UserService();
    }

    private User createTestUser() throws Exception {
        String email = "junit-exp-" + System.currentTimeMillis() + "@test.com";
        return userService.register("Exp", "Tester", email, "secret123");
    }

    private Wallet createTestWallet(UUID userId) throws Exception {
        return walletService.createWallet(userId, "Test Wallet", "CASH", "EUR");
    }

    private RecurringExpense createTestExpense(UUID userId, UUID walletId) throws Exception {
        return expenseService.createExpense(
                userId,
                walletId.toString(),
                "Netflix",
                "15.99",
                "MONTHLY",
                "2026-05-01");
    }

    @Test
    void createExpenseSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        RecurringExpense expense = createTestExpense(user.getId(), wallet.getId());

        assertNotNull(expense.getId());
        assertEquals("Netflix", expense.getLabel());
        assertEquals(Periodicity.MONTHLY, expense.getPeriodicity());
        assertTrue(expense.isActive());
    }

    @Test
    void createExpenseWithBlankLabelThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                expenseService.createExpense(
                        user.getId(), wallet.getId().toString(), "", "15.99", "MONTHLY", "2026-05-01"));
    }

    @Test
    void createExpenseWithZeroAmountThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                expenseService.createExpense(
                        user.getId(), wallet.getId().toString(), "Netflix", "0", "MONTHLY", "2026-05-01"));
    }

    @Test
    void createExpenseWithNegativeAmountThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                expenseService.createExpense(
                        user.getId(), wallet.getId().toString(), "Netflix", "-5.00", "MONTHLY", "2026-05-01"));
    }

    @Test
    void createExpenseWithInvalidPeriodicityThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                expenseService.createExpense(
                        user.getId(), wallet.getId().toString(), "Netflix", "15.99", "WEEKLY", "2026-05-01"));
    }

    @Test
    void createExpenseWithInvalidDateThrowsValidation() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        assertThrows(ValidationException.class, () ->
                expenseService.createExpense(
                        user.getId(), wallet.getId().toString(), "Netflix", "15.99", "MONTHLY", "not-a-date"));
    }

    @Test
    void createExpenseOnOtherUsersWalletThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());
        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                expenseService.createExpense(
                        other.getId(), wallet.getId().toString(), "Netflix", "15.99", "MONTHLY", "2026-05-01"));
    }

    @Test
    void getExpensesByUserReturnsCreated() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());

        createTestExpense(user.getId(), wallet.getId());
        createTestExpense(user.getId(), wallet.getId());

        List<RecurringExpense> expenses = expenseService.getExpensesByUser(user.getId());
        assertEquals(2, expenses.size());
    }

    @Test
    void updateExpenseSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());
        RecurringExpense expense = createTestExpense(user.getId(), wallet.getId());

        RecurringExpense updated = expenseService.updateExpense(
                expense.getId(), user.getId(), "Spotify", "9.99", "MONTHLY", "2026-06-01");

        assertEquals("Spotify", updated.getLabel());
        assertEquals(0, updated.getAmount().compareTo(new java.math.BigDecimal("9.99")));
    }

    @Test
    void updateExpenseByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());
        RecurringExpense expense = createTestExpense(owner.getId(), wallet.getId());
        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                expenseService.updateExpense(
                        expense.getId(), other.getId(), "Hacked", "1.00", "MONTHLY", "2026-05-01"));
    }

    @Test
    void updateNonexistentExpenseThrowsNotFound() {
        assertThrows(RecurringExpenseNotFoundException.class, () ->
                expenseService.updateExpense(
                        UUID.randomUUID(), UUID.randomUUID(), "X", "1.00", "MONTHLY", "2026-05-01"));
    }

    @Test
    void toggleExpenseFlipsActiveFlag() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());
        RecurringExpense expense = createTestExpense(user.getId(), wallet.getId());

        assertTrue(expense.isActive());

        RecurringExpense toggled = expenseService.toggleExpense(expense.getId(), user.getId());
        assertFalse(toggled.isActive());

        RecurringExpense toggledBack = expenseService.toggleExpense(expense.getId(), user.getId());
        assertTrue(toggledBack.isActive());
    }

    @Test
    void toggleExpenseByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());
        RecurringExpense expense = createTestExpense(owner.getId(), wallet.getId());
        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                expenseService.toggleExpense(expense.getId(), other.getId()));
    }

    @Test
    void deleteExpenseSucceeds() throws Exception {
        User user = createTestUser();
        Wallet wallet = createTestWallet(user.getId());
        RecurringExpense expense = createTestExpense(user.getId(), wallet.getId());

        expenseService.deleteExpense(expense.getId(), user.getId());

        List<RecurringExpense> expenses = expenseService.getExpensesByUser(user.getId());
        assertTrue(expenses.isEmpty());
    }

    @Test
    void deleteExpenseByOtherUserThrowsUnauthorized() throws Exception {
        User owner = createTestUser();
        Wallet wallet = createTestWallet(owner.getId());
        RecurringExpense expense = createTestExpense(owner.getId(), wallet.getId());
        User other = createTestUser();

        assertThrows(UnauthorizedOperationException.class, () ->
                expenseService.deleteExpense(expense.getId(), other.getId()));
    }

    @Test
    void deleteNonexistentExpenseThrowsNotFound() {
        assertThrows(RecurringExpenseNotFoundException.class, () ->
                expenseService.deleteExpense(UUID.randomUUID(), UUID.randomUUID()));
    }
}
