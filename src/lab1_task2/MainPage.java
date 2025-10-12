package lab1_task2;

import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MainPage {
	static ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();
	static Scanner sc = new Scanner(System.in);
	
	public static int checkInt() {
		while (true){
			try {
				return Integer.parseInt(sc.nextLine());
			}
			catch (IllegalArgumentException e) {
				System.out.println("Enter a valid number");
			}
		}
	}
	
	public static double checkDouble() {
		while (true){
			try {
				return Double.parseDouble(sc.nextLine());
			}
			catch (IllegalArgumentException e) {
				System.out.println("Enter a valid number");
			}
		}
	}
	
	public static void main(String args[ ]) {
		//open - read from json file to accounts - read 1st numberJ = last accID
        mainMenu();
    }
    
	public static void mainMenu() {
		while (true) {
			System.out.println("===Main Menu===");
			System.out.println("1. Choose bank account");
			System.out.println("2. Create bank account");
			System.out.println("3. Delete bank account");
			System.out.println("4. Exit program");
			
			int choice = checkInt();
			
			switch(choice) {
				case 1:
					System.out.print("Enter the bank account number: ");
					int id_1 = checkInt();
					try {
						accountMenu(retrieveAccount(id_1));
					}
					catch (NoSuchElementException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 2:
					int numberJ = 0; //will be read from json and deleted from here
					int id_2 = numberJ + 1;
					BankAccount account = new BankAccount(id_2);
					accounts.add(account);
					//add to json
					//rewrite numberJ in json
					System.out.println("Account №" + id_2 + " was created successfuly");
					System.out.println("\nWould you like to start working with this account?");
					System.out.println("1. Yes");
					System.out.println("2. No");
					
					int startWorkChoice = checkInt();
					
					switch(startWorkChoice) {
						case 1:
							accountMenu(retrieveAccount(id_2));
						case 2:
							break;
					}
					break;
				case 3:
					System.out.print("Enter the bank account number: ");
					int id_3 = checkInt();
					try {
						//ask to proceed + notif. saving will be lost after
						accounts.remove(retrieveAccount(id_3));
						//remove from json
						System.out.println("Account №" + id_3 + " was deleted successfuly");
					}
					catch (NoSuchElementException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 4:
					return;
				default:
					System.out.println("Incorrect number");
			}
		}
    }
	
	public static BankAccount retrieveAccount(int id) {
		//search in accounts and return account or exception
		for (BankAccount account : accounts) {
			if (account.getId() == id) {
				return account;
			}
		}
		throw new NoSuchElementException("Account with this number was not found");
	}
	
	public static int checkAccount(int id) {
		//search in accounts and return account or exception
		for (BankAccount account : accounts) {
			if (account.getId() == id) {
				return id;
			}
		}
		throw new NoSuchElementException("Account with this number was not found");
	}
	
	public static void accountMenu(BankAccount account) {
		while (true) {
			System.out.println("===Account №" + account.getId() + "Menu===");
			System.out.println("1. Show balance");
			System.out.println("2. Deposit");
			System.out.println("3. Witdraw");
			System.out.println("4. Transfer money");
			System.out.println("5. Display transactions");
			System.out.println("6. Return to main menu");
			
			int choice = checkInt();
			
			switch (choice) {
				case 1:
					System.out.println("Current balance: " + account.getBalance() + "₽");
					break;
				case 2:
					System.out.print("Enter the amount: ");
					try {
						double amount_2 = checkDouble();
						account.deposit(amount_2);
						System.out.print("Deposit - " + amount_2 + "₽, current balance - " + account.getBalance() + "₽");
					}
					catch (IllegalArgumentException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 3:
					System.out.print("Enter the amount: ");
					try {
						double amount_3 = checkDouble();
						account.withdraw(amount_3);
						System.out.println("Withdraw - " + amount_3 + "₽, current balance - " + account.getBalance() + "₽");
					}
					catch (IllegalArgumentException | IllegalStateException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 4:
					System.out.print("Enter the account number of recepient: ");
					System.out.print("Enter the amount: ");
					try {
						int recepientID = checkAccount(checkInt());
						double amount_4 = checkDouble();
						account.sending(amount_4, recepientID);
						retrieveAccount(recepientID).receiving(amount_4, account.getId());
						System.out.println("Sending - " + amount_4 + "₽, current balance - " + account.getBalance() + "₽");
					}
					catch(IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 5:
					TransactionType transactionType = null;
					double amountMin = 0;
					double amountMax = 0;
					LocalDate dateMin = LocalDate.MIN;
					LocalDate dateMax = LocalDate.MAX;
					
					String filterTransactionType = (transactionType != null) ? transactionType.toString() : "All";
					
					System.out.println("===Filters===");
					System.out.println("1. Transaction type: " + filterTransactionType);
					System.out.println("2. Amount: ");
					if (amountMin == 0 && amountMax == 0) {
						System.out.println("All");
					}
					else if (amountMin != 0 && amountMax == 0){
						System.out.println(amountMin + "₽ - Any");
					}
					else if (amountMin == 0 && amountMax != 0){
						System.out.println("0₽ - " + amountMax + "₽");
					}
					else if (amountMin == amountMax) {
						System.out.println(amountMin + "₽");
					}
					else {
						System.out.println(amountMin + "₽ - " + amountMax + "₽");
					}
					System.out.println("3. Date: ");
					if (dateMin == LocalDate.MIN && dateMax == LocalDate.MAX) {
						System.out.println("All");
					}
					else if (dateMin != LocalDate.MIN && dateMax == LocalDate.MAX){
						System.out.println("After " + dateMin.minusDays(1));
					}
					else if (dateMin == LocalDate.MIN && dateMax != LocalDate.MAX){
						System.out.println("Before " + dateMax.plusDays(1));
					}
					else if (dateMin == dateMax) {
						System.out.println(dateMin);
					}
					else {
						System.out.println(dateMin + " - " + dateMax);
					}
					System.out.println("4. Apply filters");
					
					int currentFilter = checkInt();
					
					switch (currentFilter) {
						case 1:
							System.out.println("===Set type===");
							System.out.println("1. Deposits");
							System.out.println("2. Withdrawals");
							System.out.println("3. Sendings");
							System.out.println("4. Receivings");
							System.out.println("5. Delete filter");
							
							int choiceType = checkInt();
							
							switch (choiceType) {
								case 1:
									transactionType = TransactionType.Deposit;
									break;
								case 2:
									transactionType = TransactionType.Withdraw;
									break;
								case 3:
									transactionType = TransactionType.Sending;
									break;
								case 4:
									transactionType = TransactionType.Receiving;
									break;
								case 5:
									transactionType = null;
								default:
									System.out.println("Incorrect number");
							}
							break;
						case 2:
							while (true) {
								System.out.println("===Set amount range===");
								System.out.println("To skip a min/max press enter");
								double setAmountMin;
								double setAmountMax;
								System.out.print("Min: ");
								try {
									setAmountMin = Double.parseDouble(sc.nextLine());
								}
								catch (NumberFormatException e) {
									setAmountMin = 0;
								}
								System.out.print("Max: ");
								try {
									setAmountMax = Double.parseDouble(sc.nextLine());
								}
								catch (NumberFormatException e) {
									setAmountMax = 0;
								}
								
								boolean appropriateRange = true;
								String message = "";
								if (setAmountMin < 0) {
									appropriateRange = false;
									message += "\nMinimum amount can't be less than zero";
								}
								if (setAmountMax < 0) {
									appropriateRange = false;
									message += "\nMaximum amount can't be less than zero";
								}
								if (setAmountMin > setAmountMax && setAmountMax != 0) {
									appropriateRange = false;
									message += "\nMinimum amount can't exceed maximum amount";
								}
								
								if (appropriateRange == false) {
									System.out.print(message);
								}
								else {
									amountMin = setAmountMin;
									amountMax = setAmountMax;
									break;
								}
							}
							break;
						case 3:
							while (true) {
								System.out.println("===Set date range===");
								System.out.println("To skip a start/end press enter");
								LocalDate setDateMin;
								LocalDate setDateMax;
								System.out.print("Start date: ");
								try {
									setDateMin = LocalDate.parse(sc.nextLine());
								}
								catch (IllegalArgumentException e) {
									setDateMin = LocalDate.MIN;
								}
								System.out.print("End date: ");
								try {
									setDateMax = LocalDate.parse(sc.nextLine());
								}
								catch (IllegalArgumentException e) {
									setDateMax = LocalDate.MAX;
								}
								
								boolean appropriateRange = true;
								String message = "";
								if (setDateMin != LocalDate.MIN && setDateMin.isBefore(LocalDate.MIN)) {
									appropriateRange = false;
									message += "\nInappropriate start date";
								}
								if (setDateMax != LocalDate.MAX && setDateMax.isBefore(LocalDate.MIN)) {
									appropriateRange = false;
									message += "\nInappropriate end date";
								}
								if (setDateMin != LocalDate.MIN && setDateMax != LocalDate.MAX && setDateMin.isAfter(setDateMax)) {
									appropriateRange = false;
									message += "\nStart date can't be after the end date";
								}
								
								if (appropriateRange == false) {
									System.out.print(message);
								}
								else {
									dateMin = setDateMin;
									dateMax = setDateMax;
									break;
								}
							}
							break;
						case 4:
							if (transactionType == null && amountMin == 0 && amountMax == 0 && dateMin == LocalDate.MIN && dateMax == null) {
								account.getTransactionHistory();
							}
							else {
								account.filterTransactions(transactionType, amountMin, amountMax, dateMin, dateMax);
							}
							break;
						}
					break;
				case 6:
					return;
			}
		}
	}
}
