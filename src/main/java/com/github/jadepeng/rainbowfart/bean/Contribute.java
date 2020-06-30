package com.github.jadepeng.rainbowfart.bean;

import java.util.List;

/**
 *  配置项
 * @author jqpeng
 */
public class Contribute {
    List<String> keywords;
    List<String> voices;
    List<String> text;
    String name;

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

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public String getName() {
        if (name == null) {
            return this.keywords.get(0);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}