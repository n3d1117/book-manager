package it.ned.bookmanager.controller;

import java.util.Collections;
import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.BookManagerService;
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
    @Mock private BookManagerService service;

    @InjectMocks private BookManagerController controller;

    private static final Author AUTHOR_FIXTURE = new Author("1", "George Orwell");
    private static final Book BOOK_FIXTURE = new Book("1", "Animal Farm", 93);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /* Authors */

    @Test
    public void testAllAuthorsRetrieval() {
        List<Author> authors = Collections.singletonList(AUTHOR_FIXTURE);
        when(service.getAllAuthors()).thenReturn(authors);

        controller.allAuthors();

        verify(view).showAllAuthors(authors);
        verifyNoMoreInteractions(ignoreStubs(service));
    }

    /* Add author */

    @Test
    public void testAuthorAddedSuccessfully() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.addAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).saveAuthor(AUTHOR_FIXTURE);
        inOrder.verify(view).authorAdded(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAddedWhenAlreadyExisting() {
        Author georgeOrwellClone = new Author(BOOK_FIXTURE.getId(), "George Orwell's clone");
        when(service.findAuthorById(georgeOrwellClone.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.addAuthor(georgeOrwellClone);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(georgeOrwellClone.getId());
        inOrder.verify(view).authorNotAddedError(georgeOrwellClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete author */

    @Test
    public void testAuthorDeletedSuccessfully() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(service).deleteAuthor(AUTHOR_FIXTURE);
        inOrder.verify(view).authorDeleted(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorDeletionFailureWhenItDoesNotExist() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorNotDeletedError(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Assign author to book */

    @Test
    public void testAuthorAssignedToBookSuccessfully() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);
        when(service.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(service).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(service).assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verify(view).assignedAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAssignedToBookWhenAuthorDoesNotExist() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);
        when(service.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorNotAssignedToBookError(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAssignedToBookWhenBookDoesNotExist() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(null);
        when(service.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(service).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(view).authorNotAssignedToBookError(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAssignedToBookWhenAuthorAlreadyPresent() {
        when(service.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);
        when(service.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(service).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(view).authorAlreadyAssignedToBookError(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Books */

    @Test
    public void testAllBooksRetrieval() {
        List<Book> books = Collections.singletonList(BOOK_FIXTURE);
        when(service.getAllBooks()).thenReturn(books);

        controller.allBooks();

        verify(view).showAllBooks(books);
        verifyNoMoreInteractions(ignoreStubs(service));
    }

    /* Add book */

    @Test
    public void testBookAddedSuccessfully() {
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.addBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).saveBook(BOOK_FIXTURE);
        inOrder.verify(view).bookAdded(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookNotAddedWhenAlreadyExisting() {
        Book animalFarmClone = new Book(BOOK_FIXTURE.getId(), "Animal Farm, a clone", 93);
        when(service.findBookById(animalFarmClone.getId())).thenReturn(BOOK_FIXTURE);

        controller.addBook(animalFarmClone);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findBookById(animalFarmClone.getId());
        inOrder.verify(view).bookNotAddedError(animalFarmClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete book */

    @Test
    public void testBookDeletedSuccessfully() {
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(service).deleteBook(BOOK_FIXTURE);
        inOrder.verify(view).bookDeleted(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookDeletionFailureWhenItDoesNotExist() {
        when(service.findBookById(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(service, view);
        inOrder.verify(service).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookNotDeletedError(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

}