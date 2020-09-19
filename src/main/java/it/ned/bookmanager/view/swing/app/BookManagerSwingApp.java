package it.ned.bookmanager.view.swing.app;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import it.ned.bookmanager.controller.BookManagerController;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.transactional.AuthorTransactionalService;
import it.ned.bookmanager.service.transactional.BookTransactionalService;
import it.ned.bookmanager.transaction.TransactionManager;
import it.ned.bookmanager.transaction.mongo.TransactionMongoManager;
import it.ned.bookmanager.view.swing.BookManagerSwingView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.awt.*;
import java.util.concurrent.Callable;

@Command(mixinStandardHelpOptions=true)
public class BookManagerSwingApp implements Callable<Void> {

    private static final Logger LOGGER = LogManager.getLogger(BookManagerSwingApp.class);

    @Option(names = { "--mongo-url" }, description = "MongoDB replica set URL")
    private static final String MONGO_URL = "mongodb://localhost:27017";

    @Option(names = { "--db-name" }, description = "Database name")
    private static final String DB_NAME = "bookmanager";

    @Option(names = { "--db-author-collection" }, description = "Authors collection name")
    private static final String AUTHOR_COLLECTION = "authors";

    @Option(names = { "--db-book-collection" }, description = "Books collection name")
    private static final String BOOK_COLLECTION = "books";

    public static void main(String[] args) {
        LOGGER.info("App started");
        new CommandLine(new BookManagerSwingApp()).execute(args);
    }

    @Override
    public Void call() {
        EventQueue.invokeLater(() -> {
            try {
                BookManagerSwingView view = new BookManagerSwingView();

                MongoClient client = MongoClients.create(MONGO_URL);
                TransactionManager transactionManager = new TransactionMongoManager(client, DB_NAME,
                        AUTHOR_COLLECTION, BOOK_COLLECTION);

                AuthorService authorService = new AuthorTransactionalService(transactionManager);
                BookService bookService = new BookTransactionalService(transactionManager);

                BookManagerController controller = new BookManagerController(authorService, bookService, view);
                view.setController(controller);

                view.pack();
                view.setLocationRelativeTo(null); // this centers the view
                view.setVisible(true);

                controller.allAuthors();
                controller.allBooks();
            } catch (Exception e) {
                LOGGER.debug(() -> String.format("Caught Exception: %s", e.getMessage()));
            }
        });
        return null;
    }
}
