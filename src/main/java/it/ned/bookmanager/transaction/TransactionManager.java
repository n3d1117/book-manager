package it.ned.bookmanager.transaction;

public interface TransactionManager {
    public <R> R doInTransaction(TransactionCode<R> code);
}
