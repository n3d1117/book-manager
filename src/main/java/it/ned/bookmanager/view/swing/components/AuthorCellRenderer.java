package it.ned.bookmanager.view.swing.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import it.ned.bookmanager.model.Author;

public class AuthorCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 3763727730210493273L;

	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        value = ((Author)value).getName();
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        if (isSelected)
            setBackground(new Color(-9929840));
        return this;
    }

}
