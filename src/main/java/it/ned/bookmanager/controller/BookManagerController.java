package it.ned.bookmanager.controller;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.view.BookManagerView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
        view.showAllBooks(bookService.getAllBooks());
    }

    public void allAuthors() {
        LOGGER.debug(() -> "Showing all authors");
        view.showAllAuthors(authorService.getAllAuthors());
    }

    public void addAuthor(Author author) {
        LOGGER.debug(() -> String.format("Adding author %s", author.toString()));
        if (!authorExists(author)) {
            authorService.addAuthor(author);
            view.authorAdded(author);
        } else {
            view.authorNotAddedError(author);
        }
    }

    public void addBook(Book book) {
        LOGGER.debug(() -> String.format("Adding book %s", book.toString()));
        if (!bookExists(book)) {
            bookService.addBook(book);
            view.bookAdded(book);
        } else {
            view.bookNotAddedError(book);
        }
    }

    public void deleteAuthor(Author author) {
        LOGGER.debug(() -> String.format("Deleting author %s", author.toString()));
        if (authorExists(author)) {
            authorService.deleteAuthor(author);
            view.authorDeleted(author);
        } else {
            view.authorNotDeletedError(author);
        }
    }

    public void deleteBook(Book book) {
        LOGGER.debug(() -> String.format("Deleting book %s", book.toString()));
        if (bookExists(book)) {
            bookService.deleteBook(book);
            view.bookDeleted(book);
        } else {
            view.bookNotDeletedError(book);
        }
    }

    public void assignAuthorToBook(Author author, Book book) {
        LOGGER.debug(() -> String.format("Assigning author %s to book %s", author.toString(), book.toString()));
        if (!authorExists(author)) {
            view.authorDoesNotExistError(author);
        } else if (!bookExists(book)) {
            view.bookDoesNotExistError(book);
        } else if (bookHasAuthor(book)) {
            view.authorAlreadyAssignedToBookError(author, book);
        } else {
            authorService.assignAuthorToBook(author, book);
            view.assignedAuthorToBook(author, book);
        }
    }

    public void allBooksFromAuthor(Author author) {
        LOGGER.debug(() -> String.format("Showing all books from author %s", author.toString()));
        if (authorExists(author)) {
            List<Book> writtenBooks = authorService.allWrittenBooks(author);
            view.showBooksFromAuthor(author, writtenBooks);
        } else {
            view.authorDoesNotExistError(author);
        }
    }

    private boolean authorExists(Author author) {
        return authorService.findAuthorById(author.getId()) != null;
    }

    private boolean bookExists(Book book) {
        return bookService.findBookById(book.getId()) != null;
    }

    private boolean bookHasAuthor(Book book) {
        return authorService.findAuthorFromBookId(book.getId()) != null;
    }

}
