# Smart Expense & Fraud-Pattern Detector

A pure Java console application for tracking expenses and identifying suspicious transactions using rule-based anomaly detection.

The application uses only standard JDK classes for production code. It does not require external libraries to compile or run.

## Project Structure

```text
Java_Project2/
├── README.md
├── src/
│   └── com/
│       └── expensetracker/
│           ├── AnomalyDetector.java
│           ├── FileStorage.java
│           ├── FlaggedTransaction.java
│           ├── InvalidTransactionException.java
│           ├── Main.java
│           ├── ReportGenerator.java
│           ├── Transaction.java
│           ├── TransactionManager.java
│           └── User.java
└── test/
    └── com/
        └── expensetracker/
            └── TransactionManagerTest.java
```

All production classes are in the single package:

```java
com.expensetracker
```

## Application Features

- Add expense transactions
- View all saved transactions
- Filter transactions by user, category, or date range
- Detect duplicate transactions
- Detect unusually large spending spikes
- Detect transactions made between midnight and 5 AM
- Generate monthly spending summaries
- Generate flagged-transaction reports
- Save and load data from a readable CSV file
- Validate user input without crashing the application

## Class Walkthrough

### Transaction.java

`Transaction` is the basic data model for one expense.

It contains:

- `id` - unique transaction identifier
- `userId` - identifier of the user who made the purchase
- `amount` - expense amount
- `category` - category such as Food, Travel, or Utilities
- `timestamp` - date and time of the transaction
- `description` - optional explanation of the expense

The class provides:

- A full constructor
- Getters and setters for every field
- A readable `toString()` implementation
- Javadocs for its public API

### User.java

`User` represents a user and owns a list of transactions.

It contains:

- `userId`
- `name`
- `List<Transaction> transactions`

It supports:

- Adding transactions
- Removing transactions
- Reading the transaction list

The transaction list is returned as an unmodifiable view so callers cannot replace or directly mutate the internal collection.

### TransactionManager.java

`TransactionManager` owns the application transaction collection and provides transaction operations.

Supported operations:

- `addTransaction`
- `deleteTransaction`
- `updateTransaction`
- `getAllTransactions`
- `getTransactionsByUser`
- `getTransactionsByCategory`
- `getTransactionsByDateRange`

Validation includes:

- Transactions cannot be null
- Amounts cannot be negative
- Amounts must be finite numbers
- User IDs cannot be blank
- Categories cannot be blank
- Timestamps are required
- Date ranges cannot be reversed
- Transaction IDs must be unique

### InvalidTransactionException.java

`InvalidTransactionException` is the custom checked exception used when transaction input is invalid or a requested transaction cannot be found.

Examples of situations that produce this exception:

- Negative amount
- Blank category
- Missing timestamp
- Duplicate transaction ID
- Missing transaction during deletion or update
- Invalid date range

### AnomalyDetector.java

`AnomalyDetector` applies the fraud-detection rules.

#### Duplicate detection

`detectDuplicates` flags transactions with:

- The same user
- The same amount
- The same category
- Different timestamps within five minutes

Duplicate transactions receive a risk score of `8`.

The duplicate window can be configured using the constructor that accepts a `Duration`.

#### Spike detection

`detectSpikes` compares a transaction with historical transactions from the same user and category.

The threshold is:

```text
mean + 2 * standard deviation
```

A transaction above this threshold is flagged as a spending spike and receives a risk score of `6`.

At least two historical transactions are required to establish a baseline. The transaction being tested is excluded from its own historical baseline.

#### Odd timing detection

`detectOddTiming` flags transactions occurring from midnight up to, but not including, 5:00 AM.

Odd-timing transactions receive a risk score of `3`.

#### Statistical helpers

The detector also provides:

- `calculateMean`
- `calculateStdDev`

The standard deviation calculation uses population standard deviation.

### FlaggedTransaction.java

`FlaggedTransaction` wraps a suspicious transaction with the information explaining why it was flagged.

It contains:

- The original `Transaction`
- A reason such as `DUPLICATE`, `SPIKE`, or `ODD_TIMING`
- A numeric risk score

Configured risk scores are:

| Reason | Score |
|---|---:|
| `DUPLICATE` | 8 |
| `SPIKE` | 6 |
| `ODD_TIMING` | 3 |

### ReportGenerator.java

`ReportGenerator` prints formatted console reports.

#### Monthly summary

`generateMonthlySummary` displays:

- The current month
- User ID
- Total spending
- Spending grouped by category
- The top three categories by spending

#### Flagged report

`generateFlaggedReport` displays:

- Transaction ID
- Timestamp
- Category
- Detection reason
- Risk score

The report combines results from all three anomaly rules.

### FileStorage.java

`FileStorage` saves and loads transactions using a readable CSV file named:

```text
transactions.csv
```

The CSV header is:

```text
id,userId,amount,category,timestamp,description
```

