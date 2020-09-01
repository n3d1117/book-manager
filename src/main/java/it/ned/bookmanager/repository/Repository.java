package it.ned.bookmanager.repository;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
    T findById(String id);
    T add(T t);
    T delete(T t);
}
