package it.ned.bookmanager.view.swing.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BookTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -8723816840181481973L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        if (table.isCellSelected(row, column))
            setBackground(new Color(0xE38484));
        else
            setBackground(new Color(243, 243, 243));
        return this;
    }
}
