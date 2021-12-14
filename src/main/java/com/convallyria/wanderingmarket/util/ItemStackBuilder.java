package com.convallyria.wanderingmarket.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemStackBuilder {

    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(Material mat) {
        this.ITEM_STACK = new ItemStack(mat);
    }

    public ItemStackBuilder(ItemStack item) {
        this.ITEM_STACK = item;
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withName(String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setDisplayName(ColorUtils.colorful(name));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(@Nullable String name) {
        if (name == null) return this;
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(ColorUtils.colorful(name));
        meta.setLore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withDurability(int durability) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        final Damageable damageable = (Damageable) meta;
        damageable.setDamage(durability);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addFlags(final ItemFlag... flags) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(flags);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder withSkullOwner(final OfflinePlayer player) {
        Material type = ITEM_STACK.getType();
        if (type == Material.PLAYER_HEAD) {
            final ItemMeta meta = ITEM_STACK.getItemMeta();
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(player.getName());
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withSkullOwner is only applicable for skulls!");
        }
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder setSkullOwner(final UUID uuid) {
        Material type = ITEM_STACK.getType();
        if (type == Material.PLAYER_HEAD) {
            final ItemMeta meta = ITEM_STACK.getItemMeta();
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(Bukkit.getPlayer(uuid).getName());
            return this;
        } else {
            throw new IllegalArgumentException("withSkullOwner is only applicable for skulls!");
        }
    }

	public ItemStackBuilder withModel(int model) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(model);
        ITEM_STACK.setItemMeta(meta);
		return this;
	}


    public ItemStackBuilder withEnchantment(Enchantment enchantment, final int level) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withType(Material material) {
        ITEM_STACK.setType(material);
        return this;
    }

    public ItemStackBuilder clearLore() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setLore(new ArrayList<String>());
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withColor(Color color) {
        Material type = ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();
            meta.setColor(color);
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return ITEM_STACK;
    }

}
