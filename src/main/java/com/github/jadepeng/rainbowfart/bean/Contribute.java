package com.github.jadepeng.rainbowfart.bean;

import java.util.List;

/**
 *  配置项
 * @author jqpeng
 */
public class Contribute {
    List<String> keywords;
    List<String> voices;

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getVoices() {
        return voices;
    }

    public void setVoices(List<String> voices) {
        this.voices = voices;
    }
}
