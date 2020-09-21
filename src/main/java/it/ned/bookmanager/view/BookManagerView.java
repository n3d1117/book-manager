package it.ned.bookmanager.view;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface BookManagerView {
    void showAllAuthors(List<Author> allAuthors);
    void showAllBooks(List<Book> allBooks);

    void authorAdded(Author author);
    void authorDeleted(Author author);
    void bookAdded(Book book);
    void bookDeleted(Book book);

    void deletedAllBooksForAuthor(Author author);

    void authorNotAddedBecauseAlreadyExistsError(Author existingAuthor);
    void authorNotDeletedBecauseNotFoundError(Author author);
    void bookNotAddedBecauseAlreadyExistsError(Book book);
    void bookNotDeletedBecauseNotFoundError(Book book);
}
