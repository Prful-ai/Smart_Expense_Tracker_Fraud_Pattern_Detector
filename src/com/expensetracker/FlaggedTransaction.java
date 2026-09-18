package com.expensetracker;

/**
 * Represents a transaction identified by an anomaly rule.
 */
public class FlaggedTransaction {
    private final Transaction transaction;
    private final String reason;
    private final int riskScore;

    /**
     * Creates a flagged transaction.
     *
     * @param transaction transaction identified by a rule
     * @param reason anomaly reason
     * @param riskScore numeric risk score assigned to the reason
     */
    public FlaggedTransaction(Transaction transaction, String reason, int riskScore) {
        this.transaction = transaction;
        this.reason = reason;
        this.riskScore = riskScore;
    }

    /**
     * Returns the wrapped transaction.
     *
     * @return wrapped transaction
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Returns the anomaly reason.
     *
     * @return anomaly reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the risk score.
     *
     * @return risk score
     */
    public int getRiskScore() {
        return riskScore;
    }

    /**
     * Returns a readable representation of the flagged transaction.
     *
     * @return formatted flagged transaction details
     */
    @Override
    public String toString() {
        return "FlaggedTransaction{" +
                "transaction=" + transaction +
                ", reason='" + reason + '\'' +
                ", riskScore=" + riskScore +
                '}';
    }
}
