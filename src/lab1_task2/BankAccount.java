package lab1_task2;

import java.util.*;
import java.io.*;
import java.time.LocalDate;

public final class BankAccount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private double balance;
	private List<Transaction> transactionHistory;
	
	BankAccount(int id, double balance, List<Transaction> transactionHistory) {
		this.id = id;
		this.balance = balance;
		this.transactionHistory = new ArrayList<>(transactionHistory == null ? List.of() : transactionHistory);
	}
	
	public int getId() {return id;}
	public double getBalance() {return balance;}
	public void getTransactionHistory() {
		for (Transaction transaction : transactionHistory) {
			printTransaction(transaction);
		}
	}
	
	public void printTransaction(Transaction transaction) {
			String type = transaction.getType().toString();
			if (type == "Sending") {
				type += " | To: " + transaction.getRecepient();
			}
			if (type == "Receiving") {
				type += " | From: " + transaction.getSender();
			}
			double amount = transaction.getAmount();
			LocalDate date = transaction.getDate();
			
			System.out.println(type + " | " + amount + "₽ | " + date);
	}
	
	public BankAccount deposit(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot deposit non-positive amount of money");
		}
		Transaction op = new Transaction(TransactionType.Deposit, amount, LocalDate.now());
		List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
		newTransactionHistory.add(op);
		return new BankAccount(this.id, this.balance + amount, newTransactionHistory);
	}
	
	public BankAccount withdraw(double amount){
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot withdraw non-positive amount of money");
		}
		if (this.balance < amount) {
			throw new IllegalStateException("You cannot withdraw more money that you own");
		}
		Transaction op = new Transaction(TransactionType.Withdraw, amount, LocalDate.now());
		List<Transaction> newTransactionHistory = new ArrayList<>(this.transactionHistory);
		newTransactionHistory.add(op);
		return new BankAccount(this.id, this.balance - amount, newTransactionHistory);
	}
	
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
	
	public void filterTransactions(TransactionType type, double amountMin, double amountMax, LocalDate dateMin, LocalDate dateMax) {
		for (Transaction transaction : transactionHistory) {
			if (type != null && type != transaction.getType()) {
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
			printTransaction(transaction);
		}
	}
	
	public String toString() {
		return String.valueOf(id);
	}
}