The class supports:

- Saving all transactions
- Loading existing transactions
- Creating missing parent directories
- Handling commas and quotation marks in CSV fields
- Returning an empty list when no data file exists

### Main.java

`Main` is the console entry point.

The application performs these steps:

1. Loads existing transactions from `transactions.csv`.
2. Creates the transaction manager, anomaly detector, and report generator.
3. Displays the command-line menu.
4. Reads and validates user input.
5. Executes the selected operation.
6. Saves transactions when the application exits.

The menu options are:

```text
1. Add transaction
2. View all transactions
3. Run fraud detection
4. View monthly report
5. View flagged transactions
6. Exit
```

Invalid menu choices and malformed data are handled with messages instead of terminating the application.

## How To Compile

Open PowerShell in the project root:

```powershell
cd "C:\Users\LENOVO\OneDrive\Documents\Java_Project2"
```

Compile all production classes:

```powershell
javac -Xlint:all -d out src\com\expensetracker\*.java
```

The compiled `.class` files will be written to the `out` directory.

## How To Run

```powershell
java -cp out com.expensetracker.Main
```

The application will display the menu and wait for input.

## Manual Walkthrough

### Step 1: Add a normal transaction

Choose option `1` and enter values similar to:

```text
Transaction ID: 1
User ID: user-1
Amount: 10.00
Category: Food
Timestamp: 2026-09-15T12:00
Description: Lunch
```

### Step 2: Add a possible duplicate

Add another transaction with the same amount and category within five minutes:

```text
Transaction ID: 2
User ID: user-1
Amount: 10.00
Category: Food
Timestamp: 2026-09-15T12:03
Description: Second lunch purchase
```

This pair should produce `DUPLICATE` flags.

### Step 3: Add historical transactions and a spike

Add two ordinary historical transactions:

```text
Transaction ID: 3
User ID: user-1
Amount: 10.00
Category: Food
Timestamp: 2026-09-16T12:00
Description: Dinner
```

```text
Transaction ID: 4
User ID: user-1
Amount: 12.00
Category: Food
Timestamp: 2026-09-17T12:00
Description: Dinner
```

Then add an unusually large transaction:

```text
Transaction ID: 5
User ID: user-1
Amount: 100.00
Category: Food
Timestamp: 2026-09-18T12:00
Description: Large restaurant bill
```

This should produce a `SPIKE` flag.

### Step 4: Add an odd-time transaction

Add a transaction between midnight and 5 AM:

```text
Transaction ID: 6
User ID: user-1
Amount: 25.00
Category: Travel
Timestamp: 2026-09-18T02:30
Description: Late-night ride
```

This should produce an `ODD_TIMING` flag.

### Step 5: View transactions

Choose option `2` to print all saved transactions.

### Step 6: Run detection

Choose option `3`, enter:

```text
user-1
```

The application prints counts for duplicate, spike, and odd-timing flags.

### Step 7: View the monthly summary

Choose option `4` and enter `user-1`.

The report shows total spending, spending by category, and the top three categories.

### Step 8: View flagged transactions

Choose option `5` and enter `user-1`.

The report shows each flagged transaction, its reason, and its risk score.

### Step 9: Exit and save

Choose option `6`.

Transactions are saved automatically to:

```text
transactions.csv
```

## Testing

The test file is:

```text
test/com/expensetracker/TransactionManagerTest.java
```

It contains tests for:

- Adding a transaction through `TransactionManager`
- Detecting a large historical spending outlier

The tests use JUnit 5. The production application itself uses only standard JDK APIs.

If JUnit 5 is available on the classpath, compile the tests with a command similar to:

```powershell
javac -cp "junit-platform-console-standalone.jar;out" -d out-test test\com\expensetracker\TransactionManagerTest.java
```

Run them with:

```powershell
java -jar junit-platform-console-standalone.jar execute --class-path out;out-test --scan-class-path
```

The exact JUnit command depends on where the JUnit standalone JAR is installed.

## Error Handling

The application handles errors in several layers:

- Input parsing catches invalid numbers and timestamps.
- Transaction validation throws `InvalidTransactionException`.
- The CLI catches validation errors and continues running.
- File loading errors produce a message and start with an empty transaction list.
- File saving errors are reported when the application exits.
- Missing or empty CSV files are treated as having no transactions.

## Design Principles

The project follows a modular design:

- Data models store data.
- `TransactionManager` handles transaction operations and validation.
- `AnomalyDetector` handles fraud rules and statistics.
- `ReportGenerator` handles presentation of reports.
- `FileStorage` handles persistence.
- `Main` handles user interaction and application flow.

This separation keeps each class focused on one primary responsibility and avoids placing all behavior in the main class.

## Current Verification

The production code has been compiled successfully with:

```powershell
javac -Xlint:all -d out src\com\expensetracker\*.java
```

The console startup and exit flow has also been smoke-tested successfully.
