package com.expensetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists transactions in a readable CSV file.
 */
public class FileStorage {
    private static final String DEFAULT_FILE_NAME = "transactions.csv";
    private final Path filePath;

    /**
     * Creates storage using the default transactions CSV file.
     */
    public FileStorage() {
        this(Paths.get(DEFAULT_FILE_NAME));
    }

    /**
     * Creates storage using a specified file path.
     *
     * @param filePath path of the CSV file
     */
    public FileStorage(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null.");
        }
        this.filePath = filePath;
    }

    /**
     * Saves transactions to the CSV file, replacing its previous contents.
     *
     * @param transactions transactions to save
     * @throws IOException if the file cannot be written
     */
    public void saveTransactions(List<Transaction> transactions) throws IOException {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions cannot be null.");
        }
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("id,userId,amount,category,timestamp,description");
            writer.newLine();
            for (Transaction transaction : transactions) {
                if (transaction == null) {
                    continue;
                }
                writer.write(toCsvRow(transaction));
                writer.newLine();
            }
        }
    }

    /**
     * Loads transactions from the CSV file.
     *
     * @return loaded transactions, or an empty list when the file does not exist
     * @throws IOException if the file cannot be read or contains malformed data
     */
    public List<Transaction> loadTransactions() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return transactions;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null) {
                return transactions;
            }
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> fields = parseCsvLine(line);
                if (fields.size() != 6) {
                    throw new IOException("Malformed transaction row: " + line);
                }
                try {
                    transactions.add(new Transaction(
                            Integer.parseInt(fields.get(0)),
                            fields.get(1),
                            Double.parseDouble(fields.get(2)),
                            fields.get(3),
                            LocalDateTime.parse(fields.get(4)),
                            fields.get(5)));
                } catch (RuntimeException exception) {
                    throw new IOException("Malformed transaction row: " + line, exception);
                }
            }
        }
        return transactions;
    }

    private String toCsvRow(Transaction transaction) {
        return String.join(",",
                escapeCsv(String.valueOf(transaction.getId())),
                escapeCsv(transaction.getUserId()),
                escapeCsv(String.valueOf(transaction.getAmount())),
                escapeCsv(transaction.getCategory()),
                escapeCsv(transaction.getTimestamp().toString()),
                escapeCsv(transaction.getDescription()));
    }

    private String escapeCsv(String value) {
        String safeValue = value == null ? "" : value;
        if (safeValue.contains(",") || safeValue.contains("\"")
                || safeValue.contains("\n") || safeValue.contains("\r")) {
            return '"' + safeValue.replace("\"", "\"\"") + '"';
        }
        return safeValue;
    }

    private List<String> parseCsvLine(String line) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean insideQuotes = false;
        for (int index = 0; index < line.length(); index++) {
            char currentCharacter = line.charAt(index);
            if (currentCharacter == '"') {
                if (insideQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    field.append('"');
                    index++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (currentCharacter == ',' && !insideQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(currentCharacter);
            }
        }
        if (insideQuotes) {
            throw new IOException("Unclosed quoted CSV field.");
        }
        fields.add(field.toString());
        return fields;
    }
}
