package it.ned.bookmanager.repository.mongo;

import it.ned.bookmanager.model.Author;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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

public class AuthorMongoRepositoryTest {

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    private MongoClient client;
    private MongoCollection<Author> collection;
    private AuthorMongoRepository repository;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_AUTHOR_COLLECTION = "authors";

    private static final Author AUTHOR_FIXTURE_1 = new Author("1", "George Orwell");
    private static final Author AUTHOR_FIXTURE_2 = new Author("2", "Dan Brown");

    @Before
    public void setup() {
        client = MongoClients.create(container.getReplicaSetUrl());
        repository = new AuthorMongoRepository(client, client.startSession(), DB_NAME, DB_AUTHOR_COLLECTION);

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
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllAuthorsWhenThereAreMany() {
        collection.insertOne(AUTHOR_FIXTURE_1);
        collection.insertOne(AUTHOR_FIXTURE_2);
        assertThat(repository.findAll()).containsExactly(AUTHOR_FIXTURE_1, AUTHOR_FIXTURE_2);
    }

    @Test
    public void testFindAuthorByIdNotFound() {
        assertThat(repository.findById("1")).isNull();
    }

    @Test
    public void testFindAuthorByIdFound() {
        collection.insertOne(AUTHOR_FIXTURE_1);
        collection.insertOne(AUTHOR_FIXTURE_2);
        assertEquals(AUTHOR_FIXTURE_2, repository.findById(AUTHOR_FIXTURE_2.getId()));
    }

    @Test
    public void testAddAuthor() {
        repository.add(AUTHOR_FIXTURE_1);
        assertThat(allAuthorsInDatabase()).containsExactly(AUTHOR_FIXTURE_1);
    }

    @Test
    public void testDeleteAuthor() {
        collection.insertOne(AUTHOR_FIXTURE_1);
        repository.delete(AUTHOR_FIXTURE_1.getId());
        assertThat(allAuthorsInDatabase()).isEmpty();
    }

    private List<Author> allAuthorsInDatabase() {
        return StreamSupport
                .stream(collection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

}