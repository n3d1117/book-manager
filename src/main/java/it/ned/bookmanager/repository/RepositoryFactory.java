package it.ned.bookmanager.repository;

public interface RepositoryFactory {
    AuthorRepository createAuthorRepository();
    BookRepository createBookRepository();
}
