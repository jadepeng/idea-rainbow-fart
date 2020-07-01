package com.github.jadepeng.rainbowfart.settings.tts;
import com.github.jadepeng.rainbowfart.bean.Contribute;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TTSTableModel  extends AbstractTableModel {

    private static final int KEYWORDS_COLUMN = 1;
    private static final int TEXT_COLUMN = 2;
    private static final int NAME_COLUME = 0;

    private final List<Contribute> dataLists;

    public TTSTableModel() {
        this.dataLists = new ArrayList<>();
    }

    public List<Contribute> getDataLists() {
        return dataLists;
    }

    public void addRow(@NotNull Contribute contribute) {
        dataLists.add(contribute);
        fireTableDataChanged();
    }

    public void addRows(@NotNull Collection<Contribute> contributes) {
        dataLists.addAll(contributes);
        fireTableDataChanged();
    }

    public void deleteRow(int index) {
        dataLists.remove(index);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return dataLists.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final Contribute contribute = dataLists.get(rowIndex);

        switch (columnIndex) {
            case NAME_COLUME:
                return contribute.getName();
            case KEYWORDS_COLUMN:
                return String.join("\n",contribute.getKeywords());
            case TEXT_COLUMN:
                return String.join("\n",contribute.getText());
            default:
                throw new IllegalArgumentException("unknown column index: " + columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String text = (String) aValue;
        switch (columnIndex) {
            case KEYWORDS_COLUMN:
                dataLists.get(rowIndex).setKeywords(Arrays.asList(text.split("\n")));
                break;
            case TEXT_COLUMN:
                dataLists.get(rowIndex).setText(Arrays.asList(text.split("\n")));
                break;
            case NAME_COLUME:
                dataLists.get(rowIndex).setName(text);
                break;
            default:
                throw new IllegalArgumentException("unknown column index: " + columnIndex);
        }
    }
}