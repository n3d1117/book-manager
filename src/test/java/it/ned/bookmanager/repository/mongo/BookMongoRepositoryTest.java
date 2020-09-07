package it.ned.bookmanager.repository.mongo;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;

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

public class BookMongoRepositoryTest {

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    private MongoClient client;
    private ClientSession session;
    private BookMongoRepository repository;
    private MongoCollection<Book> collection;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_BOOK_COLLECTION = "books";

    private static final Book BOOK_FIXTURE_1 = new Book("1", "Animal Farm", 93, "1");
    private static final Book BOOK_FIXTURE_2 = new Book("2", "1984", 283, "1");

    @Before
    public void setup() {
        client = MongoClients.create(container.getReplicaSetUrl());
        session = client.startSession();

        repository = new BookMongoRepository(client, session, DB_NAME, DB_BOOK_COLLECTION);

        MongoDatabase database = client.getDatabase(DB_NAME);

        // Always start with a clean database
        database.drop();

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        collection = database
                .getCollection(DB_BOOK_COLLECTION, Book.class)
                .withCodecRegistry(pojoCodecRegistry);
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
    public void testFindAllBooksWhenDatabaseIsEmpty() {
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllBooksWhenThereAreMany() {
        collection.insertOne(BOOK_FIXTURE_1);
        collection.insertOne(BOOK_FIXTURE_2);
        assertThat(repository.findAll()).containsExactly(BOOK_FIXTURE_1, BOOK_FIXTURE_2);
    }

    @Test
    public void testFindBookByIdNotFound() {
        assertThat(repository.findById("1")).isNull();
    }

    @Test
    public void testFindBookByIdFound() {
        collection.insertOne(BOOK_FIXTURE_1);
        collection.insertOne(BOOK_FIXTURE_2);
        assertEquals(BOOK_FIXTURE_2, repository.findById(BOOK_FIXTURE_2.getId()));
    }

    @Test
    public void testAddBook() {
        repository.add(BOOK_FIXTURE_1);
        assertThat(allBooksInDatabase()).containsExactly(BOOK_FIXTURE_1);
    }

    @Test
    public void testDeleteBook() {
        collection.insertOne(BOOK_FIXTURE_1);
        repository.delete(BOOK_FIXTURE_1.getId());
        assertThat(allBooksInDatabase()).isEmpty();
    }

    @Test
    public void testDeleteAllWrittenBooksFromAuthorId() {
        Author author = new Author("1", "George Orwell");
        Author author2 = new Author("2", "Dan Brown");

        Book theDaVinciCode = new Book("3", "The Da Vinci Code", 402, author2.getId());

        collection.insertOne(BOOK_FIXTURE_1);
        collection.insertOne(theDaVinciCode);
        collection.insertOne(BOOK_FIXTURE_2);

        repository.deleteAllBooksForAuthorId(author.getId());
        assertThat(allBooksInDatabase()).containsExactly(theDaVinciCode);
    }

    private List<Book> allBooksInDatabase() {
        return StreamSupport
                .stream(collection.find().spliterator(), false)
                .collect(Collectors.toList());
    }
}
