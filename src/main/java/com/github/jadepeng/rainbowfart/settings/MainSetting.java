package com.github.jadepeng.rainbowfart.settings;

import com.github.jadepeng.rainbowfart.Context;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MainSetting implements Configurable {

    SettingsComponent settingForm;

    /**
     * Returns the visible name of the configurable component.
     * Note, that this method must return the display name
     * that is equal to the display name declared in XML
     * to avoid unexpected errors.
     *
     * @return the visible name of the configurable component
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Fart Settings";
    }

    /**
     * Creates new Swing form that enables user to configure the settings.
     * Usually this method is called on the EDT, so it should not take a long time.
     * <p>
     * Also this place is designed to allocate resources (subscriptions/listeners etc.)
     *
     * @return new Swing form to show, or {@code null} if it cannot be created
     * @see #disposeUIResources
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        settingForm = new SettingsComponent();
        return settingForm.component();
    }

    /**
     * Indicates whether the Swing form was modified or not.
     * This method is called very often, so it should not take a long time.
     *
     * @return {@code true} if the settings were modified, {@code false} otherwise
     */
    @Override
    public boolean isModified() {
        return true;
    }

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     *
     * @throws ConfigurationException if values cannot be applied
     */
    @Override
    public void apply() throws ConfigurationException {
        FartSettings settings = FartSettings.getInstance();
        if (settingForm != null) {
            settings.setEnable(settingForm.isRainbowEnabled());
            settings.setCustomVoicePackage(settingForm.getPackage());
            settings.setType(settings.getType());
            settings.setTtsSettings(settings.getTtsSettings());
        }
        // 重新加载
        Context.loadConfig();
    }
}
