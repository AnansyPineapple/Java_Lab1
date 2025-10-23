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
    
    /** Contain all user accounts */
    private static ArrayList<BankAccount> accounts;
    
    /** Return index of account in collection according to given identifier */
    public static int getIndex(int id) {
	// Sort accounts until match found
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
     */
    public static int accountExists(int id) {
	// Sort accounts until match found
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
	// Try to open the file
	try {
	    File dataFile = new File("data.ser");
	    
	    // Truthify existence of data by creating file and/or maxId and accounts
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
	    // Open menu only if maxId and accounts are defined
	    mainMenu();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
    
    /** Save information into the file */
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
	// Create main window frame 350x270
	mainWindow = new JFrame("Main Menu");
	mainWindow.setSize(350, 270);
	
	// Create panel for buttons
	JPanel mainWindowPanel = new JPanel();
	mainWindowPanel.setLayout(new BoxLayout(mainWindowPanel, BoxLayout.Y_AXIS));
    	
	// Create four menu buttons
    	JButton choose, create, delete, exit;
    	
    	// Button to choose account 300x50
    	choose = new JButton("Choose bank account");
    	choose.setPreferredSize(new Dimension(300,50));
    	choose.setMaximumSize(new Dimension(300,50));
    	choose.setAlignmentX(Component.CENTER_ALIGNMENT);
    	choose.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Create new frame for selection
    		JFrame accountSelection = new JFrame("Select Account");
    		accountSelection.setLayout(new BorderLayout());
    		
    		// Truthify accounts list contains information by showing message otherwise
    		if (accounts == null || accounts.isEmpty()) {
    		    JOptionPane.showMessageDialog(accountSelection, "You don't have any accounts", "Notification", JOptionPane.INFORMATION_MESSAGE);
		    return;
    		}
    		else {
    		    // Set selection frame size
    		    accountSelection.setPreferredSize(new Dimension(300, 165));
    		    
    		    // Create main panel
    		    JPanel accountPanel = new JPanel(new FlowLayout());
    		    
    		    // Visualise account list with fixed width, only one account can be picked
    		    JList<BankAccount> accountList = new JList<>(accounts.toArray(new BankAccount[0]));
		    accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    accountList.setFixedCellWidth(240);
		    
		    // Create scroller 280x80, vertical bar appears only if accounts do not fit
		    JScrollPane listScroller = new JScrollPane(accountList);
		    listScroller.setPreferredSize(new Dimension(280, 80));
		    listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    // Create panel for buttons located on the right side
		    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		    
		    // Create return button with event handler
		    JButton back = new JButton("Return");
		    back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    // Close window if button is pressed
			    accountSelection.dispose();
			}
		    });
		    
		    // Create selection button with event handler
		    JButton select = new JButton("OK");
		    
		    // Cannot be pressed until an account is selected
		    select.setEnabled(false);
		    select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    // Make a link to selected account 
			    BankAccount selected = accountList.getSelectedValue();
			    
			    // Close current and main menu windows, open account window
			    accountSelection.dispose();
			    mainWindow.setVisible(false);
			    accountMenu(getIndex(selected.getId()));
			}
		    });
		    
		    // Add listener to the list
		    accountList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			    // Truthify an option is picked by enabling a select button
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
		    
		    // Add buttons to the panel
		    buttonPanel.add(select);
		    buttonPanel.add(back);
		    
		    // Add scroller and button panel to the main panel with certain placement
		    accountPanel.add(listScroller, BorderLayout.CENTER);
		    accountPanel.add(buttonPanel, BorderLayout.SOUTH);
		    
		    // Add panel to the frame
		    accountSelection.add(accountPanel, BorderLayout.CENTER);
    		}
    		
    		// Close window if cross is pressed
    		accountSelection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		
    		// Place window in the center of the screen
		accountSelection.setLocationRelativeTo(null);
		
		// Make window visible
		accountSelection.setVisible(true);
	    }
    	});
    	
    	// Button to create account 300x50
    	create = new JButton("Create bank account");
    	create.setPreferredSize(new Dimension(300,50));
    	create.setMaximumSize(new Dimension(300,50));
    	create.setAlignmentX(Component.CENTER_ALIGNMENT);
    	create.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Create new frame for creation
    		JFrame accountCreation = new JFrame();
		accountCreation.setLayout(new BorderLayout());
		
		// Set identifier for new account
		int id = maxId + 1;
		
		// Create account with zero balance and absence of transactions
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
    	
    	// Button to delete account 300x50
    	delete = new JButton("Delete bank account");
    	delete.setPreferredSize(new Dimension(300,50));
    	delete.setMaximumSize(new Dimension(300,50));
    	delete.setAlignmentX(Component.CENTER_ALIGNMENT);
    	delete.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		// Create new frame for deletion
    		JFrame accountDeletion = new JFrame("Select Account");
    		accountDeletion.setLayout(new BorderLayout());
    		
    		// Truthify accounts list contains information by showing message otherwise
    		if (accounts == null || accounts.isEmpty()) {
    		    JOptionPane.showMessageDialog(accountDeletion, "You don't have any accounts", "Notification", JOptionPane.INFORMATION_MESSAGE);
    		    return;
    		}
    		else {
    		    // Set deletion frame size
    		    accountDeletion.setPreferredSize(new Dimension(300, 165));
    		    
    		    // Crete main panel
    		    JPanel deletionPanel = new JPanel(new FlowLayout());
    		    
    		    // Visualize account list with fixed width, only one account can be picked
    		    JList<BankAccount> deletionList = new JList<>(accounts.toArray(new BankAccount[0]));
    		    deletionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    		    deletionList.setFixedCellWidth(240);
    		    
    		    // Create scroller 280x80, vertical bar appears only if accounts do not fit
    		    JScrollPane listScroller = new JScrollPane(deletionList);
    		    listScroller.setPreferredSize(new Dimension(280, 80));
    		    listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    		    
    		    // Create panel for buttons located on the right side
    		    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		    
    		    // Create return button with event handler
    		    JButton back = new JButton("Return");
    		    back.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    			    // Close window if button is pressed
    			    accountDeletion.dispose();
    			}
    		    });
    		    
    		    // Create deletion button with event handler
    		    JButton select = new JButton("Delete");
    		    
    		    // Cannot be pressed until an account is selected
    		    select.setEnabled(false);
    		    select.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    			    // Make a link to selected account
    			    BankAccount selected = deletionList.getSelectedValue();
    			    int id = selected.getId();
    			    
    			    // Create options for further warning
    			    Object[] options = {"Proceed", "Return"};
    			    
    			    // Make a warning
    			    int choice = JOptionPane.showOptionDialog(accountDeletion, "If you delete this account, your money will be lost! \nDo you want to proceed?", 
    				    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
    			    
    			    // Truthify user answered positively by closing window otherwise
    			    if (choice == JOptionPane.YES_OPTION) {
    				// Delete account from the list and update data
    				accounts.remove(getIndex(id));
    				save();
    				
    				// Remove account from visible list instantly
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
    		    
    		    
    		    // Add listener to the list
    		    deletionList.addListSelectionListener(new ListSelectionListener() {
    			public void valueChanged(ListSelectionEvent e) {
    			    // Truthify an option is picked by enabling a select button
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
    		    
    		    // Add buttons to the panel
    		    buttonPanel.add(select);
    		    buttonPanel.add(back);
    		    
    		    // Add scroller and button panel to the main panel with certain placement
    		    deletionPanel.add(listScroller, BorderLayout.CENTER);
    		    deletionPanel.add(buttonPanel, BorderLayout.SOUTH);
    		    
    		    // Add panel to the frame
    		    accountDeletion.add(deletionPanel, BorderLayout.CENTER);
    		}
    		
    		// Close window if cross is pressed
    		accountDeletion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		
    		// Disable the opportunity to resize the window
    		accountDeletion.setResizable(false);
    		
    		// Resize the window if elements do not fit
    		accountDeletion.pack();
    		
    		// Place window in the center of the screen
    		accountDeletion.setLocationRelativeTo(null);
    		
    		// Make window visible
    		accountDeletion.setVisible(true);
    	    }
    	});
    	
    	//HERE
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
    	mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
    
    public static void accountMenu(int index) {
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
    		BankAccount account = accounts.get(index);
    		
    		JFrame balanceDemo = new JFrame("Balance");
    		balanceDemo.setPreferredSize(new Dimension(250, 100));
    		
    		JPanel balanceMessagePanel = new JPanel();
    		balanceMessagePanel.setLayout(new BorderLayout());
    		
    		String message = "Current balance: " + account.getBalance() + "₽";
    		JLabel balanceMessage = new JLabel(message, JLabel.CENTER);
    		
    		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
    		JButton back = new JButton("Return");
    		back.setBounds(95, 70, 73, 30);
    		back.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent e) {
    			balanceDemo.dispose();
    			}
    		    });
    		
    		buttonPanel.add(back);
    		
    		balanceMessagePanel.add(balanceMessage, BorderLayout.CENTER);
    		balanceMessagePanel.add(buttonPanel, BorderLayout.SOUTH);
    		balanceDemo.add(balanceMessagePanel, BorderLayout.CENTER);
    		
    		balanceDemo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		balanceDemo.setResizable(true);
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
    		BankAccount account = accounts.get(index);
    		
    		JFrame depositFrame = new JFrame("Deposit");
    		depositFrame.setPreferredSize(new Dimension(250, 130));
    		
    		JPanel depositPanel = new JPanel();
    		depositPanel.setLayout(new BorderLayout());
    		
    		JLabel depositMessage = new JLabel("Enter the amount:", JLabel.LEFT);
    		JTextField amountField = new JTextField(15);
    		amountField.setPreferredSize(new Dimension(200, 25));
    		
    		JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    		mainPanel.add(depositMessage);
    		mainPanel.add(amountField);
    		
    		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
    		JButton depositButton = new JButton("Deposit");
    		depositButton.setEnabled(false);
    	        depositButton.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	                try {
    	                    String amountEntered = amountField.getText().trim();
    	                    double amountDeposit = Double.parseDouble(amountEntered);
    	                    BankAccount newAccount = account.deposit(amountDeposit);
    	                    accounts.set(getIndex(account.getId()), newAccount);
    	                    save();
    	                    
    	                    String depositMessage = "Deposit - " + amountDeposit + "₽, current balance - " + newAccount.getBalance() + "₽";
    	                    JOptionPane.showMessageDialog(depositFrame, depositMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
    	                }
    	                catch (IllegalArgumentException ex) {
    	                    JOptionPane.showMessageDialog(depositFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	                }
    	                depositFrame.dispose();
    	            }
    	        });
    		
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
    	        
    		JButton back = new JButton("Return");
    		back.setBounds(95, 70, 73, 30);
    		back.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent e) {
    			depositFrame.dispose();
    			}
    		    });
    		
    		buttonPanel.add(depositButton);
    		buttonPanel.add(back);
    		
    		depositPanel.add(mainPanel, BorderLayout.CENTER);
    		depositPanel.add(buttonPanel, BorderLayout.SOUTH);
    		depositFrame.add(depositPanel, BorderLayout.CENTER);
    		
    		depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		depositFrame.setResizable(true);
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
    		BankAccount account = accounts.get(index);
    		
    		JFrame withdrawFrame = new JFrame();
    		withdrawFrame.setPreferredSize(new Dimension(250, 130));
    		
    		JPanel withdrawPanel = new JPanel();
    		withdrawPanel.setLayout(new BorderLayout());
    		
    		JLabel withdrawMessage = new JLabel("Enter the amount:", JLabel.LEFT);
    		JTextField amountField = new JTextField(15);
    		amountField.setPreferredSize(new Dimension(200, 25));
    		
    		JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    		mainPanel.add(withdrawMessage);
    		mainPanel.add(amountField);
    		
    		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
    		JButton withdrawButton = new JButton("Withdraw");
    	        withdrawButton.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	                try {
    	                    String amountEntered = amountField.getText().trim();
	                    double amountWithdraw = Double.parseDouble(amountEntered);
	                    BankAccount newAccount = account.withdraw(amountWithdraw);
	                    accounts.set(getIndex(account.getId()), newAccount);
	                    save();
	                    
	                    String withdrawMessage = "Withdraw - " + amountWithdraw + "₽, current balance - " + newAccount.getBalance() + "₽";
	                    JOptionPane.showMessageDialog(withdrawFrame, withdrawMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);
    	                }
    	                catch (IllegalArgumentException | IllegalStateException ex) {
    	                    JOptionPane.showMessageDialog(withdrawFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	                }
    	                withdrawFrame.dispose();
    	            }
    	        });
    		
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
    	        
    		JButton back = new JButton("Return");
    		back.setBounds(95, 70, 73, 30);
    		back.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent e) {
    			withdrawFrame.dispose();
    			}
    		    });
    		
    		buttonPanel.add(withdrawButton);
    		buttonPanel.add(back);
    		
    		withdrawPanel.add(mainPanel, BorderLayout.CENTER);
    		withdrawPanel.add(buttonPanel, BorderLayout.SOUTH);
    		withdrawFrame.add(withdrawPanel, BorderLayout.CENTER);
    		
    		withdrawFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		withdrawFrame.setResizable(true);
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
    		BankAccount account = accounts.get(index);
    		
    		JFrame transferFrame = new JFrame("Transfer money");
    		transferFrame.setPreferredSize(new Dimension(300, 195));
    		
    		JPanel transferPanel = new JPanel();
    	        transferPanel.setLayout(new BorderLayout());
    	        
    	        JLabel transferMessage = new JLabel("Choose the recepient:", JLabel.LEFT);
    	        JPanel recepientPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    	        recepientPanel.add(transferMessage);
    	        
    	        ArrayList<BankAccount> otherAccounts = new ArrayList<>(accounts);
    	        otherAccounts.remove(account);
    	        
        	    	if (otherAccounts.isEmpty()) {
                    JOptionPane.showMessageDialog(transferFrame, "No accounts are available", "Notification", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
        	    	
        	    	JList<BankAccount> recepientList = new JList<>(otherAccounts.toArray(new BankAccount[0]));
        	        recepientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        	        recepientList.setFixedCellWidth(240);
        	        
        	        JScrollPane listScroller = new JScrollPane(recepientList);
        	        listScroller.setPreferredSize(new Dimension(280, 80));
        	        listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        	        
        	        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        	        JLabel amountLabel = new JLabel("Enter the amount:");
        	        JTextField amountField = new JTextField(15);
        	        amountField.setPreferredSize(new Dimension(200, 25));
        	        
        	        amountPanel.add(amountLabel);
        	        amountPanel.add(amountField);
        	        
        	        JPanel mainPanel = new JPanel();
        	        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        	        mainPanel.add(recepientPanel);
        	        mainPanel.add(listScroller);
        	        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        	        mainPanel.add(amountPanel);
        	        
        	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        	        
        	        JButton transferButton = new JButton("Transfer");
        	        transferButton.setEnabled(false);
        	        transferButton.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent e) {
        	        		try {
        	        		    BankAccount recepient = recepientList.getSelectedValue();
        	        		    int recepientId = recepient.getId();
        	        		    
        	        		    String amountEntered = amountField.getText().trim();
        	        		    double amountTransfer = Double.parseDouble(amountEntered);
        	        		    
        	        		    double commission = amountTransfer * 0.0015;
        	        		    double total = amountTransfer + commission;
        	        		    
        	        		    BankAccount newAccount = account.sending(total, recepientId);
        	        		    BankAccount newRecepient = recepient.receiving(amountTransfer, account.getId());
        	        		    accounts.set(getIndex(account.getId()), newAccount);
        	        		    accounts.set(getIndex(recepientId), newRecepient);
        	        		    
        	        		    save();
        	        		    
        	        		    String transferMessage = "Deduction of comission - " + commission + "₽ \n" + "Sending - " + amountTransfer + "₽, current balance - " + newAccount.getBalance() + "₽ ";
        	        		    JOptionPane.showMessageDialog(transferFrame, transferMessage, "Notification", JOptionPane.INFORMATION_MESSAGE);        
        	        		}
        	        		catch (IllegalArgumentException | IllegalStateException | NoSuchElementException ex) {
        	        		    JOptionPane.showMessageDialog(transferFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        	        		}
        	            }
        	        });
        	        
        	        JButton back = new JButton("Return");
        	        back.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent e) {
        	                transferFrame.dispose();
        	            }
        	        });
        	        
        	        recepientList.addListSelectionListener(new ListSelectionListener() {
        	            public void valueChanged(ListSelectionEvent e) {
        	                if (!e.getValueIsAdjusting()) {
        	                    String amountEntered = amountField.getText().trim();
        	                    BankAccount selectedRecepient = recepientList.getSelectedValue();
        	                    transferButton.setEnabled(selectedRecepient != null && !amountEntered.isEmpty());
        	                }
        	        		
        	            }
        	        });
        	        
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
        	        
        	        buttonPanel.add(transferButton);
        	        buttonPanel.add(back);
        	        
        	        transferPanel.add(mainPanel, BorderLayout.CENTER);
        	        transferPanel.add(buttonPanel, BorderLayout.SOUTH);
        	        transferFrame.add(transferPanel, BorderLayout.CENTER);
        	        
        	        transferFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        	        transferFrame.setResizable(true);
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
    		BankAccount account = accounts.get(index);
    		
    		JFrame filterFrame = new JFrame("Filters");
    		filterFrame.setPreferredSize(new Dimension(300, 220));
    		
    		JPanel filterPanel = new JPanel();
    		filterPanel.setLayout(new BorderLayout());
    		
    		JPanel mainPanel = new JPanel();
    	        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    		
    	        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    		JLabel typeMessage = new JLabel("Transaction Type:", JLabel.LEFT);
    	        
    		String[] options = {"None", "Deposit", "Withdraw", "Sending", "Receiving"};
    		JComboBox<String> typeBox = new JComboBox<>(options);
    		typeBox.setPreferredSize(new Dimension(150, 25));
    		
    		typeBox.setSelectedIndex(0);
    		
    	        typePanel.add(typeMessage);
    	        typePanel.add(typeBox);
    	        
    	        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	        JLabel amountMessage = new JLabel("Amount:", JLabel.LEFT);
    	        
    	        JTextField minField = new JTextField();
    	        minField.setPreferredSize(new Dimension(70, 25));
    	        minField.setText("None");
    	        minField.setForeground(Color.GRAY);
    	        
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
    	        
    	        JTextField maxField = new JTextField();
	        maxField.setPreferredSize(new Dimension(70, 25));
	        maxField.setText("Any");
	        maxField.setForeground(Color.GRAY);
	        
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
    	        
	        amountPanel.add(amountMessage);
	        amountPanel.add(minField);
	        amountPanel.add(dashAmount);
	        amountPanel.add(maxField);
	        
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
                        } else {
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
                    } else {
                        dateMax = LocalDate.MAX;
                        endDatePicker.getJFormattedTextField().setText("End");
                        endDatePicker.getJFormattedTextField().setForeground(Color.GRAY);
                    }
                }
    	        });
	        
	        datePanel.add(dateMessage);
	        datePanel.add(startDatePicker);
	        datePanel.add(dashDate);
	        datePanel.add(endDatePicker);	
	        
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		
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
	        
    		JButton apply = new JButton("Apply");
    	        apply.addActionListener(new ActionListener() {
    	            public void actionPerformed(ActionEvent e) {
    	        		String selected = (String) typeBox.getSelectedItem();
    	    	        transactionType = ((selected != "None") ? TransactionType.valueOf(selected) : null);
    	    	        
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
	    	        
	    	        Date startDate = (Date) startDatePicker.getModel().getValue();
    	    	        dateMin = ((startDate != null) ? LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(startDate)) : LocalDate.MIN); 
    	    	        
    	    	        Date endDate = (Date) endDatePicker.getModel().getValue();
	    	        dateMax = ((endDate != null) ? LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(endDate)) : LocalDate.MAX); 
    	    	        
	    	        if (endDate != null && startDate != null && startDate.after(endDate)) {
	    	            JOptionPane.showMessageDialog(filterFrame, "Start date cannot be after end date", "Error", JOptionPane.ERROR_MESSAGE);
	    	            return;
	    	        }
	    	        
    	    	        ArrayList<Transaction> filteredTransactions;
    	    	        if (transactionType == null && amountMin == 0 && amountMax == 0 && dateMin == LocalDate.MIN && dateMax == null) {
    	    	            filteredTransactions = account.getTransactionHistory();
    	    	        } 
    	    	        else {
    	    	            filteredTransactions = account.filterTransactions(transactionType, amountMin, amountMax, dateMin, dateMax);
    	    	        }
    	    	        
    	    	        if (filteredTransactions == null || filteredTransactions.isEmpty()) {
    	    	            JOptionPane.showMessageDialog(filterFrame, "No matching transactions", "Notification", JOptionPane.INFORMATION_MESSAGE);
    	    	            return;
    	    	        }
    	    	        
    	        		filterFrame.dispose();
    	        		
    	        		JFrame historyFrame = new JFrame("History of transactions");
    	        		historyFrame.setPreferredSize(new Dimension(350,250));
    	        		
    	        		JPanel historyPanel = new JPanel(new BorderLayout());
    	        		
    	        		JList<Transaction> transactionsList = new JList<>(filteredTransactions.toArray(new Transaction[0]));
    	        	        transactionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	        	        transactionsList.setFixedCellWidth(320);
    	        	        
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
    	        	        
    	        	        JScrollPane listScroller = new JScrollPane(transactionsList);
    	                listScroller.setPreferredSize(new Dimension(330, 150));
    	                listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	                
    	                JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    	                
    	                JButton returnToFilters = new JButton("Return");
    	                returnToFilters.addActionListener(new ActionListener() {
    	                public void actionPerformed(ActionEvent e) {
    	                    historyFrame.dispose();
    	                    }
    	                });
    	                
    	                returnPanel.add(returnToFilters);
    	                historyPanel.add(listScroller, BorderLayout.CENTER);
    	                historyPanel.add(returnPanel, BorderLayout.SOUTH);
    	                
    	                historyFrame.add(historyPanel, BorderLayout.CENTER);
    	                
    	                historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	                historyFrame.setResizable(true);
    	                historyFrame.pack();
    	                historyFrame.setLocationRelativeTo(null);
    	                historyFrame.setVisible(true);
    	            }
    	        });
    	        
    	        JButton back = new JButton("Return");
    	        back.setBounds(95, 70, 73, 30);
		back.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			filterFrame.dispose();
			}
		    });
		
		buttonPanel.add(cancel);
		buttonPanel.add(apply);
		buttonPanel.add(back);
		
    		mainPanel.add(typePanel);
    		mainPanel.add(amountPanel);
    		mainPanel.add(datePanel);
    		mainPanel.add(buttonPanel);
    		
    		filterPanel.add(mainPanel);
    		
    		filterFrame.add(filterPanel);

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
    		accountWindow.dispatchEvent(new WindowEvent(accountWindow, WindowEvent.WINDOW_CLOSING));
    	    }
    	});
    	
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
    	accountWindow.setResizable(false);
    	accountWindow.setLocationRelativeTo(null);
    	accountWindow.setVisible(true);
	}
}
