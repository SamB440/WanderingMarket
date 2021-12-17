package com.convallyria.wanderingmarket.listener;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.gui.sell.SellGUI;
import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.convallyria.wanderingmarket.task.WanderingMarketSpawner;
import com.convallyria.wanderingmarket.translation.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;

public record WanderingMarketListener(WanderingMarket plugin) implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity rightClicked = event.getRightClicked();


        if (rightClicked instanceof WanderingTrader wanderingTrader) {
            if (wanderingTrader.getPersistentDataContainer().has(WanderingMarketSpawner.KEY, PersistentDataType.INTEGER)) {
                if (!player.isSneaking()) {
                    if (wanderingTrader.getRecipes().isEmpty()) {
                        plugin.adventure().player(player).sendMessage(Translations.NO_TRADES);
                    }
                    return;
                }
                if (player.getInventory().getItemInMainHand().getType().isAir()) return;
                SellGUI sellGUI = new SellGUI(plugin, player.getInventory().getItemInMainHand(), wanderingTrader);
                Bukkit.getScheduler().runTaskLater(plugin, () -> sellGUI.open(player), 1L);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) return;
        final Player player = (Player) event.getWhoClicked();
        if (event.getSlot() != 2) return; // Result slot

        MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
        // So don't use merchantInventory#getMerchant - that returns an inventory... not the entity... bukkit api...
        if (merchantInventory.getHolder() instanceof WanderingTrader wanderingTrader) {
            if (wanderingTrader.getPersistentDataContainer().has(WanderingMarketSpawner.KEY, PersistentDataType.INTEGER)) {
                final MerchantRecipe recipe = merchantInventory.getSelectedRecipe();
                if (recipe == null) return;
                for (MarketItem marketItem : plugin.getGlobalMarket().getActiveMarketItems()) {
                    final ItemStack sellItem = marketItem.sell();
                    final ItemStack buyItem = marketItem.buy();
                    if (sellItem.equals(recipe.getResult()) && buyItem.equals(recipe.getIngredients().get(0))) {
                        if (!marketItem.seller().isOnline()) {
                            marketItem.setSold(true);
                            return;
                        }

                        Player seller = marketItem.seller().getPlayer();
                        plugin.adventure().player(seller).sendMessage(Translations.ITEMS_SOLD.args(Component.text(sellItem.getType().name()), Component.text(sellItem.getAmount()), Component.text(buyItem.getType().name()), Component.text(buyItem.getAmount())).color(NamedTextColor.GREEN));
                        seller.getInventory().addItem(buyItem).forEach((index, drop) -> seller.getWorld().dropItem(seller.getEyeLocation(), drop));
                        plugin.getGlobalMarket().removeMarketItem(marketItem);
                        return;
                    }
                }

                plugin.adventure().player(player).sendMessage(Translations.NO_TRADE_MATCH.color(NamedTextColor.RED));
                event.setCancelled(true);
            }
        }
    }
}
