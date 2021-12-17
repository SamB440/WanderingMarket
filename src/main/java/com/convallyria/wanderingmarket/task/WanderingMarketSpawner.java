package com.convallyria.wanderingmarket.task;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.config.Configuration;
import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.convallyria.wanderingmarket.translation.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public record WanderingMarketSpawner(WanderingMarket plugin) implements Runnable {

    public static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getPlugin(WanderingMarket.class), "wanderingmarket");

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() < Configuration.MINIMUM_PLAYERS.getInt()) return;
        if (plugin.getGlobalMarket().getActiveMarketItems().isEmpty()) return;

        // Get a random online player
        final Player player = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
        if (player == null) return;

        final Component component = Component.text().append(Component.text("The Wandering Market has arrived near").color(NamedTextColor.GRAY))
                .append(Component.space()).append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                .append(Component.text(".").color(NamedTextColor.GRAY))
                .decorate(TextDecoration.ITALIC)
                .hoverEvent(HoverEvent.showText(
                        Component.text("The Wandering Market will randomly appear to an online player, where they can trade on the global market!")
                                .color(NamedTextColor.WHITE)))
                .build();
        plugin.adventure().all().sendMessage(component);
        plugin.adventure().player(player).sendMessage(Translations.INSTRUCTION.color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));

        final Location location = player.getLocation().clone().add(Math.random() * 10, 0, Math.random() * 10);
        WanderingTrader wanderingTrader = (WanderingTrader) location.getWorld().spawnEntity(location, EntityType.WANDERING_TRADER);
        wanderingTrader.setCustomName("Wandering Market");
        wanderingTrader.setCustomNameVisible(true);
        wanderingTrader.setBreed(false);
        wanderingTrader.setTarget(player);
        wanderingTrader.setRecipes(new ArrayList<>());
        wanderingTrader.setDespawnDelay(Configuration.DELAY.getInt());
        wanderingTrader.setAware(false);
        wanderingTrader.setInvulnerable(true);
        location.getWorld().playSound(location, Sound.EVENT_RAID_HORN, 3f, 1f);

        final List<MerchantRecipe> recipes = new ArrayList<>();
        for (MarketItem marketItem : plugin.getGlobalMarket().getActiveMarketItems()) {
            MerchantRecipe merchantRecipe = new MerchantRecipe(marketItem.sell(), 1);
            merchantRecipe.addIngredient(marketItem.buy());
            recipes.add(merchantRecipe);
        }
        wanderingTrader.setRecipes(recipes);
        wanderingTrader.getPersistentDataContainer().set(KEY, PersistentDataType.INTEGER, recipes.size());
    }
}
