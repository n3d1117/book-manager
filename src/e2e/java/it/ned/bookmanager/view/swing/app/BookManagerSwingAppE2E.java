package it.ned.bookmanager.view.swing.app;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RunWith(GUITestRunner.class)
public class BookManagerSwingAppE2E extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    private MongoClient client;
    private FrameFixture window;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_AUTHOR_COLLECTION = "authors";
    private static final String DB_BOOK_COLLECTION = "books";

    private static final Author AUTHOR_FIXTURE_1 = new Author("1", "George Orwell");
    private static final Author AUTHOR_FIXTURE_2 = new Author("2", "Dan Brown");

    private static final Book BOOK_FIXTURE_1 = new Book("2", "1984", 283, "1");
    private static final Book BOOK_FIXTURE_2 = new Book("1", "Animal Farm", 93, "1");

    @Override
    protected void onSetUp() {
        client = MongoClients.create(container.getReplicaSetUrl());
        client.getDatabase(DB_NAME).drop();

        addTestAuthorToDatabase(AUTHOR_FIXTURE_1);
        addTestAuthorToDatabase(AUTHOR_FIXTURE_2);
        addTestBookToDatabase(BOOK_FIXTURE_1);
        addTestBookToDatabase(BOOK_FIXTURE_2);

        application("it.ned.bookmanager.view.swing.app.BookManagerSwingApp")
                .withArgs(
                        "--mongo-url=" + container.getReplicaSetUrl(),
                        "--db-name=" + DB_NAME,
                        "--db-author-collection=" + DB_AUTHOR_COLLECTION,
                        "--db-book-collection=" + DB_BOOK_COLLECTION
                )
                .start();

        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame jFrame) {
                return "Book Manager".equals(jFrame.getTitle()) && jFrame.isShowing();
            }
        }).using(robot());
    }

    @Override @After
    public void onTearDown() {
        client.close();
    }

    @Test @GUITest
    public void testOnStartupAllInitialElementsAreShown() {
        assertThat(window.list("authorsList").contents())
                .anySatisfy(e -> assertThat(e).contains(AUTHOR_FIXTURE_1.getName()))
                .anySatisfy(e -> assertThat(e).contains(AUTHOR_FIXTURE_2.getName()));
        assertThat(window.comboBox("authorsCombobox").contents())
                .anySatisfy(e -> assertThat(e).contains(AUTHOR_FIXTURE_1.getName()))
                .anySatisfy(e -> assertThat(e).contains(AUTHOR_FIXTURE_2.getName()));
        assertThat(window.table("booksTable").contents()[0])
                .anySatisfy(e -> assertThat(e).contains(BOOK_FIXTURE_1.getTitle()));
        assertThat(window.table("booksTable").contents()[1])
                .anySatisfy(e -> assertThat(e).contains(BOOK_FIXTURE_2.getTitle()));
    }

    private void addTestAuthorToDatabase(Author author) {
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        MongoCollection<Author>  collection = client
                .getDatabase(DB_NAME)
                .getCollection(DB_AUTHOR_COLLECTION, Author.class)
                .withCodecRegistry(pojoCodecRegistry);
        collection.insertOne(author);
    }

    private void addTestBookToDatabase(Book book) {
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        MongoCollection<Book>  collection = client
                .getDatabase(DB_NAME)
                .getCollection(DB_BOOK_COLLECTION, Book.class)
                .withCodecRegistry(pojoCodecRegistry);
        collection.insertOne(book);
    }
}
