package it.ned.bookmanager.view.swing.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BookTableCellRenderer extends DefaultTableCellRenderer {

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
