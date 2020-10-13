package it.ned.bookmanager.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.AuthorRepository;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.repository.RepositoryFactory;
import it.ned.bookmanager.repository.mongo.AuthorMongoRepository;
import it.ned.bookmanager.repository.mongo.BookMongoRepository;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.transactional.AuthorTransactionalService;
import it.ned.bookmanager.service.transactional.BookTransactionalService;
import it.ned.bookmanager.transaction.TransactionCode;
import it.ned.bookmanager.transaction.TransactionManager;
import it.ned.bookmanager.view.BookManagerView;

public class BookManagerControllerRaceConditionIT {

	@Mock
	private BookManagerView view;

	private AuthorRepository authorRepository;
	private BookRepository bookRepository;
	private BookManagerController controller;

	private MongoClient client;

	private static final String DB_NAME = "bookmanager";
	private static final String DB_AUTHOR_COLLECTION = "authors";
	private static final String DB_BOOK_COLLECTION = "books";

	@Mock
	private TransactionManager transactionManager;
	@Mock
	private RepositoryFactory repositoryFactory;

	private static final DockerImageName mongoImage = DockerImageName.parse("mongo").withTag("4.0.10");

	@ClassRule
	public static final MongoDBContainer container = new MongoDBContainer(mongoImage).withExposedPorts(27017);

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		client = MongoClients.create(container.getReplicaSetUrl());
		client.getDatabase(DB_NAME).drop();
		ClientSession session = client.startSession();

		authorRepository = spy(new AuthorMongoRepository(client, session, DB_NAME, DB_AUTHOR_COLLECTION));
		bookRepository = spy(new BookMongoRepository(client, session, DB_NAME, DB_BOOK_COLLECTION));

		when(repositoryFactory.createAuthorRepository()).thenReturn(authorRepository);
		when(repositoryFactory.createBookRepository()).thenReturn(bookRepository);

		when(transactionManager.doInTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));

		AuthorService authorService = new AuthorTransactionalService(transactionManager);
		BookService bookService = new BookTransactionalService(transactionManager);

		controller = new BookManagerController(authorService, bookService, view);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testNewAuthorConcurrently() {
		List<Author> authors = new ArrayList<>();
		Author georgeOrwell = new Author("1", "George Orwell");

		when(authorRepository.findById(anyString()))
				.thenAnswer(invocation -> authors.stream().findFirst().orElse(null));
		doAnswer(invocation -> {
			authors.add(georgeOrwell);
			return null;
		}).when(authorRepository).add(any(Author.class));

		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(() -> controller.addAuthor(georgeOrwell))).peek(Thread::start)
				.collect(Collectors.toList());
		await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
		assertThat(authors).containsExactly(georgeOrwell);
	}

	@Test
	public void testDeleteAuthorConcurrently() {
		List<Author> authors = new ArrayList<>();
		Author georgeOrwell = new Author("1", "George Orwell");

		when(authorRepository.findById(anyString())).thenAnswer(invocation -> authors.isEmpty() ? georgeOrwell : null);
		doAnswer(invocation -> {
			authors.add(georgeOrwell);
			return null;
		}).when(authorRepository).delete(anyString());

		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(() -> controller.deleteAuthor(georgeOrwell))).peek(Thread::start)
				.collect(Collectors.toList());
		await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
		assertThat(authors).containsExactly(georgeOrwell);
	}

	@Test
	public void testNewBookConcurrently() {
		List<Book> books = new ArrayList<>();
		Book animalFarm = new Book("1", "Animal Farm", 93, "1");

		when(bookRepository.findById(anyString())).thenAnswer(invocation -> books.stream().findFirst().orElse(null));
		doAnswer(invocation -> {
			books.add(animalFarm);
			return null;
		}).when(bookRepository).add(any(Book.class));

		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> controller.addBook(animalFarm)))
				.peek(Thread::start).collect(Collectors.toList());
		await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
		assertThat(books).containsExactly(animalFarm);
	}

	@Test
	public void testDeleteBookConcurrently() {
		List<Book> books = new ArrayList<>();
		Book animalFarm = new Book("1", "Animal Farm", 93, "1");

		when(bookRepository.findById(anyString())).thenAnswer(invocation -> books.isEmpty() ? animalFarm : null);
		doAnswer(invocation -> {
			books.add(animalFarm);
			return null;
		}).when(bookRepository).delete(anyString());

		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> controller.deleteBook(animalFarm)))
				.peek(Thread::start).collect(Collectors.toList());
		await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
		assertThat(books).containsExactly(animalFarm);
	}

}
