package it.ned.bookmanager.view;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface BookManagerView {
    void showAllBooks(List<Book> allBooks);
    void showAllAuthors(List<Author> allAuthors);

    void authorAdded(Author author);
    void authorDeleted(Author author);
    void bookAdded(Book book);
    void bookDeleted(Book book);

    void authorNotAddedBecauseAlreadyExistsError(Author author);
    void authorNotDeletedBecauseNotFoundError(Author author);
    void bookNotAddedBecauseAlreadyExistsError(Book book);
    void bookNotDeletedBecauseNotFoundError(Book book);
}
