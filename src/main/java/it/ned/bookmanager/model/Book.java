package it.ned.bookmanager.model;

import java.util.Objects;

public class Book {

    private String id;
    private String title;
    private Integer numberOfPages;
    private String authorId;

    // By default all POJOs must include a public or protected, empty constructor
    // See also http://mongodb.github.io/mongo-java-driver/3.12/bson/pojos/
    protected Book() { }

    public Book(String id, String title, Integer numberOfPages, String authorId) {
        this.id = id;
        this.title = title;
        this.numberOfPages = numberOfPages;
        this.authorId = authorId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setNumberOfPages(final Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) &&
                Objects.equals(title, book.title) &&
                Objects.equals(numberOfPages, book.numberOfPages) &&
                Objects.equals(authorId, book.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, numberOfPages, authorId);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", numberOfPages=" + numberOfPages +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
