package lab1_task2;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.event.*;

public class Manager{
    static JFrame mainWindow;
    
	private static int maxId;
	private static ArrayList<BankAccount> accounts;
	
	static Scanner scanner = new Scanner(System.in);
	
	public static int readInt() {
		while (true){
			try {
				return Integer.parseInt(scanner.nextLine());
			}
			catch (IllegalArgumentException e) {
				System.out.println("Enter a valid number");
			}
		}
	}
	
	public static double readDouble() {
		while (true){
			try {
				return Double.parseDouble(scanner.nextLine());
			}
			catch (IllegalArgumentException e) {
				System.out.println("Enter a valid number");
			}
		}
	}
	
	public static int getIndex(int id) {
		for (BankAccount account : accounts) {
			if (account.getId() == id) {
				return accounts.indexOf(account);
			}
		}
		throw new NoSuchElementException("Account with this number was not found");
	}
	
	public static int accountExists(int id) {
		//search in accounts and returns id or exception
		for (BankAccount account : accounts) {
			if (account.getId() == id) {
				return account.getId();
			}
		}
		throw new NoSuchElementException("Account with this number was not found");
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		try {
			File dataFile = new File("data.ser");
			if (dataFile.exists() && dataFile.length() > 0) {
				FileInputStream fileInput = new FileInputStream("data.ser");
				try (ObjectInputStream objectInput = new ObjectInputStream(fileInput)) {
					maxId = objectInput.readInt();
					accounts = (ArrayList<BankAccount>) objectInput.readObject();
				}
			}
			else {
				if (!dataFile.exists()) {
					dataFile.createNewFile();
				}
				maxId = 0;
				accounts = new ArrayList<BankAccount>();
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			mainMenu();
		}
    }
    
	public static void save() {
		try {
		 FileOutputStream fileOutput = new FileOutputStream("data.ser");
		 try (ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {
			objectOutput.writeInt(maxId);
			 objectOutput.writeObject(accounts);
			 fileOutput.flush();
			 objectOutput.flush();
		}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void mainMenu() {
		mainWindow = new JFrame("Main Menu");
		mainWindow.setSize(350, 270);
		
		JPanel mainWindowPanel = new JPanel();
    	mainWindowPanel.setLayout(new BoxLayout(mainWindowPanel, BoxLayout.Y_AXIS));
    	
    	JButton choose, create, delete, exit;
    	
    	choose = new JButton("Choose bank account");
    	choose.setPreferredSize(new Dimension(300,50));
    	choose.setMaximumSize(new Dimension(300,50));
    	choose.setAlignmentX(Component.CENTER_ALIGNMENT);
    	choose.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFrame accountSelection = new JFrame("Select Account");
    			accountSelection.setLayout(new BorderLayout());
    			
    			if (accounts == null || accounts.isEmpty()) {
    				accountSelection.setPreferredSize(new Dimension(250, 100));
    				
    				JPanel nullPanel = new JPanel(new BorderLayout());
     			    
    				JLabel nullMessage = new JLabel("You don't have any accounts", JLabel.CENTER);
    				
    				JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    				
    				JButton back = new JButton("Return");
    				back.setBounds(95, 70, 73, 30);
    				back.addActionListener(new ActionListener() {
    					public void actionPerformed(ActionEvent e) {
 	                       accountSelection.dispose();
 	                   }
    				});
    				
    				 buttonPanel.add(back);
    				    
    				 nullPanel.add(nullMessage, BorderLayout.CENTER);
    				 nullPanel.add(buttonPanel, BorderLayout.SOUTH);
    				 accountSelection.add(nullPanel, BorderLayout.CENTER);
    			}
    			else {
        		   accountSelection.setPreferredSize(new Dimension(300, 200));
        		   
        		   JPanel accountPanel = new JPanel(new FlowLayout());
    			   
    			   JList<BankAccount> accountList = new JList<>(accounts.toArray(new BankAccount[0]));
 	               accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 	               accountList.setVisibleRowCount(3);
 	               accountList.setFixedCellWidth(240);
 	             
	               JScrollPane listScroller = new JScrollPane(accountList);
	               listScroller.setPreferredSize(new Dimension(280, 120));
	               listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	               
	               JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	               
	               JButton back = new JButton("Return");
    			   back.addActionListener(new ActionListener() {
    				   public void actionPerformed(ActionEvent e) {
 	                       accountSelection.dispose();
    				   }
    			   });
	               
	               JButton select = new JButton("OK");
	               select.setEnabled(false);
	               select.addActionListener(new ActionListener() {
	            	   public void actionPerformed(ActionEvent e) {
	            		   BankAccount selected = accountList.getSelectedValue();
	            		   accountSelection.dispose();
	            		   mainWindow.setVisible(false);
	            		   accountMenu(getIndex(selected.getId()));
	            	   }
	               });
	               
	               accountList.addListSelectionListener(new ListSelectionListener() {
	                   public void valueChanged(ListSelectionEvent e) {
	                       if (!e.getValueIsAdjusting()) {
	                           if (accountList.getSelectedIndex() == -1) {
	                        	   select.setEnabled(false);
	                           } 
	                           else {
	                        	   select.setEnabled(true);
	                           }
	                       }
	                   }
	               });
	               
	               buttonPanel.add(select);
	               buttonPanel.add(back);
	               
	               accountPanel.add(listScroller, BorderLayout.CENTER);
	               accountPanel.add(buttonPanel, BorderLayout.SOUTH);
	               
	               accountSelection.add(accountPanel, BorderLayout.CENTER);
    			}
    			accountSelection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    			accountSelection.setResizable(true);
    			accountSelection.pack();
    			accountSelection.setLocationRelativeTo(null);
    			accountSelection.setVisible(true);
    		}
    	});
    	
		create = new JButton("Create bank account");
		create.setPreferredSize(new Dimension(300,50));
		create.setMaximumSize(new Dimension(300,50));
		create.setAlignmentX(Component.CENTER_ALIGNMENT);
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame accountCreation = new JFrame();
				accountCreation.setPreferredSize(new Dimension(250, 100));
				
				JPanel creationMessagePanel = new JPanel();
				creationMessagePanel.setLayout(new BorderLayout());
				
				int id = maxId + 1;
				BankAccount account = new BankAccount(id, 0, null);
				maxId = account.getId();
				accounts.add(account);
				
				String message = "Account №" + id + " was created successfuly";
				JLabel creationMessage = new JLabel(message, JLabel.CENTER);
				
				JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				
				JButton back = new JButton("Return");
      		   	back.setBounds(95, 70, 73, 30);
      		   	back.addActionListener(new ActionListener() {
      			   public void actionPerformed(ActionEvent e) {
      				   accountCreation.dispose();
	                   }
 				});
      		   	buttonPanel.add(back);
				
				creationMessagePanel.add(creationMessage, BorderLayout.CENTER);
				creationMessagePanel.add(buttonPanel, BorderLayout.SOUTH);
				accountCreation.add(creationMessagePanel, BorderLayout.CENTER);
				
				accountCreation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				accountCreation.setResizable(true);
				accountCreation.pack();
    			accountCreation.setLocationRelativeTo(null);
    			accountCreation.setVisible(true);
			}
		});
		
		delete = new JButton("Delete bank account");
		delete.setPreferredSize(new Dimension(300,50));
		delete.setMaximumSize(new Dimension(300,50));
		delete.setAlignmentX(Component.CENTER_ALIGNMENT);
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame accountDeletion = new JFrame("Select Account");
    			accountDeletion.setLayout(new BorderLayout());
    			
    			if (accounts == null || accounts.isEmpty()) {
    				accountDeletion.setPreferredSize(new Dimension(250, 100));
    				JPanel nullPanel = new JPanel(new BorderLayout());
     			    
    				JLabel nullMessage = new JLabel("You don't have any accounts", JLabel.CENTER);
    				
    				JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    				
    				JButton back = new JButton("Return");
    				back.setBounds(95, 70, 73, 30);
    				back.addActionListener(new ActionListener() {
    					public void actionPerformed(ActionEvent e) {
 	                       accountDeletion.dispose();
 	                   }
    				});
    				
    				 buttonPanel.add(back);
    				    
    				 nullPanel.add(nullMessage, BorderLayout.CENTER);
    				 nullPanel.add(buttonPanel, BorderLayout.SOUTH);
    				 accountDeletion.add(nullPanel, BorderLayout.CENTER);
    			}
    			else {
        		   accountDeletion.setPreferredSize(new Dimension(300, 200));
        		   
        		   JPanel deletionPanel = new JPanel(new FlowLayout());
    			   
    			   JList<BankAccount> deletionList = new JList<>(accounts.toArray(new BankAccount[0]));
 	               deletionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 	               deletionList.setVisibleRowCount(3);
 	               deletionList.setFixedCellWidth(240);
 	             
	               JScrollPane listScroller = new JScrollPane(deletionList);
	               listScroller.setPreferredSize(new Dimension(280, 120));
	               listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	               
	               JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	               
	               JButton back = new JButton("Return");
    			   back.addActionListener(new ActionListener() {
    				   public void actionPerformed(ActionEvent e) {
 	                       accountDeletion.dispose();
    				   }
    			   });
	               
	               JButton select = new JButton("Delete");
	               select.setEnabled(false);
	               select.addActionListener(new ActionListener() {
	            	   public void actionPerformed(ActionEvent e) {
	            		   BankAccount selected = deletionList.getSelectedValue();
	            		   int id = selected.getId();
	            		   
	            		   accounts.remove(getIndex(id));
	            		   save();
	            		   deletionList.setListData(accounts.toArray(new BankAccount[0]));
	            		   
	            		   JFrame deletionMessageFrame = new JFrame();
	            		   deletionMessageFrame.setPreferredSize(new Dimension(250, 100));
	            		   JPanel deletionMessagePanel = new JPanel(new BorderLayout());
	        			   
	            		   String message = "Account №" + id + " was deleted successfuly";
	            		   JLabel deletionMessage = new JLabel(message, JLabel.CENTER);
	       				
	            		   JPanel buttonPanelM = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	       				
	            		   JButton back = new JButton("Return");
	            		   back.setBounds(95, 70, 73, 30);
	            		   back.addActionListener(new ActionListener() {
	            			   public void actionPerformed(ActionEvent e) {
	            				   deletionMessageFrame.dispose();
	    	                   }
	       				});
	            		   buttonPanelM.add(back);
	       				    
	            		   deletionMessagePanel.add(deletionMessage, BorderLayout.CENTER);
	            		   deletionMessagePanel.add(buttonPanelM, BorderLayout.SOUTH);
	            		   deletionMessage.add(deletionMessagePanel, BorderLayout.CENTER);
	            		   
	            		   deletionMessageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	            		   deletionMessageFrame.setResizable(true);
	            		   deletionMessageFrame.pack();
	            		   deletionMessageFrame.setLocationRelativeTo(null);
	            		   deletionMessageFrame.setVisible(true);
	            	   }
	               });
	               
	               deletionList.addListSelectionListener(new ListSelectionListener() {
	                   public void valueChanged(ListSelectionEvent e) {
	                       if (!e.getValueIsAdjusting()) {
	                           if (deletionList.getSelectedIndex() == -1) {
	                        	   select.setEnabled(false);
	                           } 
	                           else {
	                        	   select.setEnabled(true);
	                           }
	                       }
	                   }
	               });
	               
	               buttonPanel.add(select);
	               buttonPanel.add(back);
	               
	               deletionPanel.add(listScroller, BorderLayout.CENTER);
	               deletionPanel.add(buttonPanel, BorderLayout.SOUTH);
	               
	               accountDeletion.add(deletionPanel, BorderLayout.CENTER);
    			}
    			accountDeletion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    			accountDeletion.setResizable(true);
    			accountDeletion.pack();
    			accountDeletion.setLocationRelativeTo(null);
    			accountDeletion.setVisible(true);
			}
		});
		
		exit = new JButton("Exit");
		exit.setPreferredSize(new Dimension(300,50));
		exit.setMaximumSize(new Dimension(300,50));
		exit.setAlignmentX(Component.CENTER_ALIGNMENT);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		mainWindowPanel.add(Box.createVerticalGlue());
		mainWindowPanel.add(choose);
		mainWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainWindowPanel.add(create);
		mainWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainWindowPanel.add(delete);
		mainWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainWindowPanel.add(exit);
		mainWindowPanel.add(Box.createVerticalGlue());
		mainWindow.getContentPane().add(BorderLayout.CENTER, mainWindowPanel);
	    
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				Object[] options = { "Yes", "No" };
                int choice = JOptionPane.showOptionDialog(e.getWindow(), "Close program?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,options[0]);
                if (choice == 0) {
                	save();
                    e.getWindow().setVisible(false);
                    System.exit(0);
                }
			}

			public void windowOpened(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowActivated(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}
		});
		mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
	}
	
