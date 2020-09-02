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
    public List<Book> getAllBooks() {
        return transactionManager.doInTransaction(
                factory -> factory.createBookRepository().findAll()
        );
    }

    @Override
    public Book findBookById(String id) {
        return transactionManager.doInTransaction(
                factory -> factory.createBookRepository().findById(id)
        );
    }

    @Override
    public void addBook(Book book) {
        transactionManager.doInTransaction(factory -> {
            if (book != null)
                factory.createBookRepository().add(book);
            return null;
        });
    }

    @Override
    public void deleteBook(Book book) {
        transactionManager.doInTransaction(factory -> {
            if (book != null)
                factory.createBookRepository().delete(book.getId());
            return null;
        });
    }
}
