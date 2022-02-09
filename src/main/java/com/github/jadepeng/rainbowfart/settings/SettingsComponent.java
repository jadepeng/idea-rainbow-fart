package com.github.jadepeng.rainbowfart.settings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;

import com.github.jadepeng.rainbowfart.Context;
import com.github.jadepeng.rainbowfart.bean.Manifest;
import com.github.jadepeng.rainbowfart.settings.tts.AddRowAction;
import com.github.jadepeng.rainbowfart.settings.tts.DeleteRowAction;
import com.github.jadepeng.rainbowfart.settings.tts.TTSColumnModel;
import com.github.jadepeng.rainbowfart.settings.tts.TTSTableModel;
import com.github.jadepeng.rainbowfart.settings.tts.TableCellTextAreaRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

/**
 * Setting Component
 *
 * @author jqpeng
 */
public class SettingsComponent {
    private final TextFieldWithBrowseButton txtPackagePath = new TextFieldWithBrowseButton();
    ComboBox cbxType = new ComboBox(new ListComboBoxModel(VoicePackageType.getTypes()));
    private final JPanel myMainPanel;
    private final JBCheckBox chkEnable = new JBCheckBox("Enable Rainbow Fart");

    Map<String, String> name2vcn = new HashMap<>();

    JPanel ttsGroup;

    ComboBox<String> cbxVcn = new ComboBox<>();

    ComboBox<String> cbxBuiltinPackage = new ComboBox<String>(
            new ListComboBoxModel<String>(Arrays.asList("built-in-voice-chinese", "built-in-voice-english", "tts-xiaoling")));


    TTSTableModel tableModel = new TTSTableModel();

    public SettingsComponent() {
        // load vcn
        try {
            loadVCN();
            this.cbxVcn.setModel(new ListComboBoxModel(new ArrayList<>(this.name2vcn.keySet())));
        } catch (Exception ignored) {
        }

        txtPackagePath.addBrowseFolderListener("Choose Custom Voice Package", "Custom Voice Package Path:", null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        ttsGroup = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("VCN: "), cbxVcn, 1, false)
                .addComponent(new JBLabel("Match Rule Setting: "), 0)
                .addComponent(createTTSTable())
                .getPanel();

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(chkEnable, 1)
                .addLabeledComponent(new JBLabel("Voice Package Type: "), cbxType, 1, false)
                .addSeparator()
                .addComponent(new JBLabel("Builtin Configuration: "), 1)
                .addLabeledComponent(new JBLabel("Choose Builtin Package: "), cbxBuiltinPackage, 1, false)
                .addSeparator()
                .addComponent(new JBLabel("Custom Configuration: "), 1)
                .addLabeledComponent(new JBLabel("Custom Voice Package Path: "), txtPackagePath, 1, false)
                .addSeparator()
                .addComponent(new JBLabel("TTS Configuration: "), 1)
//                .addLabeledComponent(new JBLabel("TTS: "), new LinkLabel("use: https://www.xfyun.cn/services/online_tts", AllIcons.Icons.Ide.NextStep), 1, false)
                .addComponent(ttsGroup, 1)
                .addComponentFillVertically(new JPanel(), 1)
                .getPanel();

        bindSetting();
    }

    @NotNull
    private SimpleToolWindowPanel createTTSTable() {
        TTSColumnModel columns = new TTSColumnModel("Name", "Keywords", "Texts");
        final JBTextField cell = new JBTextField();
        cell.getDocument().putProperty("filterNewlines", Boolean.FALSE);
        final DefaultCellEditor cellEditor = new DefaultCellEditor(cell);
        InputMap iMap = cell.getInputMap(JComponent.WHEN_FOCUSED);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), KeyEvent.getKeyText(KeyEvent.VK_ENTER));
        ActionMap aMap = cell.getActionMap();
        aMap.put(KeyEvent.getKeyText(KeyEvent.VK_ENTER), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cell.setText(cell.getText() + "\n");
            }
        });
        columns.getColumn(0).setPreferredWidth(40);
        columns.getColumn(0).setPreferredWidth(100);
        columns.getColumn(0).setCellEditor(new DefaultCellEditor(new JBTextField()));
        columns.getColumn(1).setCellEditor(cellEditor);
        columns.getColumn(2).setCellEditor(cellEditor);

        JTable table = new JTable(tableModel, columns);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new TableCellTextAreaRenderer());


        JBScrollPane scrollPane = new JBScrollPane(table);

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AddRowAction(tableModel));
        group.add(new DeleteRowAction(tableModel, table));

        SimpleToolWindowPanel tablePanel = new SimpleToolWindowPanel(false);

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("ttsSettingBar", group, false);
        toolbar.setTargetComponent(tablePanel);

        tablePanel.add(scrollPane);
        tablePanel.setToolbar(toolbar.getComponent());
        return tablePanel;
    }

    void loadVCN() throws IOException {
        URL filePath =  Context.class.getResource("/vcn.json");
        String jsonText = IOUtils.toString(filePath.openStream(), "utf-8");
        JsonObject vcnJson = new JsonParser().parse(jsonText).getAsJsonObject();
        JsonArray data = vcnJson.getAsJsonArray("data");
        for (int i = 0; i < data.size(); i++) {
            JsonObject item = data.get(i).getAsJsonObject();
            String name = item.get("desc").getAsString() + "(" +
                    item.get("languages").getAsString() + ")" + "-" + item.get("characteristic").getAsString();
            String code = item.get("id").getAsJsonObject().get("code").getAsString();
            name2vcn.put(name, code);
        }
    }

    private void bindSetting() {
        FartSettings settings = FartSettings.getInstance();
        this.chkEnable.setSelected(settings.isEnable());
        this.txtPackagePath.setText(settings.getCustomVoicePackage());
        if (settings.getTtsSettings() == null) {
            settings.setTtsSettings(new TTSSettings());
        }
        if (StringUtils.isEmpty(settings.getTtsSettings().getResourceText())) {
            settings.getTtsSettings().setResourceText(Context.getBuiltinTtsText());

        }
        Manifest manifest = new Gson().fromJson(settings.getTtsSettings().getResourceText(), Manifest.class);
        this.tableModel.addRows(manifest.getContributes());
        this.cbxVcn.setSelectedItem(settings.getTtsSettings().getVcnName());
        this.cbxType.setSelectedItem(settings.getType() == null ? VoicePackageType.Builtin.toString() : settings.getType().toString());
        this.cbxBuiltinPackage.setSelectedItem(settings.getBuildinPackage());
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

    public String getBuiltinPackage() {
        return this.cbxBuiltinPackage.getSelectedItem().toString();
    }

    public VoicePackageType getType() {
        return VoicePackageType.valueOf(this.cbxType.getSelectedItem().toString());
    }

    public TTSSettings getTTSSetting() {
        TTSSettings settings = new TTSSettings();
        Manifest manifest = new Manifest();
        manifest.setContributes(this.tableModel.getDataLists());
        settings.setResourceText(new GsonBuilder().setPrettyPrinting().create().toJson(manifest));
        if(this.cbxVcn.getSelectedItem() != null) {
            settings.setVcnName(this.cbxVcn.getSelectedItem().toString());
            settings.setVcn(this.name2vcn.get(settings.getVcnName()));
        }
        return settings;
    }
}
