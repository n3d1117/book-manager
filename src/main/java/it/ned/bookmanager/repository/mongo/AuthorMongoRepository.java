package it.ned.bookmanager.repository.mongo;

import com.mongodb.MongoClient;
import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.repository.AuthorRepository;
import org.apache.logging.log4j.LogManager;

public class AuthorMongoRepository extends MongoRepository<Author> implements AuthorRepository {

    public AuthorMongoRepository(MongoClient mongoClient, String dbName, String collectionName) {
        super(mongoClient, dbName, collectionName, Author.class, LogManager.getLogger(AuthorMongoRepository.class));
    }

}
