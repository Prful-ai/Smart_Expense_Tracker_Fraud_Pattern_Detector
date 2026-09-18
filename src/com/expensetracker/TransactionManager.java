package com.expensetracker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manages creation, retrieval, updating, and deletion of transactions.
 */
public class TransactionManager {
    private final List<Transaction> transactions;

    /**
     * Creates an empty transaction manager.
     */
    public TransactionManager() {
        transactions = new ArrayList<>();
    }

    /**
     * Creates a transaction manager initialized with existing transactions.
     *
     * @param transactions transactions to manage
     * @throws InvalidTransactionException if the supplied list contains invalid data
     */
    public TransactionManager(List<Transaction> transactions) throws InvalidTransactionException {
        this();
        if (transactions == null) {
            throw new InvalidTransactionException("Transaction list cannot be null.");
        }
        for (Transaction transaction : transactions) {
            validateTransaction(transaction);
            if (findTransactionById(transaction.getId()) != null) {
                throw new InvalidTransactionException("A transaction with this ID already exists.");
            }
            this.transactions.add(transaction);
        }
    }

    /**
     * Adds a transaction after validating its required fields.
     *
     * @param transaction transaction to add
     * @throws InvalidTransactionException if the transaction is invalid or its ID already exists
     */
    public void addTransaction(Transaction transaction) throws InvalidTransactionException {
        validateTransaction(transaction);
        if (findTransactionById(transaction.getId()) != null) {
            throw new InvalidTransactionException("A transaction with this ID already exists.");
        }
        transactions.add(transaction);
    }

    /**
     * Deletes a transaction by ID.
     *
     * @param transactionId ID of the transaction to delete
     * @throws InvalidTransactionException if no transaction has the supplied ID
     */
    public void deleteTransaction(int transactionId) throws InvalidTransactionException {
        Transaction transaction = findTransactionById(transactionId);
        if (transaction == null) {
            throw new InvalidTransactionException("Transaction not found: " + transactionId);
        }
        transactions.remove(transaction);
    }

    /**
     * Replaces an existing transaction with updated values.
     *
     * @param updatedTransaction updated transaction data
     * @throws InvalidTransactionException if the transaction is invalid or its ID does not exist
     */
    public void updateTransaction(Transaction updatedTransaction) throws InvalidTransactionException {
        validateTransaction(updatedTransaction);
        for (int index = 0; index < transactions.size(); index++) {
            if (transactions.get(index).getId() == updatedTransaction.getId()) {
                transactions.set(index, updatedTransaction);
                return;
            }
        }
        throw new InvalidTransactionException("Transaction not found: " + updatedTransaction.getId());
    }

    /**
     * Returns all transactions managed by this instance.
     *
     * @return unmodifiable view of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Returns transactions belonging to a user.
     *
     * @param userId user identifier to search for
     * @return matching transactions
     * @throws InvalidTransactionException if the user ID is blank
     */
    public List<Transaction> getTransactionsByUser(String userId) throws InvalidTransactionException {
        validateText(userId, "User ID");
        return transactions.stream()
                .filter(transaction -> userId.equals(transaction.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Returns transactions in a category.
     *
     * @param category category to search for
     * @return matching transactions
     * @throws InvalidTransactionException if the category is blank
     */
    public List<Transaction> getTransactionsByCategory(String category) throws InvalidTransactionException {
        validateText(category, "Category");
        return transactions.stream()
                .filter(transaction -> category.equalsIgnoreCase(transaction.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Returns transactions within an inclusive date-time range.
     *
     * @param start start of the range
     * @param end end of the range
     * @return transactions in the date range
     * @throws InvalidTransactionException if either date is null or the range is reversed
     */
    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end)
            throws InvalidTransactionException {
        if (start == null || end == null || start.isAfter(end)) {
            throw new InvalidTransactionException("A valid start and end date are required.");
        }
        return transactions.stream()
                .filter(transaction -> !transaction.getTimestamp().isBefore(start)
                        && !transaction.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }

    private Transaction findTransactionById(int transactionId) {
        return transactions.stream()
                .filter(transaction -> transaction.getId() == transactionId)
                .findFirst()
                .orElse(null);
    }

    private void validateTransaction(Transaction transaction) throws InvalidTransactionException {
        if (transaction == null) {
            throw new InvalidTransactionException("Transaction cannot be null.");
        }
        if (transaction.getAmount() < 0 || Double.isNaN(transaction.getAmount())
                || Double.isInfinite(transaction.getAmount())) {
            throw new InvalidTransactionException("Amount must be a finite, non-negative number.");
        }
        validateText(transaction.getUserId(), "User ID");
        validateText(transaction.getCategory(), "Category");
        if (transaction.getTimestamp() == null) {
            throw new InvalidTransactionException("Timestamp cannot be null.");
        }
    }

    private void validateText(String value, String fieldName) throws InvalidTransactionException {
        if (Objects.isNull(value) || value.trim().isEmpty()) {
            throw new InvalidTransactionException(fieldName + " cannot be blank.");
        }
    }

}
