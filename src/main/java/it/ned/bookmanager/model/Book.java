package it.ned.bookmanager.model;

import java.util.Objects;

public class Book {

    private String id;
    private String title;
    private Integer numberOfPages;

    // By default all POJOs must include a public or protected, empty constructor
    // See also http://mongodb.github.io/mongo-java-driver/3.9/bson/pojos/
    protected Book() { }

    public Book(String id, String title, Integer numberOfPages) {
        this.id = id;
        this.title = title;
        this.numberOfPages = numberOfPages;
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

    public void setId(final String id) {
        this.id = id;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setNumberOfPages(final Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id.equals(book.getId()) &&
                Objects.equals(title, book.getTitle()) &&
                Objects.equals(numberOfPages, book.getNumberOfPages());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, numberOfPages);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", numberOfPages=" + numberOfPages +
                '}';
    }
}
