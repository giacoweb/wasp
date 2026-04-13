package it.unipd.dei.webapp.service.exception;

public class WalletNotFoundException extends Exception {

    public WalletNotFoundException() {
        super("Wallet not found.");
    }
}
