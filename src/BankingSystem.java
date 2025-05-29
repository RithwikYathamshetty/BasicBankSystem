package src;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class BankingSystem {
    public Map<String, User> users = new HashMap<>();
    public Map<String, Account> accounts = new HashMap<>();
    private static final String TRANSACTIONS_DIR = "data/transactions/";

    // Password hashing with SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Load users from file
    public void loadUsersFromFile(String filename) {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                    users.put(parts[0], new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Users file not found or error reading. Starting fresh.");
        }
    }

    // Save users to file
    public void saveUsersToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User u : users.values()) {
                bw.write(u.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }

    // Load accounts from file
    public void loadAccountsFromFile(String filename) {
        accounts.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    accounts.put(parts[0], new Account(parts[0], Double.parseDouble(parts[1])));
                }
            }
        } catch (IOException e) {
            System.out.println("Accounts file not found or error reading. Starting fresh.");
        }
    }

    // Save accounts to file
    public void saveAccountsToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Account acc : accounts.values()) {
                bw.write(acc.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts.");
        }
    }

    // Authenticate user by username and password
    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            if (user.getPasswordHash().equals(hashPassword(password))) {
                return user;
            }
        }
        return null;
    }

    // Check if username exists
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    // Get account by account number
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Add a new user
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    // Create a new account with balance
    public void createAccount(String accNum, double balance) {
        accounts.put(accNum, new Account(accNum, balance));
    }

    // Delete an account by account number
    public boolean deleteAccount(String accNum) {
        if (accounts.containsKey(accNum)) {
            accounts.remove(accNum);
            // Also delete users linked to this account
            users.values().removeIf(u -> accNum.equals(u.getAccountNumber()));
            // Delete transaction file if exists
            File txnFile = new File(TRANSACTIONS_DIR + accNum + ".txt");
            if (txnFile.exists()) txnFile.delete();
            saveUsersToFile("data/users.txt");
            saveAccountsToFile("data/accounts.txt");
            return true;
        }
        return false;
    }

    // Deposit amount into account
    public boolean deposit(String accNum, double amount) {
        Account acc = accounts.get(accNum);
        if (acc != null && amount > 0) {
            acc.deposit(amount);
            saveAccountsToFile("data/accounts.txt");
            logTransaction(accNum, "Deposit", amount);
            return true;
        }
        return false;
    }

    // Withdraw amount from account
    public boolean withdraw(String accNum, double amount) {
        Account acc = accounts.get(accNum);
        if (acc != null && amount > 0) {
            if (acc.withdraw(amount)) {
                saveAccountsToFile("data/accounts.txt");
                logTransaction(accNum, "Withdraw", amount);
                return true;
            }
        }
        return false;
    }

    // Log transaction to file
    public void logTransaction(String accNum, String type, double amount) {
        try {
            File dir = new File(TRANSACTIONS_DIR);
            if (!dir.exists()) dir.mkdirs();
            String filePath = TRANSACTIONS_DIR + accNum + ".txt";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
                String log = String.format("%s - %s: Rs.%.2f", new Date(), type, amount);
                bw.write(log);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error logging transaction.");
        }
    }

    // Show transaction history for account
    public List<String> getTransactionHistory(String accNum) {
        List<String> history = new ArrayList<>();
        File file = new File(TRANSACTIONS_DIR + accNum + ".txt");
        if (!file.exists()) return history;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                history.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions.");
        }
        return history;
    }

    // Change password for user
    public void changePassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            user.setPasswordHash(hashPassword(newPassword));
            saveUsersToFile("data/users.txt");
        }
    }

    // Apply monthly interest to all accounts
    // rate: annual interest rate in percentage (e.g. 5 means 5%)
    public void applyMonthlyInterest(double annualRatePercent) {
        double monthlyRate = annualRatePercent / 12 / 100;
        for (Account acc : accounts.values()) {
            double interest = acc.getBalance() * monthlyRate;
            acc.deposit(interest);
            logTransaction(acc.getAccountNumber(), "Interest", interest);
        }
        saveAccountsToFile("data/accounts.txt");
    }
}
