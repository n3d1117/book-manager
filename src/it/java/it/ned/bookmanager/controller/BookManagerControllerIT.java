package it.ned.bookmanager.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.transactional.AuthorTransactionalService;
import it.ned.bookmanager.service.transactional.BookTransactionalService;
import it.ned.bookmanager.transaction.TransactionManager;
import it.ned.bookmanager.transaction.mongo.TransactionMongoManager;
import it.ned.bookmanager.view.BookManagerView;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class BookManagerControllerIT {

    private static final DockerImageName mongoImage = DockerImageName.parse("mongo").withTag("4.0.10");

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer(mongoImage).withExposedPorts(27017);

    @Mock private BookManagerView view;
    private AuthorService authorService;
    private BookService bookService;
    private BookManagerController controller;
    private MongoClient client;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_AUTHOR_COLLECTION = "authors";
    private static final String DB_BOOK_COLLECTION = "books";

    private static final Author AUTHOR_FIXTURE = new Author("1", "George Orwell");
    private static final Book BOOK_FIXTURE = new Book("1", "Animal Farm", 93, "1");

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        client = MongoClients.create(container.getReplicaSetUrl());
        client.getDatabase(DB_NAME).drop();

        TransactionManager transactionManager = new TransactionMongoManager(client, DB_NAME,
                DB_AUTHOR_COLLECTION, DB_BOOK_COLLECTION);

        authorService = new AuthorTransactionalService(transactionManager);
        bookService = new BookTransactionalService(transactionManager);

        controller = new BookManagerController(authorService, bookService, view);
    }

    @After
    public void tearDown() {
        client.close();
    }

    /* Authors */

    @Test
    public void testAllAuthorsRetrieval() {
        authorService.add(AUTHOR_FIXTURE);

        controller.allAuthors();

        verify(view).showAllAuthors(Collections.singletonList(AUTHOR_FIXTURE));
    }

    @Test
    public void testAddAuthor() {
        controller.addAuthor(AUTHOR_FIXTURE);

        verify(view).authorAdded(AUTHOR_FIXTURE);
    }

    @Test
    public void testDeleteAuthor() {
        authorService.add(AUTHOR_FIXTURE);

        controller.deleteAuthor(AUTHOR_FIXTURE);

        verify(view).deletedAllBooksForAuthor(AUTHOR_FIXTURE);
        verify(view).authorDeleted(AUTHOR_FIXTURE);
    }

    /* Books */

    @Test
    public void testAllBooksRetrieval() {
        bookService.add(BOOK_FIXTURE);

        controller.allBooks();

        verify(view).showAllBooks(Collections.singletonList(BOOK_FIXTURE));
    }

    @Test
    public void testAddBook() {
        controller.addBook(BOOK_FIXTURE);

        verify(view).bookAdded(BOOK_FIXTURE);
    }

    @Test
    public void testDeleteBook() {
        bookService.add(BOOK_FIXTURE);

        controller.deleteBook(BOOK_FIXTURE);

        verify(view).bookDeleted(BOOK_FIXTURE);
    }

}
