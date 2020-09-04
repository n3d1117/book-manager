package it.ned.bookmanager.service.transactional;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.service.AuthorService;
import it.ned.bookmanager.transaction.TransactionManager;

import java.util.List;

public class AuthorTransactionalService implements AuthorService {

    private final TransactionManager transactionManager;

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
            if (author != null)
                factory.createAuthorRepository().add(author);
            return null;
        });
    }

    @Override
    public void delete(String authorId) {
        transactionManager.doInTransaction(factory -> {
            factory.createBookRepository().deleteAllBooksForAuthorId(authorId);
            factory.createAuthorRepository().delete(authorId);
            return null;
        });
    }
}
