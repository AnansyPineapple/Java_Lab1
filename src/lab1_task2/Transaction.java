package lab1_task2;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Class that represents a financial transaction
 * @author Khismatullova Maria
 * @version 1.0
 */
public final class Transaction implements Serializable {
    /** Version of serialization */
    private static final long serialVersionUID = 1L;
    
    /** Type of transaction (from enum) */
    private TransactionType type;
    
    /** Transaction amount in rubles, must be positive */
    private double amount;
    
    /** Transaction date */
    private LocalDate date;
    
    /** Source account of transaction, 0 indicates no sender */
    private int from;
    
    /** Destination account of transaction, 0 indicates no recipient */
    private int to;
    
    /**
     * Constructor for Deposit or Withdraw transactions
     * Here the sender and recipient are not defined, and both 0 indicate their nonexistence
     * @param type type of transaction
     * @param amount amount of transaction
     * @param date date of transaction
     */
    public Transaction(TransactionType type, double amount, LocalDate date) {
	this(type, 0, 0, amount, date);
    }
    
    /**
     * Main constructor with all parameters
     * Sendings and receivings are made here, as ids of accounts on both sides are taken into consideration
     * @param type type of transaction
     * @param from id of sender
     * @param to if of recipient
     * @param amount amount of transaction
     * @param date date of transaction
     */
    public Transaction(TransactionType type, int from, int to, double amount, LocalDate date) {
	this.type = type;
	this.from = from;
	this.to = to;
	this.amount = amount;
	this.date = date;
    }
    
    /** Return transaction type */
    public TransactionType getType() {
	return type;
    }
    
    /** Return amount of transaction */
    public double getAmount() {
	return amount;
    }
    
    /** Return date of transaction */ 
    public LocalDate getDate() {
	return date;
    }
    
    /** 
     * Return sender
     * If one does not exist due to transaction type, it will return 0 as a marker
     */
    public int getSender() {
	return from;
    }
    
    /** 
     * Return recipient
     * If one does not exist due to transaction type, it will return 0 as a marker
     */
    public int getRecipient() {
	return to;
    }
    
}
