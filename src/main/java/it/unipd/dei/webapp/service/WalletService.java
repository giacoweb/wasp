package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.service.exception.SingleCurrencyRequiredException;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import it.unipd.dei.webapp.service.exception.WalletHasTransactionsException;
import it.unipd.dei.webapp.service.exception.WalletNotFoundException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WalletService {

    private final WalletDAO walletDAO;

    public WalletService() {
        this.walletDAO = new WalletDAO();
    }

    public WalletService(final WalletDAO walletDAO) {
        this.walletDAO = walletDAO;
    }

    public List<Wallet> getWalletsByUser(final UUID userId) throws SQLException {
        return walletDAO.findByUserId(userId);
    }

    public Wallet createWallet(final UUID userId,
                               final String name,
                               final String type,
                               final String currency)
            throws ValidationException, SQLException {

        String normalizedCurrency = normalizeCurrency(currency);
        validateWalletInput(name, type, normalizedCurrency);
        validateCurrencyConsistencyForCreate(userId, normalizedCurrency);

        WalletType walletType = parseWalletType(type);

        return walletDAO.create(userId, name, walletType, normalizedCurrency);
    }

    public Wallet updateWallet(final UUID walletId,
                               final UUID userId,
                               final String name,
                               final String type,
                               final String currency)
            throws ValidationException, UnauthorizedOperationException, SQLException {

        String normalizedCurrency = normalizeCurrency(currency);
        validateWalletInput(name, type, normalizedCurrency);

        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            throw new ValidationException("Wallet not found.");
        }

        if (!wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        WalletType walletType = parseWalletType(type);
        String existingCurrency = normalizeCurrency(wallet.getCurrency());
        if (!normalizedCurrency.equals(existingCurrency)) {
            validateCurrencyConsistencyForUpdate(userId, walletId, normalizedCurrency);
        }

        wallet.setName(name);
        wallet.setType(walletType);
        wallet.setCurrency(normalizedCurrency);

        return walletDAO.update(wallet);
    }

    public void deleteWallet(final UUID walletId, final UUID userId)
            throws WalletNotFoundException, UnauthorizedOperationException, WalletHasTransactionsException, SQLException {

        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            throw new WalletNotFoundException();
        }

        if (!wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        int transactionCount = walletDAO.countTransactions(walletId);

        if (transactionCount > 0) {
            throw new WalletHasTransactionsException();
        }

        walletDAO.delete(walletId);
    }

    private void validateWalletInput(final String name,
                                     final String type,
                                     final String currency) throws ValidationException {

        if (name == null || name.isBlank()) {
            throw new ValidationException("Wallet name cannot be empty.");
        }

        if (type == null || type.isBlank()) {
            throw new ValidationException("Wallet type cannot be empty.");
        }

        if (currency == null || currency.isBlank()) {
            throw new ValidationException("Currency cannot be empty.");
        }

        if (currency.length() != 3 || !currency.chars().allMatch(Character::isLetter)) {
            throw new ValidationException("Currency must be a 3-letter alphabetic code.");
        }
    }

    private void validateCurrencyConsistencyForCreate(final UUID userId,
                                                      final String requestedCurrency)
            throws SQLException, SingleCurrencyRequiredException {
        Set<String> currencies = collectWalletCurrencies(walletDAO.findByUserId(userId), null);
        currencies.add(requestedCurrency);

        if (currencies.size() > 1) {
            throw new SingleCurrencyRequiredException();
        }
    }

    private void validateCurrencyConsistencyForUpdate(final UUID userId,
                                                      final UUID walletId,
                                                      final String requestedCurrency)
            throws SQLException, SingleCurrencyRequiredException {
        Set<String> otherCurrencies = collectWalletCurrencies(walletDAO.findByUserId(userId), walletId);
        otherCurrencies.add(requestedCurrency);

        if (otherCurrencies.size() > 1) {
            throw new SingleCurrencyRequiredException();
        }
    }

    private Set<String> collectWalletCurrencies(final List<Wallet> wallets, final UUID walletIdToExclude) {
        Set<String> currencies = new HashSet<>();

        for (Wallet wallet : wallets) {
            if (walletIdToExclude != null && walletIdToExclude.equals(wallet.getId())) {
                continue;
            }

            String currency = normalizeCurrency(wallet.getCurrency());
            if (currency != null && !currency.isBlank()) {
                currencies.add(currency);
            }
        }

        return currencies;
    }

    private String normalizeCurrency(final String currency) {
        if (currency == null) {
            return null;
        }
        return currency.trim().toUpperCase();
    }

    private WalletType parseWalletType(final String type) throws ValidationException {
        if (type == null || type.isBlank()) {
            throw new ValidationException("Wallet type cannot be empty.");
        }

        try {
            return WalletType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid wallet type.");
        }
    }
}
