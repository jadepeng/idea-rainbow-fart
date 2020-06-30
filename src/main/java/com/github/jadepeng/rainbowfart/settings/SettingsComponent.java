package com.github.jadepeng.rainbowfart.settings;

import com.github.jadepeng.rainbowfart.PluginStarter;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.*;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.xml.ui.DomFileEditor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * 配置UI组件
 *
 * @author jqpeng
 */
public class SettingsComponent {
    private final TextFieldWithBrowseButton txtPackagePath = new TextFieldWithBrowseButton();
    ComboBox cbxType = new ComboBox(new ListComboBoxModel(VoicePackageType.getTypes()));
    private final JPanel myMainPanel;
    private final JBCheckBox chkEnable = new JBCheckBox("Enable Rainbow Fart");

    JPanel ttsGroup;
    JBTextField txtApiId = new JBTextField();
    JBTextField txtApiSecret = new JBTextField();
    JBTextField txtAppKey = new JBTextField();
    JTextArea txtTTSTexts = new JTextArea();
    ComboBox cbxVcn = new ComboBox(new ListComboBoxModel(Arrays.asList("x_xiaoling")));

    public SettingsComponent() {

        cbxVcn.setEnabled(true);
        txtTTSTexts.setMaximumSize(new Dimension(600, 100));
        txtPackagePath.addBrowseFolderListener("Choose Custom Voice Package", "Custom Voice Package Path:", null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        ttsGroup = FormBuilder.createFormBuilder()
                .addComponent(new LinkLabel("see https://www.xfyun.cn/services/online_tts", AllIcons.Icons.Ide.NextStep), 1)
                .addLabeledComponent(new JBLabel("App id:"), txtApiId, 1, false)
                .addLabeledComponent(new JBLabel("Api Key: "), txtAppKey, 1, false)
                .addLabeledComponent(new JBLabel("Api Secret:"), txtApiSecret, 1, false)
                .addLabeledComponent(new JBLabel("VCN: "), cbxVcn, 1, false)
                .addLabeledComponent("Setting", txtTTSTexts)
                .getPanel();

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(chkEnable, 1)
                .addLabeledComponent(new JBLabel("Voice Package Type: "), cbxType, 1, false)
                .addLabeledComponent(new JBLabel("Custom Voice Package Path: "), txtPackagePath, 1, false)
                .addLabeledComponent(new JBLabel("TTS Settings: "), ttsGroup, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        bindSetting();
    }

    private void bindSetting() {
        FartSettings settings = FartSettings.getInstance();
        this.chkEnable.setSelected(settings.isEnable());
        this.txtPackagePath.setText(settings.getCustomVoicePackage());
        if (settings.getTtsSettings() == null || StringUtils.isEmpty(settings.getTtsSettings().getResourceText())) {
            try {
                URL filePath = getClass().getClassLoader().getResource("/default.json");
                this.txtTTSTexts.setText(IOUtils.toString(filePath.openStream(), "utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (settings.getTtsSettings() != null) {
            this.txtTTSTexts.setText(settings.getTtsSettings().getResourceText());
            this.txtAppKey.setText(settings.getTtsSettings().getApiKey());
            this.txtApiId.setText(settings.getTtsSettings().getAppid());
            this.txtApiSecret.setText(settings.getTtsSettings().getApiSecret());
            this.cbxVcn.setSelectedItem(settings.getTtsSettings().getVcn());
        }
        this.cbxType.setSelectedItem(settings.getType() == null ? VoicePackageType.Builtin.toString() : settings.getType().toString());
    }

    public JPanel component() {
        return myMainPanel;
    }

    public boolean isRainbowEnabled() {
        return chkEnable.isSelected();
    }

    public String getPackage() {
        return this.txtPackagePath.getText();
    }

    public VoicePackageType getType() {
        return VoicePackageType.valueOf(this.cbxType.getSelectedItem().toString());
    }

    public TTSSettings getTTSSetting() {
        TTSSettings settings = new TTSSettings();
        settings.setApiKey(this.txtAppKey.getText());
        settings.setApiSecret(this.txtApiSecret.getText());
        settings.setAppid(this.txtApiId.getText());
        settings.setResourceText(this.txtTTSTexts.getText());
        settings.setVcn(this.cbxVcn.getSelectedItem().toString());
        return settings;
    }
}
