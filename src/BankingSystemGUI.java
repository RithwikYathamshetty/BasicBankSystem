package src;
import javax.swing.*;

import java.awt.*;
import java.util.List;

public class BankingSystemGUI {
    private BankingSystem bank;
    private JFrame frame;
    private User loggedInUser;

    public BankingSystemGUI(BankingSystem bank) {
        this.bank = bank;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Basic Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);
        showLoginScreen();
        frame.setVisible(true);
    }

    private void showLoginScreen() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel());  // empty cell
        panel.add(new JLabel());
        panel.add(loginBtn);
        panel.add(registerBtn);

        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            User user = bank.authenticate(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            } else {
                loggedInUser = user;
                if ("admin".equalsIgnoreCase(user.getRole())) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            }
        });

        registerBtn.addActionListener(e -> showRegisterScreen());
    }

    private void showRegisterScreen() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField accNumField = new JTextField();

        panel.add(new JLabel("New Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Account Number:"));
        panel.add(accNumField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        panel.add(registerBtn);
        panel.add(backBtn);

        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String accNum = accNumField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || accNum.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.");
                return;
            }

            if (bank.userExists(username)) {
                JOptionPane.showMessageDialog(frame, "Username already exists.");
                return;
            }

            if (bank.getAccount(accNum) == null) {
                bank.createAccount(accNum, 0);
            }

            bank.addUser(new User(username, BankingSystem.hashPassword(password), "user", accNum));
            bank.saveUsersToFile("data/users.txt");
            bank.saveAccountsToFile("data/accounts.txt");
            JOptionPane.showMessageDialog(frame, "Registration successful. You can login now.");
            showLoginScreen();
        });

        backBtn.addActionListener(e -> showLoginScreen());
    }

    private void showAdminMenu() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton viewAccountsBtn = new JButton("View All Accounts");
        JButton deleteAccountBtn = new JButton("Delete Account");
        JButton applyInterestBtn = new JButton("Apply Monthly Interest");
        JButton logoutBtn = new JButton("Logout");

        panel.add(viewAccountsBtn);
        panel.add(deleteAccountBtn);
        panel.add(applyInterestBtn);
        panel.add(logoutBtn);

        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();

        viewAccountsBtn.addActionListener(e -> showAllAccounts());
        deleteAccountBtn.addActionListener(e -> deleteAccountDialog());
        applyInterestBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter annual interest rate (%)", "5");
            try {
                double rate = Double.parseDouble(input);
                if (rate < 0) throw new NumberFormatException();
                bank.applyMonthlyInterest(rate);
                JOptionPane.showMessageDialog(frame, "Interest applied successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        });
        logoutBtn.addActionListener(e -> {
            loggedInUser = null;
            showLoginScreen();
        });
    }

    private void showAllAccounts() {
        StringBuilder sb = new StringBuilder();
        for (Account acc : bank.accounts.values()) {
            sb.append(acc.getAccountNumber()).append(" - Rs.").append(String.format("%.2f", acc.getBalance())).append("\n");
        }
        JOptionPane.showMessageDialog(frame, sb.length() == 0 ? "No accounts found." : sb.toString(), "All Accounts", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAccountDialog() {
        String accNum = JOptionPane.showInputDialog(frame, "Enter account number to delete:");
        if (accNum != null && !accNum.trim().isEmpty()) {
            boolean success = bank.deleteAccount(accNum.trim());
            if (success) {
                JOptionPane.showMessageDialog(frame, "Account deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
        }
    }

    private void showUserMenu() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton checkBalanceBtn = new JButton("Check Balance");
        JButton viewHistoryBtn = new JButton("View Transaction History");
        JButton changePassBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(checkBalanceBtn);
        panel.add(viewHistoryBtn);
        panel.add(changePassBtn);
        panel.add(logoutBtn);

        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();

        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) throw new NumberFormatException();
                boolean success = bank.deposit(loggedInUser.getAccountNumber(), amount);
                JOptionPane.showMessageDialog(frame, success ? "Deposit successful." : "Deposit failed.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount.");
            }
        });

        withdrawBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) throw new NumberFormatException();
                boolean success = bank.withdraw(loggedInUser.getAccountNumber(), amount);
                JOptionPane.showMessageDialog(frame, success ? "Withdrawal successful." : "Insufficient balance or error.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount.");
            }
        });

        checkBalanceBtn.addActionListener(e -> {
            Account acc = bank.getAccount(loggedInUser.getAccountNumber());
            if (acc != null) {
                JOptionPane.showMessageDialog(frame, "Balance: Rs." + String.format("%.2f", acc.getBalance()));
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
        });

        viewHistoryBtn.addActionListener(e -> {
            List<String> history = bank.getTransactionHistory(loggedInUser.getAccountNumber());
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No transactions found.");
            } else {
                JTextArea textArea = new JTextArea();
                history.forEach(line -> textArea.append(line + "\n"));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(350, 200));
                JOptionPane.showMessageDialog(frame, scrollPane, "Transaction History", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        changePassBtn.addActionListener(e -> {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(frame, pf, "Enter new password", JOptionPane.OK_CANCEL_OPTION);
            if (okCxl == JOptionPane.OK_OPTION) {
                String newPass = new String(pf.getPassword());
                if (newPass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty.");
                    return;
                }
                bank.changePassword(loggedInUser.getUsername(), newPass);
                JOptionPane.showMessageDialog(frame, "Password changed successfully.");
            }
        });

        logoutBtn.addActionListener(e -> {
            loggedInUser = null;
            showLoginScreen();
        });
    }
}
