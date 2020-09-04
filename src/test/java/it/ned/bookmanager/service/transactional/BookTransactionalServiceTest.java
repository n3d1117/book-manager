package it.ned.bookmanager.service.transactional;

import java.util.Arrays;
import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.repository.RepositoryFactory;
import it.ned.bookmanager.transaction.TransactionCode;
import it.ned.bookmanager.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

public class BookTransactionalServiceTest {

    @Mock private TransactionManager transactionManager;
    @Mock private RepositoryFactory repositoryFactory;
    @Mock private BookRepository bookRepository;
    @InjectMocks private BookTransactionalService bookService;

    private static final Book BOOK_FIXTURE_1 = new Book("1", "Animal Farm", 93, "1");
    private static final Book BOOK_FIXTURE_2 = new Book("2", "1984", 283, "1");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(transactionManager.doInTransaction(any()))
                .thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
        when(repositoryFactory.createBookRepository()).thenReturn(bookRepository);
    }

    @Test
    public void testRetrieveAllBooks() {
        List<Book> books = Arrays.asList(BOOK_FIXTURE_1, BOOK_FIXTURE_2);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> retrievedBooks = bookService.findAll();

        assertEquals(books, retrievedBooks);
        verify(transactionManager).doInTransaction(any());
        verify(bookRepository).findAll();
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    public void testFindBookById() {
        when(bookRepository.findById(BOOK_FIXTURE_1.getId())).thenReturn(BOOK_FIXTURE_1);

        Book retrievedBook = bookService.findById(BOOK_FIXTURE_1.getId());

        assertEquals(BOOK_FIXTURE_1, retrievedBook);
        verify(transactionManager).doInTransaction(any());
        verify(bookRepository).findById(BOOK_FIXTURE_1.getId());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    public void testAddBook() {
        bookService.add(BOOK_FIXTURE_1);

        InOrder inOrder = inOrder(transactionManager, bookRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(bookRepository).add(BOOK_FIXTURE_1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAddBookWhenBookIsNull() {
        bookService.add(null);

        verify(transactionManager).doInTransaction(any());
        verifyNoInteractions(bookRepository);
    }

    @Test
    public void testDeleteBook() {
        bookService.delete(BOOK_FIXTURE_1.getId());

        InOrder inOrder = inOrder(transactionManager, bookRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(bookRepository).delete(BOOK_FIXTURE_1.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteAllBooksFromAuthorId() {
        Author georgeOrwell = new Author("1", "George Orwell");
        bookService.deleteAllBooksForAuthorId(georgeOrwell.getId());

        InOrder inOrder = inOrder(transactionManager, bookRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(bookRepository).deleteAllBooksForAuthorId(georgeOrwell.getId());
        inOrder.verifyNoMoreInteractions();
    }
}