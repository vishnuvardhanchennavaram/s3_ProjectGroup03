package library.views;

import javax.swing.*;

import library.controller.BookController;
import library.models.Books;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddBookPanel extends JPanel {
	private JPanel cards;
	private JTextField bookId;
	private JTextField title;
	private JTextField author;
	private JTextField isbn;
	private JTextField publisher;

	AddBookPanel(JPanel cards, String librarianPanel) {
		this.cards = cards; // Assign the reference to the cards panel
		initializeUI();
	}

	private void initializeUI() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(5, 5, 5, 5);
		bookId = addLabelAndTextField("Book ID:", constraints, 1);
		title = addLabelAndTextField("Title:", constraints, 2);
		author = addLabelAndTextField("Author:", constraints, 3);
		isbn = addLabelAndTextField("ISBN:", constraints, 4);
		publisher = addLabelAndTextField("Publisher:", constraints, 5);

		JButton backButton = new JButton("Back");
		backButton.addActionListener(e -> {
			((CardLayout) cards.getLayout()).show(cards, "LIBRARIAN_PANEL");
		});

		JButton saveButton = new JButton("save");
		saveButton.addActionListener(e -> initiateAddBook());

		constraints.gridy = 7;
		constraints.gridx = 0;
		add(backButton, constraints);
		constraints.gridx = 1;
		add(saveButton, constraints);

	}

	private void initiateAddBook() {
		Map<String, String> bookData = new LinkedHashMap<>();
		String book_id = bookId.getText();
		String book_title = title.getText();
		String book_author = author.getText();
		String book_isbn = isbn.getText();
		String book_publisher = publisher.getText();

		if (book_id.isEmpty() || book_title.isEmpty() || book_author.isEmpty() || book_isbn.isEmpty() || book_publisher.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
	        return; // Exit the method if any field is empty
	    }
		
		// Create the new book object
		Books book = new Books(book_id, book_title, book_author, book_isbn, book_publisher);

		if (addBook(book)) {
			JOptionPane.showMessageDialog(this, "Book added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
			LibrarianPanel.manageBooks();
			((CardLayout) cards.getLayout()).show(cards, "LIBRARIAN_PANEL");

		} else {
			JOptionPane.showMessageDialog(this, "Adding the book failed");
		}

	}

	public static boolean addBook(Books book) {
		BookController bookController = new BookController();
		if(isUnique(book)) {
			bookController.addBook(book);
			return true;
		}
		return false;

	}

	private static boolean isUnique(Books book) {
		List<Books> bookList = Books.loadBooksFromCSV();
		for (Books b : bookList) {
			if (b.getBookId().equals(book.getBookId())) {
				return false;
			}
		}

		for (Books b : bookList) {
			if (b.getBookTitle().equals(book.getBookTitle()) && b.getAuthor().equals(book.getAuthor())) {
				return false;
			}
		}

		return true;

	}

	private JTextField addLabelAndTextField(String labelText, GridBagConstraints constraints, int gridy) {
		JLabel label = new JLabel(labelText);
		constraints.gridx = 0;
		constraints.gridy = gridy;
		add(label, constraints);
		JTextField textField = new JTextField(20);
		constraints.gridx = 1;
		add(textField, constraints);
		return textField;
	}

}
