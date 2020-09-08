package it.ned.bookmanager.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.ClientSession;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.repository.AuthorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthorMongoRepository extends MongoRepository<Author> implements AuthorRepository {

    private static final Logger authorRepoLogger = LogManager.getLogger(AuthorMongoRepository.class);

    public AuthorMongoRepository(MongoClient mongoClient, ClientSession session, String dbName, String collectionName) {
        super(mongoClient, session, collectionName, dbName, Author.class, authorRepoLogger);
    }

}
