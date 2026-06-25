package com.example.github.action.demo.api;

import java.util.regex.Pattern;

public final class PhoneNormalizer {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern VALID_PHONE = Pattern.compile("^\\+?[0-9\\-\\.\\s()]{7,20}$");

    private PhoneNormalizer() {}

    public static String normalize(String phone) {
        if (phone == null) return null;
        String trimmed = phone.trim();
        if (trimmed.isEmpty()) return "";
        // Keep leading + if present
        boolean plus = trimmed.startsWith("+");
        StringBuilder sb = new StringBuilder();
        for (char c : trimmed.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
        }
        String digits = sb.toString();
        if (plus) return "+" + digits;
        return digits;
    }

    public static boolean isValidPhoneFormat(String phone) {
        if (phone == null) return false;
        String trimmed = phone.trim();
        if (trimmed.isEmpty()) return false;
        return VALID_PHONE.matcher(trimmed).matches();
    }
}