package com.github.jadepeng.rainbowfart.settings.tts;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class DeleteRowAction extends DumbAwareAction {

    private final TTSTableModel model;
    private final JTable table;

    public DeleteRowAction(@NotNull TTSTableModel model, @NotNull JTable table) {
        super("Delete", "Delete", AllIcons.General.Remove);

        this.model = model;
        this.table = table;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            model.deleteRow(selectedRow);
        }
    }
}
