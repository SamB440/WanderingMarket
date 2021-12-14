package com.convallyria.wanderingmarket.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtils {

    private ColorUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String color(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String colorful(final String message) {
        return replaceHexColors('&', ChatColor.translateAlternateColorCodes('&', message));
    }

    /*
     * Sourcecode from GitHub @ https://github.com/SirBlobman/Colored-Signs/blob/master/hex/src/main/java/com/SirBlobman/colored/signs/utility/HexColorUtility.java
     */

    public static String replaceHexColors(final char colorChar, final String string) {
        final Pattern pattern = getReplaceAllRgbPattern(colorChar);
        final Matcher matcher = pattern.matcher(string);

        final StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                matcher.appendReplacement(buffer, colorChar + "#$2");
                continue;
            }

            try {
                final String hexCodeString = matcher.group(2);
                final String hexCode = parseHexColor(hexCodeString);
                matcher.appendReplacement(buffer, hexCode);
            } catch (final NumberFormatException ignored) { }
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static Pattern getReplaceAllRgbPattern(final char colorChar) {
        final String colorCharString = Character.toString(colorChar);
        final String colorCharPattern = Pattern.quote(colorCharString);

        final String patternString = ("(" + colorCharPattern + ")?" + colorCharPattern + "#([0-9a-fA-F]{6})");
        return Pattern.compile(patternString);
    }

    private static String parseHexColor(String string) throws NumberFormatException {
        if (string.startsWith("#")) string = string.substring(1);
        if (string.length() != 6) throw new NumberFormatException("Invalid hex length");

        Color.decode("#" + string);
        final StringBuilder assembled = new StringBuilder();

        assembled.append(org.bukkit.ChatColor.COLOR_CHAR);
        assembled.append("x");

        final char[] charArray = string.toCharArray();
        for (final char character : charArray) {
            assembled.append(org.bukkit.ChatColor.COLOR_CHAR);
            assembled.append(character);
        }

        return assembled.toString();
    }

    public static int getARGB(int red, int green, int blue, int alpha) {
        int encoded = 0;
        encoded = encoded | blue;
        encoded = encoded | (green << 8);
        encoded = encoded | (red << 16);
        encoded = encoded | (alpha << 24);
        return encoded;
    }
}
