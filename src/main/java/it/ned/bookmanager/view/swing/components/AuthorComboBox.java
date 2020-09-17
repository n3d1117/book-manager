package it.ned.bookmanager.view.swing.components;

import it.ned.bookmanager.model.Author;

import javax.swing.*;
import java.awt.*;

public class AuthorComboBox<T> extends JComboBox<T> {

    public AuthorComboBox(DefaultComboBoxModel<T> authors) {
        super(authors);
        setRenderer(new AuthorRenderer());
    }

    class AuthorRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Author) {
                value = "👤 " + ((Author)value).getName();
            }
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (getSelectedItem() == null && index < 0)
                setText("-- Choose --");
            return this;
        }
    }


}
