package com.expensetracker;

/**
 * Signals invalid transaction input or a missing transaction.
 */
public class InvalidTransactionException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a descriptive message.
     *
     * @param message explanation of the validation failure
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
}
