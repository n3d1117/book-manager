package it.ned.bookmanager.transaction;

import it.ned.bookmanager.repository.RepositoryFactory;

import java.util.function.Function;

@FunctionalInterface
public interface TransactionCode<R> extends Function<RepositoryFactory, R> {

}
