package it.ned.bookmanager.controller;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.view.BookManagerView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BookManagerController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final BookManagerView view;

    private static final Logger LOGGER = LogManager.getLogger(BookManagerController.class);

    public BookManagerController(AuthorService authorService, BookService bookService, BookManagerView view) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.view = view;
    }

    public void allBooks() {
        LOGGER.debug(() -> "Showing all books");
        view.showAllBooks(bookService.findAll());
    }

    public void allAuthors() {
        LOGGER.debug(() -> "Showing all authors");
        view.showAllAuthors(authorService.findAll());
    }

    public void addAuthor(Author author) {
        LOGGER.debug(() -> String.format("Adding author %s", author.toString()));
        if (!authorExists(author)) {
            authorService.add(author);
            view.authorAdded(author);
        } else {
            view.authorNotAddedError(author);
        }
    }

    public void addBook(Book book) {
        LOGGER.debug(() -> String.format("Adding book %s", book.toString()));
        if (!bookExists(book)) {
            bookService.add(book);
            view.bookAdded(book);
        } else {
            view.bookNotAddedError(book);
        }
    }

    public void deleteAuthor(Author author) {
        LOGGER.debug(() -> String.format("Deleting author %s", author.toString()));
        if (authorExists(author)) {
            authorService.delete(author.getId());
            view.authorDeleted(author);
        } else {
            view.authorNotDeletedError(author);
        }
    }

    public void deleteBook(Book book) {
        LOGGER.debug(() -> String.format("Deleting book %s", book.toString()));
        if (bookExists(book)) {
            bookService.delete(book.getId());
            view.bookDeleted(book);
        } else {
            view.bookNotDeletedError(book);
        }
    }

    private boolean authorExists(Author author) {
        return authorService.findById(author.getId()) != null;
    }

    private boolean bookExists(Book book) {
        return bookService.findById(book.getId()) != null;
    }

}
