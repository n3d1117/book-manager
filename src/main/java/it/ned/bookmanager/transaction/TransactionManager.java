package it.ned.bookmanager.transaction;

public interface TransactionManager {
	<T> T doInTransaction(TransactionCode<T> code);
}
