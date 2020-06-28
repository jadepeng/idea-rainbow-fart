package com.github.jadepeng.rainbowfart.bean;

import java.util.List;

/**
 *  清单文件
 * @author jqpeng
 */
public class Manifest {

    private String name;

    private String displayName;

    private String avatar;

    private String avatarDark;
    private String version;
    private String description;
    private List languages;
    private String author;
    private String gender;
    private String locale;
    private List<Contribute> contributes;

    public Manifest(){

    }

    public Manifest(String name, String displayName, String avatar, String avatarDark, String version, String description, List languages, String author, String gender, String locale) {
        this.name = name;
        this.displayName = displayName;
        this.avatar = avatar;
        this.avatarDark = avatarDark;
        this.version = version;
        this.description = description;
        this.languages = languages;
        this.author = author;
        this.gender = gender;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarDark() {
        return avatarDark;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public List getLanguages() {
        return languages;
    }

    public String getAuthor() {
        return author;
    }

    public String getGender() {
        return gender;
    }

    public String getLocale() {
        return locale;
    }

    public List<Contribute> getContributes() {
        return contributes;
    }

    public void setContributes(List<Contribute> contributes) {
        this.contributes = contributes;
    }
}
