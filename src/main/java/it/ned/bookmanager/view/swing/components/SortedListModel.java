package it.ned.bookmanager.view.swing.components;

import javax.swing.*;
import java.util.*;

public class SortedListModel<T extends Comparable<? super T>> extends AbstractListModel<T> {

	private static final long serialVersionUID = -5369997753780397039L;
	
	private final transient List<T> items;

    public SortedListModel() {
        items = new ArrayList<>(Collections.emptyList());
    }

    public List<T> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    public T getElementAt(int i) {
        return items.get(i);
    }

    public void addElement(T element) {
        if (!items.contains(element)) {
            items.add(element);
            Collections.sort(items);
            fireContentsChanged(this, 0, items.size());
        }
    }

    public void removeElement(T element) {
        items.remove(element);
        fireContentsChanged(this, 0, getSize());
    }
}
