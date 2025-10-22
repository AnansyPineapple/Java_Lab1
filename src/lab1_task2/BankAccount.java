package lab1_task2;

import java.util.*;
import java.io.*;
import java.time.LocalDate;

/**
 * Class that represents a single Bank Account
 * @author Khismatullova Maria
 * @version 1.0
 */
public final class BankAccount implements Serializable {
    /** Version of serialization */
    private static final long serialVersionUID = 1L;
    
    /** Personal identifier of each account, unique within a file */
    private int id;
    
    /** Current balance, must be >= 0 */
    private double balance;
    
    /** List that contains history of transactions */
    private List<Transaction> transactionHistory;
    
    /**
     * Constructor of bank account
     * @param id identifier of account
     * @param balance current balance
     * @param transactionHistory history of transactions, if null an empty list will be used
     */
    BankAccount(int id, double balance, List<Transaction> transactionHistory) {
	this.id = id;
	this.balance = balance;
	this.transactionHistory = new ArrayList<>(transactionHistory == null ? List.of() : transactionHistory);
    }
    
    /** Returns identifier */
    public int getId() {
	return id;
    }
    
    /** Returns current balance*/
    public double getBalance() {
	return balance;
    }
    
    /** Returns whole history of transactions */
    public ArrayList<Transaction> getTransactionHistory() {
	return (ArrayList<Transaction>) transactionHistory;
    }
    
    /** Prints a single transaction */
    public String printTransaction(Transaction transaction) {
	// Convert transaction type into string
	String type = transaction.getType().toString();
	
	// Add sender or recipient if needed
	if (type.equals("Sending")) {
	    type += " | To: " + transaction.getRecipient();
	}
	if (type.equals("Receiving")) {
	    type += " | From: " + transaction.getSender();
	}
	
	// Extract other data on transaction
	double amount = transaction.getAmount();
	LocalDate date = transaction.getDate();
	
	// Join and format information, then return it in a single string
	return (type + " | " + amount + "₽ | " + date);
    }
    
    /** 
     * Processes a deposit
     * Returns new account due to immutability
     * @param amount amount to deposit, must be positive
     */
    public BankAccount deposit(double amount) {
	// Truthify amount > 0 by throwing an exception if needed
	if (amount <= 0) {
	    throw new IllegalArgumentException("You cannot deposit non-positive amount of money");
	}
	
	// Create new transaction
	Transaction op = new Transaction(TransactionType.Deposit, amount, LocalDate.now());
	
	// Rewrite old transaction history with adding new transaction
	List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
	newTransactionHistory.add(op);
	
	// Return new bank account
	return new BankAccount(this.id, this.balance + amount, newTransactionHistory);
    }
    
    /**
     * Processes a withdrawal
     * Returns new account due to immutability
     * @param amount amount to withdraw, must be positive
     */
    public BankAccount withdraw(double amount) {
	// Truthify amount > 0 by throwing an exception if needed
	if (amount <= 0) {
	    throw new IllegalArgumentException("You cannot withdraw non-positive amount of money");
	}
	
	// Truthify amount does not exceeds current balance by throwing an exception if needed
	if (this.balance < amount) {
	    throw new IllegalStateException("You cannot withdraw more money that you own");
	}
	
	// Create new transaction
	Transaction op = new Transaction(TransactionType.Withdraw, amount, LocalDate.now());
	
	// Rewrite old transaction history with adding new transaction
	List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
	newTransactionHistory.add(op);
	
	// Return new bank account
	return new BankAccount(this.id, this.balance - amount, newTransactionHistory);
    }
	
    /**
     * Processes a sending
     * Returns new account due to immutability
     * @param amount amount to send, must be positive
     * @param recipient identifier of a recepient, already checked for existence
     */
    public BankAccount sending(double amount, int recipient) {
	// Truthify amount > 0 by throwing an exception if needed
	if (amount <= 0) {
	    throw new IllegalArgumentException("You cannot send non-positive amount of money");
	}
	
	// Truthify amount does not exceeds current balance by throwing an exception if needed
	if (this.balance < amount) {
	    throw new IllegalStateException("You cannot send more money that you own");
	}
	
	// Create new transaction
	Transaction op = new Transaction(TransactionType.Sending, this.id, recipient, amount, LocalDate.now());
	
	// Rewrite old transaction history with adding new transaction
	List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
	newTransactionHistory.add(op);
	
	// Return new bank account
	return new BankAccount(this.id, this.balance - amount, newTransactionHistory);
    }
    
    /**
     * Processes a receiving
     * Returns new account due to immutability
     * @param amount amount to receive, must be positive
     * @param sender identifier of a sender, already checked for existence
     */
    public BankAccount receiving(double amount, int sender) {
	// Create new transaction
	Transaction op = new Transaction(TransactionType.Receiving, sender, this.id, amount, LocalDate.now());
	
	// Rewrite old transaction history with adding new transaction
	List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
	newTransactionHistory.add(op);
	
	// Return new bank account
	return new BankAccount(this.id, this.balance + amount, newTransactionHistory);
    }
    
    /**
     * Filter transactions by given filters
     * @param type transaction type, if null it will not be taken into account
     * @param amountMin minimum amount for transactions, must be 0 to ignore this filter or more otherwise
     * @param amountMax maximum amount for transactions, must be 0 to ignore this filter or more otherwise
     * @param dateMin start date, must be equal to LocalDate.MIN to ignore or formatted as dd-MM-yyyy otherwise
     * @param dateMax end date, must be equal to LocalDate.MAX to ignore or formatted as dd-MM-yyyy otherwise
     * All boundary parameters are included into search
     */
    public ArrayList<Transaction> filterTransactions(TransactionType type, double amountMin, double amountMax, LocalDate dateMin, LocalDate dateMax) {
	// Create matching results collection
	List<Transaction> filteredTransactions = new ArrayList<>();
	
	// Check transaction against all active filters
	for (Transaction transaction : transactionHistory) {
	    // Skip if type does not match filter
	    if (type != null && !type.equals(transaction.getType())) {
		continue;
	    }
	    
	    // Skip if minimum amount exceeds given
	    if (amountMin != 0 && amountMin > transaction.getAmount()) {
		continue;
	    }
	    
	    // Skip if maximum amount exceeds given
	    if (amountMax != 0 && amountMax < transaction.getAmount()) {
		continue;
	    }
	    
	    // Skip if start date is past given
	    if (!dateMin.equals(LocalDate.MIN) && dateMin.isAfter(transaction.getDate())) {
		continue;
	    }
	    
	    // Skip if end date is before given
	    if (!dateMax.equals(LocalDate.MAX) && dateMax.isBefore(transaction.getDate())) {
		continue;
	    }
	    
	    // Add to list if matches
	    filteredTransactions.add(transaction);
	}
	
	// Return list of matched transactions
	return (ArrayList<Transaction>) filteredTransactions;
    }
    
    /** String representation of account as its identifier */
    @Override
    public String toString() {
	return String.valueOf(id);
    }
}
