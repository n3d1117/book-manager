package it.ned.bookmanager.view.swing.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface TextFieldDocumentListener extends DocumentListener {

	@Override
	default void changedUpdate(DocumentEvent e) {
		// Plain text documents never fire this event
	}

}
