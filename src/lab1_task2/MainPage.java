package lab1_task2;

import java.util.Scanner;
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
	
	//screen clear
	public static void main(String args[ ]) {
		//open - read from json file to accounts - read 1st numberJ = last accID
        mainMenu();
    }
    
	public static void mainMenu() {
		while (true) {
			//clearConsole();
			System.out.println("===Main Menu===");
			System.out.println("1. Choose bank account");
			System.out.println("2. Create bank account");
			System.out.println("3. Delete bank account");
			System.out.println("4. Exit program");
			
			int choice = checkInt();
			
			switch(choice) {
				case 1:
					//clearConsole();
					System.out.print("Enter the bank account number: ");
					int id_1 = checkInt();
					try {
						accountMenu(findAccount(id_1));
					}
					catch (NoSuchElementException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 2:
					//clearConsole();
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
							accountMenu(findAccount(id_2));
						case 2:
							break;
					}
					break;
				case 3:
					//clearConsole();
					System.out.print("Enter the bank account number: ");
					int id_3 = checkInt();
					try {
						//ask to proceed + notif. saving will be lost after
						accounts.remove(findAccount(id_3));
						//remove from json
						System.out.println("Account №" + id_3 + " was deleted successfuly");
					}
					catch (NoSuchElementException e) {
						System.out.println(e.getMessage() + "\n");
					}
					break;
				case 4:
					return;
			}
		}
    }
	
	public static BankAccount findAccount(int id) {
		//search in accounts and return account or exception
		for (BankAccount account : accounts) {
			if (account.getId() == id) {
				return account;
			}
		}
		throw new NoSuchElementException("Account with this number was not found");
	}
	
	public static void accountMenu(BankAccount account) {
		//clearConsole();
		System.out.println("Hello");
	}
}
