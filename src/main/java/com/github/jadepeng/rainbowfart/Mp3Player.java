package com.github.jadepeng.rainbowfart;

import com.github.jadepeng.rainbowfart.bean.Contribute;
import com.github.jadepeng.rainbowfart.settings.FartSettings;
import com.github.jadepeng.rainbowfart.settings.VoicePackageType;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Mp3Player {

    /**
     * play in a single thread pool
     */
    static ExecutorService playerTheadPool;

    /**
     *  TTS Cache Directory
     */
    static String TEMP_TTS_CACHE_DIR = System.getProperties().getProperty("user.home") + File.separator + "rainbowfart";

    static {
        // create cache dir while
        try {
            File dir = new File(TEMP_TTS_CACHE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }catch (Exception e){

        }

        // init thread pool
        ThreadFactory playerFactory = new ThreadFactoryBuilder()
                .setNameFormat("player-pool-%d").build();
        playerTheadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), playerFactory, new ThreadPoolExecutor.AbortPolicy());
    }


    static void playTTS(List<Contribute> contributes, FartSettings settings) {
        List<String> texts = contributes.stream().flatMap(c -> c.getText().stream()).collect(Collectors.toList());
        String text = texts.get(new Random().nextInt() % texts.size());
        File cacheFile = Paths.get(TEMP_TTS_CACHE_DIR, settings.getTtsSettings().getVcn() + (settings.getTtsSettings().getVcn() + text).hashCode() + ".mp3").toFile();
        // If not exist, try online tts
        if(!cacheFile.exists()){
            try {
                VoicePackageMakerApp.syncTts2File(text,cacheFile.getPath(),settings.getTtsSettings().getVcn());
            } catch (Exception e) {
            }
        }
        if(cacheFile.exists()) {
            try {
                playStream(new FileInputStream(cacheFile.toString()));
            } catch (Exception e) {
            }
        }
    }

    public static void play(List<Contribute> contributes) {
        FartSettings settings = FartSettings.getInstance();
        if (!settings.isEnable()) {
            return;
        }

        if (settings.getType() == VoicePackageType.TTS) {
            playTTS(contributes, settings);
            return;
        }

        List<String> voices = contributes.stream().flatMap(c -> c.getVoices().stream()).collect(Collectors.toList());
        // play in single thread
        playerTheadPool.submit(() -> {
            String file = voices.get(new Random().nextInt() % voices.size());
            try {
                InputStream inputStream = null;
                if (settings.getType() == VoicePackageType.Builtin) {
                    inputStream = Context.class.getResourceAsStream("/" + Context.BUILD_IN_VOICE_PACKAGE + "/" + file);
                } else {
                    File mp3File = Paths.get(settings.getCustomVoicePackage(), file).toFile();
                    if (mp3File.exists()) {
                        try {
                            inputStream = new FileInputStream(mp3File);
                        } catch (FileNotFoundException e) {
                        }
                    } else {
                        return;
                    }
                }
                playStream(inputStream);
                inputStream.close();
            } catch (JavaLayerException e) {
            } catch (IOException e) {
            }
        });
    }

    private static void playStream(InputStream inputStream) throws JavaLayerException {
        if (inputStream != null) {
            Player player = new Player(inputStream);
            player.play();
            player.close();
        }
    }


}
