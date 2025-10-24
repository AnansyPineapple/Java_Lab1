package lab1_task2;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.jdatepicker.impl.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Class that manages bank accounts of a signle user
 * @author Khismatullova Maria
 * @version 1.0
 */
public class Manager{
    /** Window of main menu
     *  Provides management of all accounts
     */
    static JFrame mainWindow;
    
    /** Type of transactions for filtration */
    static TransactionType transactionType = null;
    
    /** Minimal amount for filtration */
    static double amountMin = 0;
    
    /** Maximum amount for filtration */
    static double amountMax = 0;
    
    /** Start date for filtration */
    static LocalDate dateMin = LocalDate.MIN;
    
    /** End date for filtration */
    static LocalDate dateMax = LocalDate.MAX;
    
    /**
     * Current maximum account number
     * Used for account number assignment
     */
    private static int maxId;
    
    /** Collection of all user accounts */
    private static ArrayList<BankAccount> accounts;
    
    /** 
     * Return index of account in collection according to given identifier
     * @param id account identifier to search for
     * @throws NoSuchElementException if account was not found 
     */
    public static int getIndex(int id) {
	for (BankAccount account : accounts) {
	    if (account.getId() == id) {
		return accounts.indexOf(account);
	    }
	}
	throw new NoSuchElementException("Account with this number was not found");
    }
    
    /** 
     * Return identifier of account in collection according to given identifier
     * The purpose is to check whether account exists or not
     * @param id account identifier to search for
     * @throws NoSuchElementException if account was not found 
     */
    public static int accountExists(int id) {
	for (BankAccount account : accounts) {
	    if (account.getId() == id) {
		return account.getId();
	    }
	}
	throw new NoSuchElementException("Account with this number was not found");
    }
    
