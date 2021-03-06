package it.ned.bookmanager.view.swing.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

public class BookTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -762364697884733353L;

	private static class BookAuthorPair {

		private final Book book;
		private final Author author;

		public BookAuthorPair(Book book, Author author) {
			this.book = book;
			this.author = author;
		}

		public Book getBook() {
			return book;
		}

		public Author getAuthor() {
			return author;
		}
	}

	private final transient List<BookAuthorPair> books;

	private static final List<String> columns = Arrays.asList("Title", "Author", "Number of pages");

	public BookTableModel() {
		this.books = new ArrayList<>(Collections.emptyList());
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 2) {
			return Integer.class;
		}
		return String.class;
	}

	@Override
	public int getRowCount() {
		return books.size();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(int col) {
		return columns.get(col);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		BookAuthorPair bookAuthorPair = books.get(rowIndex);
		if (columnIndex == 0)
			return bookAuthorPair.getBook().getTitle();
		else if (columnIndex == 1)
			return bookAuthorPair.getAuthor().getName();
		else
			return bookAuthorPair.getBook().getNumberOfPages();
	}

	public Book getBookAt(int row) {
		return books.get(row).getBook();
	}

	public void addElement(Book book, Author author) {
		if (books.stream().noneMatch(o -> o.getBook().equals(book))) {
			books.add(new BookAuthorPair(book, author));
			books.sort(Comparator.comparing(BookAuthorPair::getBook));
			fireTableDataChanged();
		}
	}

	public void removeElement(Book book) {
		books.removeIf(bookAuthorPair -> bookAuthorPair.getBook().equals(book));
		fireTableDataChanged();
	}

	public void removeAllBooksFromAuthorId(String authorId) {
		books.removeIf(bookAuthorPair -> bookAuthorPair.getAuthor().getId().equals(authorId));
		books.sort(Comparator.comparing(BookAuthorPair::getBook));
		fireTableDataChanged();
	}
}
