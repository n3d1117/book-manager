package it.ned.bookmanager.service;

import java.util.List;

public interface Service<T> {
    List<T> findAll();
    T findById(String id);
    void add(T t);
    void delete(String id);
}
