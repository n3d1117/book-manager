package it.ned.bookmanager.view.swing.components;

import javax.swing.*;
import java.util.*;

public class SortedListModel<T extends Comparable<? super T>> extends AbstractListModel<T> {

    private final transient SortedSet<T> model;

    public SortedListModel() {
        model = new TreeSet<>();
    }

    public int getSize() {
        return model.size();
    }

    @SuppressWarnings("unchecked")
    public T getElementAt(int i) {
        return (T) model.toArray()[i];
    }

    public void addElement(T element) {
        SwingUtilities.invokeLater(() -> {
            if (model.add(element))
                fireContentsChanged(this, 0, getSize());
        });
    }

    public void removeElement(T element) {
        boolean removed = model.remove(element);
        if (removed)
            fireContentsChanged(this, 0, getSize());
    }
}
