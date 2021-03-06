package it.ned.bookmanager.view.swing.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class BookManagerSwingApp implements Callable<Void> {

	private static final Logger LOGGER = LogManager.getLogger(BookManagerSwingApp.class);

	@Option(names = { "--mongo-replica-set-url" }, description = "MongoDB replica set URL")
	private String mongoUrl = "mongodb://localhost:27017";

	@Option(names = { "--db-name" }, description = "Database name")
	private String dbName = "bookmanager";

	@Option(names = { "--db-author-collection" }, description = "Authors collection name")
	private String authorCollection = "authors";

	@Option(names = { "--db-book-collection" }, description = "Books collection name")
	private String bookCollection = "books";

	public static void main(String[] args) {
		LOGGER.info("App started");
		new CommandLine(new BookManagerSwingApp()).execute(args);
	}

	@Override
	public Void call() {
		EventQueue.invokeLater(() -> {
			try {
				BookManagerSwingView view = new BookManagerSwingView();

				MongoClient client = MongoClients.create(mongoUrl);
				TransactionManager transactionManager = new TransactionMongoManager(client, dbName, authorCollection,
						bookCollection);

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
