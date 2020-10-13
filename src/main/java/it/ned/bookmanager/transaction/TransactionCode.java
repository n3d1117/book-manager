package it.ned.bookmanager.transaction;

import java.util.function.Function;

import it.ned.bookmanager.repository.RepositoryFactory;

@FunctionalInterface
public interface TransactionCode<T> extends Function<RepositoryFactory, T> {

}
