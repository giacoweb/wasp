package it.unipd.dei.webapp.service.exception;

public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}