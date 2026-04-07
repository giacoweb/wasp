package it.unipd.dei.webapp.service.exception;

public class WalletHasTransactionsException extends Exception {

    public WalletHasTransactionsException() {
        super("Wallet cannot be deleted because it has linked transactions.");
    }
}
