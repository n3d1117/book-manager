package it.ned.bookmanager.view.swing.components;

import it.ned.bookmanager.model.Author;

import javax.swing.*;
import java.awt.*;

public class AuthorCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Author) {
            value = "👤 " + ((Author)value).getName();
        }
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        if (isSelected)
            setBackground(new Color(-9929840));
        return this;
    }

}
