package com.fnv_tw.utils;

import com.google.gson.Gson;

public class SerializerUtil {
    private static final Gson gson = new Gson();

    public static <T> String serialize(T object) {
        return gson.toJson(object);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
