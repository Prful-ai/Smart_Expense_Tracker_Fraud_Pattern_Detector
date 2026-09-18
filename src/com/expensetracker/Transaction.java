package com.expensetracker;

import java.time.LocalDateTime;

/**
 * Represents a single expense transaction.
 */
public class Transaction {
    private int id;
    private String userId;
    private double amount;
    private String category;
    private LocalDateTime timestamp;
    private String description;

    /**
     * Creates a transaction with the supplied details.
     *
     * @param id unique transaction identifier
     * @param userId identifier of the user who made the transaction
     * @param amount transaction amount
     * @param category expense category
     * @param timestamp date and time of the transaction
     * @param description transaction description
     */
    public Transaction(int id, String userId, double amount, String category,
            LocalDateTime timestamp, String description) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
        this.description = description;
    }

    /**
     * Returns the transaction identifier.
     *
     * @return transaction identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Updates the transaction identifier.
     *
     * @param id new transaction identifier
     */
    public void setId(int id) {
        this.id = id;
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
     * Returns the transaction amount.
     *
     * @return transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Updates the transaction amount.
     *
     * @param amount new transaction amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns the expense category.
     *
     * @return expense category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Updates the expense category.
     *
     * @param category new expense category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the transaction timestamp.
     *
     * @return transaction timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Updates the transaction timestamp.
     *
     * @param timestamp new transaction timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the transaction description.
     *
     * @return transaction description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the transaction description.
     *
     * @param description new transaction description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a readable representation of this transaction.
     *
     * @return formatted transaction details
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                '}';
    }
}
