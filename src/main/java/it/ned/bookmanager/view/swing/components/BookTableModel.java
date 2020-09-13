package it.ned.bookmanager.view.swing.components;

import it.ned.bookmanager.model.Book;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookTableModel extends AbstractTableModel {

    private final transient List<Book> books;
    private static final String[] columnNames = {"Title", "Author", "Number of pages"};

    public BookTableModel() {
        this.books = new ArrayList<>(Collections.emptyList());
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value;
        Book book = books.get(rowIndex);
        if (columnIndex == 0)
            value = book.getTitle();
        else if (columnIndex == 1)
            value = book.getAuthorId();
        else
            value = book.getNumberOfPages().toString();
        return value;
    }

    public void addElement(Book bookToAdd) {
        books.add(bookToAdd);
        Collections.sort(books);
        fireTableDataChanged();
    }
}