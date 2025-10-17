package lab1_task2;

import java.io.Serializable;
import java.time.LocalDate;

public final class Transaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
}
