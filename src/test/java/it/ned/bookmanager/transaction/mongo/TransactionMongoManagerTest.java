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
import static org.junit.Assert.assertEquals;
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

        // NOTE: MongoClients.create() returns a final instance of MongoClient, and by default
        // Mockito cannot spy a final type. However, mocking final classes can be activated
        // explicitly by the mockito extension mechanism: just create in the classpath a file
        // /mockito-extensions/org.mockito.plugins.MockMaker containing the value "mock-maker-inline".
        // See also: https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#39
        client = spy(MongoClients.create(container.getReplicaSetUrl()));

        session = spy(client.startSession());

        MongoDatabase database = client.getDatabase(DB_NAME);
        database.drop();

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

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testInitialCollectionsAreCreatedCorrectly() {
        assertThat(client.getDatabase(DB_NAME).listCollectionNames())
                .containsExactly(AUTHOR_COLLECTION_NAME, BOOK_COLLECTION_NAME);
    }

    @Test
    public void testNewCollectionsAreCreatedCorrectly() {
        String newAuthorCollection = "new_collection_1";
        String newBookCollection = "new_collection_2";
        transactionManager = new TransactionMongoManager(client, DB_NAME, newAuthorCollection, newBookCollection);
        assertThat(client.getDatabase(DB_NAME).listCollectionNames())
                .contains(newAuthorCollection, newBookCollection);
    }

    @Test
    public void testTransactionReturnValueIsCorrect() {
        Author retrieved = transactionManager.doInTransaction(factory -> AUTHOR_FIXTURE);
        assertEquals(AUTHOR_FIXTURE, retrieved);
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
    public void tesRollbackOnTransactionFailure() {
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