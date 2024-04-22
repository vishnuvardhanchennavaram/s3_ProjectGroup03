package library.models;

import java.util.ArrayList;
import java.util.List;

import library.utils.FileUtil;

public class Borrow {
	private static final String FILE_PATH = "src/transactions.csv";
	
    private String bookId;
    private String userName;
    private String borrowDate;
    private String returnDate;

    public Borrow(String bookId, String userName, String borrowDate, String returnDate) {
        this.bookId = bookId;
        this.userName = userName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userName;
    }

    public void setUserId(String userId) {
        this.userName = userId;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }
    
    public String toCSV() {
        return bookId + "," + userName + "," + borrowDate + "," + returnDate;
    }
    
    public static List<Borrow> loadTransactionsFromCSV() {
        List<Borrow> t_list = new ArrayList<>();
        FileUtil fileUtil = FileUtil.getInstance();
        List<String> lines = fileUtil.readFile(FILE_PATH);
        for (String line : lines) {
            String[] data = line.split(",");
                if (data.length >= 4) {
                	String bookId = data[0].trim();
                    String userName = data[1].trim();
                    String borrowDate = data[2].trim();
                    String returnDate = data[3].trim();
                    Borrow transaction = new Borrow(bookId, userName, borrowDate, returnDate);
                    t_list.add(transaction);
                }
            }
        
        
        return t_list;
    }
    
}