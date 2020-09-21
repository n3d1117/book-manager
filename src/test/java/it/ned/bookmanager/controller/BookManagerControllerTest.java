package it.ned.bookmanager.controller;

import java.util.Collections;
import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.exception.AuthorDuplicateException;
import it.ned.bookmanager.service.exception.AuthorNotFoundException;
import it.ned.bookmanager.service.exception.BookDuplicateException;
import it.ned.bookmanager.service.exception.BookNotFoundException;
import it.ned.bookmanager.view.BookManagerView;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

public class BookManagerControllerTest {

    @Mock private BookManagerView view;
    @Mock private AuthorService authorService;
    @Mock private BookService bookService;

    @InjectMocks private BookManagerController controller;

    private static final Author AUTHOR_FIXTURE = new Author("1", "George Orwell");
    private static final Book BOOK_FIXTURE = new Book("1", "Animal Farm", 93, "1");

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* Authors */

    @Test
    public void testAllAuthorsRetrieval() {
        List<Author> authors = Collections.singletonList(AUTHOR_FIXTURE);
        when(authorService.findAll()).thenReturn(authors);

        controller.allAuthors();

        verify(view).showAllAuthors(authors);
        verifyNoMoreInteractions(ignoreStubs(authorService));
    }

    /* Add author */

    @Test
    public void testAuthorAddedSuccessfully() {
        when(authorService.findById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.addAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).add(AUTHOR_FIXTURE);
        inOrder.verify(view).authorAdded(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAddedWhenAlreadyExisting() {
        Author georgeOrwellClone = new Author(AUTHOR_FIXTURE.getId(), "George Orwell's clone");
        doThrow(new AuthorDuplicateException("Author already exists", AUTHOR_FIXTURE))
                .when(authorService).add(georgeOrwellClone);

        controller.addAuthor(georgeOrwellClone);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).add(georgeOrwellClone);
        inOrder.verify(view).authorNotAddedBecauseAlreadyExistsError(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete author */

    @Test
    public void testAuthorDeletedSuccessfully() {
        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).delete(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).deletedAllBooksForAuthor(AUTHOR_FIXTURE);
        inOrder.verify(view).authorDeleted(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorDeletionFailureWhenItDoesNotExist() {
        doThrow(new AuthorNotFoundException("Author not found"))
                .when(authorService).delete(AUTHOR_FIXTURE.getId());

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).delete(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorNotDeletedBecauseNotFoundError(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Books */

    @Test
    public void testAllBooksRetrieval() {
        List<Book> books = Collections.singletonList(BOOK_FIXTURE);
        when(bookService.findAll()).thenReturn(books);

        controller.allBooks();

        verify(view).showAllBooks(books);
        verifyNoMoreInteractions(ignoreStubs(bookService));
    }

    /* Add book */

    @Test
    public void testBookAddedSuccessfully() {
        when(bookService.findById(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.addBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).add(BOOK_FIXTURE);
        inOrder.verify(view).bookAdded(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookNotAddedWhenAlreadyExisting() {
        Book animalFarmClone = new Book(BOOK_FIXTURE.getId(), "Animal Farm, a clone", 93, "1");
        doThrow(new BookDuplicateException("Book already exists"))
                .when(bookService).add(animalFarmClone);

        controller.addBook(animalFarmClone);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).add(animalFarmClone);
        inOrder.verify(view).bookNotAddedBecauseAlreadyExistsError(animalFarmClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete book */

    @Test
    public void testBookDeletedSuccessfully() {
        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).delete(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookDeleted(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookDeletionFailureWhenItDoesNotExist() {
        doThrow(new BookNotFoundException("Author not found"))
                .when(bookService).delete(BOOK_FIXTURE.getId());

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).delete(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookNotDeletedBecauseNotFoundError(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

}