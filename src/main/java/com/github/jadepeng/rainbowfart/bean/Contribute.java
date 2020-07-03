package com.github.jadepeng.rainbowfart.bean;

import java.util.ArrayList;
import java.util.List;

/**
 *  Contribute
 * @author jqpeng
 */
public class Contribute {
    List<String> keywords;
    List<String> voices;
    List<String> text;
    List<String> regexps;
    String name;

    public Contribute(){
        name = "";
        keywords = new ArrayList<>();
        text = new ArrayList<>();
        regexps = new ArrayList<>();
    }

    public List<String> getRegexps() {
        return regexps;
    }

    public void setRegexps(List<String> regexps) {
        this.regexps = regexps;
    }

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