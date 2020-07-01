package com.github.jadepeng.rainbowfart;

import com.github.jadepeng.rainbowfart.bean.Contribute;
import com.github.jadepeng.rainbowfart.bean.Manifest;
import com.github.jadepeng.rainbowfart.settings.VoicePackageType;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.github.jadepeng.rainbowfart.settings.FartSettings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * app context 21:00 23:30 23:00 23:30
 * if while if while while test if
 *
 * @author jqpeng
 */
public class Context {

    private static Manifest manifest;

    /**
     * keyword->Contribute
     */
    private static Map<String, Contribute> keyword2Contributes;

    final static String BUILD_IN_VOICE_PACKAGE = "xiaoling";

    static Pattern keywordPattern;

    static HashMap<String, String> schedule = new HashMap<String, String>() {{
        put("$time_morning", "0930");
        put("$time_before_noon", "1130");
        put("$time_noon", "1200");
        put("$time_after_noon", "1400");
        put("$time_evening", "2100");
        put("$time_midnight", "2330");
    }};

    static ExecutorService preparePlayThreadPool;

    static ScheduledExecutorService scheduledExecutorService;

    static {
        preparePlayThreadPool = new ThreadPoolExecutor(2, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),  new ThreadFactoryBuilder()
                .setNameFormat("prepare-play-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * init
     *
     * @param manifest
     */
    public static void init(Manifest manifest) {
        Context.manifest = manifest;
        keyword2Contributes = new HashMap<>(128);

        resetSchedulePool();

        manifest.getContributes().stream().parallel().forEach(contribute -> {
            contribute.getKeywords().forEach(keyword -> {
                if(StringUtils.isBlank(keyword)){
                    return;
                }
                scheduleTimerTask(keyword);
                keyword2Contributes.put(keyword, contribute);
            });
        });

        // build regex
        String regex = String.join("|", keyword2Contributes.keySet().stream().map(s -> s.replaceAll("\\$|\\.|\\+|\\(|\\)|\\[|\\]", "\\$&")).collect(Collectors.toList()));
        keywordPattern = Pattern.compile(regex);
    }

    private static void resetSchedulePool() {
        if (scheduledExecutorService != null) {
            // clear schedule
            scheduledExecutorService.shutdown();
        }
        scheduledExecutorService = new ScheduledThreadPoolExecutor(20);
    }

    public static String getBuiltinTtsText() {
        try {
            URL filePath = Context.class.getClassLoader().getResource("/default.json");
            return IOUtils.toString(filePath.openStream(), "utf-8");
        } catch (IOException e) {
            return "";
        }
    }


    static String readVoicePackageJson(String name) throws IOException {
        FartSettings settings = FartSettings.getInstance();
        boolean isCustomer = StringUtils.isNotEmpty(settings.getCustomVoicePackage());
        if (isCustomer) {
            return FileUtils.readFileToString(Paths.get(settings.getCustomVoicePackage(), name).toFile(), "utf-8");
        }
        URL filePath = PluginStarter.class.getClassLoader().getResource("/" + BUILD_IN_VOICE_PACKAGE + "/" + name);
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
            // TTS 使用配置里的数据
            String json = settings.getType() != VoicePackageType.TTS ? readVoicePackageJson("manifest.json") : settings.getTtsSettings().getResourceText();
            if (StringUtils.isBlank(json)) {
                json = getBuiltinTtsText();
            }
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

    static void scheduleTimerTask(String type) {
        Date now = new Date();
        Calendar tomorrow = new GregorianCalendar();
        tomorrow.setTime(now);
        tomorrow.add(Calendar.DATE, 1);
        Calendar today = new GregorianCalendar();
        today.setTime(now);
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
                play(Arrays.asList(keyword2Contributes.get(type)));
            }, delay, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
        } else if (type.equals("$time_each_hour")) {
            int minutes = new Date().getMinutes();
            // 整点报时
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                int hour = new Date().getHours();
                if (hour >= 10 && hour <= 17) {
                    play(Arrays.asList(keyword2Contributes.get(type)));
                }
            }, (60 - minutes) * 60 * 1000, 60 * 60 * 1000, TimeUnit.MILLISECONDS);
        }
    }


    /**
     * get Candidate voices
     *
     * @param inputHistory
     * @return
     */
    public static List<Contribute> getCandidate(String inputHistory) {

        final List<Contribute> candidate = new ArrayList<>();

        FartSettings settings = FartSettings.getInstance();
        if (!settings.isEnable()) {
            return candidate;
        }
        if (keywordPattern != null) {
            Matcher matcher = keywordPattern.matcher(inputHistory);
            if (matcher.find()) {
                String keyword = matcher.group();
                if (keyword2Contributes.containsKey(keyword)) {
                    candidate.add(keyword2Contributes.get(keyword));
                }
            }
        }
        if (candidate.isEmpty()) {
            candidate.addAll(findMatchContribute(inputHistory));
        }
        return candidate;
    }

    static List<Contribute> findMatchContribute(String inputHistory) {
        List<Contribute> candidate = new ArrayList<>();
        if (inputHistory.contains(":")) {
            String finalInputHistory = inputHistory.replace(":", "");
            schedule.forEach((key, time) -> {
                if (finalInputHistory.contains(time)) {
                    if (keyword2Contributes.containsKey(key)) {
                        candidate.add(keyword2Contributes.get(key));
                    }
                }
            });
        }
        return candidate;
    }

    public static void play(List<Contribute> contributes) {
        // play in thread
        preparePlayThreadPool.submit(()-> Mp3Player.play(contributes));
    }

    public static void main(String[] args) {
        System.out.println("xx+()$".replaceAll("\\$|\\.|\\+|\\(|\\)|\\[|\\]", "\\\\$0"));
    }
}
