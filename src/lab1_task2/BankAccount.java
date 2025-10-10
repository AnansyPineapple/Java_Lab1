package lab1_task2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
	private int id;
	private double balance;
	private List<Operation> transactionHistory;
	
	BankAccount(int id) {
		this.id = id;
		this.balance = 0;
		this.transactionHistory = new ArrayList<>();
	}
	
	public int getId() {return id;}
	public double getBalance() {return balance;}
	public void getHistory() {
		for (Operation transaction : transactionHistory) {
			String type = transaction.getType().toString();
			if (type == "Transaction") {
				type += " | To: " + transaction.getRecepient();
			}
			if (type == "Receiving") {
				type += " | From: " + transaction.getSender();
			}
			double amount = transaction.getAmount();
			LocalDate date = transaction.getDate();
			
			System.out.println(type + "|" + amount + "₽ | " + date);
		}
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void deposit(double amount){
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot deposit non-positive amount of money.");
		}
		this.balance += amount;
		Operation op = new Operation(OperationType.Deposit, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
	
	public void withdraw(double amount){
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot withdraw non-positive amount of money.");
		}
		if (this.balance < amount) {
			throw new IllegalStateException("You cannot withdraw more money that you own.");
		}
		this.balance -= amount;
		Operation op = new Operation(OperationType.Withdraw, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
	
	public void transaction(double amount, int receiver) {
		if (amount <= 0) {
			throw new IllegalArgumentException("You cannot send non-positive amount of money.");
		}
		if (this.balance < amount) {
			throw new IllegalStateException("You cannot send more money that you own.");
		}
		this.balance -= balance;
		Operation op = new Operation(OperationType.Transaction, this.id, receiver, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
	
	public void receiving(double amount, int sender) {
		this.balance += balance;
		Operation op = new Operation(OperationType.Receiving, sender, this.id, amount, LocalDate.now());
		this.transactionHistory.add(op);
	}
}
