package it.unipd.dei.webapp.service.exception;

public class RecurringExpenseNotFoundException extends Exception {
    public RecurringExpenseNotFoundException() {
        super("Recurring expense not found.");
    }
}
