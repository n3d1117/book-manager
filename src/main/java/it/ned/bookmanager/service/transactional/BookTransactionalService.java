package it.ned.bookmanager.service.transactional;

import it.ned.bookmanager.model.Book;
import it.ned.bookmanager.service.BookService;
import it.ned.bookmanager.transaction.TransactionManager;

import java.util.List;

public class BookTransactionalService implements BookService {

    private final TransactionManager transactionManager;

    public BookTransactionalService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Book> findAll() {
        return transactionManager.doInTransaction(
                factory -> factory.createBookRepository().findAll()
        );
    }

    @Override
    public Book findById(String id) {
        return transactionManager.doInTransaction(
                factory -> factory.createBookRepository().findById(id)
        );
    }

    @Override
    public void add(Book book) {
        transactionManager.doInTransaction(factory -> {
            if (book != null)
                factory.createBookRepository().add(book);
            return null;
        });
    }

    @Override
    public void delete(String bookId) {
        transactionManager.doInTransaction(factory -> {
            factory.createBookRepository().delete(bookId);
            return null;
        });
    }

    @Override
    public void deleteAllBooksForAuthorId(String authorId) {
        transactionManager.doInTransaction(factory -> {
            factory.createBookRepository().deleteAllBooksForAuthorId(authorId);
            return null;
        });
    }
}
