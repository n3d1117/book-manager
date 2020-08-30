package it.ned.bookmanager.model;

import java.util.Objects;

public class Book {

    private final String id;
    private final String title;
    private final Integer numberOfPages;

    public Book(String id, String name, Integer numberOfPages) {
        this.id = id;
        this.title = name;
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
