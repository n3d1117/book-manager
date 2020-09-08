package it.ned.bookmanager.transaction.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.*;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.mockito.Mockito.*;

public class TransactionMongoManagerTest {

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    private TransactionMongoManager transactionManager;

    private MongoClient client;
    private ClientSession session;
    private MongoCollection<Author> authorCollection;
    private MongoCollection<Book> bookCollection;

    private static final String DB_NAME = "bookmanager";
    private static final String AUTHOR_COLLECTION_NAME = "authors";
    private static final String BOOK_COLLECTION_NAME = "books";

    private static final Author AUTHOR_FIXTURE = new Author("1", "George Orwell");
    private static final Book BOOK_FIXTURE = new Book("1", "Animal Farm", 93, "1");

    @Before
    public void setup() {

        client = spy(MongoClients.create(container.getReplicaSetUrl()));
        session = spy(client.startSession());

        MongoDatabase database = client.getDatabase(DB_NAME);
        database.drop();

        database.createCollection(AUTHOR_COLLECTION_NAME);
        database.createCollection(BOOK_COLLECTION_NAME);

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        authorCollection = database
                .getCollection(AUTHOR_COLLECTION_NAME, Author.class)
                .withCodecRegistry(pojoCodecRegistry);
        bookCollection = database
                .getCollection(BOOK_COLLECTION_NAME, Book.class)
                .withCodecRegistry(pojoCodecRegistry);

        transactionManager = new TransactionMongoManager(client, DB_NAME,
                AUTHOR_COLLECTION_NAME, BOOK_COLLECTION_NAME);
        when(client.startSession()).thenReturn(session);
    }

    @AfterClass
    public static void stopContainer() {
        container.stop();
    }

    @After
    public void tearDown() {
        session.close();
        client.close();
    }

    @Test
    public void testSuccessfulTransaction() {
        transactionManager.doInTransaction(factory -> {
            authorCollection.insertOne(session, AUTHOR_FIXTURE);
            bookCollection.insertOne(session, BOOK_FIXTURE);
            return null;
        });
        assertThat(allAuthorsInDatabase()).containsExactly(AUTHOR_FIXTURE);
        assertThat(allBooksInDatabase()).containsExactly(BOOK_FIXTURE);
        verify(session).close();
    }

    @Test
    public void testTransactionFailureEnsureRollback() {
        transactionManager.doInTransaction(factory -> {
            authorCollection.insertOne(session, AUTHOR_FIXTURE);
            bookCollection.insertOne(session, BOOK_FIXTURE);
            throw new MongoException("Simulating a transaction failure here!");
        });
        assertThat(allAuthorsInDatabase()).isEmpty();
        assertThat(allBooksInDatabase()).isEmpty();
        verify(session).close();
    }

    private List<Author> allAuthorsInDatabase() {
        return StreamSupport
                .stream(authorCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<Book> allBooksInDatabase() {
        return StreamSupport
                .stream(bookCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }
}