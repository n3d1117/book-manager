package it.ned.bookmanager.view;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface BookManagerView {
    void showAllBooks(List<Book> allBooks);
    void showAllAuthors(List<Author> allAuthors);
    void showBooksFromAuthor(Author author, List<Book> writtenBooks);

    void authorAdded(Author author);
    void authorDeleted(Author author);
    void bookAdded(Book book);
    void bookDeleted(Book book);
    void assignedAuthorToBook(Author author, Book book);

    void authorNotAddedError(Author author);
    void authorNotDeletedError(Author author);
    void bookNotAddedError(Book book);
    void bookNotDeletedError(Book book);
    void authorDoesNotExistError(Author author);
    void bookDoesNotExistError(Book book);
    void authorAlreadyAssignedToBookError(Author author, Book book);
}
