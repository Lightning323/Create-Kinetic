package com.lightning323.createkinetic.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MiscUtils {
    public static String generateRandomString(int n) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return ThreadLocalRandom.current().ints(n, 0, chars.length())
                .mapToObj(i -> String.valueOf(chars.charAt(i)))
                .collect(Collectors.joining());
    }

}
