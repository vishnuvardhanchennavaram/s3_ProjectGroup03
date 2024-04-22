package library.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import library.controller.BookController;
import library.controller.BorrowController;
import library.models.Books;
import library.models.Borrow;
import library.models.Users;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

public class MemberPanel extends JPanel {
	private JPanel cards;
	private String loginPanelKey;
    private JLabel timeLabel;
    private JTable searchResultsTable;
    private JTable transactionsTable;
	private JPanel borrowedBooksPanel;
	private String username;

    public MemberPanel(String username, JPanel cards, String loginPanelKey) {
        super(new BorderLayout());
        this.username = username;
		this.cards = cards;
	    this.loginPanelKey = loginPanelKey;
	    initializeUI();
	}
	private void initializeUI() {
        Users user = Users.getUserByUsername(Users.loadUsersFromCSV(), username);
        add(createHeader(user), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        createSearchBooksPanel();
        borrowedBooksPanel = createBorrowedBooksPanel();
        tabbedPane.addTab("Search Books", createSearchBooksPanel());
        tabbedPane.addTab("Borrowed Books", createBorrowedBooksPanel());
        add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
            	showNoBooks();
                
            } else if (tabbedPane.getSelectedIndex() == 1) {
                viewCurrentLoans();
            }
        });
        
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 0, 1000); // Update every second
    }

    private JPanel createHeader(Users user) {
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFirstName() + " " + user.getLastName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
	JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        // System time label
        timeLabel = new JLabel(getCurrentTime());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        rightPanel.add(timeLabel, BorderLayout.EAST);

	JButton logOutButton = new JButton("Log Out");
        logOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(logOutButton);
        logOutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out...", "Logout", JOptionPane.INFORMATION_MESSAGE);
            CardLayout cl = (CardLayout) cards.getLayout();
            cl.show(cards, loginPanelKey);
        });
		
        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private void updateTime() {
        timeLabel.setText(getCurrentTime());
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }
    
    private String getTodaysDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(new Date());
    }
    
    private String getDueDate(String borrowedDate) {
    	try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = dateFormat.parse(borrowedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 30); // Add 30 days to the borrowed date
            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace(); // Handle parsing exceptions appropriately
            return null; // Return null if parsing fails
        }
    }
    

    private JPanel createSearchBooksPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // rows, cols, hgap, vgap

        // Labels and text fields for searching
        JLabel titleLabel = new JLabel("Book Title:");
        JTextField titleField = new JTextField();
        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();
        JLabel publisherLabel = new JLabel("Publisher:");
        JTextField publisherField = new JTextField();

        formPanel.add(titleLabel);
        formPanel.add(titleField);
        formPanel.add(authorLabel);
        formPanel.add(authorField);
        formPanel.add(publisherLabel);
        formPanel.add(publisherField);

        // Configure button panel with GridBagLayout for more control
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        gbc.insets = new Insets(5, 0, 5, 5); // Top and bottom padding with some right margin

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        JButton borrowButton = new JButton("Borrow");

        gbc.gridx = 0; // Position for the "Search" button
        buttonPanel.add(searchButton, gbc);

        gbc.gridx = 1; // Position for the "Show All" button
        buttonPanel.add(showAllButton, gbc);

        gbc.gridx = 2; // Position for the "Borrow" button
        buttonPanel.add(borrowButton, gbc);

        searchButton.addActionListener(e -> performSearch(titleField.getText(), authorField.getText(), publisherField.getText()));
        showAllButton.addActionListener(e -> showAllBooks());
        borrowButton.addActionListener(e -> borrowSelectedBook());

        // Table to display search results
        searchResultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);

        // Adding components to search panel
        searchPanel.add(formPanel, BorderLayout.NORTH);
        searchPanel.add(buttonPanel, BorderLayout.CENTER);
        searchPanel.add(scrollPane, BorderLayout.SOUTH);

        return searchPanel;
    }

    private void borrowSelectedBook() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow != -1) {
            String bookStatus = searchResultsTable.getValueAt(selectedRow, 3).toString();
            if ("Available".equals(bookStatus)) { // Correct string comparison
                String title = searchResultsTable.getValueAt(selectedRow, 0).toString();
                Books book = Books.getBookByTitle(Books.loadBooksFromCSV(), title);
                BorrowController borrowController = new BorrowController();
    			 List<Borrow> current_list = borrowController.getAllCurrentBorrows();
                     for (Borrow b : current_list) {
                    	 if(b.getUserId().equals(username)) {
                    		 JOptionPane.showMessageDialog(this, "User can borrow only 1 book at a time. Please return existing book to borrow new one.", "Warning", JOptionPane.WARNING_MESSAGE);
                    		 return;
                    	 }
                     }
    			String borrowedDate = getTodaysDate();
                String returnDate = getDueDate(borrowedDate);
                borrowController.createBorrow(book.getBookId(), username, borrowedDate, returnDate);
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showAllBooks();
            } else {
                JOptionPane.showMessageDialog(this, "This book is currently unavailable.", "Unavailable", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.", "Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createBorrowedBooksPanel() {
    	JPanel borrowedBooksPanel = new JPanel(new BorderLayout());
        transactionsTable = new JTable();
        JScrollPane transactionScrollPane = new JScrollPane(transactionsTable);
        borrowedBooksPanel.add(transactionScrollPane, BorderLayout.CENTER);
        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> returnSelectedBook());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);

        // Adding components to the borrowedBooksPanel
        borrowedBooksPanel.add(transactionScrollPane, BorderLayout.CENTER);
        borrowedBooksPanel.add(buttonPanel, BorderLayout.SOUTH);
        viewCurrentLoans();
        return borrowedBooksPanel;
    }

    private void viewCurrentLoans() {
        List<Borrow> transactionList = Borrow.loadTransactionsFromCSV();
        String[] columnNames = { "Book Title", "User Name", "Borrowed Date", "Due Date" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Borrow entry : transactionList) {
            String bookId = entry.getBookId();
            String userId = entry.getUserId();
            if (userId.equals(username)) {
            	Books book = Books.getBookByBookId(Books.loadBooksFromCSV(), bookId);
                Users user = Users.getUserByUsername(Users.loadUsersFromCSV(), userId);
                String userFullName = user.getFirstName() + " " + user.getLastName();
                String title = book.getBookTitle();
                Object[] rowData = { title, userFullName, entry.getBorrowDate(), entry.getReturnDate() };
                model.addRow(rowData);
            }
            
        }
        transactionsTable.setModel(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        transactionsTable.setRowSorter(sorter);
    }
    
    
    

    private void performSearch(String title, String author, String publisher) {
		List<Books> results = new ArrayList<>();
		BookController books = new BookController();
		// Depending on the input fields, call the appropriate search method
		if (!title.isEmpty() && author.isEmpty() && publisher.isEmpty()) {
			results = books.searchBooksByTitle(title);
		} else if (title.isEmpty() && !author.isEmpty() && publisher.isEmpty()) {
			results = books.searchAuthor(author);
		} else if (title.isEmpty() && author.isEmpty() && !publisher.isEmpty()) {
			results = books.searchPublisher(publisher);
		} else {
			JOptionPane.showMessageDialog(this, "No such Book Present.", "Search Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update the table with the results

		updateSearchResultsTable(results);

	}

    private void showNoBooks() {
    	List<Books> results = new ArrayList<>();
		updateSearchResultsTable(results);
    }
    
    private void showAllBooks() {
    	BookController books = new BookController();
		List<Books> results = books.getBooksList(); // Assume getAllBooks returns all books
		updateSearchResultsTable(results);
    }

    private void returnSelectedBook() {
    	   int selectedRow = transactionsTable.getSelectedRow();
           if (selectedRow != -1) {
        	   String title = transactionsTable.getValueAt(selectedRow, 0).toString();
               Books book = Books.getBookByTitle(Books.loadBooksFromCSV(), title);
               String book_id = book.getBookId();
               BorrowController borrowController = new BorrowController();
               borrowController.removeBorrow(book_id);
           }
           viewCurrentLoans();
    }
    
    
    
    
    private void updateSearchResultsTable(List<Books> results) {
        String[] columnNames = { "Title", "Author", "Publisher", "Available" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Books book : results) {
        	String status;
			BorrowController borrow = new BorrowController();
			if(borrow.getBookStatus(book.getBookId())) {
				status = "Unavailable";
			}
			else {
				status = "Available";
			}
            model.addRow(new Object[] { book.getBookTitle(), book.getAuthor(), book.getPublisher(), status });
        }
        searchResultsTable.setModel(model);
    }
}