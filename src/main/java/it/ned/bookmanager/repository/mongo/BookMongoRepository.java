package it.ned.bookmanager.repository.mongo;

import static com.mongodb.client.model.Filters.eq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;

import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.BookRepository;

public class BookMongoRepository extends MongoRepository<Book> implements BookRepository {

    private static final Logger bookRepoLogger = LogManager.getLogger(BookMongoRepository.class);

    public BookMongoRepository(MongoClient mongoClient, ClientSession session, String dbName,
                               String collectionName) {
        super(mongoClient, session, collectionName, dbName, Book.class, bookRepoLogger);
    }

    @Override
    public void deleteAllBooksForAuthorId(String authorId) {
        bookRepoLogger.debug(() -> String.format("Deleting all books from author with id %s", authorId));
        collection.deleteMany(session, eq("authorId", authorId));
    }
}
