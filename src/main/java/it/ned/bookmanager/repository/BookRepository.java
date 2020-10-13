package it.ned.bookmanager.repository;

import it.ned.bookmanager.model.Book;

public interface BookRepository extends Repository<Book> {
	void deleteAllBooksForAuthorId(String authorId);
}
