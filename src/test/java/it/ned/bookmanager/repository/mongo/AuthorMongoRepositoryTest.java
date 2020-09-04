package it.ned.bookmanager.repository.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import it.ned.bookmanager.model.Author;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.*;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.Assert.assertEquals;

public class AuthorMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient client;
    private MongoCollection<Author> collection;
    private AuthorMongoRepository authorRepository;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_AUTHOR_COLLECTION = "authors";

    private static final Author AUTHOR_FIXTURE_1 = new Author("1", "George Orwell");
    private static final Author AUTHOR_FIXTURE_2 = new Author("2", "Dan Brown");

    @BeforeClass
    public static void startServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void stopServer() {
        server.shutdown();
    }

    @Before
    public void setUp() {

        client = new MongoClient(new ServerAddress(serverAddress));

        authorRepository = new AuthorMongoRepository(client, DB_NAME, DB_AUTHOR_COLLECTION);

        MongoDatabase database = client.getDatabase(DB_NAME);

        // Always start with a clean database
        database.drop();

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        collection = database
                .getCollection(DB_AUTHOR_COLLECTION, Author.class)
                .withCodecRegistry(pojoCodecRegistry);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAllAuthorsWhenDatabaseIsEmpty() {
        assertThat(authorRepository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllAuthorsWhenThereAreMany() {
        collection.insertOne(AUTHOR_FIXTURE_1);
        collection.insertOne(AUTHOR_FIXTURE_2);
        assertThat(authorRepository.findAll()).containsExactly(AUTHOR_FIXTURE_1, AUTHOR_FIXTURE_2);
    }

    @Test
    public void testFindAuthorByIdNotFound() {
        assertThat(authorRepository.findById("1")).isNull();
    }

    @Test
    public void testFindAuthorByIdFound() {
        collection.insertOne(AUTHOR_FIXTURE_1);
        collection.insertOne(AUTHOR_FIXTURE_2);
        assertEquals(AUTHOR_FIXTURE_2, authorRepository.findById(AUTHOR_FIXTURE_2.getId()));
    }

    @Test
    public void testAddAuthor() {
        authorRepository.add(AUTHOR_FIXTURE_1);
        assertThat(allAuthorsInDatabase()).containsExactly(AUTHOR_FIXTURE_1);
    }

    @Test
    public void testDeleteAuthor() {
        collection.insertOne(AUTHOR_FIXTURE_1);
        authorRepository.delete(AUTHOR_FIXTURE_1.getId());
        assertThat(allAuthorsInDatabase()).isEmpty();
    }

    private List<Author> allAuthorsInDatabase() {
        return StreamSupport
                .stream(collection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

}