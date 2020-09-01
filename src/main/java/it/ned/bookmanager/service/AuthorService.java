package it.ned.bookmanager.service;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface AuthorService {
    public List<Author> getAllAuthors();
    public Author findAuthorById(String id);
    public Author findAuthorFromBookId(String bookId);
    public void saveAuthor(Author author);
    public void deleteAuthor(Author author);
    public void assignAuthorToBook(Author author, Book book);
}
