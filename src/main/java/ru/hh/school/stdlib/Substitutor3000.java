package ru.hh.school.stdlib;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Substitutor3000 {
    private HashMap<String, String> hashMap;

    private static final Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");

    public Substitutor3000() {
        hashMap = new HashMap<String, String>();
    }

    public synchronized void put(String key, String value) {
        hashMap.put(key, value);
    }

    public synchronized String get(String key) {
        if (!hashMap.containsKey(key))
            return null;

        Matcher matcher = pattern.matcher(hashMap.get(key));
        while (matcher.find()) {
            String tok = "";
            if (hashMap.containsKey(matcher.group(1))) {
                tok = hashMap.get(matcher.group(1)) + " ";
            }
            matcher = pattern.matcher(matcher.replaceFirst(tok));
        }
        String result = matcher.replaceFirst("");
        return result;
    }
}
