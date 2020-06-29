package com.github.jadepeng.rainbowfart.settings;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 *  配置UI组件
 * @author jqpeng
 */
public class SettingsComponent {
    private final TextFieldWithBrowseButton txtPackagePath = new TextFieldWithBrowseButton();
    private final JPanel myMainPanel;
    public SettingsComponent() {

        txtPackagePath.addBrowseFolderListener("Choose Custom Voice Package","Custom Voice Package Path:",null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(chkEnable, 1)
                .addLabeledComponent(new JBLabel("Custom Voice Package Path: "), txtPackagePath, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        FartSettings settings = FartSettings.getInstance();
        this.chkEnable.setSelected(settings.isEnable());
        this.txtPackagePath.setText(settings.getCustomVoicePackage());
    }
}
