package it.ned.bookmanager.service;

import it.ned.bookmanager.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    Book findBookById(String id);
    void addBook(Book book);
    void deleteBook(Book book);
}
