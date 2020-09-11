package it.ned.bookmanager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.repository.mongo.MongoRepositoryFactory;
import it.ned.bookmanager.service.transactional.BookTransactionalService;
import it.ned.bookmanager.transaction.TransactionManager;
import it.ned.bookmanager.transaction.mongo.TransactionMongoManager;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BookServiceMongoRepositoryIT {

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    private BookService service;
    private BookRepository repository;
    private MongoClient client;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_BOOK_COLLECTION = "books";
    private static final String DB_AUTHOR_COLLECTION = "authors";

    private static final Book BOOK_FIXTURE = new Book("1", "Animal Farm", 93, "1");

    @Before
    public void setup() {
        client = MongoClients.create(container.getReplicaSetUrl());
        client.getDatabase(DB_NAME).drop();

        TransactionManager transactionManager = new TransactionMongoManager(client, DB_NAME,
                DB_AUTHOR_COLLECTION, DB_BOOK_COLLECTION);
        service = new BookTransactionalService(transactionManager);

        MongoRepositoryFactory repositoryFactory = new MongoRepositoryFactory(client, client.startSession(),
                DB_NAME, DB_AUTHOR_COLLECTION, DB_BOOK_COLLECTION);
        repository = repositoryFactory.createBookRepository();

        for (Book book: repository.findAll())
            repository.delete(book.getId());
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAllBooks() {
        repository.add(BOOK_FIXTURE);
        assertThat(service.findAll()).containsExactly(BOOK_FIXTURE);
    }

    @Test
    public void testBookFoundWhenAdded() {
        repository.add(BOOK_FIXTURE);
        assertEquals(BOOK_FIXTURE, service.findById(BOOK_FIXTURE.getId()));
    }

    @Test
    public void testBookNotFoundWhenDeleted() {
        repository.add(BOOK_FIXTURE);
        repository.delete(BOOK_FIXTURE.getId());
        assertNull(service.findById(BOOK_FIXTURE.getId()));
    }

    @Test
    public void testDeleteAllBooksFromAuthor() {
        Book nineteenEightyFour = new Book("2", "1984", 283, "1");
        Book ulysses = new Book("3", "Ulysses", 1341, "2");
        repository.add(BOOK_FIXTURE);
        repository.add(nineteenEightyFour);
        repository.add(ulysses);

        repository.deleteAllBooksForAuthorId("1");

        assertThat(service.findAll()).containsExactly(ulysses);
    }

}
