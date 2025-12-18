import java.util.Scanner;

/* =========================
   CUSTOM EXCEPTIONS
   ========================= */
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String msg) {
        super(msg);
    }
}

class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}

/* =========================
   ABSTRACT CLASS
   ========================= */
abstract class BankAccount {
    private int accountNumber;
    private double balance;

    public BankAccount(int accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Encapsulation
    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // Abstraction
    public abstract void calculateInterest();
}

/* =========================
   INHERITANCE + POLYMORPHISM
   ========================= */
class SavingsAccount extends BankAccount {

    public SavingsAccount(int accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    public void calculateInterest() {
        double interest = getBalance() * 0.04;
        setBalance(getBalance() + interest);
        System.out.println("Interest added successfully!");
    }
}

/* =========================
   INTERFACE
   ========================= */
interface BankService {
    void deposit(double amount);
    void withdraw(double amount) throws InsufficientBalanceException;
    void checkBalance();
}

/* =========================
   USER CLASS (LOGIN SECURITY)
   ========================= */
class User {
    private String username;
    private String password;
    private int attemptsLeft = 3;
    private boolean locked = false;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean login(String u, String p) throws InvalidCredentialsException {
        if (locked) {
            throw new InvalidCredentialsException(
                    "Account locked due to multiple failed attempts!");
        }

        if (username.equals(u) && password.equals(p)) {
            attemptsLeft = 3; // reset attempts
            return true;
        } else {
            attemptsLeft--;
            if (attemptsLeft == 0) {
                locked = true;
                throw new InvalidCredentialsException(
                        "Account locked! Too many wrong login attempts.");
            }
            throw new InvalidCredentialsException(
                    "Invalid credentials! Attempts left: " + attemptsLeft);
        }
    }
}

/* =========================
   SERVICE IMPLEMENTATION
   ========================= */
class BankServiceImpl implements BankService {
    private BankAccount account;

    public BankServiceImpl(BankAccount account) {
        this.account = account;
    }

    @Override
    public void deposit(double amount) {
        account.setBalance(account.getBalance() + amount);
        System.out.println("Deposit successful!");
    }

    @Override
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > account.getBalance()) {
            throw new InsufficientBalanceException("Insufficient balance!");
        }
        account.setBalance(account.getBalance() - amount);
        System.out.println("Withdrawal successful!");
    }

    @Override
    public void checkBalance() {
        System.out.println("Current Balance: ₹" + account.getBalance());
    }
}

/* =========================
   MAIN CLASS
   ========================= */
public class OnlineBankingSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Default user
        User user = new User("arjun", "1234");

        BankAccount account = new SavingsAccount(101, 5000);
        BankService service = new BankServiceImpl(account);

        boolean loggedIn = false;

        // LOGIN LOOP (3 ATTEMPTS)
        while (!loggedIn) {
            try {
                System.out.print("Enter Username: ");
                String u = sc.next();

                System.out.print("Enter Password: ");
                String p = sc.next();

                if (user.login(u, p)) {
                    System.out.println("\nLogin Successful!\n");
                    loggedIn = true;
                }

            } catch (InvalidCredentialsException e) {
                System.out.println("Login Error: " + e.getMessage());

                if (user.isLocked()) {
                    System.out.println("Please contact bank support.");
                    sc.close();
                    return;
                }
            }
        }

        // MENU
        int choice;
        do {
            System.out.println("\n===== ONLINE BANKING MENU =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Add Interest");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter amount to deposit: ");
                        service.deposit(sc.nextDouble());
                        break;

                    case 2:
                        System.out.print("Enter amount to withdraw: ");
                        service.withdraw(sc.nextDouble());
                        break;

                    case 3:
                        service.checkBalance();
                        break;

                    case 4:
                        account.calculateInterest();
                        break;

                    case 5:
                        System.out.println("Thank you for using Online Banking!");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InsufficientBalanceException e) {
                System.out.println("Transaction Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid input!");
                sc.nextLine(); // clear buffer
            }

        } while (choice != 5);

        sc.close();
        System.out.println("\nSession Ended.");
    }
}
