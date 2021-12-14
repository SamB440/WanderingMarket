package com.convallyria.wanderingmarket.config.representation;

import org.bukkit.Location;
import org.bukkit.Sound;

public record ConfigSound(Sound sound, float volume, float pitch) {

    public void play(final Location target) {
        if (target.getWorld() == null) return;
        target.getWorld().playSound(target, sound, pitch, volume);
    }
}
