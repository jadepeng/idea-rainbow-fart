package com.github.jadepeng.rainbowfart.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "FartSettings", storages = @Storage("rainbow_fart.xml"))
public class FartSettings implements PersistentStateComponent<FartSettings> {

    protected boolean enable = true;
    protected String customVoicePackage;
    protected TTSSettings ttsSettings;
    private VoicePackageType type;

    public VoicePackageType getType() {
        return type;
    }

    public void setType(VoicePackageType type) {
        this.type = type;
    }

    public TTSSettings getTtsSettings() {
        return ttsSettings;
    }

    public void setTtsSettings(TTSSettings ttsSettings) {
        this.ttsSettings = ttsSettings;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getCustomVoicePackage() {
        return customVoicePackage;
    }

    public void setCustomVoicePackage(String customVoicePackage) {
        this.customVoicePackage = customVoicePackage;
    }

    /**
     * @return a component state. All properties, public and annotated fields are serialized. Only values, which differ
     * from default (i.e. the value of newly instantiated class) are serialized. {@code null} value indicates
     * that the returned state won't be stored, as a result previously stored state will be used.
     * @see XmlSerializer
     */
    @Nullable
    @Override
    public FartSettings getState() {
        return this;
    }

    /**
     * This method is called when new component state is loaded. The method can and will be called several times, if
     * config files were externally changed while IDEA running.
     *
     * @param state loaded component state
     * @see XmlSerializerUtil#copyBean(Object, Object)
     */
    @Override
    public void loadState(@NotNull FartSettings state) {
        this.customVoicePackage = state.customVoicePackage;
        this.enable = state.isEnable();
    }

    public static FartSettings getInstance(){
        return ServiceManager.getService(FartSettings.class);
    }
}
