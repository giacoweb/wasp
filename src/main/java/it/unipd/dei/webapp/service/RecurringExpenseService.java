package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.RecurringExpenseDAO;
import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Periodicity;
import it.unipd.dei.webapp.model.RecurringExpense;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.service.exception.RecurringExpenseNotFoundException;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RecurringExpenseService {

    private final RecurringExpenseDAO expenseDAO;
    private final WalletDAO walletDAO;

    public RecurringExpenseService() {
        this.expenseDAO = new RecurringExpenseDAO();
        this.walletDAO = new WalletDAO();
    }

    public RecurringExpenseService(final RecurringExpenseDAO expenseDAO, final WalletDAO walletDAO) {
        this.expenseDAO = expenseDAO;
        this.walletDAO = walletDAO;
    }

    public List<RecurringExpense> getExpensesByUser(final UUID userId) throws SQLException {
        return expenseDAO.findByUserId(userId);
    }

    public RecurringExpense createExpense(final UUID userId,
                                          final String walletIdStr,
                                          final String label,
                                          final String amountStr,
                                          final String periodicityStr,
                                          final String nextDueStr)
            throws ValidationException, UnauthorizedOperationException, SQLException {

        UUID walletId = parseUUID(walletIdStr, "Wallet ID");
        BigDecimal amount = parsePositiveAmount(amountStr);
        Periodicity periodicity = parsePeriodicity(periodicityStr);
        LocalDate nextDue = parseDate(nextDueStr);

        if (label == null || label.isBlank()) {
            throw new ValidationException("Label cannot be empty.");
        }

        Wallet wallet = walletDAO.findById(walletId);
        if (wallet == null || !wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        return expenseDAO.create(walletId, label.trim(), amount, periodicity, nextDue);
    }

    public RecurringExpense updateExpense(final UUID expenseId,
                                          final UUID userId,
                                          final String label,
                                          final String amountStr,
                                          final String periodicityStr,
                                          final String nextDueStr)
            throws ValidationException, RecurringExpenseNotFoundException, UnauthorizedOperationException, SQLException {

        RecurringExpense existing = loadAndCheckOwnership(expenseId, userId);

        if (label == null || label.isBlank()) {
            throw new ValidationException("Label cannot be empty.");
        }

        BigDecimal amount = parsePositiveAmount(amountStr);
        Periodicity newPeriodicity = parsePeriodicity(periodicityStr);

        LocalDate nextDue;
        if (!newPeriodicity.equals(existing.getPeriodicity())) {
            nextDue = calculateNextDue(newPeriodicity);
        } else {
            nextDue = parseDate(nextDueStr);
        }

        return expenseDAO.update(expenseId, label.trim(), amount, newPeriodicity, nextDue);
    }

    public void toggleExpense(final UUID expenseId, final UUID userId)
            throws RecurringExpenseNotFoundException, UnauthorizedOperationException, SQLException {

        loadAndCheckOwnership(expenseId, userId);
        expenseDAO.toggleActive(expenseId);
    }

    public void deleteExpense(final UUID expenseId, final UUID userId)
            throws RecurringExpenseNotFoundException, UnauthorizedOperationException, SQLException {

        loadAndCheckOwnership(expenseId, userId);
        expenseDAO.delete(expenseId);
    }

    private RecurringExpense loadAndCheckOwnership(final UUID expenseId, final UUID userId)
            throws RecurringExpenseNotFoundException, UnauthorizedOperationException, SQLException {

        RecurringExpense expense = expenseDAO.findById(expenseId);
        if (expense == null) {
            throw new RecurringExpenseNotFoundException();
        }

        Wallet wallet = walletDAO.findById(expense.getWalletId());
        if (wallet == null || !wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        return expense;
    }

    private LocalDate calculateNextDue(final Periodicity periodicity) {
        return switch (periodicity) {
            case MONTHLY   -> LocalDate.now().plusMonths(1);
            case QUARTERLY -> LocalDate.now().plusMonths(3);
            case YEARLY    -> LocalDate.now().plusYears(1);
        };
    }

    private UUID parseUUID(final String value, final String fieldName) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid " + fieldName + " format.");
        }
    }

    private BigDecimal parsePositiveAmount(final String value) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Amount cannot be empty.");
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Amount must be greater than zero.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException("Amount must be a valid number.");
        }
    }

    private Periodicity parsePeriodicity(final String value) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Periodicity cannot be empty.");
        }
        try {
            return Periodicity.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid periodicity value.");
        }
    }

    private LocalDate parseDate(final String value) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Next due date cannot be empty.");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new ValidationException("Date must be in YYYY-MM-DD format.");
        }
    }
}
