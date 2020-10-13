package it.ned.bookmanager.repository.mongo;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.Logger;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import it.ned.bookmanager.repository.Repository;

//
// A generic repository using MongoDB APIs that provides basic operations on any java object,
// such as findAll, findById, add and delete, leveraging mongodb's POJO support for
// automatic serialization. See also: http://mongodb.github.io/mongo-java-driver/3.12/bson/pojos/
// NOTE: This class assumes that your object has a field named 'id', or at least one field annotated
// with @BsonId. See documentation for more details.
//
public class MongoRepository<T> implements Repository<T> {

	protected final MongoCollection<T> collection;

	// NOTE: each operation in the transaction *must* be associated with the session
	// See also: https://docs.mongodb.com/manual/core/transactions/
	protected final ClientSession session;

	protected final String entityShortName;
	private final Logger logger;

	// To benefit from automatic serialization of Java objects into documents (and
	// viceversa)
	// we need a MongoCollection instance configured with the Pojo’s type.
	// Since this is a generic class, we find out the runtime type by passing the
	// class
	// of the type parameter to the constructor. We also use the passed type to
	// extract
	// the entity's short name (i.e "Book"), for logging purposes.
	public MongoRepository(MongoClient mongoClient, ClientSession session, String collectionName, String dbName,
			Class<T> entityType, Logger logger) {
		this.session = session;
		this.logger = logger;
		this.entityShortName = entityType.getSimpleName();

		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		collection = mongoClient.getDatabase(dbName).getCollection(collectionName, entityType)
				.withCodecRegistry(pojoCodecRegistry);
	}

	@Override
	public List<T> findAll() {
		logger.debug(() -> String.format("Finding all objects of type %s", entityShortName));
		return StreamSupport.stream(collection.find(session).spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public T findById(String id) {
		logger.debug(() -> String.format("Finding %s with id %s", entityShortName, id));
		// When querying POJOs you must query against the document field name and not
		// the
		// Pojo’s property name. The _id document key here maps to the POJO’s id
		// property.
		// See also: http://mongodb.github.io/mongo-java-driver/3.12/bson/pojos/
		return collection.find(session, eq("_id", id)).first();
	}

	@Override
	public void add(T t) {
		logger.debug(() -> String.format("Adding %s: %s", entityShortName, t.toString()));
		collection.insertOne(session, t);
	}

	@Override
	public void delete(String id) {
		logger.debug(() -> String.format("Deleting %s with id %s", entityShortName, id));
		collection.deleteOne(session, eq("_id", id));
	}

}
