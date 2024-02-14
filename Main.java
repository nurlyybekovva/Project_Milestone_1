package org.example;
import java.sql.*;
import java.util.Scanner;
import java.sql.Date;
import java.util.Date.*;

public class Main {
    public static void main(String[] args) throws Exception{
        String URL = "jdbc:postgresql://localhost:5432/financial+db";
        String USER = "postgres";
        String PASS = "231481";

        Connection connection = DriverManager.getConnection(URL, USER, PASS);
        Scanner scan = new Scanner(System.in);
        createTable(connection);
        while(true){
            System.out.println("⋆｡‧˚ʚ♡ɞ˚‧｡⋆MENU⋆｡‧˚ʚ♡ɞ˚‧｡⋆");
            System.out.println("1. View all financial transactions");
            System.out.println("2. Add a new financial transaction");
            System.out.println("3. Income and expense report for the selected period");
            System.out.println("4. Delete or update certain existing transactions");
            System.out.println("5. Bank account balance");
            System.out.println("6. Exit");

            System.out.println("Enter your choice: ");
            int choice = scan.nextInt();
            if (choice == 1){
                viewTransactions(connection);
            }
            if (choice == 2) {
                System.out.println("Enter the transaction type(income/expense): ");
                String type = scan.next();
                System.out.println("Enter the transaction amount: ");
                Double amount = scan.nextDouble();
                System.out.println("Enter the transaction category: ");
                String category = scan.next();
                System.out.println("Enter the date of the transaction(YYYY-MM-DD): ");
                String transactionDate = scan.next();

                addTransactions(connection, type, amount, category, transactionDate);
                System.out.println("♡ Successfully ♡");
            }
            if (choice == 3){
                System.out.println("~Enter the period for which you want to generate a Transaction Report~");
                System.out.println("Enter the start day: ");
                String startDate = scan.next();
                System.out.println("Enter the end date: ");
                String endDate = scan.next();
                generateReport(connection, startDate, endDate);
            }
            if (choice == 4) {
                System.out.println("--Delete or update certain existing transactions--");
                System.out.println("1. Delete");
                System.out.println("2. Update");

                int choose0 = scan.nextInt();
                if (choose0 == 1){
                    System.out.println("Enter id of the transaction you want to delete: ");
                    int id = scan.nextInt();
                    delTransactions(connection, id);
                    System.out.println("♡ Successfully ♡");
                }
                if (choose0 == 2){
                    System.out.println("Enter the transaction details to update: ");
                    System.out.println("Enter the ID: ");
                    int id = scan.nextInt();
                    System.out.println("Enter the transaction type(income/expense): ");
                    String type = scan.next();
                    System.out.println("Enter the transaction amount: ");
                    Double amount = scan.nextDouble();
                    System.out.println("Enter the transaction category: ");
                    String category = scan.next();
                    System.out.println("Enter the date of the transaction(YYYY-MM-DD): ");
                    String transactionDate = scan.next();
                    updTransactions(connection, id, type, amount, category, transactionDate);
                    System.out.println("♡ Successfully ♡");
                }
            }
            if (choice == 5){
                balance(connection);
            }
            if (choice == 6){
                System.out.println("Good bye!");
                break;
            }
        }

    }

    //create table
    public static void createTable(Connection connection) throws Exception{
        Statement statement = connection.createStatement();
        String cr = "create table if not exists finance (id serial primary key, type varchar(30), amount real, category varchar(30), transactionDate date)";
        statement.executeUpdate(cr);
    }
    // add
    public static void addTransactions(Connection connection, String type, Double amount, String category, String transactionDate) throws Exception{
        PreparedStatement add = connection.prepareStatement("insert into finance (type, amount, category, transactionDate) values (?, ?, ?, ?)");

        add.setString(1, type);
        add.setDouble(2, amount);
        add.setString(3, category);
        add.setDate(4, Date.valueOf(transactionDate));
        add.executeUpdate();
    }
    // delete
    public static void delTransactions(Connection connection, int id) throws Exception{
        PreparedStatement del = connection.prepareStatement("delete from finance where id = ?");
        del.setInt(1, id);
        del.executeUpdate();
    }
    // update
    public static void updTransactions(Connection connection, int id, String type, Double amount, String category, String transactionDate) throws Exception{
        PreparedStatement upd = connection.prepareStatement("update finance set type = ?, amount = ?, category = ?, transactionDate = ? where id = ?");
        upd.setString(1, type);
        upd.setDouble(2, amount);
        upd.setString(3, category);
        upd.setDate(4, Date.valueOf(transactionDate));
        upd.setInt(5, id);
        upd.executeUpdate();
    }
    // read
    public static void viewTransactions(Connection connection) throws Exception{
        String str = "select * from finance";
        Statement view = connection.createStatement();
        ResultSet results = view.executeQuery(str);
        while (results.next()){
            int id = results.getInt("id");
            String type = results.getString("type");
            Double amount = results.getDouble("amount");
            String category = results.getString("category");
            Date transactionDate = results.getDate("transactionDate");
            System.out.println("ID: " + id + ", Type: " + type + ", Amount: " + amount + ", Category: " + category + ", Date: " + transactionDate);
            System.out.println();
        }
    }
    // report for incomes and expenses
    public static void generateReport(Connection connection, String startDate, String endDate) throws Exception{
        String str = "select * from finance where transactionDate between ? and ?";
        PreparedStatement report = connection.prepareStatement(str);
        report.setDate(1, Date.valueOf(startDate));
        report.setDate(2, Date.valueOf(endDate));
        ResultSet results = report.executeQuery();
        double totalIncome = 0;
        double totalExpense = 0;
        while (results.next()){
            String type = results.getString("type");
            Double amount = results.getDouble("amount");
            if (type.equals("income")){
                totalIncome += amount;
            }
            else{
                totalExpense += amount;
            }
        }
        System.out.println("Income for period " + startDate + " to " + endDate + ": " + totalIncome);
        System.out.println("Expense for period " + startDate + " to " + endDate + ": " + totalExpense);
    }
    public static void balance(Connection connection) throws Exception{
        String str = "select * from finance";
        PreparedStatement bal = connection.prepareStatement(str);
        ResultSet results = bal.executeQuery();
        double total = 0;
        while (results.next()){
            String type = results.getString("type");
            Double amount = results.getDouble("amount");
            if (type.equals("income")){
                total += amount;
            }
            else{
                total -= amount;
            }
        }
        System.out.println("Your bank account balance is " + total + " tenge.");
    }
}