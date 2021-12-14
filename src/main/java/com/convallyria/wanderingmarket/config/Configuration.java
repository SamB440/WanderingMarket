package com.convallyria.wanderingmarket.config;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.config.representation.ConfigSound;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public enum Configuration {
    //todo
    ;

    private final String key;

    Configuration(String key) {
        this.key = key;
    }

    public static Object get(String key) {
        return JavaPlugin.getPlugin(WanderingMarket.class).getConfig().get(key);
    }

    public Object get() {
        return get(key);
    }

    public static double getDouble(String key) {
        return JavaPlugin.getPlugin(WanderingMarket.class).getConfig().getDouble(key);
    }

    public static ConfigurationSection getSection(String key) {
        return JavaPlugin.getPlugin(WanderingMarket.class).getConfig().getConfigurationSection(key);
    }

    public static ConfigSound getConfigSound(String key) {
        ConfigurationSection section = getSection(key);
        final Sound sound = Sound.valueOf(section.getString("sound"));
        final float volume = (float) section.getDouble("volume");
        final float pitch = (float) section.getDouble("pitch");
        return new ConfigSound(sound, volume, pitch);
    }

    public ConfigSound getConfigSound() {
        return getConfigSound(key);
    }
}
