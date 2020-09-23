package it.ned.bookmanager.view;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.ned.bookmanager.controller.BookManagerController;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.AuthorRepository;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.repository.mongo.AuthorMongoRepository;
import it.ned.bookmanager.repository.mongo.BookMongoRepository;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.transactional.AuthorTransactionalService;
import it.ned.bookmanager.service.transactional.BookTransactionalService;
import it.ned.bookmanager.transaction.TransactionManager;
import it.ned.bookmanager.transaction.mongo.TransactionMongoManager;
import it.ned.bookmanager.view.swing.BookManagerSwingView;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import java.util.concurrent.TimeUnit;

import static org.assertj.swing.assertions.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(GUITestRunner.class)
public class BookManagerSwingViewIT extends AssertJSwingJUnitTestCase {

    private MongoClient client;
    private BookManagerController controller;
    private BookManagerSwingView view;

    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    private FrameFixture window;

    private static final String DB_NAME = "bookmanager";
    private static final String DB_AUTHOR_COLLECTION = "authors";
    private static final String DB_BOOK_COLLECTION = "books";

    private static final long TIMEOUT_SECONDS = 3;

    @ClassRule
    public static final MongoDBContainer container = new MongoDBContainer().withExposedPorts(27017);

    @Override
    protected void onSetUp() {
        client = MongoClients.create(container.getReplicaSetUrl());
        ClientSession session = client.startSession();
        client.getDatabase(DB_NAME).drop();

        authorRepository = new AuthorMongoRepository(client, session, DB_NAME, DB_AUTHOR_COLLECTION);
        bookRepository = new BookMongoRepository(client, session, DB_NAME, DB_BOOK_COLLECTION);

        for (Author author : authorRepository.findAll())
            authorRepository.delete(author.getId());
        for (Book book : bookRepository.findAll())
            bookRepository.delete(book.getId());

        GuiActionRunner.execute(() -> {
            TransactionManager transactionManager = new TransactionMongoManager(client, DB_NAME,
                    DB_AUTHOR_COLLECTION, DB_BOOK_COLLECTION);
            AuthorService authorService = new AuthorTransactionalService(transactionManager);
            BookService bookService = new BookTransactionalService(transactionManager);
            view = new BookManagerSwingView();
            controller = new BookManagerController(authorService, bookService, view);
            view.setController(controller);
            return view;
        });
        window = new FrameFixture(robot(), view);
        window.show();

        robot().waitForIdle();

        GuiActionRunner.execute(() -> {
            view.requestFocusInWindow();
            view.toFront();
        });
    }

    @Override @After
    public void onTearDown() {
        client.close();
    }

