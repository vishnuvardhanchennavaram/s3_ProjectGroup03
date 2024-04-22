package library.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;
import javax.swing.table.JTableHeader;
import library.controller.BookController;
import library.controller.BorrowController;
import library.models.Books;
import library.models.Borrow;
import library.models.Users;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LibrarianPanel extends JPanel {
    private JPanel cards;
    private String loginPanelKey;
    private JLabel timeLabel;
    private JTable usersTable;
    private JTable transactionsTable;
    private static JTable booksTable;
    private String username;

    public LibrarianPanel(String username, JPanel cards, String loginPanelKey) {
        super(new BorderLayout());
        this.cards = cards;
        this.username = username;
        this.loginPanelKey = loginPanelKey;
        initializeUI();
    }

    private void initializeUI() {
        Users user = Users.getUserByUsername(Users.loadUsersFromCSV(), username);
        add(createHeader(user), BorderLayout.NORTH);
        add(createFunctionPanel(), BorderLayout.CENTER);
        manageBooks();
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
        rightPanel.add(timeLabel);

        JButton logOutButton = new JButton("Log Out");
        logOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center align component
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

    private JPanel createFunctionPanel() {
        JPanel functionPanel = new JPanel(new BorderLayout());

        // Create a tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create panels for managing users, managing books, and transactions
        JPanel manageUsersPanel = new JPanel(new BorderLayout());
        JPanel manageBooksPanel = new JPanel(new BorderLayout());
        JPanel transactionsPanel = new JPanel(new BorderLayout());

        // Add the panels to the tabbed pane
        tabbedPane.addTab("Manage Books", manageBooksPanel);
        tabbedPane.addTab("Transactions", transactionsPanel);
        tabbedPane.addTab("View Users", manageUsersPanel);

        // Add a ChangeListener to the tabbed pane
        tabbedPane.addChangeListener(e -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            int selectedIndex = sourceTabbedPane.getSelectedIndex();
            if (selectedIndex == 0) {
                manageBooks();
            } else if (selectedIndex == 1) {
                viewCurrentLoans();
            } else if (selectedIndex == 2) {
                manageUsers();
            }
        });

        // Add the tabbed pane to the function panel
        functionPanel.add(tabbedPane, BorderLayout.CENTER);

        // Populate the manage books panel
        booksTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(booksTable);
        manageBooksPanel.add(bookScrollPane, BorderLayout.CENTER);

        // Populate the manage users panel
        usersTable = new JTable();
        JScrollPane userScrollPane = new JScrollPane(usersTable);
        manageUsersPanel.add(userScrollPane, BorderLayout.CENTER);

        transactionsTable = new JTable();
        JScrollPane transactionScrollPane = new JScrollPane(transactionsTable);
        transactionsPanel.add(transactionScrollPane, BorderLayout.CENTER);

        // Create buttons for managing books
        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> {
            JPanel addBookPanel = new AddBookPanel(cards, "LIBRARIAN_PANEL");
            cards.add(addBookPanel, "Add_Book");
            CardLayout cardLayout = (CardLayout) cards.getLayout();
            cardLayout.show(cards, "Add_Book");
            cards.revalidate();
            cards.repaint();
        });

        JButton deleteButton = new JButton("Remove Book");
        deleteButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow != -1) {
                String bookid = (String) booksTable.getValueAt(selectedRow, 0);
                BookController bookController = new BookController();
                bookController.removeBook(bookid);
                manageBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        manageBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        return functionPanel;
    }

    private void manageUsers() {
        java.util.List<Users> userList = Users.loadUsersFromCSV();
        String[] columnNames = { "User Type", "First Name", "Last Name", "Email", "Phone", "Username" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Add data to the table model
        for (Users user : userList) {
            Object[] rowData = { user.getUserType(), user.getFirstName(), user.getLastName(), user.getEmail(),
                    user.getPhNo(), user.getUserName() };
            model.addRow(rowData);
        }

        // Set the table model
        usersTable.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        // Custom Comparator to sort by "First Name"
        Comparator<String> firstNameComparator = (a, b) -> a.compareToIgnoreCase(b);
        sorter.setComparator(1, firstNameComparator);

        usersTable.setRowSorter(sorter);

        // Disable table header sorting interactions
        JTableHeader header = usersTable.getTableHeader();
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Override click events to do nothing
            }
        });

        // Sort based on the first name by default
        sorter.toggleSortOrder(1); // Always sorts based on the first name
    }

    private void viewCurrentLoans() {
        java.util.List<Borrow> t_list = Borrow.loadTransactionsFromCSV();
        String[] columnNames = { "Book Title", "User Name", "Borrowed Date", "Due Date" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Borrow entry : t_list) {
            String book_id = entry.getBookId();
            String user_name = entry.getUserId();
            Books book = Books.getBookByBookId(Books.loadBooksFromCSV(), book_id);
            Users user = Users.getUserByUsername(Users.loadUsersFromCSV(), user_name);
            String user_full_name = user.getFirstName() + " " + user.getLastName();
            String title = book.getBookTitle();
            Object[] rowData = { title, user_full_name, entry.getBorrowDate(), entry.getReturnDate() };
            model.addRow(rowData);
        }

        // Set the table model
        transactionsTable.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        transactionsTable.setRowSorter(sorter);
    }

    public static void manageBooks() {
        java.util.List<Books> bookList = Books.loadBooksFromCSV();
        String[] columnNames = { "Book ID", "Title", "Author", "ISBN", "Publisher", "Status" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Add data to the table model
        for (Books book : bookList) {
            String status;
            BorrowController borrow = new BorrowController();
            if (borrow.getBookStatus(book.getBookId())) {
                status = "Unavailable";
            } else {
                status = "Available";
            }
            Object[] rowData = { book.getBookId(), book.getBookTitle(), book.getAuthor(), book.getIsbn(), book.getPublisher(), status };
            model.addRow(rowData);
        }

        // Set the table model
        booksTable.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        booksTable.setRowSorter(sorter);
    }
}