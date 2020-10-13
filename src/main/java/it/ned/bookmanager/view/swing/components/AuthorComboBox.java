package it.ned.bookmanager.view.swing.components;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import it.ned.bookmanager.model.Author;

public class AuthorComboBox<T> extends JComboBox<T> {

	private static final long serialVersionUID = -2441908901802016061L;

	public AuthorComboBox(DefaultComboBoxModel<T> authors) {
        super(authors);
        setRenderer(new AuthorRenderer());
    }

    class AuthorRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -5572726167934009611L;

		@Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Author) {
                value = ((Author)value).getName();
            }
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (getSelectedItem() == null && index < 0)
                setText("-- Choose --");
            return this;
        }
    }


}
