package it.ned.bookmanager.controller;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.BookManagerService;
import it.ned.bookmanager.view.BookManagerView;

public class BookManagerController {

    private final BookManagerService service;
    private final BookManagerView view;

    public BookManagerController(BookManagerService service, BookManagerView view) {
        this.service = service;
        this.view = view;
    }

    public void allBooks() {
        view.showAllBooks(service.getAllBooks());
    }

    public void allAuthors() {
        view.showAllAuthors(service.getAllAuthors());
    }

    public void addAuthor(Author author) {
        if (!authorExists(author)) {
            service.saveAuthor(author);
            view.authorAdded(author);
        } else {
            view.authorNotAddedError(author);
        }
    }

    public void addBook(Book book) {
        if (!bookExists(book)) {
            service.saveBook(book);
            view.bookAdded(book);
        } else {
            view.bookNotAddedError(book);
        }
    }

    public void deleteAuthor(Author author) {
        if (authorExists(author)) {
            service.deleteAuthor(author);
            view.authorDeleted(author);
        } else {
            view.authorNotDeletedError(author);
        }
    }

    public void deleteBook(Book book) {
        if (bookExists(book)) {
            service.deleteBook(book);
            view.bookDeleted(book);
        } else {
            view.bookNotDeletedError(book);
        }
    }

    private boolean authorExists(Author author) {
        return service.findAuthorById(author.getId()) != null;
    }

    private boolean bookExists(Book book) {
        return service.findBookById(book.getId()) != null;
    }
}
