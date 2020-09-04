package it.ned.bookmanager.service.transactional;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.repository.AuthorRepository;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.service.exception.AuthorAlreadyInDatabaseException;
import it.ned.bookmanager.service.exception.AuthorNotFoundException;
import it.ned.bookmanager.transaction.TransactionManager;

import java.util.List;

public class AuthorTransactionalService implements AuthorService {

    private final TransactionManager transactionManager;

    private static final String AUTHOR_NOT_FOUND_ERROR_MESSAGE = "Author with id %s not found in database.";
    private static final String AUTHOR_ALREADY_IN_DB_ERROR_MESSAGE = "Author with id %s is already in database.";

    public AuthorTransactionalService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Author> findAll() {
        return transactionManager.doInTransaction(
                factory -> factory.createAuthorRepository().findAll()
        );
    }

    @Override
    public Author findById(String id) {
        return transactionManager.doInTransaction(
                factory -> factory.createAuthorRepository().findById(id)
        );
    }

    public void add(Author author) {
        transactionManager.doInTransaction(factory -> {
            if (author != null) {
                AuthorRepository authorRepository = factory.createAuthorRepository();
                if (authorRepository.findById(author.getId()) != null)
                    throw new AuthorAlreadyInDatabaseException(
                            String.format(AUTHOR_ALREADY_IN_DB_ERROR_MESSAGE, author.getId())
                    );
                authorRepository.add(author);
            }
            return null;
        });
    }

    @Override
    public void delete(String authorId) {
        transactionManager.doInTransaction(factory -> {
            AuthorRepository authorRepository = factory.createAuthorRepository();
            if (authorRepository.findById(authorId) == null)
                throw new AuthorNotFoundException(
                        String.format(AUTHOR_NOT_FOUND_ERROR_MESSAGE, authorId)
                );
            factory.createBookRepository().deleteAllBooksForAuthorId(authorId);
            authorRepository.delete(authorId);
            return null;
        });
    }
}
