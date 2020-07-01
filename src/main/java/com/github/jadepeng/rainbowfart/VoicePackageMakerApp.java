package com.github.jadepeng.rainbowfart;

import com.github.jadepeng.rainbowfart.bean.Contribute;
import com.github.jadepeng.rainbowfart.bean.Manifest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.*;
import okio.ByteString;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 声音包制作工具
 */
public class VoicePackageMakerApp {
    private static final String hostUrl = "https://tts-api.xfyun.cn/v2/tts";
    // not and stram  stream()

    // 到控制台-语音合成页面获取
    private static final String APPID = "51b60849";

    // 到控制台-语音合成页面获取
    private static final String API_SECRET = "708c15ae7967bdaa26e1023a138cb05c";

    //到控制台-语音合成页面获取
    private static final String API_KEY = "d73ef7b81eb0228f1e7bb2b586d9f935";

    // 默认发音人
    private static final String DEFAULT_VCN = "x_xiaoling";

    // x_xiaoling x2_xiaofang
    private static final String VoicePackageDir = "voicesPackages";

    private static final String manifestFile = "manifest.json";

    public static final Gson json = new Gson();


    public static void syncTts2File(String text, String mp3Path, String vcn) throws Exception {
        AtomicInteger finishCount = new AtomicInteger();
        tts2mp3(text, mp3Path, vcn, finishCount);
        // 等待完成
        while (finishCount.get() < 1) {
            Thread.sleep(100);
        }
    }


    public static void main(String[] args) throws Exception {
        AtomicInteger finishCount = new AtomicInteger();

        String vcnName = args.length > 0 ? args[0] : DEFAULT_VCN;
        String description = args.length > 1 ? args[1] : "讯飞小玲";
        String author = args.length > 2 ? args[2] : "jadepeng";
        // 文件夹
        File packagePath = Paths.get(VoicePackageDir, vcnName).toFile();
        if (!packagePath.exists()) {
            packagePath.mkdirs();
        }

        String packageName = packagePath.toString();
        String json = new String(FileUtil.read(manifestFile));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Manifest manifest = gson.fromJson(json, Manifest.class);

        int count = 0;
        for (Contribute contribute : manifest.getContributes()) {
            String name = contribute.getName().replace("$", "");
            int index = 1;
            if (contribute.getVoices() == null) {
                contribute.setVoices(new ArrayList<>());
            } else {
                contribute.getVoices().clear();
            }
            for (String text : contribute.getText()) {
                count++;
                String fileName = name + (index++) + ".mp3";
                contribute.getVoices().add(fileName);
                tts2mp3(text, Paths.get(packageName, fileName).toString(), vcnName, finishCount);
            }
        }

        // 更新manifest文件
        String finalJson = gson.toJson(manifest);
        FileUtil.save(packageName, manifestFile, finalJson.getBytes("UTF-8"));

        // 等待完成
        while (finishCount.get() < count) {
            Thread.sleep(1000);
        }

        System.exit(0);
    }

    static void tts2mp3(String text, String mp3Path, String vcn, AtomicInteger finishCount) throws Exception {
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, API_KEY, API_SECRET);
        OkHttpClient client = new OkHttpClient.Builder().build();
        //将url中的 schema http://和https://分别替换为ws:// 和 wss://
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        // 存放音频的文件
        File f = new File(mp3Path);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream os = new FileOutputStream(f);
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //发送数据
                JsonObject frame = new JsonObject();
                JsonObject business = new JsonObject();
                JsonObject common = new JsonObject();
                JsonObject data = new JsonObject();
                // 填充common
                common.addProperty("app_id", APPID);
                //填充business
                business.addProperty("aue", "lame");
                business.addProperty("sfl", 1);
                business.addProperty("tte", "UTF8");//小语种必须使用UNICODE编码
                business.addProperty("vcn", vcn);//到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值，若试用未添加的发音人会报错11200
                business.addProperty("pitch", 50);
                business.addProperty("volume", 50);
                business.addProperty("speed", 50);
                //填充data
                data.addProperty("status", 2);//固定位2
                try {
                    data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("utf8")));
                    //使用小语种须使用下面的代码，此处的unicode指的是 utf16小端的编码方式，即"UTF-16LE"”
                    //data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("UTF-16LE")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //填充frame
                frame.add("common", common);
                frame.add("business", business);
                frame.add("data", data);
                webSocket.send(frame.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                //处理返回数据
                System.out.println("receive=>" + text);
                ResponseData resp = null;
                try {
                    resp = json.fromJson(text, ResponseData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resp != null) {
                    if (resp.getCode() != 0) {
                        System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                        return;
                    }
                    if (resp.getData() != null) {
                        String result = resp.getData().audio;
                        byte[] audio = Base64.getDecoder().decode(result);
                        try {
                            os.write(audio);
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (resp.getData().status == 2) {
                            // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                            System.out.println("session end ");
                            System.out.println("合成的音频文件保存在：" + f.getPath());
                            webSocket.close(1000, "");
                            finishCount.incrementAndGet();
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println("socket closing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println("socket closed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("connection failed");
            }
        });
    }

    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("hmac username=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();
        return httpUrl.toString();
    }

    public static class ResponseData {
        private int code;
        private String message;
        private String sid;
        private Data data;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return this.message;
        }

        public String getSid() {
            return sid;
        }

        public Data getData() {
            return data;
        }
    }

    public static class Data {
        private int status;  //标志音频是否返回结束  status=1，表示后续还有音频返回，status=2表示所有的音频已经返回
        private String audio;  //返回的音频，base64 编码
        private String ced;  // 合成进度
    }
}