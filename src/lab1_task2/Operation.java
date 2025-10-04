package lab1_task2;

import java.time.LocalDate;

public class Operation {
	private OperationType type;
	private double amount;
	private LocalDate date;
	private int from;
	private int to;
	
	public Operation(OperationType type, double amount, LocalDate date) {
		this(type, 0, 0, amount, date);
	}
	
	public Operation(OperationType type, int from, int to, double amount, LocalDate date) {
		this.type = type;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.date = date;
	}
	
	public OperationType getType() {
		return type;
	}
	public double getAmount() {
		return amount;
	}
	public LocalDate getDate() {
		return date;
	}
	public int getSender() {
		//exception if 0
		return from;
	}
	public int getRecepient() {
		//exception in 0
		return to;
	}
}
