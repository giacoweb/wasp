package it.unipd.dei.webapp.service.exception;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException() {
        super("Email is already registered.");
    }
}