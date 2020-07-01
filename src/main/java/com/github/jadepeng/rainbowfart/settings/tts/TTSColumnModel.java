package com.github.jadepeng.rainbowfart.settings.tts;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class TTSColumnModel  extends DefaultTableColumnModel {

    public TTSColumnModel(String... names) {
        for (int i = 0; i < names.length; i++) {
            addColumn(i, names[i]);
        }
    }

    private void addColumn(int i, String name) {
        final TableColumn column = new TableColumn(i);
        column.setHeaderValue(name);
        addColumn(column);
    }
}
