package it.ned.bookmanager.service.exception;

public class BookNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6268823929080995386L;

	public BookNotFoundException(String message) {
        super(message);
    }
}
