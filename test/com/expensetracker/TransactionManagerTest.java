package com.expensetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests transaction management and spike detection behavior.
 */
public class TransactionManagerTest {
    /**
     * Verifies that a valid transaction can be added and retrieved.
     *
     * @throws Exception if transaction validation fails unexpectedly
     */
    @Test
    public void addTransactionStoresTransaction() throws Exception {
        TransactionManager transactionManager = new TransactionManager();
        Transaction transaction = createTransaction(1, 25.0, "Food", LocalDateTime.of(2026, 9, 1, 12, 0));

        transactionManager.addTransaction(transaction);

        assertEquals(1, transactionManager.getAllTransactions().size());
        assertEquals(transaction, transactionManager.getAllTransactions().get(0));
    }

    /**
     * Verifies that an amount above the category's historical threshold is flagged as a spike.
     *
     * @throws Exception if transaction validation fails unexpectedly
     */
    @Test
    public void detectSpikesFlagsLargeHistoricalOutlier() throws Exception {
        List<Transaction> transactions = Arrays.asList(
                createTransaction(1, 10.0, "Food", LocalDateTime.of(2026, 9, 1, 12, 0)),
                createTransaction(2, 10.0, "Food", LocalDateTime.of(2026, 9, 2, 12, 0)),
                createTransaction(3, 100.0, "Food", LocalDateTime.of(2026, 9, 3, 12, 0)));

        List<FlaggedTransaction> flaggedTransactions = new AnomalyDetector().detectSpikes(transactions);

        assertEquals(1, flaggedTransactions.size());
        assertEquals(3, flaggedTransactions.get(0).getTransaction().getId());
        assertTrue(flaggedTransactions.get(0).getReason().equals("SPIKE"));
    }

    private Transaction createTransaction(int id, double amount, String category, LocalDateTime timestamp) {
        return new Transaction(id, "user-1", amount, category, timestamp, "Test transaction");
    }
}
