package it.ned.bookmanager.view;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface BookManagerView {
    void showAllBooks(List<Book> allBooks);
    void showAllAuthors(List<Author> allAuthors);
    void authorAdded(Author author);
    void authorNotAddedError(Author author);
    void bookAdded(Book book);
    void bookNotAddedError(Book book);
    void authorDeleted(Author author);
    void authorNotDeletedError(Author author);
    void bookDeleted(Book book);
    void bookNotDeletedError(Book book);
}
