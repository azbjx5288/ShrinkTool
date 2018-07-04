package com.shrinktool.base.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GsonHelper {
    private static Gson gson = newGson();

    public static HashMap<String, String> convert2Map(JsonObject object) {
        if (null == object) {
            return new HashMap<>();
        }

        return gson.fromJson(object.toString(), new TypeToken<HashMap<String, String>>() {}.getType());
    }

    public static JsonObject convert2gson(Object o) {
        String json = toJson(o);
        JsonParser jsonParser = new JsonParser();
        return (JsonObject)jsonParser.parse(json);
    }

    /** 将‘实例’转成一般的json字符串 */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /** 将json字符串转换成‘实例’ */
    public static <T> T fromJson(String json, Class<T> clz) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT){
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, typeOfT);
    }

    /** 服务器使用的类型可能与客户端不匹配，需要特别的TypeAdapter进行处理 */
    public static Gson newGson() {
        GsonBuilder builder = new GsonBuilder();
        BooleanSerializer serializer = new BooleanSerializer();
        builder.registerTypeAdapter(Boolean.class, serializer);
        builder.registerTypeAdapter(boolean.class, serializer);
        builder.registerTypeAdapter(JsonString.class, new JsonStringSerializer());
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return builder.create();
    }
}
