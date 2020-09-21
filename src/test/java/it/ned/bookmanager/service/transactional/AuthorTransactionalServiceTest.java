package it.ned.bookmanager.service.transactional;

import java.util.Arrays;
import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.repository.AuthorRepository;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.repository.RepositoryFactory;
import it.ned.bookmanager.service.exception.AuthorDuplicateException;
import it.ned.bookmanager.service.exception.AuthorNotFoundException;
import it.ned.bookmanager.transaction.TransactionCode;
import it.ned.bookmanager.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        MockitoAnnotations.openMocks(this);
        when(transactionManager.doInTransaction(any()))
                .thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
        when(repositoryFactory.createAuthorRepository()).thenReturn(authorRepository);
        when(repositoryFactory.createBookRepository()).thenReturn(bookRepository);
    }

    @Test
    public void testRetrieveAllAuthors() {
        List<Author> authors = Arrays.asList(AUTHOR_FIXTURE_1, AUTHOR_FIXTURE_2);
        when(authorRepository.findAll()).thenReturn(authors);

        List<Author> retrievedAuthors = authorService.findAll();

        assertEquals(authors, retrievedAuthors);
        verify(transactionManager).doInTransaction(any());
        verify(authorRepository).findAll();
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testFindAuthorById() {
        when(authorRepository.findById(AUTHOR_FIXTURE_1.getId())).thenReturn(AUTHOR_FIXTURE_1);

        Author retrieved = authorService.findById(AUTHOR_FIXTURE_1.getId());

        assertEquals(AUTHOR_FIXTURE_1, retrieved);
        verify(transactionManager).doInTransaction(any());
        verify(authorRepository).findById(AUTHOR_FIXTURE_1.getId());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testFindAuthorByIdWhenAuthorDoesNotExist() {
        when(authorRepository.findById(AUTHOR_FIXTURE_1.getId())).thenReturn(null);

        Author retrievedAuthor = authorService.findById(AUTHOR_FIXTURE_1.getId());

        assertNull(retrievedAuthor);
        verify(transactionManager).doInTransaction(any());
        verify(authorRepository).findById(AUTHOR_FIXTURE_1.getId());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testAddAuthorSuccessfully() {
        authorService.add(AUTHOR_FIXTURE_1);

        InOrder inOrder = inOrder(transactionManager, authorRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(authorRepository).add(AUTHOR_FIXTURE_1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAddAuthorWhenAuthorIsNull() {
        authorService.add(null);

        verify(transactionManager).doInTransaction(any());
        verifyNoInteractions(authorRepository);
    }

    @Test
    public void testAddAuthorShouldFailWhenAlreadyInDatabase() {
        when(authorRepository.findById(AUTHOR_FIXTURE_1.getId())).thenReturn(AUTHOR_FIXTURE_1);

        AuthorDuplicateException e = assertThrows(AuthorDuplicateException.class, () ->
                authorService.add(AUTHOR_FIXTURE_1)
        );
        assertTrue(e.getMessage().contains(AUTHOR_FIXTURE_1.getId()));
        assertEquals(AUTHOR_FIXTURE_1, e.getExistingAuthor());
    }

    @Test
    public void testDeleteAuthorThrowsExceptionWhenItDoesNotExist() {
        String authorIdToDelete = "3";
        when(authorRepository.findById(authorIdToDelete)).thenReturn(null);

        AuthorNotFoundException e = assertThrows(AuthorNotFoundException.class, () ->
                authorService.delete(authorIdToDelete)
        );
        assertTrue(e.getMessage().contains(authorIdToDelete));
    }

    @Test
    public void testDeleteAuthorSuccessfully() {
        when(authorRepository.findById(AUTHOR_FIXTURE_1.getId())).thenReturn(AUTHOR_FIXTURE_1);
        authorService.delete(AUTHOR_FIXTURE_1.getId());

        InOrder inOrder = inOrder(transactionManager, authorRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(authorRepository).findById(AUTHOR_FIXTURE_1.getId());
        inOrder.verify(authorRepository).delete(AUTHOR_FIXTURE_1.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDeleteAuthorAndAssociatedBooks() {
        when(authorRepository.findById(AUTHOR_FIXTURE_1.getId())).thenReturn(AUTHOR_FIXTURE_1);
        authorService.delete(AUTHOR_FIXTURE_1.getId());

        InOrder inOrder = inOrder(transactionManager, authorRepository, bookRepository);
        inOrder.verify(transactionManager).doInTransaction(any());
        inOrder.verify(bookRepository).deleteAllBooksForAuthorId(AUTHOR_FIXTURE_1.getId());
        inOrder.verify(authorRepository).delete(AUTHOR_FIXTURE_1.getId());
        inOrder.verifyNoMoreInteractions();
    }
}