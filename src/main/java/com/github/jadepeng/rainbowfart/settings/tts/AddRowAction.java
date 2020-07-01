package com.github.jadepeng.rainbowfart.settings.tts;
import com.github.jadepeng.rainbowfart.bean.Contribute;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

public final class AddRowAction extends DumbAwareAction {

    private final TTSTableModel model;

    public AddRowAction(@NotNull TTSTableModel model) {
        super("Add", "Add", AllIcons.General.Add);

        this.model = model;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        model.addRow(new Contribute());
    }
}