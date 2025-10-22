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
    
    /** Personal number of each account, unique within a file */
    private int id;
    
    /** Current balance */
    private double balance;
    
    /** List that contains history of transactions */
    private List<Transaction> transactionHistory;
    
    /**
     * Constructor of bank account
     * @param id id of account
     * @param balance current balance
     * @param transactionHistory history of transactions, if null an empty list will be used
     */
    BankAccount(int id, double balance, List<Transaction> transactionHistory) {
	this.id = id;
	this.balance = balance;
	this.transactionHistory = new ArrayList<>(transactionHistory == null ? List.of() : transactionHistory);
    }
    
    /** Returns id of account*/
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
     * Proceeds a deposit
     * Returns new account due to immutability
     * @param amount takes amount as input 
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
     * Proceeds a withdrawal
     * Returns new account due to immutability
     * @param amount takes amount as input
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
	
    //here
	public BankAccount sending(double amount, int receiver) {
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot send non-positive amount of money");
		}
		if (this.balance < amount) {
			throw new IllegalStateException("You cannot send more money that you own");
		}
		Transaction op = new Transaction(TransactionType.Sending, this.id, receiver, amount, LocalDate.now());
		List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
		newTransactionHistory.add(op);
		return new BankAccount(this.id, this.balance - amount, newTransactionHistory);
	}
	
	public BankAccount receiving(double amount, int sender) {
		Transaction op = new Transaction(TransactionType.Receiving, sender, this.id, amount, LocalDate.now());
		List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
		newTransactionHistory.add(op);
		return new BankAccount(this.id, this.balance + amount, newTransactionHistory);
	}
	
	public ArrayList<Transaction> filterTransactions(TransactionType type, double amountMin, double amountMax, LocalDate dateMin, LocalDate dateMax) {
	    List<Transaction> filteredTransactions = new ArrayList<>();
	    for (Transaction transaction : transactionHistory) {
		if (type != null && !type.equals(transaction.getType())) {
		    continue;
		}
		if (amountMin != 0 && amountMin > transaction.getAmount()) {
			continue;
		}
		if (amountMax != 0 && amountMax < transaction.getAmount()) {
			continue;
		}
		if (dateMin != LocalDate.MIN && dateMin.isAfter(transaction.getDate())) {
			continue;
		}
		if (dateMax != null && dateMax.isBefore(transaction.getDate())) {
			continue;
		}
		filteredTransactions.add(transaction);
	    }
	    return (ArrayList<Transaction>) filteredTransactions;
	}
	
	public String toString() {
		return String.valueOf(id);
	}
}
