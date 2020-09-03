package it.ned.bookmanager.service;

import it.ned.bookmanager.model.Book;

public interface BookService extends Service<Book> {
    void deleteAllBooksForAuthorId(String authorId);
}
