package it.ned.bookmanager.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.ClientSession;

import it.ned.bookmanager.repository.AuthorRepository;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.repository.RepositoryFactory;

public class MongoRepositoryFactory implements RepositoryFactory {

    private final MongoClient client;
    private final ClientSession session;
    private final String databaseName;
    private final String authorsCollectionName;
    private final String booksCollectionName;

    public MongoRepositoryFactory(MongoClient client, ClientSession session, String dbName,
                                  String authorsCollectionName, String booksCollectionName) {
        this.client = client;
        this.session = session;
        this.databaseName = dbName;
        this.authorsCollectionName = authorsCollectionName;
        this.booksCollectionName = booksCollectionName;
    }

    @Override
    public AuthorRepository createAuthorRepository() {
        return new AuthorMongoRepository(client, session, databaseName, authorsCollectionName);
    }

    @Override
    public BookRepository createBookRepository() {
        return new BookMongoRepository(client, session, databaseName, booksCollectionName);
    }
}
