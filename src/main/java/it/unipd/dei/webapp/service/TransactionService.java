package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.TransactionDAO;
import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Transaction;
import it.unipd.dei.webapp.model.TransactionType;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final WalletDAO walletDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.walletDAO = new WalletDAO();
    }

    public TransactionService(final TransactionDAO transactionDAO, final WalletDAO walletDAO) {
        this.transactionDAO = transactionDAO;
        this.walletDAO = walletDAO;
    }

    public List<Transaction> getTransactionsByWallet(final UUID walletId, final UUID userId)
            throws UnauthorizedOperationException, ValidationException, SQLException {

        verifyWalletOwnership(walletId, userId);
        return transactionDAO.findByWalletId(walletId);
    }

    public Transaction createTransaction(final UUID walletId,
                                         final UUID userId,
                                         final String amountStr,
                                         final String category,
                                         final String dateStr,
                                         final String note)
            throws UnauthorizedOperationException, ValidationException, SQLException {

        verifyWalletOwnership(walletId, userId);

        BigDecimal amount = parseAmount(amountStr);
        TransactionType type = resolveType(amount);
        LocalDate date = parseDate(dateStr);

        validateTransactionInput(category);

        return transactionDAO.create(
                walletId,
                type,
                amount.abs(),
                category.trim(),
                date,
                normalizeNote(note));
    }

    public Transaction updateTransaction(final UUID transactionId,
                                         final UUID userId,
                                         final String amountStr,
                                         final String category,
                                         final String dateStr,
                                         final String note)
            throws UnauthorizedOperationException, ValidationException, SQLException {

        Transaction existing = transactionDAO.findById(transactionId);

        if (existing == null) {
            throw new ValidationException("Transaction not found.");
        }

        verifyWalletOwnership(existing.getWalletId(), userId);

        BigDecimal amount = parseAmount(amountStr);
        TransactionType type = resolveType(amount);
        LocalDate date = parseDate(dateStr);

        validateTransactionInput(category);

        existing.setType(type);
        existing.setAmount(amount.abs());
        existing.setCategory(category.trim());
        existing.setTransactionDate(date);
        existing.setNote(normalizeNote(note));

        return transactionDAO.update(existing);
    }

    public void deleteTransaction(final UUID transactionId, final UUID userId)
            throws UnauthorizedOperationException, ValidationException, SQLException {

        Transaction existing = transactionDAO.findById(transactionId);

        if (existing == null) {
            throw new ValidationException("Transaction not found.");
        }

        verifyWalletOwnership(existing.getWalletId(), userId);

        transactionDAO.delete(transactionId);
    }

    private void verifyWalletOwnership(final UUID walletId, final UUID userId)
            throws UnauthorizedOperationException, ValidationException, SQLException {

        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            throw new ValidationException("Wallet not found.");
        }

        if (!wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }
    }

    private static final int MAX_CATEGORY_LENGTH = 80;

    private void validateTransactionInput(final String category) throws ValidationException {
        if (category == null || category.isBlank()) {
            throw new ValidationException("Category cannot be empty.");
        }

        if (category.trim().length() > MAX_CATEGORY_LENGTH) {
            throw new ValidationException("Category must not exceed " + MAX_CATEGORY_LENGTH + " characters.");
        }
    }

    private BigDecimal parseAmount(final String amountStr) throws ValidationException {
        if (amountStr == null || amountStr.isBlank()) {
            throw new ValidationException("Amount cannot be empty.");
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr.trim()).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                throw new ValidationException("Amount cannot be zero.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid amount format.");
        }
    }

    private TransactionType resolveType(final BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
    }

    private LocalDate parseDate(final String dateStr) throws ValidationException {
        if (dateStr == null || dateStr.isBlank()) {
            throw new ValidationException("Transaction date cannot be empty.");
        }

        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private String normalizeNote(final String note) {
        if (note == null) {
            return null;
        }
        String trimmed = note.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
