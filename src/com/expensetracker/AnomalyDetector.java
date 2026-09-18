package com.expensetracker;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Applies rule-based checks to identify unusual transactions.
 */
public class AnomalyDetector {
    private static final Duration DEFAULT_DUPLICATE_WINDOW = Duration.ofMinutes(5);
    private final Duration duplicateWindow;

    /**
     * Creates a detector using a five-minute duplicate window.
     */
    public AnomalyDetector() {
        this(DEFAULT_DUPLICATE_WINDOW);
    }

    /**
     * Creates a detector with a configurable duplicate window.
     *
     * @param duplicateWindow maximum time between matching transactions
     * @throws IllegalArgumentException if the window is null, negative, or zero
     */
    public AnomalyDetector(Duration duplicateWindow) {
        if (duplicateWindow == null || duplicateWindow.isZero() || duplicateWindow.isNegative()) {
            throw new IllegalArgumentException("Duplicate window must be greater than zero.");
        }
        this.duplicateWindow = duplicateWindow;
    }

    /**
     * Flags transactions with the same user, amount, and category within the duplicate window.
     *
     * @param transactions transactions to inspect
     * @return flagged duplicate transactions
     */
    public List<FlaggedTransaction> detectDuplicates(List<Transaction> transactions) {
        List<FlaggedTransaction> flaggedTransactions = new ArrayList<>();
        Set<Integer> flaggedIds = new HashSet<>();
        if (transactions == null) {
            return flaggedTransactions;
        }

        for (int firstIndex = 0; firstIndex < transactions.size(); firstIndex++) {
            Transaction first = transactions.get(firstIndex);
            if (first == null || first.getTimestamp() == null) {
                continue;
            }
            for (int secondIndex = firstIndex + 1; secondIndex < transactions.size(); secondIndex++) {
                Transaction second = transactions.get(secondIndex);
                if (isDuplicate(first, second)) {
                    addFlaggedTransaction(flaggedTransactions, flaggedIds, first, "DUPLICATE", 8);
                    addFlaggedTransaction(flaggedTransactions, flaggedIds, second, "DUPLICATE", 8);
                }
            }
        }
        return flaggedTransactions;
    }

    /**
     * Flags transactions that exceed the category's historical mean by two standard deviations.
     *
     * @param transactions transactions to inspect
     * @return flagged spike transactions
     */
    public List<FlaggedTransaction> detectSpikes(List<Transaction> transactions) {
        List<FlaggedTransaction> flaggedTransactions = new ArrayList<>();
        Set<Integer> flaggedIds = new HashSet<>();
        if (transactions == null) {
            return flaggedTransactions;
        }

        for (Transaction transaction : transactions) {
            if (transaction == null || transaction.getUserId() == null || transaction.getCategory() == null) {
                continue;
            }
            List<Transaction> historicalTransactions = transactions.stream()
                    .filter(Objects::nonNull)
                    .filter(candidate -> candidate != transaction)
                    .filter(candidate -> transaction.getUserId().equals(candidate.getUserId()))
                    .filter(candidate -> transaction.getCategory().equalsIgnoreCase(candidate.getCategory()))
                    .toList();
            if (historicalTransactions.size() < 2) {
                continue;
            }
            double mean = calculateMean(historicalTransactions);
            double standardDeviation = calculateStdDev(historicalTransactions);
            if (transaction.getAmount() > mean + (2 * standardDeviation)) {
                addFlaggedTransaction(flaggedTransactions, flaggedIds, transaction, "SPIKE", 6);
            }
        }
        return flaggedTransactions;
    }

    /**
     * Flags transactions that occurred between midnight and 5 AM.
     *
     * @param transactions transactions to inspect
     * @return flagged odd-timing transactions
     */
    public List<FlaggedTransaction> detectOddTiming(List<Transaction> transactions) {
        List<FlaggedTransaction> flaggedTransactions = new ArrayList<>();
        if (transactions == null) {
            return flaggedTransactions;
        }
        for (Transaction transaction : transactions) {
            if (transaction == null || transaction.getTimestamp() == null) {
                continue;
            }
            LocalTime time = transaction.getTimestamp().toLocalTime();
            if (time.isBefore(LocalTime.of(5, 0))) {
                flaggedTransactions.add(new FlaggedTransaction(transaction, "ODD_TIMING", 3));
            }
        }
        return flaggedTransactions;
    }

    /**
     * Calculates the arithmetic mean of transaction amounts.
     *
     * @param transactions transactions to calculate
     * @return mean amount, or zero for an empty or null list
     */
    public double calculateMean(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return 0.0;
        }
        return transactions.stream()
                .filter(Objects::nonNull)
                .mapToDouble(Transaction::getAmount)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates the population standard deviation of transaction amounts.
     *
     * @param transactions transactions to calculate
     * @return population standard deviation, or zero for an empty list
     */
    public double calculateStdDev(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return 0.0;
        }
        double mean = calculateMean(transactions);
        double variance = transactions.stream()
                .filter(Objects::nonNull)
                .mapToDouble(transaction -> Math.pow(transaction.getAmount() - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    private boolean isDuplicate(Transaction first, Transaction second) {
        if (second == null || second.getTimestamp() == null
                || !Objects.equals(first.getUserId(), second.getUserId())
                || !Objects.equals(first.getCategory(), second.getCategory())
                || Double.compare(first.getAmount(), second.getAmount()) != 0) {
            return false;
        }
        Duration difference = Duration.between(first.getTimestamp(), second.getTimestamp()).abs();
        return !difference.isZero() && difference.compareTo(duplicateWindow) <= 0;
    }

    private void addFlaggedTransaction(List<FlaggedTransaction> flaggedTransactions,
            Set<Integer> flaggedIds, Transaction transaction, String reason, int riskScore) {
        if (flaggedIds.add(transaction.getId())) {
            flaggedTransactions.add(new FlaggedTransaction(transaction, reason, riskScore));
        }
    }
}
