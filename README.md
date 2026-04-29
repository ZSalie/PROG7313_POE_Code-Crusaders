

# BudJET

BudJET is an Android budgeting application designed to help users track expenses, manage spending goals, and store expense information locally. The app uses a local Room database so that user, expense, goal, and photo-path data can be saved offline.

## Github repo
https://github.com/ZSalie/PROG7313_POE_Code-Crusaders.git

## YouTube Link
https://www.youtube.com/watch?v=15Uid-oOeiI

## Features

### User Login

Users can log into the app using a username and password.

The login system checks the entered details against the locally stored user records. After a successful login, the user is taken to the wallet/dashboard screen.

### User Registration

Users can create an account by entering their name, email, and password.

The account details are saved locally in the Room database and can later be used for login.

### Expense Entry Creation

Users can create an expense entry by entering the required expense details, including:

- Category
- Amount
- Description
- Date
- Start time
- End time

Each expense is saved to the local database and linked to the current user.

### Expense Categories

Users can assign each expense to a category.

Examples of categories used in the app include:

- Groceries
- Entertainment
- Clothing
- Maintenance
- Utilities
- Travel

These categories help organise expenses and allow the user to filter or summarise spending.

### Optional Expense Photo

Users can optionally attach a photo to an expense entry.

The app supports:

- Taking a photo using the camera
- Selecting an existing image from the gallery

The photo is saved using the app’s private file storage, and the saved image path is stored with the expense record in the database.

If a photo exists for an expense, it can be shown in the expense list as a thumbnail.

### Minimum and Maximum Monthly Goals

Users can set a minimum and maximum monthly spending goal.

The goal screen allows the user to enter:

- Minimum monthly spending goal
- Maximum monthly spending goal

The app then compares the user’s current monthly spending against these values and displays whether the user is:

- Below the minimum goal
- Within the goal range
- Over the maximum goal

The goal progress is also shown using a progress bar.

### Expense List

Users can view a list of expenses they have created.

Each expense item displays important details such as:

- Category
- Description
- Date
- Amount
- Photo thumbnail, if one was saved

This allows users to review their recorded expenses in one place.

### View Expenses by User-Selectable Period

Users can select a start date and end date to view expenses within a specific period.

The app filters the expense list based on the selected date range.

This helps users review spending for a chosen day, week, month, or custom period.

### Category Totals for a Selected Period

The app can show the total number of expense entries for each category during a selected date range.

This helps users understand how their spending is distributed across different categories within a specific period.

### Local Offline Database

All important app data is saved locally using Room, which is built on SQLite.

The local database stores:

- Users
- Expenses
- Goals
- Expense photo paths

Because the app uses local storage, the main features can work offline without requiring an internet connection.

## Technologies Used

- Kotlin
- Android Studio
- Room Database
- SQLite
- RecyclerView
- Material Components
- FileProvider
- Android Camera and Gallery Intents
- JUnit unit testing

## Testing

The app includes unit tests for important logic, such as:

- Signup validation
- Expense validation
- Goal validation
- Goal status calculation
- Goal progress calculation
- Photo path validation

These tests help confirm that the app’s core logic works correctly before running the full Android application.

## Project Structure

```text
com.example.budjet
│
├── data
│   ├── AppDatabase.kt
│   ├── BudJetDao.kt
│   ├── User.kt
│   ├── Expense.kt
│   └── Goal.kt
│
├── MainActivity.kt
├── LoginActivity.kt
├── WalletActivity.kt
├── AddExpenseActivity.kt
├── ExpenseListActivity.kt
├── GoalActivity.kt
├── GoalUtils.kt
└── ExpensePhotoUtils.kt