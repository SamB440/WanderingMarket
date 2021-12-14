package com.convallyria.wanderingmarket.util;

import org.bukkit.Location;

public class LocationUtil {

    public static boolean isSimilar(final Location one, final Location two) {
        return one.getBlockX() == two.getBlockX() && one.getBlockY() == two.getBlockY() && one.getBlockZ() == two.getBlockZ();
    }
}
