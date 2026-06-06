package com.commander.eventeditor.util;

import android.content.Context;
import android.net.Uri;

import com.commander.eventeditor.model.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON辅助类 - 读写和处理JSON数据
 */
public class JsonHelper {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Gson compactGson = new Gson();

    /**
     * 从Uri读取JSON文件并解析为Event列表
     */
    public static List<Event> loadEventsFromUri(Context context, Uri uri) throws Exception {
        InputStream is = context.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        String json = sb.toString();
        return parseEventsFromJson(json);
    }

    /**
     * 从JSON字符串解析事件列表
     */
    public static List<Event> parseEventsFromJson(String json) {
        // Try list first
        Type listType = new TypeToken<List<Event>>() {}.getType();
        try {
            List<Event> parsed = gson.fromJson(json, listType);
            if (parsed != null && !parsed.isEmpty()) {
                return parsed;
            }
        } catch (Exception ignored) {}

        // Try single object
        try {
            Event single = gson.fromJson(json, Event.class);
            if (single != null) {
                List<Event> result = new ArrayList<>();
                result.add(single);
                return result;
            }
        } catch (Exception ignored) {}

        return new ArrayList<>();
    }

    /**
     * 格式化JSON字符串
     */
    public static String formatJson(String json) {
        try {
            Object obj = compactGson.fromJson(json, Object.class);
            return gson.toJson(obj);
        } catch (Exception e) {
            return json;
        }
    }

    /**
     * 压缩JSON字符串
     */
    public static String minifyJson(String json) {
        try {
            Object obj = gson.fromJson(json, Object.class);
            return compactGson.toJson(obj);
        } catch (Exception e) {
            return json;
        }
    }

    /**
     * 将Event对象转为JSON字符串（格式化）
     */
    public static String toPrettyJson(Event event) {
        return gson.toJson(event);
    }

    /**
     * 将Event列表转为JSON字符串（格式化）
     */
    public static String toPrettyJson(List<Event> events) {
        return gson.toJson(events);
    }

    /**
     * 保存JSON到缓存文件
     */
    public static String saveToCache(Context context, String json, String fileName) {
        try {
            File cacheDir = context.getCacheDir();
            File file = new File(cacheDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
}
