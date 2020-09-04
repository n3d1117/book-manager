package it.ned.bookmanager.repository.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import it.ned.bookmanager.repository.Repository;
import org.apache.logging.log4j.Logger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRepository<T> implements Repository<T> {

    protected final MongoCollection<T> collection;

    protected final String entityShortName;
    protected final Logger logger;

    // To benefit from automatic serialization of Java objects into documents (and viceversa)
    // we need a MongoCollection instance configured with the Pojo’s type.
    // To find out the runtime type of generic type parameters, we pass the class
    // of the type parameter into the constructor of the generic type.
    // See also https://stackoverflow.com/a/3437930
    // We also use the passed type to extract the entity short name, for logging purposes.
    public MongoRepository(MongoClient mongoClient, String dbName, String collectionName, Class<T> type, Logger logger) {
        this.logger = logger;
        this.entityShortName = type.getSimpleName();

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        collection = mongoClient
                .getDatabase(dbName)
                .getCollection(collectionName, type)
                .withCodecRegistry(pojoCodecRegistry);
    }

    @Override
    public List<T> findAll() {
        logger.debug(() -> String.format("Finding all objects of type %s", entityShortName));
        return StreamSupport
                .stream(collection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public T findById(String id) {
        logger.debug(() -> String.format("Finding %s with id %s", entityShortName, id));
        // When querying POJOs you must query against the document field name and not the
        // Pojo’s property name. The _id document key here maps to the POJO’s id property.
        // See also: http://mongodb.github.io/mongo-java-driver/3.9/bson/pojos/
        return collection.find(eq("_id", id)).first();
    }

    @Override
    public void add(T t) {
        logger.debug(() -> String.format("Adding %s: %s", entityShortName, t.toString()));
        collection.insertOne(t);
    }

    @Override
    public void delete(String id) {
        logger.debug(() -> String.format("Deleting %s with id %s", entityShortName, id));
        collection.deleteOne(eq("_id", id));
    }

}
