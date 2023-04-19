package com.fnv_tw.utils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class ObjectToLinkedHashMapConverter<T> {
    public LinkedHashMap<String, Object> toLinkedHashMap(T object) throws IllegalAccessException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }
}
