package library.models;
import java.util.ArrayList;
import java.util.List;

import library.utils.FileUtil;

public class Books {
	private static final String FILE_PATH = "src/books.csv";
	
    private String bookId;
    private String bookTitle;
    private String author;
    private String isbn;
    private String publisher;

    public Books(String bookId, String bookTitle, String author, String isbn, String publisher) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }
    
    public static Books getBookByBookId(List<Books> bookList, String bookid) {
        for (Books book : bookList) {
            if (book.getBookId().equals(bookid)) {
                return book;
            }
        }
        return null; // If user with the given username is not found
    }

    public static Books getBookByTitle(List<Books> bookList, String title) {
        for (Books book : bookList) {
            if (book.getBookTitle().equals(title)) {
                return book;
            }
        }
        return null; // If user with the given username is not found
    }
    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

      
    public String toCSV() {
        return bookId + "," + bookTitle + "," + author + "," + isbn + "," + publisher;
    }

    public static List<Books> loadBooksFromCSV() {
        List<Books> bookList = new ArrayList<>();
        FileUtil fileUtil = FileUtil.getInstance();
        List<String> lines = fileUtil.readFile(FILE_PATH);
        for (String line : lines) {
            String[] data = line.split(",");
                if (data.length >= 5) {
                	String bookId = data[0].trim();
                    String bookTitle = data[1].trim();
                    String author = data[2].trim();
                    String isbn = data[3].trim();
                    String publisher = data[4].trim();
        
                    Books book = new Books(bookId, bookTitle, author, isbn, publisher);
                    bookList.add(book);
                }
            }
        
        
        return bookList;
    }
}
