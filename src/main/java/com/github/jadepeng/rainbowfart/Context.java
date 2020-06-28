package com.github.jadepeng.rainbowfart;

import com.github.jadepeng.rainbowfart.bean.Manifest;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.github.jadepeng.rainbowfart.settings.FartSettings;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * app context
 *
 * @author jqpeng
 */
public class Context {

    private static Manifest manifest;

    /**
     * keyword->voices
     */
    private static Map<String, List<String>> keyword2Voices;

    final static String eachHour = "$time_each_hour";

    static HashMap<String, String> schedule = new HashMap<String, String>() {{
        put("$time_morning", "0930");
        put("$time_before_noon", "1130");
        put("$time_noon", "1200");
        put("$time_after_noon", "1400");
        put("$time_evening", "2100");
        put("$time_midnight", "2330");
    }};


    /**
     * play in a single thread pool
     */
    static ExecutorService playerTheadPool;

    static ScheduledExecutorService scheduledExecutorService;

    static {
        ThreadFactory playerFactory = new ThreadFactoryBuilder()
                .setNameFormat("player-pool-%d").build();
        playerTheadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), playerFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * init
     *
     * @param manifest
     */
    public static void init(Manifest manifest) {
        Context.manifest = manifest;
        keyword2Voices = new HashMap<>(128);

        if (scheduledExecutorService != null) {
            // clear schedule
            scheduledExecutorService.shutdown();
        }
        scheduledExecutorService = new ScheduledThreadPoolExecutor(10);

        Date now = new Date();
        Calendar tomorrow = new GregorianCalendar();
        tomorrow.setTime(now);
        tomorrow.add(Calendar.DATE, 1);
        Calendar today = new GregorianCalendar();
        today.setTime(now);
        manifest.getContributes().stream().parallel().forEach(contribute -> {
            contribute.getKeywords().forEach(keyword -> {
                try {
                    scheduleTimerTask(tomorrow, today, now, keyword);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                keyword2Voices.put(keyword, contribute.getVoices());
            });
        });
    }


    static String readVoicePackageJson(String name) throws IOException {
        FartSettings settings = FartSettings.getInstance();
        boolean isCustomer = StringUtils.isNotEmpty(settings.getCustomVoicePackage());
        if (isCustomer) {
            return FileUtils.readFileToString(Paths.get(settings.getCustomVoicePackage(), name).toFile(), "utf-8");
        }
        URL filePath = ResourcesLoader.class.getClassLoader().getResource("/build-in-voice-chinese/" + name);
        return IOUtils.toString(filePath.openStream(), "utf-8");
    }

    /**
     * 加载配置
     */
    public static void loadConfig() {
        try {
            //
            FartSettings settings = FartSettings.getInstance();
            if (!settings.isEnable()) {
                return;
            }
            String json = readVoicePackageJson("manifest.json");
            Gson gson = new Gson();
            Manifest manifest = gson.fromJson(json, Manifest.class);
            // load contributes.json
            if (manifest.getContributes() == null) {
                String contributesText = readVoicePackageJson("contributes.json");
                Manifest contributes = gson.fromJson(contributesText, Manifest.class);
                if (contributes.getContributes() != null) {
                    manifest.setContributes(contributes.getContributes());
                }
            }
            Context.init(manifest);

        } catch (IOException e) {
        }
    }

    static void scheduleTimerTask(Calendar tomorrow, Calendar today, Date now, String type) {
        if (schedule.containsKey(type)) {
            String timeString = schedule.get(type);
            int hour = Integer.parseInt(timeString.substring(0, 2));
            int minutes = Integer.parseInt(timeString.substring(2, 4));
            tomorrow.set(Calendar.HOUR, hour);
            tomorrow.set(Calendar.MINUTE, minutes);
            today.set(Calendar.HOUR, hour);
            today.set(Calendar.MINUTE, minutes);
            long delay = now.getTime() > today.getTime().getTime() ? (tomorrow.getTime().getTime() - now.getTime()) : (today.getTime().getTime() - now.getTime());
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                play(keyword2Voices.get(type));
            }, delay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
        } else if (type.equals(eachHour)) {
            int minutes = new Date().getMinutes();
            // 整点报时
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                int hour = new Date().getHours();
                if (hour >= 10 && hour <= 17) {
                    play(keyword2Voices.get(type));
                }
                //(60 - minutes) * 60 * 1000
            }, 600, 60 * 60 * 1000, TimeUnit.MILLISECONDS);
        }
    }


    /**
     * get Candidate voices
     *
     * @param inputHistory
     * @return
     */
    public static List<String> getCandidate(String inputHistory) {


        final List<String> candidate = new ArrayList<>();

        FartSettings settings = FartSettings.getInstance();
        if (!settings.isEnable()) {
            return candidate;
        }
        if (keyword2Voices != null) {
            keyword2Voices.forEach((keyword, voices) -> {
                if (inputHistory.contains(keyword)) {
                    candidate.addAll(voices);
                }
            });
        }
        if (candidate.isEmpty()) {
            candidate.addAll(findSpecialKeyword(inputHistory));
        }
        return candidate;
    }

    static List<String> findSpecialKeyword(String inputHistory) {
        List<String> candidate = new ArrayList<>();
        if (inputHistory.contains(":")) {
            String finalInputHistory = inputHistory.replace(":", "");
            schedule.forEach((key, time) -> {
                if (finalInputHistory.contains(time)) {
                    if (keyword2Voices.containsKey(key)) {
                        candidate.addAll(keyword2Voices.get(key));
                    }
                }
            });
        }
        return candidate;
    }

    public static void play(List<String> voices) {

        FartSettings settings = FartSettings.getInstance();
        if (!settings.isEnable()) {
            return;
        }
        // play in single thread
        playerTheadPool.submit(() -> {
            String file = voices.get(new Random().nextInt() % voices.size());
            try {
                InputStream inputStream = null;
                if (StringUtils.isEmpty(settings.getCustomVoicePackage())) {
                    inputStream = Context.class.getResourceAsStream("/build-in-voice-chinese/" + file);
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
                if (inputStream != null) {
                    Player player = new Player(inputStream);
                    player.play();
                    player.close();
                }
            } catch (JavaLayerException e) {
            }
        });
    }
}
