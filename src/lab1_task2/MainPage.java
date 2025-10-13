package lab1_task2;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
					int numberJ = accounts.size(); //will be read from json and deleted from here
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
						default:
							System.out.println("Incorrect number");
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
			System.out.println("===Account №" + account.getId() + " Menu===");
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
					try {
						System.out.print("Enter the amount: ");
						double amount_2 = checkDouble();
						account.deposit(amount_2);
						System.out.println("Deposit - " + amount_2 + "₽, current balance - " + account.getBalance() + "₽");
					}
					catch (IllegalArgumentException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 3:
					try {
						System.out.print("Enter the amount: ");
						double amount_3 = checkDouble();
						account.withdraw(amount_3);
						System.out.println("Withdraw - " + amount_3 + "₽, current balance - " + account.getBalance() + "₽");
					}
					catch (IllegalArgumentException | IllegalStateException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 4:
					try {
						System.out.print("Enter the account number of recepient: ");
						int recepientID = checkAccount(checkInt());
						System.out.print("\nEnter the amount: ");
						double amount_4 = checkDouble();
						double commission = amount_4 * 0.0015;
						account.sending(amount_4 + commission, recepientID);
						retrieveAccount(recepientID).receiving(amount_4, account.getId());
						System.out.println("Deduction of commission: " + commission + "₽");
						System.out.println("Sending - " + amount_4 + "₽, current balance - " + account.getBalance() + "₽");
					}
					catch(IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 5:
					boolean filtering = true;
					TransactionType transactionType = null;
					double amountMin = 0;
					double amountMax = 0;
					LocalDate dateMin = LocalDate.MIN;
					LocalDate dateMax = LocalDate.MAX;
					
					while (filtering) {
						String filterTransactionType = (transactionType != null) ? transactionType.toString() : "All";
						System.out.println("===Filters===");
						System.out.println("1. Transaction type: " + filterTransactionType);
						System.out.print("2. Amount: ");
						if (amountMin == 0 && amountMax == 0) {
							System.out.println("All");
						} else if (amountMin != 0 && amountMax == 0) {
							System.out.println(amountMin + "₽ - Any");
						} else if (amountMin == amountMax) {
							System.out.println(amountMin + "₽");
						} else {
							System.out.println(amountMin + "₽ - " + amountMax + "₽");
						}
						System.out.print("3. Date: ");
						if (dateMin == LocalDate.MIN && dateMax == LocalDate.MAX) {
							System.out.println("All");
						} else if (dateMin != LocalDate.MIN && dateMax == LocalDate.MAX) {
							System.out.println("After " + dateMin.minusDays(1));
						} else if (dateMin == LocalDate.MIN && dateMax != LocalDate.MAX) {
							System.out.println("Before " + dateMax.plusDays(1));
						} else if (dateMin == dateMax) {
							System.out.println(dateMin);
						} else {
							System.out.println(dateMin + " - " + dateMax);
						}
						System.out.println("4. Apply filters");
						System.out.println("5. Return to account menu");
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
								break;
							default:
								System.out.println("Incorrect number");
							}
							break;
						case 2:
							boolean filterAmount = true;
							while (filterAmount) {
								System.out.println("===Set amount range===");
								System.out.println("1. Set Min: " + ((amountMin == 0) ? "None" : amountMin));
								System.out.println("2. Set Max: " + ((amountMax == 0) ? "None" : amountMax));
								System.out.println("3. Delete filters");
								System.out.println("4. Return to filters menu");
								double setAmountMin = amountMin;
								double setAmountMax = amountMax;
								
								int choiceAmount = checkInt();
								switch (choiceAmount) {
								case 1:
									try {
										System.out.print("Enter min amount: ");
										setAmountMin = Double.parseDouble(sc.nextLine());
										if (setAmountMin < 0) {
											throw new IllegalArgumentException();
										}
										amountMin = setAmountMin;
									} catch (IllegalArgumentException e) {
										setAmountMin = 0;
										System.out.println("Min was set wrong and was deleted");
									}
									break;
								case 2:
									try {
										System.out.print("Enter max amount: ");
										setAmountMax = Double.parseDouble(sc.nextLine());
										if (setAmountMax < 0) {
											throw new IllegalArgumentException();
										}
										if (setAmountMin > setAmountMax && setAmountMax != 0) {
											throw new IllegalStateException();
										}
										amountMax = setAmountMax;
									} catch (IllegalArgumentException | IllegalStateException e) {
										setAmountMax = 0;
										System.out.println("Max was set wrong and was deleted");
									}
									break;
								case 3:
									amountMin = 0;
									amountMax = 0;
									break;
								case 4:
									filterAmount = false;
									break;
								default:
									System.out.println("Incorrect number");
								}
							}
							break;
						case 3:
							boolean filterDate = true;
							while (filterDate) {
								System.out.println("===Set date range===");
								System.out.println("1. Start date: " + ((dateMin == LocalDate.MIN) ? "None" : dateMin));
								System.out.println("2. End date: " + ((dateMax == LocalDate.MAX) ? "None" : dateMax));
								System.out.println("3. Delete filters");
								System.out.println("4. Return to filters menu");
								LocalDate setDateMin = dateMin;
								LocalDate setDateMax = dateMax;
								
								int choiceDate = checkInt();
								switch (choiceDate) {
									case 1:
										try {
											System.out.print("Enter start date as yyyy-MM-dd: ");
											setDateMin = LocalDate.parse(sc.nextLine());
											dateMin = setDateMin;
											if (setDateMax != LocalDate.MAX && setDateMin.isAfter(setDateMax)) {
												throw new IllegalStateException();
											}
										} catch (DateTimeParseException | IllegalStateException e) {
											setDateMin = LocalDate.MIN;
											System.out.println("Start date was set wrong and was deleted");
										}
										break;
									case 2:
										try {
											System.out.print("Enter end date as yyyy-MM-dd: ");
											setDateMax = LocalDate.parse(sc.nextLine());
											dateMax = setDateMax;
											if (setDateMax != LocalDate.MAX && setDateMin.isAfter(setDateMax)) {
												throw new IllegalStateException();
											}
										} catch (DateTimeParseException | IllegalStateException e) {
											setDateMax = LocalDate.MAX;
											System.out.println("End date was set wrong and was deleted");
										}
										break;
									case 3:
										dateMin = LocalDate.MIN;
										dateMax = LocalDate.MAX;
										filterDate = false;
									case 4:
										filterDate = false;
										break;
									default:
										System.out.println("Incorrect number");
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
						case 5:
							filtering = false;
							break;
						default:
							System.out.println("Incorrect number");
						}
					}
				break;
				case 6:
					return;
				default:
					System.out.println("Incorrect number");
			}
		}
	}
}
