package library.controller;

import library.models.Books;
import library.utils.FileUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookController {
    private List<Books> booksList;
    private String FILE_PATH = "src/books.csv";
    public BookController() {
        // Initialize the book list with books loaded from CSV when the controller is created
        this.booksList = Books.loadBooksFromCSV();
    }

    // Add a new book to the list
    public void addBook(Books book) {
        this.booksList.add(book);
        updateCSV();
       
    }

    // Remove a book from the list by bookId
    public void removeBook(String bookId) {
        this.booksList.removeIf(book -> book.getBookId().equals(bookId));
        updateCSV();
       
    }

    // Sort books by a specified attribute using a lambda function
    public void sortBooks(Comparator<Books> comparator) {
        this.booksList.sort(comparator);
    }

    // Get a list of all books
    public List<Books> getBooksList() {
        return this.booksList;
    }

    
    // sorting books by title
    public void sortBooksByTitle() {
        sortBooks((book1, book2) -> book1.getBookTitle().compareTo(book2.getBookTitle()));
    }

    // Method to search books by title
    public List<Books> searchBooksByTitle(String title) {
        return booksList.stream()
                        .filter(book -> book.getBookTitle().toLowerCase().contains(title.toLowerCase()))
                        .collect(Collectors.toList());
    }

    // method to display all books
    public void displayBooks() {
        for (Books book : booksList) {
            System.out.println(book);
        }
    }
//    stream API to search by author
	public List<Books> searchAuthor(String author) {
		return booksList.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
	}
// stream API to search by publisher name
	public List<Books> searchPublisher(String publisher) {
		return booksList.stream()
                .filter(book -> book.getPublisher().toLowerCase().contains(publisher.toLowerCase()))
                .collect(Collectors.toList());
	}
//	method to get list of books that match the bookId
	public Books getBookById(String bookId) {
        for (Books book : booksList) {
            if (book.getBookId().equals(bookId)) {
                return book;
            }
        }
        return null;  // Return null if no book matches the given ID
    }
	private void updateCSV() {
        List<String> csvLines = new ArrayList<>();
        for (Books book : booksList) {
            csvLines.add(book.toCSV());
        }
        FileUtil.writeToCSV(FILE_PATH, csvLines);
    }
	
	
}