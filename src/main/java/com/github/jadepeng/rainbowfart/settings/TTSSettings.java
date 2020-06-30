package com.github.jadepeng.rainbowfart.settings;

/**
 * tts 合成配置
 */
public class TTSSettings {
    String appid;
    String apiSecret;
    String apiKey;
    String vcn;
    String resourceText;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getVcn() {
        return vcn;
    }

    public void setVcn(String vcn) {
        this.vcn = vcn;
    }

    public String getResourceText() {
        return resourceText;
    }

    public void setResourceText(String resourceText) {
        this.resourceText = resourceText;
    }
}
