package lab1_task2;

import java.time.LocalDate;

public class Transaction {
	private TransactionType type;
	private double amount;
	private LocalDate date;
	private int from;
	private int to;
	
	public Transaction(TransactionType type, double amount, LocalDate date) {
		this(type, 0, 0, amount, date);
	}
	
	public Transaction(TransactionType type, int from, int to, double amount, LocalDate date) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.date = date;
	}
	
	public TransactionType getType() {
		return type;
	}
	public double getAmount() {
		return amount;
	}
	public LocalDate getDate() {
		return date;
	}
	public int getSender() {
		return from;
	}
	public int getRecepient() {
		return to;
	}
	
	public void setType(TransactionType type) {
		this.type = type;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public void setSender(int from) {
		this.from = from;
	}
	public void setRecepient(int to) {
		this.to = to;
	}
}