//	public static void accountMenu(int index) {
//		mainWindow = new JFrame("Main Menu");
//		mainWindow.setSize(350, 270);
//		
//	    mainPanel = new JPanel();
//    	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//    	
//    	
//	}
	
//	public static void accountMenu(int index) {
//		while (true) {
//			BankAccount account = accounts.get(index);
//			System.out.println("===Account №" + account.getId() + " Menu===");
//			System.out.println("1. Show balance");
//			System.out.println("2. Deposit");
//			System.out.println("3. Witdraw");
//			System.out.println("4. Transfer money");
//			System.out.println("5. Display transactions");
//			System.out.println("6. Return to main menu");
//			
//			int choice = checkInt();
//			
//			switch (choice) {
//				case 1:
//					System.out.println("Current balance: " + account.getBalance() + "₽");
//					break;
//				case 2:
//					try {
//						System.out.print("Enter the amount: ");
//						double amount_2 = checkDouble();
//						BankAccount newAccount = account.deposit(amount_2);
//						accounts.set(getIndex(account.getId()), newAccount);
//						System.out.println("Deposit - " + amount_2 + "₽, current balance - " + newAccount.getBalance() + "₽");
//						save();
//					}
//					catch (IllegalArgumentException e) {
//						System.out.println(e.getMessage() + "\n");
//					}
//					break;
//				case 3:
//					try {
//						System.out.print("Enter the amount: ");
//						double amount_3 = checkDouble();
//						BankAccount newAccount = account.withdraw(amount_3);
//						accounts.set(getIndex(account.getId()), newAccount);
//						System.out.println("Withdraw - " + amount_3 + "₽, current balance - " + newAccount.getBalance() + "₽");
//						save();
//					}
//					catch (IllegalArgumentException | IllegalStateException e) {
//						System.out.println(e.getMessage() + "\n");
//					}
//					break;
//				case 4:
//					try {
//						System.out.print("Enter the account number of recepient: ");
//						int recepientId = idExists(checkInt());
//						System.out.print("Enter the amount: ");
//						double amount_4 = checkDouble();
//						
//						double commission = amount_4 * 0.0015;
//						
//						BankAccount newAccount = account.sending(amount_4 + commission, recepientId);
//						accounts.set(getIndex(account.getId()), newAccount);
//						
//						BankAccount recepient = accounts.get(getIndex(recepientId));
//						BankAccount newRecepient = recepient.receiving(amount_4, account.getId());
//						accounts.set(getIndex(recepient.getId()), newRecepient);
//						
//						System.out.println("Deduction of commission: " + commission + "₽");
//						System.out.println("Sending - " + amount_4 + "₽, current balance - " + newAccount.getBalance() + "₽");
//						save();
//					}
//					catch(IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
//						System.out.println(e.getMessage() + "\n");
//					}
//					break;
//				case 5:
//					boolean filtering = true;
//					TransactionType transactionType = null;
//					double amountMin = 0;
//					double amountMax = 0;
//					LocalDate dateMin = LocalDate.MIN;
//					LocalDate dateMax = LocalDate.MAX;
//					
//					while (filtering) {
//						String filterTransactionType = (transactionType != null) ? transactionType.toString() : "All";
//						System.out.println("===Filters===");
//						System.out.println("1. Transaction type: " + filterTransactionType);
//						System.out.print("2. Amount: ");
//						if (amountMin == 0 && amountMax == 0) {
//							System.out.println("All");
//						} else if (amountMin != 0 && amountMax == 0) {
//							System.out.println(amountMin + "₽ - Any");
//						} else if (amountMin == amountMax) {
//							System.out.println(amountMin + "₽");
//						} else {
//							System.out.println(amountMin + "₽ - " + amountMax + "₽");
//						}
//						System.out.print("3. Date: ");
//						if (dateMin == LocalDate.MIN && dateMax == LocalDate.MAX) {
//							System.out.println("All");
//						} else if (dateMin != LocalDate.MIN && dateMax == LocalDate.MAX) {
//							System.out.println("After " + dateMin.minusDays(1));
//						} else if (dateMin == LocalDate.MIN && dateMax != LocalDate.MAX) {
//							System.out.println("Before " + dateMax.plusDays(1));
//						} else if (dateMin == dateMax) {
//							System.out.println(dateMin);
//						} else {
//							System.out.println(dateMin + " - " + dateMax);
//						}
//						System.out.println("4. Apply filters");
//						System.out.println("5. Return to account menu");
//						int currentFilter = checkInt();
//						switch (currentFilter) {
//						case 1:
//							System.out.println("===Set type===");
//							System.out.println("1. Deposits");
//							System.out.println("2. Withdrawals");
//							System.out.println("3. Sendings");
//							System.out.println("4. Receivings");
//							System.out.println("5. Delete filter");
//	
//							int choiceType = checkInt();
//							switch (choiceType) {
//							case 1:
//								transactionType = TransactionType.Deposit;
//								break;
//							case 2:
//								transactionType = TransactionType.Withdraw;
//								break;
//							case 3:
//								transactionType = TransactionType.Sending;
//								break;
//							case 4:
//								transactionType = TransactionType.Receiving;
//								break;
//							case 5:
//								transactionType = null;
//								break;
//							default:
//								System.out.println("Incorrect number");
//							}
//							break;
//						case 2:
//							boolean filterAmount = true;
//							while (filterAmount) {
//								System.out.println("===Set amount range===");
//								System.out.println("1. Set Min: " + ((amountMin == 0) ? "None" : amountMin));
//								System.out.println("2. Set Max: " + ((amountMax == 0) ? "None" : amountMax));
//								System.out.println("3. Delete filters");
//								System.out.println("4. Return to filters menu");
//								double setAmountMin = amountMin;
//								double setAmountMax = amountMax;
//								
//								int choiceAmount = checkInt();
//								switch (choiceAmount) {
//								case 1:
//									try {
//										System.out.print("Enter min amount: ");
//										setAmountMin = Double.parseDouble(sc.nextLine());
//										if (setAmountMin < 0) {
//											throw new IllegalArgumentException();
//										}
//										amountMin = setAmountMin;
//									} catch (IllegalArgumentException e) {
//										setAmountMin = 0;
//										System.out.println("Min was set wrong and was deleted");
//									}
//									break;
//								case 2:
//									try {
//										System.out.print("Enter max amount: ");
//										setAmountMax = Double.parseDouble(sc.nextLine());
//										if (setAmountMax < 0) {
//											throw new IllegalArgumentException();
//										}
//										if (setAmountMin > setAmountMax && setAmountMax != 0) {
//											throw new IllegalStateException();
//										}
//										amountMax = setAmountMax;
//									} catch (IllegalArgumentException | IllegalStateException e) {
//										setAmountMax = 0;
//										System.out.println("Max was set wrong and was deleted");
//									}
//									break;
//								case 3:
//									amountMin = 0;
//									amountMax = 0;
//									break;
//								case 4:
//									filterAmount = false;
//									break;
//								default:
//									System.out.println("Incorrect number");
//								}
//							}
//							break;
//						case 3:
//							boolean filterDate = true;
//							while (filterDate) {
//								System.out.println("===Set date range===");
//								System.out.println("1. Start date: " + ((dateMin == LocalDate.MIN) ? "None" : dateMin));
//								System.out.println("2. End date: " + ((dateMax == LocalDate.MAX) ? "None" : dateMax));
//								System.out.println("3. Delete filters");
//								System.out.println("4. Return to filters menu");
//								LocalDate setDateMin = dateMin;
//								LocalDate setDateMax = dateMax;
//								
//								int choiceDate = checkInt();
//								switch (choiceDate) {
//									case 1:
//										try {
//											System.out.print("Enter start date as yyyy-MM-dd: ");
//											setDateMin = LocalDate.parse(sc.nextLine());
//											dateMin = setDateMin;
//											if (setDateMax != LocalDate.MAX && setDateMin.isAfter(setDateMax)) {
//												throw new IllegalStateException();
//											}
//										} catch (DateTimeParseException | IllegalStateException e) {
//											setDateMin = LocalDate.MIN;
//											System.out.println("Start date was set wrong and was deleted");
//										}
//										break;
//									case 2:
//										try {
//											System.out.print("Enter end date as yyyy-MM-dd: ");
//											setDateMax = LocalDate.parse(sc.nextLine());
//											dateMax = setDateMax;
//											if (setDateMax != LocalDate.MAX && setDateMin.isAfter(setDateMax)) {
//												throw new IllegalStateException();
//											}
//										} catch (DateTimeParseException | IllegalStateException e) {
//											setDateMax = LocalDate.MAX;
//											System.out.println("End date was set wrong and was deleted");
//										}
//										break;
//									case 3:
//										dateMin = LocalDate.MIN;
//										dateMax = LocalDate.MAX;
//										filterDate = false;
//									case 4:
//										filterDate = false;
//										break;
//									default:
//										System.out.println("Incorrect number");
//								}
//							}
//							break;
//						case 4:
//							if (transactionType == null && amountMin == 0 && amountMax == 0 && dateMin == LocalDate.MIN && dateMax == null) {
//								account.getTransactionHistory();
//							} 
//							else {
//								account.filterTransactions(transactionType, amountMin, amountMax, dateMin, dateMax);
//							}
//							break;
//						case 5:
//							filtering = false;
//							break;
//						default:
//							System.out.println("Incorrect number");
//						}
//					}
//				break;
//				case 6:
//					return;
//				default:
//					System.out.println("Incorrect number");
//			}
//		}
//	}
}
