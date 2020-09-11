package it.ned.bookmanager.transaction.mongo;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import it.ned.bookmanager.repository.mongo.MongoRepositoryFactory;
import it.ned.bookmanager.transaction.TransactionCode;
import it.ned.bookmanager.transaction.TransactionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class TransactionMongoManager implements TransactionManager {

    private final MongoClient mongoClient;
    private final String databaseName;
    private final String authorsCollectionName;
    private final String booksCollectionName;

    private static final Logger LOGGER = LogManager.getLogger(TransactionMongoManager.class);

    public TransactionMongoManager(MongoClient mongoClient, String databaseName, String authorsCollectionName,
                                   String booksCollectionName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        this.authorsCollectionName = authorsCollectionName;
        this.booksCollectionName = booksCollectionName;

        // In MongoDB 4.2 and earlier, when dealing with multi-document transactions you must
        // create the collection (if it does not already exist) *before* the transaction happens.
        // See also: https://docs.mongodb.com/manual/core/transactions/
        MongoDatabase database = mongoClient.getDatabase(this.databaseName);
        for (String collectionName: asList(authorsCollectionName, booksCollectionName)) {
            if (!database.listCollectionNames().into(new ArrayList<>()).contains(collectionName)) {
                LOGGER.debug(() -> String.format("Creating collection %s", collectionName));
                database.createCollection(collectionName);
            }
        }
    }

    @Override
    public <T> T doInTransaction(TransactionCode<T> code) {
        T result = null;

        ClientSession clientSession = mongoClient.startSession();

        // See also: https://docs.mongodb.com/manual/core/transactions/
        TransactionOptions options = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();

        MongoRepositoryFactory repositoryFactory = new MongoRepositoryFactory(mongoClient, clientSession,
                databaseName, authorsCollectionName, booksCollectionName);
        TransactionBody<T> body = (() -> code.apply(repositoryFactory));

        try {
            result = clientSession.withTransaction(body, options);
        } catch (RuntimeException e) {
            LOGGER.debug(() -> String.format("Caught a RuntimeException: %s", e.getMessage()));
        } finally {
            clientSession.close();
        }

        return result;
    }
}
