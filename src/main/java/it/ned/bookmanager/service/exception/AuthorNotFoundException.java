package it.ned.bookmanager.service.exception;

public class AuthorNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7310672143520663201L;

	public AuthorNotFoundException(String message) {
		super(message);
	}
}
