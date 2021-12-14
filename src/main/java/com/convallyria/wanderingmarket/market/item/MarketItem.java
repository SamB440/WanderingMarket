package com.convallyria.wanderingmarket.market.item;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class MarketItem {

    private ItemStack sell;
    private ItemStack buy;
    private OfflinePlayer seller;

    public MarketItem(ItemStack sell, ItemStack buy, OfflinePlayer seller) {
        this.sell = sell;
        this.buy = buy;
        this.seller = seller;
    }

    public ItemStack buy() {
        return buy;
    }

    public ItemStack sell() {
        return sell;
    }

    public OfflinePlayer seller() {
        return seller;
    }
}
