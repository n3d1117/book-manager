package it.ned.bookmanager.repository;

import java.util.List;

public interface Repository<T> {
	List<T> findAll();

	T findById(String id);

	void add(T t);

	void delete(String id);
}
