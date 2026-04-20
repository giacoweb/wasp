package it.unipd.dei.webapp.service.exception;

public class SingleCurrencyRequiredException extends ValidationException {

    public SingleCurrencyRequiredException() {
        super("All wallets for a user must use the same currency.");
    }

    public SingleCurrencyRequiredException(final String message) {
        super(message);
    }
}
