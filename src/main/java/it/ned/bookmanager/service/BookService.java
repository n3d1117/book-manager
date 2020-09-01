package it.ned.bookmanager.service;

import it.ned.bookmanager.model.Book;

import java.util.List;

public interface BookService {
    public List<Book> getAllBooks();
    public Book findBookById(String id);
    public void saveBook(Book book);
    public void deleteBook(Book book);
}
