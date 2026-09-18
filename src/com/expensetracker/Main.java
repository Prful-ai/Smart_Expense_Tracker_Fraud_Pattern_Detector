package com.expensetracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the Smart Expense & Fraud-Pattern Detector console application.
 */
public class Main {
    /**
     * Starts the command-line application.
     *
     * @param args command-line arguments, which are not required
     */
    public static void main(String[] args) {
        FileStorage fileStorage = new FileStorage();
        TransactionManager transactionManager = loadTransactionManager(fileStorage);
        AnomalyDetector anomalyDetector = new AnomalyDetector();
        ReportGenerator reportGenerator = new ReportGenerator(transactionManager, anomalyDetector);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        addTransaction(scanner, transactionManager);
                        break;
                    case "2":
                        viewAllTransactions(transactionManager);
                        break;
                    case "3":
                        runFraudDetection(scanner, transactionManager, anomalyDetector);
                        break;
                    case "4":
                        viewMonthlyReport(scanner, reportGenerator);
                        break;
                    case "5":
                        viewFlaggedTransactions(scanner, reportGenerator);
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select an option from 1 to 6.");
                }
            }
        } finally {
            saveTransactions(fileStorage, transactionManager);
        }
        System.out.println("Goodbye.");
    }

    private static TransactionManager loadTransactionManager(FileStorage fileStorage) {
        try {
            List<Transaction> transactions = fileStorage.loadTransactions();
            return new TransactionManager(transactions);
        } catch (Exception exception) {
            System.out.println("Unable to load saved transactions: " + exception.getMessage());
            return new TransactionManager();
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Smart Expense & Fraud-Pattern Detector ===");
        System.out.println("1. Add transaction");
        System.out.println("2. View all transactions");
        System.out.println("3. Run fraud detection");
        System.out.println("4. View monthly report");
        System.out.println("5. View flagged transactions");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addTransaction(Scanner scanner, TransactionManager transactionManager) {
        try {
            int id = readInteger(scanner, "Transaction ID: ");
            String userId = readText(scanner, "User ID: ");
            double amount = readDouble(scanner, "Amount: ");
            String category = readText(scanner, "Category: ");
            LocalDateTime timestamp = readDateTime(scanner, "Timestamp (yyyy-MM-ddTHH:mm): ");
            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            transactionManager.addTransaction(
                    new Transaction(id, userId, amount, category, timestamp, description));
            System.out.println("Transaction added.");
        } catch (InvalidTransactionException | IllegalArgumentException exception) {
            System.out.println("Transaction was not added: " + exception.getMessage());
        }
    }

    private static void viewAllTransactions(TransactionManager transactionManager) {
        List<Transaction> transactions = transactionManager.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }
        System.out.println("\nAll Transactions");
        System.out.println("--------------------------------------------------------------------------------");
        transactions.forEach(System.out::println);
    }

    private static void runFraudDetection(Scanner scanner, TransactionManager transactionManager,
            AnomalyDetector anomalyDetector) {
        try {
            String userId = readText(scanner, "User ID to inspect: ");
            List<Transaction> transactions = transactionManager.getTransactionsByUser(userId);
            int duplicateCount = anomalyDetector.detectDuplicates(transactions).size();
            int spikeCount = anomalyDetector.detectSpikes(transactions).size();
            int oddTimingCount = anomalyDetector.detectOddTiming(transactions).size();
            System.out.println("Fraud detection complete for " + userId + ".");
            System.out.println("Duplicate flags: " + duplicateCount);
            System.out.println("Spike flags: " + spikeCount);
            System.out.println("Odd timing flags: " + oddTimingCount);
        } catch (InvalidTransactionException | IllegalArgumentException exception) {
            System.out.println("Unable to run fraud detection: " + exception.getMessage());
        }
    }

    private static void viewMonthlyReport(Scanner scanner, ReportGenerator reportGenerator) {
        try {
            String userId = readText(scanner, "User ID: ");
            reportGenerator.generateMonthlySummary(userId);
        } catch (IllegalArgumentException exception) {
            System.out.println("Unable to generate report: " + exception.getMessage());
        }
    }

    private static void viewFlaggedTransactions(Scanner scanner, ReportGenerator reportGenerator) {
        try {
            String userId = readText(scanner, "User ID: ");
            reportGenerator.generateFlaggedReport(userId);
        } catch (IllegalArgumentException exception) {
            System.out.println("Unable to generate flagged report: " + exception.getMessage());
        }
    }

    private static int readInteger(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please enter a whole number.");
        }
    }

    private static double readDouble(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please enter a valid amount.");
        }
    }

    private static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be blank.");
        }
        return input;
    }

    private static LocalDateTime readDateTime(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use timestamp format yyyy-MM-ddTHH:mm.");
        }
    }

    private static void saveTransactions(FileStorage fileStorage, TransactionManager transactionManager) {
        try {
            fileStorage.saveTransactions(transactionManager.getAllTransactions());
        } catch (Exception exception) {
            System.out.println("Unable to save transactions: " + exception.getMessage());
        }
    }
}
