package it.ned.bookmanager.view.swing.components;

import javax.swing.*;

public class SortedComboBoxModel<T extends Comparable<? super T>> extends DefaultComboBoxModel<T> {

    @Override
    public void addElement(T element) {
        if (getIndexOf(element ) == -1)
            insertElementAt(element, 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insertElementAt(T element, int index) {
        int size = getSize();
        int i;
        for (i = 0; i<size; i++) {
            Comparable<T> c = (Comparable<T>)getElementAt(i);
            if (c.compareTo(element) > 0)
                break;
        }
        super.insertElementAt(element, i);
    }
}
