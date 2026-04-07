package it.unipd.dei.webapp.service;

import it.unipd.dei.webapp.dao.WalletDAO;
import it.unipd.dei.webapp.model.Wallet;
import it.unipd.dei.webapp.model.WalletType;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import it.unipd.dei.webapp.service.exception.WalletHasTransactionsException;

import java.sql.SQLException;
import java.util.List;
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

        validateWalletInput(name, type, currency);

        WalletType walletType = parseWalletType(type);

        return walletDAO.create(userId, name, walletType, currency);
    }

    public Wallet updateWallet(final UUID walletId,
                               final UUID userId,
                               final String name,
                               final String type,
                               final String currency)
            throws ValidationException, UnauthorizedOperationException, SQLException {

        validateWalletInput(name, type, currency);

        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            throw new ValidationException("Wallet not found.");
        }

        if (!wallet.getUserId().equals(userId)) {
            throw new UnauthorizedOperationException();
        }

        WalletType walletType = parseWalletType(type);

        wallet.setName(name);
        wallet.setType(walletType);
        wallet.setCurrency(currency);

        return walletDAO.update(wallet);
    }

    public void deleteWallet(final UUID walletId, final UUID userId)
            throws UnauthorizedOperationException, WalletHasTransactionsException, SQLException {

        Wallet wallet = walletDAO.findById(walletId);

        if (wallet == null) {
            return;
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

        if (currency.length() != 3) {
            throw new ValidationException("Currency must be a 3-letter code.");
        }
    }

    private WalletType parseWalletType(final String type) throws ValidationException {
        try {
            return WalletType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid wallet type.");
        }
    }
}
