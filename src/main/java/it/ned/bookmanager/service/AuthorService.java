package it.ned.bookmanager.service;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface AuthorService {
    List<Author> getAllAuthors();
    Author findAuthorById(String id);
    Author findAuthorFromBookId(String bookId);
    void addAuthor(Author author);
    void deleteAuthor(Author author);
    Book assignAuthorToBook(Author author, Book book);
    List<Book> allWrittenBooks(Author author);
}
