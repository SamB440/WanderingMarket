package com.convallyria.wanderingmarket.listener;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.convallyria.wanderingmarket.translation.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public record JoinListener(WanderingMarket plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        // Get non-active market items that have been sold
        for (MarketItem marketItem : plugin.getGlobalMarket().getMarketItems()) {
            if (marketItem.seller().getUniqueId().equals(player.getUniqueId()) && marketItem.isSold()) {
                final ItemStack sellItem = marketItem.sell();
                final ItemStack buyItem = marketItem.buy();
                plugin.adventure().player(player).sendMessage(Translations.ITEMS_SOLD.args(Component.text(sellItem.getType().name()), Component.text(sellItem.getAmount()), Component.text(buyItem.getType().name()), Component.text(buyItem.getAmount())).color(NamedTextColor.GREEN));
                player.getInventory().addItem(buyItem).forEach((index, drop) -> player.getWorld().dropItem(player.getEyeLocation(), drop));
                plugin.getGlobalMarket().removeMarketItem(marketItem); // Remove it - we don't need it now!
            }
        }
    }
}