    @Test @GUITest
    public void testShowAllAuthors() {
        Author danBrown = new Author("1", "Dan Brown");
        Author georgeOrwell = new Author("2", "George Orwell");
        authorRepository.add(danBrown);
        authorRepository.add(georgeOrwell);

        GuiActionRunner.execute(() ->
                controller.allAuthors()
        );

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("authorsList").contents()).containsExactly(
                    "👤 " + danBrown.getName(), "👤 " + georgeOrwell.getName()
            );
            assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(
                    "👤 " + danBrown.getName(), "👤 " + georgeOrwell.getName()
            );
        });
    }

    @Test @GUITest
    public void testShowAllBooks() {
        Author georgeOrwell = new Author("1", "George Orwell");
        GuiActionRunner.execute(() ->
                view.getAuthorListModel().addElement(georgeOrwell)
        );
        Book nineteenEightyFour = new Book("1", "1984", 293, georgeOrwell.getId());
        Book animalFarm = new Book("2", "Animal Farm", 93, georgeOrwell.getId());
        bookRepository.add(nineteenEightyFour);
        bookRepository.add(animalFarm);

        GuiActionRunner.execute(() ->
                controller.allBooks()
        );

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            JTableFixture booksTable = window.table("booksTable");
            booksTable.requireRowCount(2);
            assertThat(booksTable.contents()[0]).containsExactly(
                    nineteenEightyFour.getTitle(),
                    georgeOrwell.getName(),
                    nineteenEightyFour.getNumberOfPages().toString()
            );
            assertThat(booksTable.contents()[1]).containsExactly(
                    animalFarm.getTitle(),
                    georgeOrwell.getName(),
                    animalFarm.getNumberOfPages().toString()
            );
        });
    }

    @Test @GUITest
    public void testAddAuthorSuccess() {
        window.textBox("authorIdTextField").enterText("1");
        window.textBox("authorNameTextField").enterText("George Orwell");
        window.button(JButtonMatcher.withName("addAuthorButton")).click();
        String expected = "👤 George Orwell";

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            window.textBox("authorIdTextField").requireEmpty();
            window.textBox("authorNameTextField").requireEmpty();
            assertThat(window.list("authorsList").contents()).containsExactly(expected);
            assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(expected);
        });
    }

    @Test @GUITest
    public void testAddAuthorError() {
        Author georgeOrwell = new Author("1", "George Orwell");
        authorRepository.add(georgeOrwell);
        window.textBox("authorIdTextField").enterText("1");
        window.textBox("authorNameTextField").enterText("Another George Orwell");
        window.button(JButtonMatcher.withName("addAuthorButton")).click();

        String expected = "👤 " + georgeOrwell.getName();
        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("authorsList").contents()).containsExactly(expected);
            assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(expected);
            window.label("authorErrorLabel").requireText("Error: Author with id 1 already exists!");
        });
    }

    @Test @GUITest
    public void testDeleteAuthorSuccess() {
        GuiActionRunner.execute(() ->
                controller.addAuthor(new Author("1", "George Orwell"))
        );
        window.list("authorsList").selectItem(0);
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("authorsList").contents()).isEmpty();
            assertThat(window.comboBox("authorsCombobox").contents()).isEmpty();
        });
    }

    @Test @GUITest
    public void testDeleteAuthorError() {
        Author georgeOrwell = new Author("1", "George Orwell");

        // Add author manually to the list & combobox, but not to the database
        GuiActionRunner.execute(() -> {
            view.getAuthorListModel().addElement(georgeOrwell);
            view.getAuthorComboBoxModel().addElement(georgeOrwell);
        });

        window.list("authorsList").selectItem(0);
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            window.list("authorsList").requireNoSelection();
            assertThat(window.list("authorsList").contents()).isEmpty();
            assertThat(window.comboBox("authorsCombobox").contents()).isEmpty();
            window.label("authorErrorLabel").requireText("Error: Author with id 1 not found!");
        });
    }

    @Test @GUITest
    public void testAddBookSuccess() {
        Author georgeOrwell = new Author("1", "George Orwell");
        GuiActionRunner.execute(() -> {
            view.getAuthorListModel().addElement(georgeOrwell);
            view.getAuthorComboBoxModel().addElement(georgeOrwell);
        });
        Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
        window.textBox("bookIdTextField").enterText(animalFarm.getId());
        window.textBox("bookTitleTextField").enterText(animalFarm.getTitle());
        window.textBox("bookLengthTextField").enterText(animalFarm.getNumberOfPages().toString());
        window.comboBox("authorsCombobox").selectItem(0);
        window.button(JButtonMatcher.withName("addBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            window.textBox("bookIdTextField").requireEmpty();
            window.textBox("bookTitleTextField").requireEmpty();
            window.textBox("bookLengthTextField").requireEmpty();
            window.comboBox("authorsCombobox").requireNoSelection();
            JTableFixture booksTable = window.table("booksTable");
            booksTable.requireRowCount(1);
            assertThat(booksTable.contents()[0]).containsExactly(
                    animalFarm.getTitle(),
                    georgeOrwell.getName(),
                    animalFarm.getNumberOfPages().toString()
            );
        });
    }

    @Test @GUITest
    public void testAddBookError() {
        Author georgeOrwell = new Author("1", "George Orwell");
        Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
        GuiActionRunner.execute(() -> {
            view.getAuthorListModel().addElement(georgeOrwell);
            view.getAuthorComboBoxModel().addElement(georgeOrwell);
        });

        bookRepository.add(animalFarm);

        window.textBox("bookIdTextField").enterText(animalFarm.getId());
        window.textBox("bookTitleTextField").enterText("Another Animal Farm");
        window.textBox("bookLengthTextField").enterText("189");
        window.comboBox("authorsCombobox").selectItem(0);
        window.button(JButtonMatcher.withName("addBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            JTableFixture booksTable = window.table("booksTable");
            booksTable.requireRowCount(1);
            assertThat(booksTable.contents()[0]).containsExactly(
                    animalFarm.getTitle(),
                    georgeOrwell.getName(),
                    animalFarm.getNumberOfPages().toString()
            );
            window.label("bookErrorLabel").requireText("Error: Book with id 1 already exists!");
        });
    }

    @Test @GUITest
    public void testDeleteBookSuccess() {
        GuiActionRunner.execute(() ->
                controller.addBook(new Book("1", "Animal Farm", 93, "1"))
        );
        window.table("booksTable").selectRows(0);
        window.button(JButtonMatcher.withName("deleteBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(window.table("booksTable").contents()).isEmpty()
        );
    }

    @Test @GUITest
    public void testDeleteBookError() {
        Author georgeOrwell = new Author("1", "George Orwell");
        Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());

        // Add book manually to the table, but not to the database
        GuiActionRunner.execute(() ->
                view.getBookTableModel().addElement(animalFarm, georgeOrwell)
        );

        window.table("booksTable").selectRows(0);
        window.button(JButtonMatcher.withName("deleteBookButton")).click();

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            window.table("booksTable").requireNoSelection();
            assertThat(window.table("booksTable").contents()).isEmpty();
            window.label("bookErrorLabel").requireText("Error: Book with id 1 not found!");
        });
    }

    @Test @GUITest
    public void testDeleteAuthorAndAssociatedBooks() {
        Author danBrown = new Author("1", "Dan Brown");
        Author georgeOrwell = new Author("2", "George Orwell");

        Book animalFarm = new Book("1", "Animal Farm", 93, georgeOrwell.getId());
        Book nineteenEightyFour = new Book("2", "1984", 293, georgeOrwell.getId());
        Book theDaVinciCode = new Book("3", "The Da Vinci Code", 402, danBrown.getId());

        GuiActionRunner.execute(() -> {
            controller.addAuthor(danBrown);
            controller.addAuthor(georgeOrwell);
            controller.addBook(animalFarm);
            controller.addBook(nineteenEightyFour);
            controller.addBook(theDaVinciCode);
        });

        window.list("authorsList").selectItem(1);
        window.button(JButtonMatcher.withName("deleteAuthorButton")).click();

        String expected = "👤 Dan Brown";

        await().atMost(TIMEOUT_SECONDS, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(window.list("authorsList").contents()).containsExactly(expected);
            assertThat(window.comboBox("authorsCombobox").contents()).containsExactly(expected);
            JTableFixture booksTable = window.table("booksTable");
            booksTable.requireRowCount(1);
            assertThat(booksTable.contents()[0]).containsExactly(
                    theDaVinciCode.getTitle(),
                    danBrown.getName(),
                    theDaVinciCode.getNumberOfPages().toString()
            );
        });
    }
}