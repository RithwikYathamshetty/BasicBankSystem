package src;
public class User {
    private String username;
    private String passwordHash;
    private String role;
    private String accountNumber;

    public User(String username, String passwordHash, String role, String accountNumber) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return username + "," + passwordHash + "," + role + "," + accountNumber;
    }
}
