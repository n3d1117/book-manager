package it.ned.bookmanager.service;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.Collections;
import java.util.List;

public class BookManagerService {

    public List<Book> getAllBooks() {
        return Collections.emptyList();
    }

    public List<Author> getAllAuthors() {
        return Collections.emptyList();
    }

    public Author findAuthorById(String id) {
        return null;
    }

    public Book findBookById(String id) {
        return null;
    }

    public Author findAuthorFromBookId(String bookId) { return null; }

    public void saveAuthor(Author author) {
    }

    public void saveBook(Book book) {
    }

    public void deleteAuthor(Author author) {
    }

    public void deleteBook(Book book) {
    }

    public void assignAuthorToBook(Author author, Book book) {
    }

}
