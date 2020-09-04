package it.ned.bookmanager.controller;

import java.util.Collections;
import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.view.BookManagerView;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        MockitoAnnotations.initMocks(this);
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
        Author georgeOrwellClone = new Author(BOOK_FIXTURE.getId(), "George Orwell's clone");
        when(authorService.findById(georgeOrwellClone.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.addAuthor(georgeOrwellClone);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findById(georgeOrwellClone.getId());
        inOrder.verify(view).authorNotAddedError(georgeOrwellClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete author */

    @Test
    public void testAuthorDeletedSuccessfully() {
        when(authorService.findById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findById(AUTHOR_FIXTURE.getId());
        inOrder.verify(authorService).delete(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorDeleted(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorDeletionFailureWhenItDoesNotExist() {
        when(authorService.findById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorNotDeletedError(AUTHOR_FIXTURE);
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
        when(bookService.findById(animalFarmClone.getId())).thenReturn(BOOK_FIXTURE);

        controller.addBook(animalFarmClone);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).findById(animalFarmClone.getId());
        inOrder.verify(view).bookNotAddedError(animalFarmClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete book */

    @Test
    public void testBookDeletedSuccessfully() {
        when(bookService.findById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).findById(BOOK_FIXTURE.getId());
        inOrder.verify(bookService).delete(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookDeleted(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookDeletionFailureWhenItDoesNotExist() {
        when(bookService.findById(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).findById(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookNotDeletedError(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

}