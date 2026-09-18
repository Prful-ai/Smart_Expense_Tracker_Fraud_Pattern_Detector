# Smart Expense & Fraud-Pattern Detector

## Created Project Files

This document lists the files created for the project and explains the responsibility of each one.

## Production Source Files

All production classes are located in:

```text
src/com/expensetracker/
```

| File | Purpose |
|---|---|
| `Transaction.java` | Data model representing one expense transaction with ID, user ID, amount, category, timestamp, and description. |
| `User.java` | Represents a user and owns a list of the user's transactions. |
| `TransactionManager.java` | Provides transaction CRUD operations, filtering, and validation. |
| `InvalidTransactionException.java` | Custom checked exception for invalid transaction data and missing transactions. |
| `AnomalyDetector.java` | Detects duplicate transactions, spending spikes, and transactions made during unusual hours. |
| `FlaggedTransaction.java` | Wraps a suspicious transaction with its reason and risk score. |
| `ReportGenerator.java` | Generates monthly spending summaries and flagged-transaction reports. |
| `FileStorage.java` | Saves and loads transaction data from the readable `transactions.csv` file. |
| `Main.java` | Console application entry point and menu controller. |

## Test Files

| File | Purpose |
|---|---|
| `test/com/expensetracker/TransactionManagerTest.java` | JUnit tests for adding transactions and detecting spending spikes. |

## Documentation Files

| File | Purpose |
|---|---|
| `README.md` | Complete project walkthrough, architecture explanation, usage instructions, manual test scenario, and JUnit guidance. |
| `statement.md` | Formal project problem statement, objectives, proposed solution, and expected outcome. |
| `PROJECT_FILES.md` | Inventory of all created project files and their responsibilities. |

## Generated and Runtime Files

| File or Folder | Purpose |
|---|---|
| `out/` | Directory containing compiled Java `.class` files. |
| `transactions.csv` | Runtime data file created when the application saves transactions on exit. |

`transactions.csv` is created automatically by the application if transactions are added and the program exits successfully. It may not exist yet if the application has only been started and exited without adding data.

## Package

All production Java classes use:

```java
package com.expensetracker;
```

## Compile the Application

From the project root, run:

```powershell
javac -Xlint:all -d out src\com\expensetracker\*.java
```

## Run the Application

```powershell
java -cp out com.expensetracker.Main
```

## Application Menu

```text
1. Add transaction
2. View all transactions
3. Run fraud detection
4. View monthly report
5. View flagged transactions
6. Exit
```

## Detection Rules

### Duplicate Transactions

Transactions are flagged when they have the same user, amount, and category within the configured five-minute time window.

Risk score: `8`

### Spending Spikes

A transaction is flagged when its amount is greater than:

```text
mean + 2 * standard deviation
```

Risk score: `6`

### Odd Timing

Transactions made between midnight and 5:00 AM are flagged as unusual.

Risk score: `3`

## Validation and Error Handling

The application validates:

- Null transactions
- Negative amounts
- Infinite and `NaN` amounts
- Blank user IDs
- Blank categories
- Missing timestamps
- Invalid date ranges
- Duplicate transaction IDs
- Invalid numeric input
- Invalid timestamp input

The command-line application catches errors and continues running instead of terminating unexpectedly.

## Verification Status

The production source has been compiled successfully using:

```powershell
javac -Xlint:all -d out src\com\expensetracker\*.java
```

The application startup and exit flow has also been smoke-tested successfully.

The JUnit test source is included, but JUnit must be available separately to execute the tests.
