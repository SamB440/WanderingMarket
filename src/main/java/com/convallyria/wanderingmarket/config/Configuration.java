package com.convallyria.wanderingmarket.config;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.config.representation.ConfigSound;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public enum Configuration {
    DELAY("delay"),
    BLACKLISTED_ITEMS("blacklisted_items");

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

    public static int getInt(String key) {
        return JavaPlugin.getPlugin(WanderingMarket.class).getConfig().getInt(key);
    }

    public int getInt() {
        return getInt(key);
    }

    public static double getDouble(String key) {
        return JavaPlugin.getPlugin(WanderingMarket.class).getConfig().getDouble(key);
    }

    public static List<Material> getMaterialList(String key) {
        final WanderingMarket plugin = JavaPlugin.getPlugin(WanderingMarket.class);
        final List<String> stringList = plugin.getConfig().getStringList(key);
        final List<Material> materialList = new ArrayList<>();
        for (String materialName : stringList) {
            final Optional<Material> material = Enums.getIfPresent(Material.class, materialName);
            if (material.isPresent()) materialList.add(material.get());
            else plugin.getLogger().warning("Could not load material type '" + materialName + "'.");
        }
        return materialList;
    }

    public List<Material> getMaterialList() {
        return getMaterialList(key);
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
