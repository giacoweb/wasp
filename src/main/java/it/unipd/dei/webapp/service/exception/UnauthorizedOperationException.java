package it.unipd.dei.webapp.service.exception;

public class UnauthorizedOperationException extends Exception {
    public UnauthorizedOperationException() {
        super("You are not authorized to perform this operation.");
    }
}
