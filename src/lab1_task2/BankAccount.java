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
			double amount = transaction.getAmount();
			LocalDate date = transaction.getDate();
			
			System.out.println(type + "|" + amount + "₽ | " + date);
		}
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void deposit(double amount) {
		//exception zero or minus deposit
		System.out.print("Hello");
	}
	
	public void withdraw(double amount) {
		//exception not enough money to withdraw
		System.out.print("Hello");
	}
}
