package it.ned.bookmanager.controller;

import java.util.ArrayList;
import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.BookManagerService;
import it.ned.bookmanager.view.BookManagerView;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookManagerControllerTest {

    @Mock private BookManagerView view;
    @Mock private BookManagerService service;

    @InjectMocks private BookManagerController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllBooksRetrieval() {
        // Setup
        List<Book> books = new ArrayList<>();
        when(service.getAllBooks()).thenReturn(books);

        // Exercise
        controller.getAllBooks();

        // Verify
        verify(view).showAllBooks(books);
    }

    @Test
    public void testAllAuthorsRetrieval() {
        // Setup
        List<Author> authors = new ArrayList<>();
        when(service.getAllAuthors()).thenReturn(authors);

        // Exercise
        controller.getAllAuthors();

        // Verify
        verify(view).showAllAuthors(authors);
    }
}