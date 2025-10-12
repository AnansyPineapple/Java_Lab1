package lab1_task2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
	private int id;
	private double balance;
	private List<Transaction> transactionHistory;
	
	BankAccount(int id) {
		this.id = id;
		this.balance = 0;
		this.transactionHistory = new ArrayList<>();
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
			
			System.out.println(type + "|" + amount + "₽ | " + date);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void deposit(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot deposit non-positive amount of money");
		}
		this.balance += amount;
		Transaction op = new Transaction(TransactionType.Deposit, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
	
	public void withdraw(double amount){
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot withdraw non-positive amount of money");
		}
		if (this.balance < amount) {
			throw new IllegalStateException("You cannot withdraw more money that you own");
		}
		this.balance -= amount;
		Transaction op = new Transaction(TransactionType.Withdraw, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
	
	public void sending(double amount, int receiver) {
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot send non-positive amount of money");
		}
		if (this.balance < amount) {
			throw new IllegalStateException("You cannot send more money that you own");
		}
		this.balance -= amount;
		Transaction op = new Transaction(TransactionType.Sending, this.id, receiver, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
	
	public void receiving(double amount, int sender) {
		this.balance += amount;
		Transaction op = new Transaction(TransactionType.Receiving, sender, this.id, amount, LocalDate.now());
		this.transactionHistory.add(op);
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
}
