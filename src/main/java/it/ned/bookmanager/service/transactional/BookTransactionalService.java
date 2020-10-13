package it.ned.bookmanager.service.transactional;

import java.util.List;

import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.service.exception.AuthorNotFoundException;
import it.ned.bookmanager.service.exception.BookDuplicateException;
import it.ned.bookmanager.service.exception.BookNotFoundException;
import it.ned.bookmanager.transaction.TransactionManager;

public class BookTransactionalService implements BookService {

	private final TransactionManager transactionManager;

	private static final String BOOK_NOT_FOUND_ERROR_MESSAGE = "Book with id %s not found in database.";
	private static final String BOOK_DUPLICATE_ERROR_MESSAGE = "Book with id %s is already in database.";
	private static final String AUTHOR_NOT_FOUND_ERROR_MESSAGE = "Author with id %s not found in database.";

	public BookTransactionalService(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public List<Book> findAll() {
		return transactionManager.doInTransaction(factory -> factory.createBookRepository().findAll());
	}

	@Override
	public Book findById(String id) {
		return transactionManager.doInTransaction(factory -> factory.createBookRepository().findById(id));
	}

	@Override
	public void add(Book book) {
		transactionManager.doInTransaction(factory -> {
			if (book != null) {
				BookRepository bookRepository = factory.createBookRepository();
				if (bookRepository.findById(book.getId()) != null) {
					Book existingBook = bookRepository.findById(book.getId());
					throw new BookDuplicateException(String.format(BOOK_DUPLICATE_ERROR_MESSAGE, book.getId()),
							existingBook);
				}
				bookRepository.add(book);
			}
			return null;
		});
	}

	@Override
	public void delete(String bookId) {
		transactionManager.doInTransaction(factory -> {
			BookRepository bookRepository = factory.createBookRepository();
			if (bookRepository.findById(bookId) == null)
				throw new BookNotFoundException(String.format(BOOK_NOT_FOUND_ERROR_MESSAGE, bookId));
			bookRepository.delete(bookId);
			return null;
		});
	}

	@Override
	public void deleteAllBooksForAuthorId(String authorId) {
		transactionManager.doInTransaction(factory -> {
			if (factory.createAuthorRepository().findById(authorId) == null)
				throw new AuthorNotFoundException(String.format(AUTHOR_NOT_FOUND_ERROR_MESSAGE, authorId));
			factory.createBookRepository().deleteAllBooksForAuthorId(authorId);
			return null;
		});
	}
}
