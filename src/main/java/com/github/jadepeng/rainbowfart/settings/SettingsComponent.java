package com.github.jadepeng.rainbowfart.settings;

import com.github.jadepeng.rainbowfart.Context;
import com.github.jadepeng.rainbowfart.bean.Manifest;
import com.github.jadepeng.rainbowfart.settings.tts.*;
import com.google.gson.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.*;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.FormBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    Map<String, String> name2vcn = new HashMap<>();

    JPanel ttsGroup;
    //    JBTextField txtApiId = new JBTextField();
//    JBTextField txtApiSecret = new JBTextField();
//    JBTextField txtAppKey = new JBTextField();
    ComboBox cbxVcn = new ComboBox();

    ComboBox cbxBuiltinPackage = new ComboBox(new ListComboBoxModel(Arrays.asList("built-in-voice-chinese", "built-in-voice-english", "tts-xiaoling")));


    TTSTableModel tableModel = new TTSTableModel();

    public SettingsComponent() {
        // load vcn
        try {
            loadVCN();
            this.cbxVcn.setModel(new ListComboBoxModel(this.name2vcn.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList())));
        } catch (IOException e) {
        }

        txtPackagePath.addBrowseFolderListener("Choose Custom Voice Package", "Custom Voice Package Path:", null,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);

        ttsGroup = FormBuilder.createFormBuilder()
//                .addLabeledComponent(new JBLabel("App id:"), txtApiId, 1, false)
//                .addLabeledComponent(new JBLabel("Api Key: "), txtAppKey, 1, false)
//                .addLabeledComponent(new JBLabel("Api Secret:"), txtApiSecret, 1, false)
                .addLabeledComponent(new JBLabel("VCN: "), cbxVcn, 1, false)
                .addComponent(new JBLabel("Match Rule Setting: "), 0)
                .addComponent(createTTSTable())
                .getPanel();

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(chkEnable, 1)
                .addLabeledComponent(new JBLabel("Voice Package Type: "), cbxType, 1, false)

                .addComponent(new JBLabel("Builtin Configuration: "), 0)
                .addLabeledComponent(new JBLabel("Choose Builtin Package: "), txtPackagePath, 1, false)
                .addComponent(new JBLabel("Custom Configuration: "), 0)
                .addLabeledComponent(new JBLabel("Custom Voice Package Path: "), txtPackagePath, 1, false)
                .addComponent(new JBLabel("TTS Configuration: "), 0)
//                .addLabeledComponent(new JBLabel("TTS: "), new LinkLabel("use: https://www.xfyun.cn/services/online_tts", AllIcons.Icons.Ide.NextStep), 1, false)
                .addComponent(ttsGroup, 0)
                .addComponentFillVertically(new JPanel(), 0)
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
        URL filePath = getClass().getClassLoader().getResource("/vcn.json");
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

//        this.txtAppKey.setText(settings.getTtsSettings().getApiKey());
//        this.txtApiId.setText(settings.getTtsSettings().getAppid());
//        this.txtApiSecret.setText(settings.getTtsSettings().getApiSecret());
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
//        settings.setApiKey(this.txtAppKey.getText());
//        settings.setApiSecret(this.txtApiSecret.getText());
//        settings.setAppid(this.txtApiId.getText());
        Manifest manifest = new Manifest();
        manifest.setContributes(this.tableModel.getDataLists());
        settings.setResourceText(new GsonBuilder().setPrettyPrinting().create().toJson(manifest));
        settings.setVcnName(this.cbxVcn.getSelectedItem().toString());
        settings.setVcn(this.name2vcn.get(settings.getVcnName()));
        return settings;
    }
}
