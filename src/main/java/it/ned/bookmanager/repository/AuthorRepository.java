package it.ned.bookmanager.repository;

import it.ned.bookmanager.model.Author;
import it.ned.bookmanager.model.Book;

import java.util.List;

public interface AuthorRepository extends Repository<Author> {
    Author findAuthorFromBookId(String bookId);
    Book assignAuthorToBook(Author author, Book book);
    List<Book> allWrittenBooksForAuthor(Author author);
}
