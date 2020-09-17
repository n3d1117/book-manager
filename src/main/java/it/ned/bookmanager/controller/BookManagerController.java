package it.ned.bookmanager.controller;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.exception.AuthorDuplicateException;
import it.ned.bookmanager.service.exception.AuthorNotFoundException;
import it.ned.bookmanager.service.exception.BookDuplicateException;
import it.ned.bookmanager.service.exception.BookNotFoundException;
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
        try {
            authorService.add(author);
            view.authorAdded(author);
        } catch(AuthorDuplicateException exception) {
            view.authorNotAddedBecauseAlreadyExistsError(author);
        }
    }

    public void addBook(Book book) {
        LOGGER.debug(() -> String.format("Adding book %s", book.toString()));
        try {
            bookService.add(book);
            view.bookAdded(book);
        } catch(BookDuplicateException exception) {
            view.bookNotAddedBecauseAlreadyExistsError(book);
        }
    }

    public void deleteAuthor(Author author) {
        LOGGER.debug(() -> String.format("Deleting author %s", author.toString()));
        try {
            authorService.delete(author.getId());
            view.deletedAllBooksForAuthor(author);
            view.authorDeleted(author);
        } catch(AuthorNotFoundException exception) {
            view.authorNotDeletedBecauseNotFoundError(author);
        }
    }

    public void deleteBook(Book book) {
        LOGGER.debug(() -> String.format("Deleting book %s", book.toString()));
        try {
            bookService.delete(book.getId());
            view.bookDeleted(book);
        } catch(BookNotFoundException exception) {
            view.bookNotDeletedBecauseNotFoundError(book);
        }
    }

}
