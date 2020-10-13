package it.ned.bookmanager.view;

import java.util.List;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

public interface BookManagerView {
	void showAllAuthors(List<Author> allAuthors);

	void showAllBooks(List<Book> allBooks);

	void authorAdded(Author author);

	void authorDeleted(Author author);

	void bookAdded(Book book);

	void bookDeleted(Book book);

	void deletedAllBooksForAuthor(Author author);

	void authorNotAddedBecauseAlreadyExistsError(Author existingAuthor);

	void authorNotDeletedBecauseNotFoundError(Author author);

	void bookNotAddedBecauseAlreadyExistsError(Book existingBook);

	void bookNotDeletedBecauseNotFoundError(Book book);
}
