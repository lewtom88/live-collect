package com.wy.model.vo;

import java.util.HashMap;
import java.util.Map;

public enum CommentType {
    LIGHT("点亮了", 1),
    GIFT("送", 2),
    GUESS("kda", 3),
    START_TAG("new game", 4);

    final String keyword;
    final int value;

    CommentType(String keyword, int value) {
        this.keyword = keyword;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static Map<String, Integer> mapping = new HashMap<>();
    static {
        for (CommentType type : CommentType.values()) {
            mapping.put(type.keyword, type.value);
        }
    }

    public static int getValue(String message, String name) {
        int r = mapping.getOrDefault(message, 0);
        if (r == 0 && message.toLowerCase().startsWith(GUESS.keyword)) {
            return GUESS.value;
        }
        if ("王者荣耀无用".equals(name) && message.equals(START_TAG.keyword)) {
            return START_TAG.getValue();
        }
        return r;
    }

}
