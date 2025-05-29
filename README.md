# 💳 BasicBankSystem

A Java-based banking system with secure user login, transaction handling, account management, and a user-friendly GUI built using Swing. Designed for learning, resume showcasing, and placement purposes.

---

## 📂 Project Structure

```
BasicBankSystem/
├── src/                  # Java source code files
│   ├── Account.java
│   ├── BankingSystem.java
│   ├── BankingSystemGUI.java
│   ├── Main.java
│   └── User.java
├── bin/                  # Compiled .class files (not tracked by Git)
├── data/                 # User and account data files
│   ├── accounts.txt
│   ├── users.txt
│   └── transactions/     # Transaction log files
├── output_images/        # GUI and feature screenshots
│   ├── login.png
│   ├── gui.png
│   └── transactions_view.png
├── .gitignore
└── README.md
```

---

## 🚀 How to Run

### 🔧 Compile the Java files
```bash
javac -d bin *.java
```

### ▶️ Run the application
```bash
java -cp bin Main
```
---
Make sure:
- Java JDK 8 or higher is installed
- The `data/` folder exists with `accounts.txt`, `users.txt`, and a `transactions/` subfolder

---

## 🛠 Features

- 🔐 **User Authentication** — Secure login with SHA-256 password hashing
- 💰 **Account Operations** — Deposit, withdraw, and transfer between accounts
- 📜 **Transaction Logging** — All activity logged in `/data/transactions/`
- 📈 **Interest Calculation** — Optionally adds periodic interest
- 🖥️ **GUI with Swing** — Clean and interactive interface
- 👥 **Admin & User Roles** — Multiple user types supported

---

## 🖼️ Screenshots

### 🔐 Login Interface  
![Login](output_images/login.png)

### 🏦 Main GUI Interface  
![GUI](output_images/gui.png)

### 📜 Transaction Logs  
![Transactions](output_images/transactions_view.png)

## 📚 Technologies Used

- Java (JDK 8+)
- Java Swing
- File I/O
- SHA-256 (Java `MessageDigest`)
- Object-Oriented Design
- 
## 🙋‍♂️ Authors
Developed by:
- Rithwik Yathamshetty
**Institution**: SR University
