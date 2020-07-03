package com.github.jadepeng.rainbowfart.settings.tts;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;

/**
 * https://blog.csdn.net/u012134727/article/details/48266957
 */
public class TableCellTextAreaRenderer extends JTextArea implements TableCellRenderer {
    public TableCellTextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        int maxPreferredHeight = 0;
        for (int i = 0; i < table.getColumnCount(); i++) {
            setText("  " + table.getValueAt(row, i));
            setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
            //setMargin(new Insets(0,10,0,10));
            maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height);
        }

        if (table.getRowHeight(row) != maxPreferredHeight) {
            table.setRowHeight(row, maxPreferredHeight);
        }

        setFont(new Font("微软雅黑", Font.PLAIN, 15));
        setText(value == null ? "" : value.toString());
        return this;
    }
}