package it.ned.bookmanager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.repository.mongo.AuthorMongoRepository;
import it.ned.bookmanager.service.transactional.AuthorTransactionalService;
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

public class AuthorServiceMongoRepositoryIT {

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    private AuthorService service;
    private AuthorMongoRepository repository;
    private MongoClient client;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_BOOK_COLLECTION = "books";
    private static final String DB_AUTHOR_COLLECTION = "authors";

    private static final Author AUTHOR_FIXTURE = new Author("1", "George Orwell");

    @Before
    public void setup() {
        client = MongoClients.create(container.getReplicaSetUrl());
        client.getDatabase(DB_NAME).drop();

        TransactionManager transactionManager = new TransactionMongoManager(client, DB_NAME,
                DB_AUTHOR_COLLECTION, DB_BOOK_COLLECTION);
        service = new AuthorTransactionalService(transactionManager);

        repository = new AuthorMongoRepository(client, client.startSession(), DB_NAME, DB_AUTHOR_COLLECTION);

        for (Author author: repository.findAll())
            repository.delete(author.getId());
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAllAuthors() {
        repository.add(AUTHOR_FIXTURE);
        assertThat(service.findAll()).containsExactly(AUTHOR_FIXTURE);
    }

    @Test
    public void testAuthorFoundWhenAdded() {
        repository.add(AUTHOR_FIXTURE);
        assertEquals(AUTHOR_FIXTURE, service.findById(AUTHOR_FIXTURE.getId()));
    }

    @Test
    public void testAuthorNotFoundWhenDeleted() {
        repository.add(AUTHOR_FIXTURE);
        repository.delete(AUTHOR_FIXTURE.getId());
        assertNull(service.findById(AUTHOR_FIXTURE.getId()));
    }
}