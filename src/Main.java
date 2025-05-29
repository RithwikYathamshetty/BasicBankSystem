package src;
import javax.swing.SwingUtilities;

public class Main {
    private static final String USERS_FILE = "data/users.txt";
    private static final String ACCOUNTS_FILE = "data/accounts.txt";

    public static void main(String[] args) {
        BankingSystem bank = new BankingSystem();
        bank.loadUsersFromFile(USERS_FILE);
        bank.loadAccountsFromFile(ACCOUNTS_FILE);

        // Ensure admin user exists
        if (bank.authenticate("admin", "admin123") == null) {
            System.out.println("Creating default admin user...");
            bank.addUser(new User("admin", BankingSystem.hashPassword("admin123"), "admin", "0000"));
            bank.createAccount("0000", 0);
            bank.saveUsersToFile(USERS_FILE);
            bank.saveAccountsToFile(ACCOUNTS_FILE);
        }

        SwingUtilities.invokeLater(() -> new BankingSystemGUI(bank));
    }
}
