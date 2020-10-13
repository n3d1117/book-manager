package it.ned.bookmanager.view.swing.components;

import javax.swing.*;

public class SortedComboBoxModel<T extends Comparable<T>> extends DefaultComboBoxModel<T> {

	private static final long serialVersionUID = 7307191885025372346L;

	@Override
    public void addElement(T element) {
        if (getIndexOf(element) == -1)
            insertElementAt(element, 0);
    }

    @Override
    public void insertElementAt(T element, int index) {
        int i;
        for (i=0; i<getSize(); i++) {
            if (getElementAt(i).compareTo(element) > 0)
                break;
        }
        super.insertElementAt(element, i);
    }
}
