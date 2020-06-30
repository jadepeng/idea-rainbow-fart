package com.github.jadepeng.rainbowfart.settings;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum VoicePackageType {
    TTS,
    Custom,
    Builtin;

    public static List<String> getTypes(){
        return Arrays.stream(VoicePackageType.values()).map(e->e.toString()).collect(Collectors.toList());
    }
}
