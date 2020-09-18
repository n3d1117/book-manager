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

import java.awt.*;

public class BookManagerSwingApp {

    private static final String DB_NAME = "bookmanager";
    private static final String DB_AUTHOR_COLLECTION = "authors";
    private static final String DB_BOOK_COLLECTION = "books";

    private static final Logger LOGGER = LogManager.getLogger(BookManagerSwingApp.class);

    public static void main(String[] args) {
        LOGGER.info("App started");

        EventQueue.invokeLater(() -> {
            BookManagerSwingView view = new BookManagerSwingView();

            MongoClient client = MongoClients.create("mongodb://localhost:27017");
            TransactionManager transactionManager = new TransactionMongoManager(client, DB_NAME,
                    DB_AUTHOR_COLLECTION, DB_BOOK_COLLECTION);

            AuthorService authorService = new AuthorTransactionalService(transactionManager);
            BookService bookService = new BookTransactionalService(transactionManager);

            BookManagerController controller = new BookManagerController(authorService, bookService, view);
            view.setController(controller);

            view.pack();
            view.setLocationRelativeTo(null); // this centers the view
            view.setVisible(true);
        });
    }
}
