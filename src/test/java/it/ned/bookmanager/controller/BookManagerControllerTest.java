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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class BookManagerControllerTest {

    @Mock private BookManagerView view;
    @Mock private AuthorService authorService;
    @Mock private BookService bookService;

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
        when(authorService.getAllAuthors()).thenReturn(authors);

        controller.allAuthors();

        verify(view).showAllAuthors(authors);
        verifyNoMoreInteractions(ignoreStubs(authorService));
    }

    /* Add author */

    @Test
    public void testAuthorAddedSuccessfully() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.addAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).addAuthor(AUTHOR_FIXTURE);
        inOrder.verify(view).authorAdded(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAddedWhenAlreadyExisting() {
        Author georgeOrwellClone = new Author(BOOK_FIXTURE.getId(), "George Orwell's clone");
        when(authorService.findAuthorById(georgeOrwellClone.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.addAuthor(georgeOrwellClone);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findAuthorById(georgeOrwellClone.getId());
        inOrder.verify(view).authorNotAddedError(georgeOrwellClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete author */

    @Test
    public void testAuthorDeletedSuccessfully() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(authorService).deleteAuthor(AUTHOR_FIXTURE);
        inOrder.verify(view).authorDeleted(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorDeletionFailureWhenItDoesNotExist() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorNotDeletedError(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Assign author to book */

    @Test
    public void testAuthorAssignedToBookSuccessfully() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);
        when(authorService.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(authorService, bookService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(bookService).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(authorService).assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verify(view).assignedAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAssignedToBookWhenAuthorDoesNotExist() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);
        when(authorService.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(authorService, bookService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorDoesNotExistError(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAssignedToBookWhenBookDoesNotExist() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(null);
        when(authorService.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(authorService, bookService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(bookService).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookDoesNotExistError(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAuthorNotAssignedToBookWhenAuthorAlreadyPresent() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);
        when(authorService.findAuthorFromBookId(BOOK_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);

        controller.assignAuthorToBook(AUTHOR_FIXTURE, BOOK_FIXTURE);

        InOrder inOrder = inOrder(authorService, bookService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(bookService).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(view).authorAlreadyAssignedToBookError(AUTHOR_FIXTURE, BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Retrieve books from given author */

    @Test
    public void testRetrievedBooksFromAuthorWhenThereAreMany() {
        List<Book> writtenBooks = asList(BOOK_FIXTURE, new Book("2", "1984", 283));
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(authorService.allWrittenBooks(AUTHOR_FIXTURE)).thenReturn(writtenBooks);

        controller.allBooksFromAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(authorService).allWrittenBooks(AUTHOR_FIXTURE);
        inOrder.verify(view).showBooksFromAuthor(AUTHOR_FIXTURE, writtenBooks);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRetrievedBooksFromAuthorWhenThereAreNone() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(AUTHOR_FIXTURE);
        when(authorService.allWrittenBooks(AUTHOR_FIXTURE)).thenReturn(Collections.emptyList());

        controller.allBooksFromAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).showBooksFromAuthor(AUTHOR_FIXTURE, Collections.emptyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRetrievedBooksFromAuthorWhenAuthorDoesNotExist() {
        when(authorService.findAuthorById(AUTHOR_FIXTURE.getId())).thenReturn(null);

        controller.allBooksFromAuthor(AUTHOR_FIXTURE);

        InOrder inOrder = inOrder(authorService, view);
        inOrder.verify(authorService).findAuthorById(AUTHOR_FIXTURE.getId());
        inOrder.verify(view).authorDoesNotExistError(AUTHOR_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    /* Books */

    @Test
    public void testAllBooksRetrieval() {
        List<Book> books = Collections.singletonList(BOOK_FIXTURE);
        when(bookService.getAllBooks()).thenReturn(books);

        controller.allBooks();

        verify(view).showAllBooks(books);
        verifyNoMoreInteractions(ignoreStubs(bookService));
    }

    /* Add book */

    @Test
    public void testBookAddedSuccessfully() {
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.addBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).addBook(BOOK_FIXTURE);
        inOrder.verify(view).bookAdded(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookNotAddedWhenAlreadyExisting() {
        Book animalFarmClone = new Book(BOOK_FIXTURE.getId(), "Animal Farm, a clone", 93);
        when(bookService.findBookById(animalFarmClone.getId())).thenReturn(BOOK_FIXTURE);

        controller.addBook(animalFarmClone);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).findBookById(animalFarmClone.getId());
        inOrder.verify(view).bookNotAddedError(animalFarmClone);
        inOrder.verifyNoMoreInteractions();
    }

    /* Delete book */

    @Test
    public void testBookDeletedSuccessfully() {
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(BOOK_FIXTURE);

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(bookService).deleteBook(BOOK_FIXTURE);
        inOrder.verify(view).bookDeleted(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testBookDeletionFailureWhenItDoesNotExist() {
        when(bookService.findBookById(BOOK_FIXTURE.getId())).thenReturn(null);

        controller.deleteBook(BOOK_FIXTURE);

        InOrder inOrder = inOrder(bookService, view);
        inOrder.verify(bookService).findBookById(BOOK_FIXTURE.getId());
        inOrder.verify(view).bookNotDeletedError(BOOK_FIXTURE);
        inOrder.verifyNoMoreInteractions();
    }

}