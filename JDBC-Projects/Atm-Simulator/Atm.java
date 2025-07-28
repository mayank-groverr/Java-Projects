import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Atm {

    // Database Connections Variables and other utility variables
    private static final String url = "jdbc:mysql://localhost:3306/atm";
    private static final String userName = "root";
    private static final String password = "12345";
    private static Connection connection;
    private static final Scanner input = new Scanner(System.in);

    // Instance Variables set to be used in class
    private int acc_no;
    private int pin;
    private int balance;
    private String name;

    // Loading Drivers and Establishing connection with the database
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e);

        }
        try {
            connection = DriverManager.getConnection(url, userName, password);
        } catch (SQLException ex) {
            System.out.println(ex);
        }

    }


    public void actualAtm() {

        System.out.println("Welcome to the Java Learning Bank");


        System.out.print("Please Enter your account no : ");
        int accNo = input.nextInt();
        System.out.print("Please Enter your Pin : ");
        int pinInput = input.nextInt();

        if (authentication(accNo, pinInput)) {
            System.out.println("Login Successful");
            while (true) {


                System.out.println("Press 1 for Deposit");
                System.out.println("Press 2 for Withdraw");
                System.out.println("Press 3 for Checking User Details");
                System.out.println("Press 4 for Check Balance");
                System.out.println("Press 5 for Update Pin");
                System.out.println("Press 6 for Log Out");
                System.out.print("Enter your choice : ");
                int ch = input.nextInt();

                if (ch == 1) {

                    System.out.print("Enter amount to be deposited : ");
                    int amount = input.nextInt();
                    deposit(amount);

                } else if (ch == 2) {

                    System.out.print("Enter amount to withdraw : ");
                    int amount = input.nextInt();
                    withdraw(amount);

                } else if (ch == 3) {

                    getDetails();

                } else if (ch == 4) {

                    getBalance();

                } else if (ch == 5) {

                    updatePin();

                } else if (ch == 6) {

                    closingResources();
                    System.out.println("Logging Out.....");
                    System.out.println("Thanks for visiting");
                    break;

                } else {

                    System.out.println("Invalid choice. Try again!");

                }

            }
        } else {

            System.out.println("Invalid User");

        }

    }


    private boolean authentication(int acc_no, int pin) {
        String query = "Select * from user where acc_no = ? and pin = ?";
        try {
            PreparedStatement check = connection.prepareStatement(query);
            check.setInt(1, acc_no);
            check.setInt(2, pin);
            ResultSet isUser = check.executeQuery();

            if (isUser.next()) {

                this.acc_no = acc_no;
                this.pin = pin;
                this.balance = isUser.getInt("balance");
                this.name = isUser.getString("Name");
                check.close();
                isUser.close();
                return true;

            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return false;
    }


    private void deposit(int amount) {
        if (amount > 0) {
            String query = "Update user set balance = balance + ? where acc_no = ?";

            if (executeQuery(query, amount, acc_no)) {
                balance += amount;
                System.out.printf("Dear %s, your account %d is credited with %d amount updated balance %d\n", name,
                        acc_no, amount, balance);
            } else {
                System.out.println("The Bank server is currently down. Sorry for the inconvenience!");
            }
        } else {
            System.out.println("Enter a valid amount. Try Again!");
        }
    }


    private void withdraw(int amount) {
        if (amount > 0 && balance >= amount) {

            String query = "Update user set balance = balance - ? where acc_no = ?";
            if (executeQuery(query, amount, acc_no)) {
                balance -= amount;
                System.out.printf(
                        "Dear %s, %d amount has been debited from your account %d. Collect your amount. Updated balance %d\n",
                        name, amount, acc_no, balance);
            }

        } else {
            if (amount < 0) {
                System.out.println("Please enter a valid amount in positive figures");
            } else {
                System.out.println("We cannot process your withdrawal, as your account balance is low.");
            }
        }
    }


    private void getDetails() {

        System.out.println("Name : " + name);
        System.out.println("Account Number : " + acc_no);
        System.out.println("Balance : " + balance);

    }


    private void getBalance() {

        System.out.println("Current Account Balance is : " + balance);

    }


    private void updatePin() {
        System.out.print("Enter a 4-Digit Strong Pin : ");
        int newPin = input.nextInt();

        if (newPin == pin) {
            System.out.println("Please enter something different than older one");

        } else {
            if (pinChecker(newPin)) {
                String query = "Update user set pin = ? where acc_no = ?";
                if (executeQuery(query, newPin, acc_no)) {
                    pin = newPin;
                    System.out.println("Pin Updated Successfully");
                } else {
                    System.out.println("Bank Server is Busy currently! ");
                }
            } else {
                System.out.println("Kindly Enter 4 digit valid pin");
            }

        }
    }

    private void closingResources() {
        acc_no = 0;
        balance = 0;
        pin = 0;
        name = null;
        try {
            connection.close();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        input.close();

    }

    private boolean pinChecker(int pin) {
        return (pin >= 1000 && pin <= 9999);
    }

    private boolean executeQuery(String query, int... param) {
        try {
            PreparedStatement dbQuery = connection.prepareStatement(query);
            for (int i = 0; i < param.length; i++) {
                dbQuery.setInt(i+1, param[i]);
            }
            dbQuery.executeUpdate();
            dbQuery.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

}
