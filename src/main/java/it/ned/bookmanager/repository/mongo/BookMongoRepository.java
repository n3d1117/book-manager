package it.ned.bookmanager.repository.mongo;

import com.mongodb.MongoClient;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.BookRepository;
import org.apache.logging.log4j.LogManager;

import static com.mongodb.client.model.Filters.eq;

public class BookMongoRepository extends MongoRepository<Book> implements BookRepository {

    public BookMongoRepository(MongoClient mongoClient, String dbName, String collectionName) {
        super(mongoClient, dbName, collectionName, Book.class, LogManager.getLogger(BookMongoRepository.class));
    }

    @Override
    public void deleteAllBooksForAuthorId(String authorId) {
        logger.debug(() -> String.format("Deleting all books from author with id %s", authorId));
        collection.deleteMany(eq("authorId", authorId));
    }
}
