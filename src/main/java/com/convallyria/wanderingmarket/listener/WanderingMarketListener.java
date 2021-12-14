package com.convallyria.wanderingmarket.listener;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.gui.sell.SellGUI;
import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.convallyria.wanderingmarket.task.WanderingMarketSpawner;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;

public record WanderingMarketListener(WanderingMarket plugin) implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity rightClicked = event.getRightClicked();
        if (!player.isSneaking()) return;

        if (rightClicked instanceof WanderingTrader wanderingTrader) {
            if (wanderingTrader.getPersistentDataContainer().has(WanderingMarketSpawner.KEY, PersistentDataType.INTEGER)) {
                if (player.getInventory().getItemInMainHand().getType().isAir()) return;
                SellGUI sellGUI = new SellGUI(plugin, player.getInventory().getItemInMainHand(), wanderingTrader);
                Bukkit.getScheduler().runTaskLater(plugin, () -> sellGUI.open(player), 1L);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTrade(PlayerTradeEvent event) {
        final Player player = event.getPlayer();
        if (event.getVillager() instanceof WanderingTrader wanderingTrader) {
            if (wanderingTrader.getPersistentDataContainer().has(WanderingMarketSpawner.KEY, PersistentDataType.INTEGER)) {
                final MerchantRecipe recipe = event.getTrade();
                for (MarketItem marketItem : plugin.getGlobalMarket().getMarketItems()) {
                    final ItemStack sellItem = marketItem.sell();
                    final ItemStack buyItem = marketItem.buy();
                    if (sellItem.equals(recipe.getResult()) && buyItem.equals(recipe.getIngredients().get(0))) {
                        if (!marketItem.seller().isOnline()) {
                            player.sendMessage(Component.text("The seller (" + marketItem.seller().getName() + ") is not online to receive the items.").color(NamedTextColor.RED));
                            event.setCancelled(true);
                            return;
                        }
                        Player seller = marketItem.seller().getPlayer();
                        seller.sendMessage(Component.text("Your items sold on the market! " + sellItem.getType() + "x" + sellItem.getAmount() + " for " + buyItem.getType() + "x" + buyItem.getAmount() + ".").color(NamedTextColor.GREEN));
                        seller.getInventory().addItem(buyItem).forEach((index, drop) -> seller.getWorld().dropItem(seller.getEyeLocation(), drop));
                        plugin.getGlobalMarket().removeMarketItem(marketItem);
                        return;
                    }
                }
                player.sendMessage(Component.text("Unable to find trade match.").color(NamedTextColor.RED));
                event.setCancelled(true);
            }
        }
    }
}