    /**
     * Set the maxId and list of accounts
     * Read information from existing file or creates new one otherwise
     */
    @SuppressWarnings("unchecked")
    public static void main(String args[]) {
	try {
	    File dataFile = new File("data.ser");
	    if (dataFile.exists() && dataFile.length() > 0) {
		// Load data
		FileInputStream fileInput = new FileInputStream("data.ser");
		try (ObjectInputStream objectInput = new ObjectInputStream(fileInput)) {
		    maxId = objectInput.readInt();
		    accounts = (ArrayList<BankAccount>) objectInput.readObject();
		}
	    }
	    else {
		// Create new data
		if (!dataFile.exists()) {
		    dataFile.createNewFile();
		}
		maxId = 0;
		accounts = new ArrayList<BankAccount>();
	    }
	    // Open menu only if maxId and accounts are defined
	    mainMenu();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
    
    /**
     * Save currents state into the file
     */
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
    
    /**
     * Create window of main menu
     * Contain control buttons with their event handlers
     */
    public static void mainMenu() {
	// Set the interface
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
    		
    		// Show message if there are no accounts
    		if (accounts == null || accounts.isEmpty()) {
    		    JOptionPane.showMessageDialog(accountSelection, "You don't have any accounts", "Notification", JOptionPane.INFORMATION_MESSAGE);
		    return;
    		}
    		else {
    		    // Set selection interface
    		    accountSelection.setPreferredSize(new Dimension(300, 165));
    		    JPanel accountPanel = new JPanel(new FlowLayout());
    		    
    		    // Visualise account list with single selection
    		    JList<BankAccount> accountList = new JList<>(accounts.toArray(new BankAccount[0]));
		    accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    accountList.setFixedCellWidth(240);
		    
		    // Create scroll pane for list
		    JScrollPane listScroller = new JScrollPane(accountList);
		    listScroller.setPreferredSize(new Dimension(280, 80));
		    listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    // Create buttons panel
		    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		    
		    // Return button - closes window
		    JButton back = new JButton("Return");
		    back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    accountSelection.dispose();
			}
		    });
		    
		    // Select button - opens account menu
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
		    
		    // Enable button only if account is selected
		    accountList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			    if (!e.getValueIsAdjusting()) {
				if (accountList.getSelectedIndex() == -1)  {
				    select.setEnabled(false);
				}
				else {
				    select.setEnabled(true);
				}
			    }
			}
		    });
		    
		    // Assemble interface
		    buttonPanel.add(select);
		    buttonPanel.add(back);
		    accountPanel.add(listScroller, BorderLayout.CENTER);
		    accountPanel.add(buttonPanel, BorderLayout.SOUTH);
		    accountSelection.add(accountPanel, BorderLayout.CENTER);
    		}
    		
    		// Set rules for window
    		accountSelection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
    		// Set creation interface
    		JFrame accountCreation = new JFrame();
		accountCreation.setLayout(new BorderLayout());
		
		// Create new bank account
		int id = maxId + 1;
		BankAccount account = new BankAccount(id, 0, null);
		
		// Update data
		maxId = account.getId();
		accounts.add(account);
		save();
		
		// Notify user about successful account creation
		String creationMessage = "Account №" + id + " was created successfuly";
		JOptionPane.showMessageDialog(accountCreation, creationMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
		return;
    	    }
    	});
    	
    	delete = new JButton("Delete bank account");
    	delete.setPreferredSize(new Dimension(300,50));
    	delete.setMaximumSize(new Dimension(300,50));
    	delete.setAlignmentX(Component.CENTER_ALIGNMENT);
    	delete.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Set deletion interface
    		JFrame accountDeletion = new JFrame("Select Account");
    		accountDeletion.setLayout(new BorderLayout());
    		
    		// Show message if there are no accounts
    		if (accounts == null || accounts.isEmpty()) {
    		    JOptionPane.showMessageDialog(accountDeletion, "You don't have any accounts", "Notification", JOptionPane.INFORMATION_MESSAGE);
    		    return;
    		}
    		else {
    		    // Set deletion interface
    		    accountDeletion.setPreferredSize(new Dimension(300, 165));
    		    JPanel deletionPanel = new JPanel(new FlowLayout());
    		    
    		    // Visualise account list with single selection
    		    JList<BankAccount> deletionList = new JList<>(accounts.toArray(new BankAccount[0]));
    		    deletionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    		    deletionList.setFixedCellWidth(240);
    		    
    		    // Create scroll pane for list
    		    JScrollPane listScroller = new JScrollPane(deletionList);
    		    listScroller.setPreferredSize(new Dimension(280, 80));
    		    listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    		    
    		    // Create buttons panel
    		    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		    
    		    // Return button - closes window
    		    JButton back = new JButton("Return");
    		    back.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    			    accountDeletion.dispose();
    			}
    		    });
    		    
    		    // Select button - delete account and removes it from all lists
    		    JButton select = new JButton("Delete");
    		    select.setEnabled(false);
    		    select.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    			    BankAccount selected = deletionList.getSelectedValue();
    			    int id = selected.getId();
    			    
    			    // Create warning
    			    Object[] options = {"Proceed", "Return"};
    			    int choice = JOptionPane.showOptionDialog(accountDeletion, "If you delete this account, your money will be lost! \nDo you want to proceed?", 
    				    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);    			  
    			    if (choice == JOptionPane.YES_OPTION) {
    				// Update data
    				accounts.remove(getIndex(id));
    				save();
    				deletionList.setListData(accounts.toArray(new BankAccount[0]));
    				
    				// Notify user about successful account deletion
    				String deletionMessage = "Account №" + id + " was deleted successfuly";
    				JOptionPane.showMessageDialog(accountDeletion, deletionMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
		    		return;
    			    }
    			    else {
    				return;
    			    }
    			}
    		    });
    		    
    		    
    		    // Enable button only if account is selected
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
    		    
    		    // Assemble interface
    		    buttonPanel.add(select);
    		    buttonPanel.add(back);
    		    deletionPanel.add(listScroller, BorderLayout.CENTER);
    		    deletionPanel.add(buttonPanel, BorderLayout.SOUTH);
    		    accountDeletion.add(deletionPanel, BorderLayout.CENTER);
    		}
    		
    		// Set rules for window
    		accountDeletion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		accountDeletion.setResizable(false);
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
    		// Trigger window closure
    		mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
    	    }
    	});
    	
    	// Assemble interface
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
    	
    	// Ask to close the window
    	mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	mainWindow.addWindowListener(new WindowListener() {
    	    public void windowClosing(WindowEvent e) {
    		Object[] options = { "Yes", "No" };
		int choice = JOptionPane.showOptionDialog(e.getWindow(), "Close program?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,options[0]);	
		if (choice == JOptionPane.YES_OPTION) {
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
    	
    	// Set rules for window
    	mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
    
    /**
     * Create window of certain account menu
     * Contain control buttons with their event handlers
     * @param index index of account in the collection
     */
    public static void accountMenu(int index) {
	// Set the interface
	JFrame accountWindow = new JFrame("Account №" + accounts.get(index).getId());
	accountWindow.setSize(350, 380);
	
	JPanel accountWindowPanel = new JPanel();
	accountWindowPanel.setLayout(new BoxLayout(accountWindowPanel, BoxLayout.Y_AXIS));
    	
    	JButton balance, deposit, withdraw, transfer, transactions, backToMenu;
    	
    	balance = new JButton("Show balance");
    	balance.setPreferredSize(new Dimension(300,50));
    	balance.setMaximumSize(new Dimension(300,50));
    	balance.setAlignmentX(Component.CENTER_ALIGNMENT);
    	balance.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Update information about current account
    		BankAccount account = accounts.get(index);
    		
    		// Set balance interface
    		JFrame balanceDemo = new JFrame("Balance");
    		balanceDemo.setPreferredSize(new Dimension(250, 100));
    		
    		JPanel balanceMessagePanel = new JPanel();
    		balanceMessagePanel.setLayout(new BorderLayout());
    		
    		// Create message with balance
    		String message = "Current balance: " + account.getBalance() + "₽";
    		JLabel balanceMessage = new JLabel(message, JLabel.CENTER);
    		
    		// Create button panel
    		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
    		// Return button - closes window
    		JButton back = new JButton("Return");
    		back.setBounds(95, 70, 73, 30);
    		back.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent e) {
    			balanceDemo.dispose();
    		    }
    		});
    		
    		//  Assemble interface
    		buttonPanel.add(back);
    		balanceMessagePanel.add(balanceMessage, BorderLayout.CENTER);
    		balanceMessagePanel.add(buttonPanel, BorderLayout.SOUTH);
    		balanceDemo.add(balanceMessagePanel, BorderLayout.CENTER);
    		
    		// Set rules for window
    		balanceDemo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		balanceDemo.setResizable(false);
    		balanceDemo.pack();
    		balanceDemo.setLocationRelativeTo(null);
    		balanceDemo.setVisible(true);
    	    }
    	});
    	
    	
    	deposit = new JButton("Deposit");
    	deposit.setPreferredSize(new Dimension(300,50));
    	deposit.setMaximumSize(new Dimension(300,50));
    	deposit.setAlignmentX(Component.CENTER_ALIGNMENT);
    	deposit.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Update information about current account
    		BankAccount account = accounts.get(index);
    		
    		// Set deposit interface
    		JFrame depositFrame = new JFrame("Deposit");
    		depositFrame.setPreferredSize(new Dimension(250, 130));
    		
    		JPanel depositPanel = new JPanel();
    		depositPanel.setLayout(new BorderLayout());
    		
    		// Amount field with panel
    		JLabel depositMessage = new JLabel("Enter the amount:", JLabel.LEFT);
    		JTextField amountField = new JTextField(15);
    		amountField.setPreferredSize(new Dimension(200, 25));
    		JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    		amountPanel.add(depositMessage);
    		amountPanel.add(amountField);
    		
    		// Create button panel
    		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
    		// Deposit button - proceeds deposit
    		JButton depositButton = new JButton("Deposit");
    		depositButton.setEnabled(false);
    	        depositButton.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	                try {
    	                    // Check the amount
    	                    String amountEntered = amountField.getText().trim();
    	                    double amountDeposit = Double.parseDouble(amountEntered);
    	                    
    	                    // Process the deposit
    	                    BankAccount newAccount = account.deposit(amountDeposit);
    	                    
    	                    // Update data
    	                    accounts.set(getIndex(account.getId()), newAccount);
    	                    save();
    	                    
    	                    // Show information message
    	                    String depositMessage = "Deposit - " + amountDeposit + "₽, current balance - " + newAccount.getBalance() + "₽";
    	                    JOptionPane.showMessageDialog(depositFrame, depositMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
    	                }
    	                // Catch an exception if happened (mostly for invalid amount)
    	                catch (IllegalArgumentException ex) {
    	                    JOptionPane.showMessageDialog(depositFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	                }
    	                depositFrame.dispose();
    	            }
    	        });
    		
    	        // Deposit button cannot be pressed until amount is entered
    	        amountField.getDocument().addDocumentListener(new DocumentListener() {
	            public void changedUpdate(DocumentEvent e) {
	        		updateButtonState();
	            }
	            
	            public void removeUpdate(DocumentEvent e) {
	                updateButtonState();
	            }
	            
	            public void insertUpdate(DocumentEvent e) {
	                updateButtonState();
	            }
	            
	            private void updateButtonState() {
	        		String amountEntered = amountField.getText().trim(); 
	        		depositButton.setEnabled(!amountEntered.isEmpty());
	            }
	        });
    	        
    	        // Return button - closes window
    		JButton back = new JButton("Return");
    		back.setBounds(95, 70, 73, 30);
    		back.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent e) {
    			depositFrame.dispose();
    		    }
    		});
    		
    		// Assemble interface
    		buttonPanel.add(depositButton);
    		buttonPanel.add(back);
    		depositPanel.add(amountPanel, BorderLayout.CENTER);
    		depositPanel.add(buttonPanel, BorderLayout.SOUTH);
    		depositFrame.add(depositPanel, BorderLayout.CENTER);
    		
    		// Set rules for window
    		depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		depositFrame.setResizable(false);
    		depositFrame.pack();
    		depositFrame.setLocationRelativeTo(null);
    		depositFrame.setVisible(true);
    	    }
    	});
    	
    	withdraw = new JButton("Withdraw");
    	withdraw.setPreferredSize(new Dimension(300,50));
    	withdraw.setMaximumSize(new Dimension(300,50));
    	withdraw.setAlignmentX(Component.CENTER_ALIGNMENT);
    	withdraw.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Update information about current account
    		BankAccount account = accounts.get(index);
    		
    		// Set withdraw interface
    		JFrame withdrawFrame = new JFrame();
    		withdrawFrame.setPreferredSize(new Dimension(250, 130));
    		
    		JPanel withdrawPanel = new JPanel();
    		withdrawPanel.setLayout(new BorderLayout());
    		
    		// Amount field with panel
    		JLabel withdrawMessage = new JLabel("Enter the amount:", JLabel.LEFT);
    		JTextField amountField = new JTextField(15);
    		amountField.setPreferredSize(new Dimension(200, 25));
    		JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    		mainPanel.add(withdrawMessage);
    		mainPanel.add(amountField);
    		
    		// Create button panel
    		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
    		// Withdraw button - proceeds withdrawal
    		JButton withdrawButton = new JButton("Withdraw");
    		withdrawButton.setEnabled(false);
    	        withdrawButton.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	        		// Try to get the amount
    	                try {
    	                    // Check the amount
    	                    String amountEntered = amountField.getText().trim();
	                    double amountWithdraw = Double.parseDouble(amountEntered);
	                    
	                    // Process the withdrawal
	                    BankAccount newAccount = account.withdraw(amountWithdraw);
	                    
	                    // Update data
	                    accounts.set(getIndex(account.getId()), newAccount);
	                    save();
	                    
	                    // Show information message
	                    String withdrawMessage = "Withdraw - " + amountWithdraw + "₽, current balance - " + newAccount.getBalance() + "₽";
	                    JOptionPane.showMessageDialog(withdrawFrame, withdrawMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
    	                }
    	                // Catch an exception if happened (mostly for invalid amount)
    	                catch (IllegalArgumentException | IllegalStateException ex) {
    	                    JOptionPane.showMessageDialog(withdrawFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	                }
    	                withdrawFrame.dispose();
    	            }
    	        });
    	        
    	        // Withdraw button cannot be pressed until amount is entered
    	        amountField.getDocument().addDocumentListener(new DocumentListener() {
	            public void changedUpdate(DocumentEvent e) {
	        		updateButtonState();
	            }
	            public void removeUpdate(DocumentEvent e) {
	                updateButtonState();
	            }
	            public void insertUpdate(DocumentEvent e) {
	                updateButtonState();
	            }
	            
	            private void updateButtonState() {
	        		String amountEntered = amountField.getText().trim(); 
	        		withdrawButton.setEnabled(!amountEntered.isEmpty());
	            }
	        });
    	        
    	        // Return button - closes window
    		JButton back = new JButton("Return");
    		back.setBounds(95, 70, 73, 30);
    		back.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent e) {
    			withdrawFrame.dispose();
    		    }
    		});
    		
    		// Assemble interface
    		buttonPanel.add(withdrawButton);
    		buttonPanel.add(back);
    		withdrawPanel.add(mainPanel, BorderLayout.CENTER);
    		withdrawPanel.add(buttonPanel, BorderLayout.SOUTH);
    		withdrawFrame.add(withdrawPanel, BorderLayout.CENTER);
    		
    		// Set rules for window
    		withdrawFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		withdrawFrame.setResizable(false);
    		withdrawFrame.pack();
    		withdrawFrame.setLocationRelativeTo(null);
    		withdrawFrame.setVisible(true);
    	    }
    	});
    	
    	transfer = new JButton("Transfer money");
    	transfer.setPreferredSize(new Dimension(300,50));
    	transfer.setMaximumSize(new Dimension(300,50));
    	transfer.setAlignmentX(Component.CENTER_ALIGNMENT);
    	transfer.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Update information about current account
    		BankAccount account = accounts.get(index);
    		
    		// Set transfer interface
    		JFrame transferFrame = new JFrame("Transfer money");
    		transferFrame.setPreferredSize(new Dimension(300, 195));
    		
    		JPanel transferPanel = new JPanel();
    	        transferPanel.setLayout(new BorderLayout());
    	        
    	        // Recepient selection field with panel
    	        JLabel transferMessage = new JLabel("Choose the recepient:", JLabel.LEFT);    	        
    	        JPanel recepientPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    	        recepientPanel.add(transferMessage);
    	        
    	        // Create list of accounts to choose from without the current one
    	        ArrayList<BankAccount> otherAccounts = new ArrayList<>(accounts);
    	        otherAccounts.remove(account);
    	        
    	        // Notify user if there are no other accounts and without opportunity to proceed
        	    	if (otherAccounts.isEmpty()) {
                    JOptionPane.showMessageDialog(transferFrame, "No accounts are available", "Notification", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
        	    	
        	    	// Visualize account list with single selection
        	    	JList<BankAccount> recepientList = new JList<>(otherAccounts.toArray(new BankAccount[0]));
        	        recepientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        	        recepientList.setFixedCellWidth(240);
        	        
        	        // Create scroll pane for list
        	        JScrollPane listScroller = new JScrollPane(recepientList);
        	        listScroller.setPreferredSize(new Dimension(280, 80));
        	        listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        	        
        	        // Amount field with panel
        	        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        	        JLabel amountLabel = new JLabel("Enter the amount:");
        	        JTextField amountField = new JTextField(15);
        	        amountField.setPreferredSize(new Dimension(200, 25));
        	        
        	        // Add text and field to the panel
        	        amountPanel.add(amountLabel);
        	        amountPanel.add(amountField);
        	        
        	        // Assemble interface
        	        JPanel mainPanel = new JPanel();
        	        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        	        mainPanel.add(recepientPanel);
        	        mainPanel.add(listScroller);
        	        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        	        mainPanel.add(amountPanel);
        	        
        	        // Create button panel
        	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        	        
        	        // Transfer button - proceeds transfer
        	        JButton transferButton = new JButton("Transfer");
        	        transferButton.setEnabled(false);
        	        transferButton.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent e) {
        	        		// Try to process the transfer
        	        		try {
        	        		    // Get the recepient from the list
        	        		    BankAccount recepient = recepientList.getSelectedValue();
        	        		    int recepientId = recepient.getId();
        	        		    
        	        		    // Check the amount
        	        		    String amountEntered = amountField.getText().trim();
        	        		    double amountTransfer = Double.parseDouble(amountEntered);
        	        		    
        	        		    // Calculate the comission
        	        		    double commission = amountTransfer * 0.0015;
        	        		    double total = amountTransfer + commission;
        	        		    
        	        		    // Process the transfer
        	        		    BankAccount newAccount = account.sending(total, recepientId);
        	        		    BankAccount newRecepient = recepient.receiving(amountTransfer, account.getId());
        	        		    
        	        		    // Update data
        	        		    accounts.set(getIndex(account.getId()), newAccount);
        	        		    accounts.set(getIndex(recepientId), newRecepient);   
        	        		    save();
        	        		    
        	        		    // Show information message
        	        		    String transferMessage = "Deduction of comission - " + commission + "₽ \n" + "Sending - " + amountTransfer + "₽, current balance - " + newAccount.getBalance() + "₽ ";
        	        		    JOptionPane.showMessageDialog(transferFrame, transferMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);        
        	        		}
        	        		// Catch an exception if happened (mostly for invalid amount)
        	        		catch (IllegalArgumentException | IllegalStateException | NoSuchElementException ex) {
        	        		    JOptionPane.showMessageDialog(transferFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        	        		}
        	            }
        	        });
        	        
        	        // Return button - closes window
        	        JButton back = new JButton("Return");
        	        back.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent e) {
        	                transferFrame.dispose();
        	            }
        	        });
        	        
        	        // First conditions to enable transfer button
        	        recepientList.addListSelectionListener(new ListSelectionListener() {
        	            public void valueChanged(ListSelectionEvent e) {
        	                if (!e.getValueIsAdjusting()) {
        	                    String amountEntered = amountField.getText().trim();
        	                    BankAccount selectedRecepient = recepientList.getSelectedValue();
        	                    transferButton.setEnabled(selectedRecepient != null && !amountEntered.isEmpty());
        	                }
        	        		
        	            }
        	        });
        	        
        	        // Second condition to enable transfer button
        	        amountField.getDocument().addDocumentListener(new DocumentListener() {
        	            public void changedUpdate(DocumentEvent e) {
        	                updateButtonState();
        	            }
        	            
        	            public void removeUpdate(DocumentEvent e) {
        	                updateButtonState();
        	            }
        	            
        	            public void insertUpdate(DocumentEvent e) {
        	                updateButtonState();
        	            }
        	            
        	            private void updateButtonState() {
        	                String amountEntered = amountField.getText().trim();
        	                BankAccount selectedRecepient = recepientList.getSelectedValue();
        	                transferButton.setEnabled(selectedRecepient != null && !amountEntered.isEmpty());
        	            }
        	        });
        	        
        	        // Assemble interface
        	        buttonPanel.add(transferButton);
        	        buttonPanel.add(back);
        	        transferPanel.add(mainPanel, BorderLayout.CENTER);
        	        transferPanel.add(buttonPanel, BorderLayout.SOUTH);
        	        transferFrame.add(transferPanel, BorderLayout.CENTER);
        	        
        	        // Set rules for window
        	        transferFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        	        transferFrame.setResizable(false);
        	        transferFrame.pack();
        	        transferFrame.setLocationRelativeTo(null);
        	        transferFrame.setVisible(true);
    	    }
    	});
    	
    	transactions = new JButton("Display transactions");
    	transactions.setPreferredSize(new Dimension(300,50));
    	transactions.setMaximumSize(new Dimension(300,50));
    	transactions.setAlignmentX(Component.CENTER_ALIGNMENT);
    	transactions.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Update information about current account
    		BankAccount account = accounts.get(index);
    		
    		// Set filter interface
    		JFrame filterFrame = new JFrame("Filters");
    		filterFrame.setPreferredSize(new Dimension(300, 220));
    		
    		JPanel mainPanel = new JPanel();
    	        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    	        
    		JPanel filterPanel = new JPanel();
    		filterPanel.setLayout(new BorderLayout());
    		
    		// Create panel for choosing type of transactions
    	        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    		JLabel typeMessage = new JLabel("Transaction Type:", JLabel.LEFT);
    	        
    		// Selection of transactions type
    		String[] options = {"None", "Deposit", "Withdraw", "Sending", "Receiving"};
    		JComboBox<String> typeBox = new JComboBox<>(options);
    		typeBox.setPreferredSize(new Dimension(150, 25));
    		typeBox.setSelectedIndex(0);
    		
    		// Add parts to panel
    	        typePanel.add(typeMessage);
    	        typePanel.add(typeBox);
    	        
    	        // Amount panel and message
    	        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	        JLabel amountMessage = new JLabel("Amount:", JLabel.LEFT);
    	        
    	        // Field for minimal amount filter
    	        JTextField minField = new JTextField();
    	        minField.setPreferredSize(new Dimension(70, 25));
    	        minField.setText("None");
    	        minField.setForeground(Color.GRAY);
    	        
    	        // Add text to the field
    	        minField.addFocusListener(new FocusAdapter() {
    	            public void focusGained(FocusEvent e) {
    	        		if (minField.getText().equals("None")) {
    	        		    minField.setText("");
	        		    minField.setForeground(Color.BLACK);
    	        		}
    	            }
    	            
    	            public void focusLost(FocusEvent e) {
    	        		if (minField.getText().isEmpty()) {
	        		    minField.setText("None");
	        		    minField.setForeground(Color.GRAY);
	        		}
    	            }
    	        });
    	        
    	        JLabel dashAmount = new JLabel(" - ", JLabel.LEFT);
    	        
    	        // Field for maximum amount filter
    	        JTextField maxField = new JTextField();
	        maxField.setPreferredSize(new Dimension(70, 25));
	        maxField.setText("Any");
	        maxField.setForeground(Color.GRAY);
	        
	        // Add text to the field
	        maxField.addFocusListener(new FocusAdapter() {
	            public void focusGained(FocusEvent e) {
	        		if (maxField.getText().equals("Any")) {
	        		    maxField.setText("");
	        		    maxField.setForeground(Color.BLACK);
	        		}
	            }
	            
	            public void focusLost(FocusEvent e) {
	        		if (maxField.getText().isEmpty()) {
	        		    maxField.setText("Any");
	        		    maxField.setForeground(Color.GRAY);
	        		}
	            }
	        });
    	        
	        // Add fields to the panel
	        amountPanel.add(amountMessage);
	        amountPanel.add(minField);
	        amountPanel.add(dashAmount);
	        amountPanel.add(maxField);
	        
	        // Date filter fields with text and calendar
	        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	        JLabel dateMessage = new JLabel("Date:", JLabel.LEFT);
    	        
    	        UtilDateModel startModel = new UtilDateModel();
    	        UtilDateModel endModel = new UtilDateModel();
            
    	        Properties properties = new Properties();
    	        properties.put("text.today", "Today");
            	properties.put("text.month", "Month");
            	properties.put("text.year", "Year");
    	       
            	JDatePanelImpl startDatePanel = new JDatePanelImpl(startModel, properties);
            	JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, null);
            	startDatePicker.getJFormattedTextField().setPreferredSize(new Dimension(70, 25));
            	
            	JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel, properties);
            	JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, null);
            	endDatePicker.getJFormattedTextField().setPreferredSize(new Dimension(70, 25));
            	
        	    	startDatePicker.getJFormattedTextField().setText("Start");
                startDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
                
        	    	startDatePicker.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Date selectedDate = (Date) startDatePicker.getModel().getValue();
                        if (selectedDate != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate = dateFormat.format(selectedDate);
                            startDatePicker.getJFormattedTextField().setText(formattedDate);
                            startDatePicker.getJFormattedTextField().setForeground(Color.BLACK);
                        } 
                        else {
                            dateMin = LocalDate.MIN;
                            startDatePicker.getJFormattedTextField().setText("Start");
                            startDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
                        }
                    }
                });
    	        
    	        JLabel dashDate = new JLabel(" - ", JLabel.LEFT);
    	        
    	        endDatePicker.getJFormattedTextField().setText("End");
                endDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
            
    	        endDatePicker.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	        		Date selectedDate = (Date) endDatePicker.getModel().getValue();
    	        		if (selectedDate != null) {
    	        		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	        		    String formattedDate = dateFormat.format(selectedDate);
    	        		    endDatePicker.getJFormattedTextField().setText(formattedDate);
    	        		    endDatePicker.getJFormattedTextField().setForeground(Color.BLACK);
    	        		} 
    	        		else {
    	        		    dateMax = LocalDate.MAX;
    	        		    endDatePicker.getJFormattedTextField().setText("End");
    	        		    endDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
                   	}
    	            }
    	        });
	        
    	        // Add fields with text to the special panel
	        datePanel.add(dateMessage);
	        datePanel.add(startDatePicker);
	        datePanel.add(dashDate);
	        datePanel.add(endDatePicker);	
	        
	        // Button panel with buttons
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
	        // Cancel button - deletes set filters
	        JButton cancel = new JButton("Clear filters");
	        cancel.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
    	    	        typeBox.setSelectedIndex(0);
    	    	        
    	    	        minField.setText("None");
    	    	        minField.setForeground(Color.GRAY);
    	    	        
    	    	        maxField.setText("Any");
    	    	        maxField.setForeground(Color.GRAY);
    	    	        
    	    	        startDatePicker.getModel().setValue(null);
    	    	        startDatePicker.getJFormattedTextField().setText("Start");
    	    	        startDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
    	    	        
    	    	        endDatePicker.getModel().setValue(null);
    	    	        endDatePicker.getJFormattedTextField().setText("End");
    	    	        endDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
	            }
	        });
	        
	        // Proceed filtering
    		JButton apply = new JButton("Apply");
    	        apply.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	        		// Define selected transaction type
    	        		String selected = (String) typeBox.getSelectedItem();
    	    	        transactionType = ((selected != "None") ? TransactionType.valueOf(selected) : null);
    	    	        
    	    	        // Define entered minimal amount
    	    	        String amountMinStr = minField.getText().trim();
    	    	        
    	    	        if (amountMinStr.equals("None") || amountMinStr.isEmpty()) {
    	    	            amountMin = 0;
    	    	        }
    	    	        else {
    	    	            try {
    	    	        		amountMin = Double.parseDouble(amountMinStr);
    	    	        		if (amountMin < 0) {
    	    	        		    JOptionPane.showMessageDialog(filterFrame, "Amount cannot be negative", "Error", JOptionPane.ERROR_MESSAGE);
    	                            return;
    	    	        		}
    	    	            }
    	    	            catch (NumberFormatException ex) {
    	    	        		JOptionPane.showMessageDialog(filterFrame, "Invalid format", "Error", JOptionPane.ERROR_MESSAGE);
    	    	        		return;
    	    	            }
    	    	        }
    	    	        
    	    	        // Define entered maximum amount
    	    	        String amountMaxStr = maxField.getText().trim();
    	    	        
	    	        if (amountMaxStr.equals("Any") || amountMaxStr.isEmpty()) {
	    	            amountMax = 0;
	    	        }
	    	        else {
	    	            try {
	    	        		amountMax = Double.parseDouble(amountMaxStr);
	    	        		if (amountMax < 0) {
	    	        		    JOptionPane.showMessageDialog(filterFrame, "Amount cannot be negative", "Error", JOptionPane.ERROR_MESSAGE);
	                            return;
	    	        		}
	    	        		if (amountMax != 0 && amountMin > amountMax) {
	    	                        JOptionPane.showMessageDialog(filterFrame, "Minimal amount cannot be greater than maximum amount", "Error", JOptionPane.ERROR_MESSAGE);
	    	                        return;
	    	                }
	    	            }
	    	            catch (NumberFormatException ex) {
	    	        		JOptionPane.showMessageDialog(filterFrame, "Invalid format", "Error", JOptionPane.ERROR_MESSAGE);
	    	        		return;
	    	            }
	    	        }
	    	        
	    	        // Define entered start and end dates
	    	        Date startDate = (Date) startDatePicker.getModel().getValue();
    	    	        dateMin = ((startDate != null) ? LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(startDate)) : LocalDate.MIN); 
    	    	        
    	    	        Date endDate = (Date) endDatePicker.getModel().getValue();
	    	        dateMax = ((endDate != null) ? LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(endDate)) : LocalDate.MAX);     	    	        
	    	        
	    	        if (endDate != null && startDate != null && startDate.after(endDate)) {
	    	            JOptionPane.showMessageDialog(filterFrame, "Start date cannot be after end date", "Error", JOptionPane.ERROR_MESSAGE);
	    	            return;
	    	        }
	    	        
	    	        // FIltering
    	    	        ArrayList<Transaction> filteredTransactions;
    	    	        if (transactionType == null && amountMin == 0 && amountMax == 0 && dateMin == LocalDate.MIN && dateMax == null) {
    	    	            filteredTransactions = account.getTransactionHistory();
    	    	        } 
    	    	        else {
    	    	            filteredTransactions = account.filterTransactions(transactionType, amountMin, amountMax, dateMin, dateMax);
    	    	        }
    	    	        
    	    	        // Show message if there were no matching transactions
    	    	        if (filteredTransactions == null || filteredTransactions.isEmpty()) {
    	    	            JOptionPane.showMessageDialog(filterFrame, "No matching transactions", "Notification", JOptionPane.INFORMATION_MESSAGE);
    	    	            return;
    	    	        }  
    	        		filterFrame.dispose();
    	        		
    	        		// Set interface for showing found transactions
    	        		JFrame historyFrame = new JFrame("History of transactions");
    	        		historyFrame.setPreferredSize(new Dimension(350,250));
    	        		JPanel historyPanel = new JPanel(new BorderLayout());
    	        		
    	        		// Create list for found transactions
    	        		JList<Transaction> transactionsList = new JList<>(filteredTransactions.toArray(new Transaction[0]));
    	        	        transactionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	        	        transactionsList.setFixedCellWidth(320);
    	        	        
    	        	        // Visualize list
    	        	        transactionsList.setCellRenderer(new DefaultListCellRenderer() {
    	        	            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	        	        		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    	        	        		if (value instanceof Transaction) {
    	        	        		    Transaction transaction = (Transaction) value;
    	        	        		    setText(account.printTransaction(transaction));
    	        	        		}
    	        	        		return this;
    	        	            }
    	        	        });
    	        	        
    	        	        // Create scroller
    	        	        JScrollPane listScroller = new JScrollPane(transactionsList);
    	                listScroller.setPreferredSize(new Dimension(330, 150));
    	                listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	                
    	                // Create panel to return to filters
    	                JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    	                
    	                // Return button - closes window
    	                JButton returnToFilters = new JButton("Return");
    	                returnToFilters.addActionListener(new ActionListener() {
    	                public void actionPerformed(ActionEvent e) {
    	                    historyFrame.dispose();
    	                    }
    	                });
    	                
    	                // Assemble interface
    	                returnPanel.add(returnToFilters);
    	                historyPanel.add(listScroller, BorderLayout.CENTER);
    	                historyPanel.add(returnPanel, BorderLayout.SOUTH);
    	                historyFrame.add(historyPanel, BorderLayout.CENTER);
    	                
    	                // Set rules for window
    	                historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	                historyFrame.setResizable(false);
    	                historyFrame.pack();
    	                historyFrame.setLocationRelativeTo(null);
    	                historyFrame.setVisible(true);
    	            }
    	        });
    	        
    	        // Return button - closes window
    	        JButton back = new JButton("Return");
    	        back.setBounds(95, 70, 73, 30);
		back.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			filterFrame.dispose();
		    }
		});
		
		// Assemble interface
		buttonPanel.add(cancel);
		buttonPanel.add(apply);
		buttonPanel.add(back);
		
    		mainPanel.add(typePanel);
    		mainPanel.add(amountPanel);
    		mainPanel.add(datePanel);
    		mainPanel.add(buttonPanel);
    		
    		filterPanel.add(mainPanel);
    		filterFrame.add(filterPanel);
    		
    		// Set rules for window
    		filterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		filterFrame.setResizable(true);
    		filterFrame.pack();
    		filterFrame.setLocationRelativeTo(null);
    		filterFrame.setVisible(true);
    	    }
    	});
    	
    	backToMenu = new JButton("Return to main menu");
    	backToMenu.setPreferredSize(new Dimension(300,50));
    	backToMenu.setMaximumSize(new Dimension(300,50));
    	backToMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
    	backToMenu.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Cause window closure if pressed
    		accountWindow.dispatchEvent(new WindowEvent(accountWindow, WindowEvent.WINDOW_CLOSING));
    	    }
    	});
    	
    	// Assemble interface
    	accountWindowPanel.add(Box.createVerticalGlue());
    	accountWindowPanel.add(balance);
    	accountWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    	accountWindowPanel.add(deposit);
    	accountWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    	accountWindowPanel.add(withdraw);
    	accountWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    	accountWindowPanel.add(transfer);
    	accountWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    	accountWindowPanel.add(transactions);
    	accountWindowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    	accountWindowPanel.add(backToMenu);
    	accountWindowPanel.add(Box.createVerticalGlue());
    	accountWindow.getContentPane().add(BorderLayout.CENTER, accountWindowPanel);
    	
    	// Ask confirmation to close the window
    	accountWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	accountWindow.addWindowListener(new WindowListener() {
    	    public void windowClosing(WindowEvent e) {
    		Object[] options = { "Yes", "No" };
    		int choice = JOptionPane.showOptionDialog(e.getWindow(), "Do you want to return?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,options[0]);    
    		if (choice == 0) {
    		    save();
    		    e.getWindow().setVisible(false);
    		    mainWindow.setVisible(true);
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
    	
    	// Set rules for window
    	accountWindow.setResizable(false);
    	accountWindow.setLocationRelativeTo(null);
    	accountWindow.setVisible(true);
    }
}
