package it.ned.bookmanager.view.swing.app;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
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

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.awaitility.Awaitility.await;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.Assert.assertEquals;

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

    private static final Book BOOK_FIXTURE_1 = new Book("1", "1984", 283, "1");
    private static final Book BOOK_FIXTURE_2 = new Book("2", "Animal Farm", 93, "1");

    private static final long TIMEOUT_SECONDS = 3;
    private static final CodecRegistry pojoCodecRegistry =  fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );

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

        robot().waitForIdle();
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
        assertEquals(2, window.table("booksTable").rowCount());
        assertThat(window.table("booksTable").contents()[0])
                .anySatisfy(e -> assertThat(e).contains(BOOK_FIXTURE_1.getTitle()));
        assertThat(window.table("booksTable").contents()[1])
                .anySatisfy(e -> assertThat(e).contains(BOOK_FIXTURE_2.getTitle()));
    }

    @Test @GUITest
    public void testAddAuthorSuccess() {
        window.textBox("authorIdTextField").enterText("3");
        window.textBox("authorNameTextField").enterText("James Joyce");
        window.button(JButtonMatcher.withName("addAuthorButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("authorsList").contents())
                    .anySatisfy(e -> assertThat(e).contains("James Joyce"));
            assertThat(window.comboBox("authorsCombobox").contents())
                    .anySatisfy(e -> assertThat(e).contains("James Joyce"));
        });
    }

    @Test @GUITest
    public void testAddAuthorError() {
        window.textBox("authorIdTextField").enterText(AUTHOR_FIXTURE_1.getId());
        window.textBox("authorNameTextField").enterText("Duplicate author");
        window.button(JButtonMatcher.withName("addAuthorButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(window.label("authorErrorLabel").text())
                    .contains(AUTHOR_FIXTURE_1.getId())
        );
    }

    @Test @GUITest
    public void testDeleteAuthorSuccess() {
        window.list("authorsList").selectItem(
                Pattern.compile(".*" + AUTHOR_FIXTURE_1.getName() + ".*")
        );
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(window.list("authorsList").contents())
                    .noneMatch(e -> e.contains(AUTHOR_FIXTURE_1.getName()))
        );
    }

    @Test @GUITest
    public void testDeleteAuthorError() {
        window.list("authorsList").selectItem(
                Pattern.compile(".*" + AUTHOR_FIXTURE_1.getName() + ".*")
        );
        removeTestAuthorFromDatabase(AUTHOR_FIXTURE_1.getId());
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(window.label("authorErrorLabel").text())
                    .contains(AUTHOR_FIXTURE_1.getId())
        );
    }

    @Test @GUITest
    public void testAddBookSuccess() {
        window.textBox("bookIdTextField").enterText("3");
        window.textBox("bookTitleTextField").enterText("The Da Vinci Code");
        window.textBox("bookLengthTextField").enterText("402");
        window.comboBox("authorsCombobox").selectItem(
                Pattern.compile(".*" + AUTHOR_FIXTURE_2.getName() + ".*")
        );
        window.button(JButtonMatcher.withName("addBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals(3, window.table("booksTable").rowCount());
            assertThat(window.table("booksTable").contents()[2])
                    .anySatisfy(e -> assertThat(e).contains("The Da Vinci Code"));
        });
    }

    @Test @GUITest
    public void testAddBookError() {
        window.textBox("bookIdTextField").enterText(BOOK_FIXTURE_1.getId());
        window.textBox("bookTitleTextField").enterText("Duplicate book");
        window.textBox("bookLengthTextField").enterText("123");
        window.comboBox("authorsCombobox").selectItem(
                Pattern.compile(".*" + AUTHOR_FIXTURE_1.getName() + ".*")
        );
        window.button(JButtonMatcher.withName("addBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(window.label("bookErrorLabel").text())
                    .contains(BOOK_FIXTURE_1.getId())
        );
    }

    @Test @GUITest
    public void testDeleteBookSuccess() {
        window.table("booksTable").selectRows(0);
        window.button(JButtonMatcher.withName("deleteBookButton")).click();
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            for (String[] content : window.table("booksTable").contents()) {
                assertThat(content).noneMatch(e -> e.contains(BOOK_FIXTURE_1.getTitle()));
            }
        });
    }

    @Test @GUITest
    public void testDeleteBookError() {
        window.table("booksTable").selectRows(0);
        removeTestBookFromDatabase(BOOK_FIXTURE_1.getId());
        window.button(JButtonMatcher.withName("deleteBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(window.label("bookErrorLabel").text())
                    .contains(BOOK_FIXTURE_1.getId())
        );
    }

    @Test @GUITest
    public void testDeleteAuthorAndAssociatedBooks() {
        window.list("authorsList").selectItem(
                Pattern.compile(".*" + AUTHOR_FIXTURE_1.getName() + ".*")
        );
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();
        // Since both book fixtures are from the same author,
        // deleting the author should leave the books table empty
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
            assertThat(window.table("booksTable").contents()).isEmpty()
        );
    }

    private MongoCollection<Author> getAuthorCollection() {
        return client
                .getDatabase(DB_NAME)
                .getCollection(DB_AUTHOR_COLLECTION, Author.class)
                .withCodecRegistry(pojoCodecRegistry);
    }

    private MongoCollection<Book> getBookCollection() {
        return client
                .getDatabase(DB_NAME)
                .getCollection(DB_BOOK_COLLECTION, Book.class)
                .withCodecRegistry(pojoCodecRegistry);
    }

    private void addTestAuthorToDatabase(Author author) {
        getAuthorCollection().insertOne(author);
    }

    private void removeTestAuthorFromDatabase(String authorId) {
        getAuthorCollection().deleteOne(eq("_id", authorId));
    }

    private void addTestBookToDatabase(Book book) {
        getBookCollection().insertOne(book);
    }

    private void removeTestBookFromDatabase(String bookId) {
        getBookCollection().deleteOne(eq("_id", bookId));
    }
}
