package com.expensetracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an expense tracker user and the user's transactions.
 */
public class User {
    private String userId;
    private String name;
    private final List<Transaction> transactions;

    /**
     * Creates a user with an empty transaction list.
     *
     * @param userId unique user identifier
     * @param name user's display name
     */
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.transactions = new ArrayList<>();
    }

    /**
     * Returns the user identifier.
     *
     * @return user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Updates the user identifier.
     *
     * @param userId new user identifier
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the user's display name.
     *
     * @return user's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the user's display name.
     *
     * @param name new display name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a transaction to this user's transaction list.
     *
     * @param transaction transaction to add; null values are ignored
     */
    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    /**
     * Removes a transaction from this user's transaction list.
     *
     * @param transaction transaction to remove
     * @return true if the transaction was removed
     */
    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }

    /**
     * Returns this user's transactions as an unmodifiable view.
     *
     * @return unmodifiable transaction list
     */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
