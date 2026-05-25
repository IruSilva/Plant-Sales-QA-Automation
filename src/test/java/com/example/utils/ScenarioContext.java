package com.example.utils;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private static final Map<String, Object> data = new HashMap<>();

    public static void set(String key, Object value) {
        data.put(key, value);
    }

    public static String getString(String key) {
        Object value = data.get(key);
        return value == null ? null : value.toString();
    }

    public static Integer getInt(String key) {
        Object value = data.get(key);
        return value == null ? null : Integer.parseInt(value.toString());
    }

    public static void clear() {
        data.clear();
    }
}