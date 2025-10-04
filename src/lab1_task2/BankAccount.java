package lab1_task2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BankAccount {
	private int id;
	private double balance;
	private String[] transactionHistory;
	
	BankAccount(int id) {
		this.id = id;
		this.balance = 0;
		this.transactionHistory = new String[0];
	}
	
	public int getId() {return id;}
	public double getBalance() {return balance;}
	public List<String> getHistory() {
		return Collections.unmodifiableList(Arrays.asList(transactionHistory));
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void deposit(double amount) {
		if (amount<=0) {
			System.out.println("Сумма пополнения должна быть положительной");
		}
	}
	
	public void withdraw(double amount) {
		
	}
}
