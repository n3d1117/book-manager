package it.ned.bookmanager.service.transactional;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.repository.AuthorRepository;
import it.ned.bookmanager.repository.BookRepository;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.transaction.TransactionManager;

import java.util.List;

public class AuthorTransactionalService implements AuthorService {

    private final TransactionManager transactionManager;

    public AuthorTransactionalService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Author> getAllAuthors() {
        return transactionManager.doInTransaction(
                factory -> factory.createAuthorRepository().findAll()
        );
    }

    @Override
    public Author findAuthorById(String id) {
        return transactionManager.doInTransaction(
                factory -> factory.createAuthorRepository().findById(id)
        );
    }

    @Override
    public void addAuthor(Author author) {
        transactionManager.doInTransaction(factory -> {
            if (author != null)
                factory.createAuthorRepository().add(author);
            return null;
        });
    }

    @Override
    public void deleteAuthor(Author author) {
        transactionManager.doInTransaction(factory -> {
            if (author != null) {
                AuthorRepository authorRepository = factory.createAuthorRepository();
                BookRepository bookRepository = factory.createBookRepository();
                authorRepository.allWrittenBooksForAuthor(author).forEach(bookRepository::delete);
                authorRepository.delete(author);
            }
            return null;
        });
    }

    @Override
    public Author findAuthorFromBookId(String bookId) {
        return transactionManager.doInTransaction(factory -> {
            if (factory.createBookRepository().findById(bookId) != null) {
                return factory.createAuthorRepository().findAuthorFromBookId(bookId);
            }
            return null;
        });
    }

    @Override
    public Book assignAuthorToBook(Author author, Book book) {
        return transactionManager.doInTransaction(factory -> {
            if (factory.createBookRepository().findById(book.getId()) != null) {
                return factory.createAuthorRepository().assignAuthorToBook(author, book);
            }
            return null;
        });
    }

    @Override
    public List<Book> allWrittenBooks(Author author) {
        return transactionManager.doInTransaction(
                factory -> factory.createAuthorRepository().allWrittenBooksForAuthor(author)
        );
    }
}
