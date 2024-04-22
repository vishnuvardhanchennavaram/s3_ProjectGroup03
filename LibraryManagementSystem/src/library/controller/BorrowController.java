package library.controller;

import library.models.Borrow;
import library.utils.FileUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BorrowController {
	private List<Borrow> borrows_list = new ArrayList<>();
	private String FILE_PATH = "src/transactions.csv";

	public BorrowController() {
		this.borrows_list = Borrow.loadTransactionsFromCSV();
	}

	public void createBorrow(String bookId, String userId, String borrowDate, String returnDate) {
		Borrow newBorrow = new Borrow(bookId, userId, borrowDate, returnDate);
		borrows_list.add(newBorrow);
		updateCSV();
	}
	
	public void removeBorrow(String bookId) {
		Iterator<Borrow> iterator = borrows_list.iterator();
	    while (iterator.hasNext()) {
	        Borrow borrow = iterator.next();
	        if (borrow.getBookId().equals(bookId)) {
	            iterator.remove();
	        }
	    }
	    updateCSV();
	}

	public List<Borrow> getAllCurrentBorrows() {
		return new ArrayList<>(borrows_list);
	}

	public Borrow getBorrowByBookId(String bookid) {
		for (Borrow borrow : borrows_list) {
			if (borrow.getBookId().equals(bookid)) {
				return borrow;
			}
		}
		return null;
	}
	
	public boolean getBookStatus(String bookid) {
		boolean found = false;
		for (Borrow borrow : borrows_list) {
			if (borrow.getBookId().equals(bookid)) {
				found = true;
			}

		}
		return found;
	}

	 private void updateCSV() {
	        List<String> csvLines = new ArrayList<>();
	        for (Borrow borrow : borrows_list) {
	            csvLines.add(borrow.toCSV());
	        }
	        FileUtil.writeToCSV(FILE_PATH, csvLines);
	    }
}