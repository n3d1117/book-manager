package it.ned.bookmanager.service.transactional;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.AuthorRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthorTransactionalServiceTest {

    @Mock private TransactionManager transactionManager;
    @Mock private RepositoryFactory repositoryFactory;
    @Mock private BookRepository bookRepository;
    @Mock private AuthorRepository authorRepository;
    @InjectMocks private AuthorTransactionalService authorService;

    private static final Author AUTHOR_FIXTURE_1 = new Author("1", "George Orwell");
    private static final Author AUTHOR_FIXTURE_2 = new Author("2", "Dan Brown");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(transactionManager.doInTransaction(any()))
                .thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
        when(repositoryFactory.createAuthorRepository()).thenReturn(authorRepository);
        when(repositoryFactory.createBookRepository()).thenReturn(bookRepository);
    }

    @Test
    public void testRetrieveAllAuthors() {
        List<Author> authors = Arrays.asList(AUTHOR_FIXTURE_1, AUTHOR_FIXTURE_2);
        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> retrievedBooks = authorService.getAllAuthors();

        assertEquals(authors, retrievedBooks);
        verify(transactionManager).doInTransaction(any());
    }

    @Test
    public void testFindAuthorById() {
        when(authorRepository.findById(AUTHOR_FIXTURE_1.getId())).thenReturn(AUTHOR_FIXTURE_1);

        Author retrieved = authorService.findAuthorById(AUTHOR_FIXTURE_1.getId());

        assertEquals(AUTHOR_FIXTURE_1, retrieved);
        verify(transactionManager).doInTransaction(any());
    }

    @Test
    public void testAddAuthor() {
        authorService.addAuthor(AUTHOR_FIXTURE_1);

        InOrder inOrder = inOrder(transactionManager, authorRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(authorRepository).add(AUTHOR_FIXTURE_1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAddAuthorWhenAuthorIsNull() {
        authorService.addAuthor(null);

        verify(transactionManager).doInTransaction(any());
        verifyNoInteractions(authorRepository);
    }

    @Test
    public void testDeleteAuthor() {
        authorService.deleteAuthor(AUTHOR_FIXTURE_1);

        InOrder inOrder = inOrder(transactionManager, authorRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(authorRepository).delete(AUTHOR_FIXTURE_1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteAuthorAndAssociatedBooks() {
        Book animalFarm = new Book("1", "Animal Farm", 93);
        Book nineteenEightFour = new Book("2", "1984", 283);
        List<Book> books = Arrays.asList(animalFarm, nineteenEightFour);

        when(authorRepository.allWrittenBooksForAuthor(AUTHOR_FIXTURE_1)).thenReturn(books);

        authorService.deleteAuthor(AUTHOR_FIXTURE_1);

        InOrder inOrder = inOrder(transactionManager, authorRepository, bookRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(bookRepository).delete(animalFarm);
        inOrder.verify(bookRepository).delete(nineteenEightFour);
        inOrder.verify(authorRepository).delete(AUTHOR_FIXTURE_1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteAuthorWhenAuthorIsNull() {
        authorService.deleteAuthor(null);

        verify(transactionManager).doInTransaction(any());
        verifyNoInteractions(authorRepository);
    }

    @Test
    public void testFindAuthorFromBookId() {
        Book book = new Book("1", "Animal Farm", 93);
        when(bookRepository.findById(book.getId())).thenReturn(book);
        when(authorRepository.findAuthorFromBookId(book.getId())).thenReturn(AUTHOR_FIXTURE_1);

        Author authorRetrieved = authorService.findAuthorFromBookId(book.getId());

        assertEquals(AUTHOR_FIXTURE_1, authorRetrieved);
        verify(transactionManager).doInTransaction(any());
    }

    @Test
    public void testFindAuthorFromBookIdFailsWhenBookDoesNotExist() {
        when(bookRepository.findById("1")).thenReturn(null);

        Author authorRetrieved = authorService.findAuthorFromBookId("1");

        assertNull(authorRetrieved);
        verify(transactionManager).doInTransaction(any());
        verifyNoInteractions(authorRepository);
    }

    @Test
    public void testAssignAuthorToBook() {
        Book book = new Book("1", "Animal Farm", 93);
        when(bookRepository.findById(book.getId())).thenReturn(book);
        when(authorRepository.assignAuthorToBook(AUTHOR_FIXTURE_1, book)).thenReturn(book);

        Book bookRetrieved = authorService.assignAuthorToBook(AUTHOR_FIXTURE_1, book);

        assertEquals(book, bookRetrieved);
        verify(transactionManager).doInTransaction(any());
    }

    @Test
    public void testAssignAuthorToBookFailsWhenBookDoesNotExist() {
        Book book = new Book("1", "Animal Farm", 93);
        when(bookRepository.findById(book.getId())).thenReturn(null);

        Book bookRetrieved = authorService.assignAuthorToBook(AUTHOR_FIXTURE_1, book);

        assertNull(bookRetrieved);
        verify(transactionManager).doInTransaction(any());
    }

    @Test
    public void testRetrieveAllBooksFromAuthor() {
        List<Book> books = Arrays.asList(
                new Book("1", "Animal Farm", 93),
                new Book("2", "1984", 283)
        );
        when(authorRepository.allWrittenBooksForAuthor(AUTHOR_FIXTURE_1)).thenReturn(books);

        List<Book> retrievedBooks = authorService.allWrittenBooks(AUTHOR_FIXTURE_1);

        assertEquals(books, retrievedBooks);
        verify(transactionManager).doInTransaction(any());
    }
}