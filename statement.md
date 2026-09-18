# Smart Expense & Fraud-Pattern Detector

## Problem Statement

Individuals and small businesses often fail to notice duplicate charges, unusual spending spikes, or transactions made at irregular times until it is too late. These issues can lead to financial loss, inaccurate records, and poor visibility into spending habits.

Most personal finance tools display totals, balances, and category summaries, but they do not actively flag suspicious transaction patterns. As a result, users may need to manually inspect long transaction lists to identify repeated charges, unexpectedly large expenses, or activity occurring during unusual hours.

The **Smart Expense & Fraud-Pattern Detector** addresses this problem through a Core Java console application that records expense transactions and applies transparent, rule-based anomaly detection. The application identifies possible duplicate transactions, detects spending spikes using the statistical rule `mean + 2 * standard deviation`, and flags transactions occurring between midnight and 5:00 AM as unusual.

The goal is to give individuals and small businesses a simple, readable, and library-free tool for improving expense visibility and discovering potentially suspicious activity early. The application also provides formatted spending reports, risk scores, CSV-based storage, input validation, and a command-line workflow for managing expense data.

## Objectives

- Record and manage expense transactions.
- Prevent invalid transaction data such as negative amounts or missing categories.
- Detect duplicate transactions within a configurable time window.
- Detect unusually large expenses within a user's spending category.
- Identify transactions made during unusual hours.
- Assign understandable reasons and risk scores to flagged transactions.
- Generate monthly summaries and flagged-transaction reports.
- Save and load transaction data using a readable CSV file.
- Keep the implementation modular, maintainable, and based only on the standard Java Development Kit.

## Proposed Solution

The application uses separate Java classes for transaction management, anomaly detection, reporting, file storage, and user interaction. Each transaction contains an identifier, user ID, amount, category, timestamp, and description.

The anomaly detector applies three rules:

1. **Duplicate detection:** flags transactions with the same user, amount, and category when they occur within five minutes of one another.
2. **Spending-spike detection:** compares an expense with historical spending in the same category and flags it when the amount is greater than `mean + 2 * standard deviation`.
3. **Odd-timing detection:** flags transactions occurring between midnight and 5:00 AM.

Each flagged transaction includes a reason and a risk score so users can understand why it was identified and prioritize review.

## Expected Outcome

The completed system will help users review expenses more efficiently, identify suspicious patterns earlier, and gain a clearer understanding of their spending. It is intended as a lightweight educational and practical tool for rule-based expense analysis rather than a replacement for professional fraud-prevention systems.
