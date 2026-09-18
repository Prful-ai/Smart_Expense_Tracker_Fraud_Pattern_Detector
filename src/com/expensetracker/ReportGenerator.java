package com.expensetracker;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates formatted spending and anomaly reports.
 */
public class ReportGenerator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final TransactionManager transactionManager;
    private final AnomalyDetector anomalyDetector;

    /**
     * Creates a report generator using the supplied transaction services.
     *
     * @param transactionManager source of transaction data
     * @param anomalyDetector detector used for flagged reports
     */
    public ReportGenerator(TransactionManager transactionManager, AnomalyDetector anomalyDetector) {
        this.transactionManager = transactionManager;
        this.anomalyDetector = anomalyDetector;
    }

    /**
     * Prints the current month's spending summary for a user.
     *
     * @param userId user identifier
     */
    public void generateMonthlySummary(String userId) {
        try {
            YearMonth currentMonth = YearMonth.now();
            List<Transaction> monthlyTransactions = transactionManager.getTransactionsByUser(userId).stream()
                    .filter(transaction -> YearMonth.from(transaction.getTimestamp()).equals(currentMonth))
                    .collect(Collectors.toList());
            double totalSpend = monthlyTransactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            Map<String, Double> spendingByCategory = groupSpendingByCategory(monthlyTransactions);

            System.out.println("\nMonthly Expense Summary - " + currentMonth);
            System.out.println("User: " + userId);
            System.out.println("Total spend: $" + formatAmount(totalSpend));
            System.out.println("\nSpend by category");
            System.out.println("------------------------------");
            System.out.printf("%-20s %12s%n", "Category", "Amount");
            System.out.println("------------------------------");
            spendingByCategory.forEach((category, amount) ->
                    System.out.printf("%-20s $%11s%n", category, formatAmount(amount)));
            if (spendingByCategory.isEmpty()) {
                System.out.println("No transactions found for this month.");
            }

            System.out.println("\nTop 3 categories");
            System.out.println("------------------------------");
            spendingByCategory.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(3)
                    .forEach(entry -> System.out.printf("%-20s $%11s%n",
                            entry.getKey(), formatAmount(entry.getValue())));
        } catch (InvalidTransactionException | RuntimeException exception) {
            System.out.println("Unable to generate monthly report: " + exception.getMessage());
        }
    }

    /**
     * Prints all detected anomalies for a user.
     *
     * @param userId user identifier
     */
    public void generateFlaggedReport(String userId) {
        try {
            List<Transaction> userTransactions = transactionManager.getTransactionsByUser(userId);
            List<FlaggedTransaction> flaggedTransactions = new ArrayList<>();
            flaggedTransactions.addAll(anomalyDetector.detectDuplicates(userTransactions));
            flaggedTransactions.addAll(anomalyDetector.detectSpikes(userTransactions));
            flaggedTransactions.addAll(anomalyDetector.detectOddTiming(userTransactions));

            System.out.println("\nFlagged Transactions Report");
            System.out.println("User: " + userId);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-6s %-18s %-12s %-14s %-10s%n",
                    "ID", "Timestamp", "Category", "Reason", "Risk Score");
            System.out.println("--------------------------------------------------------------------------------");
            if (flaggedTransactions.isEmpty()) {
                System.out.println("No suspicious transactions found.");
                return;
            }
            flaggedTransactions.stream()
                    .sorted(Comparator.comparing(flagged -> flagged.getTransaction().getTimestamp()))
                    .forEach(flagged -> {
                        Transaction transaction = flagged.getTransaction();
                        System.out.printf("%-6d %-18s %-12s %-14s %-10d%n",
                                transaction.getId(),
                                transaction.getTimestamp().format(DATE_FORMAT),
                                transaction.getCategory(),
                                flagged.getReason(),
                                flagged.getRiskScore());
                    });
        } catch (InvalidTransactionException | RuntimeException exception) {
            System.out.println("Unable to generate flagged report: " + exception.getMessage());
        }
    }

    private Map<String, Double> groupSpendingByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Transaction::getAmount)));
    }

    private String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }
}
