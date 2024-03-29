package com.convallyria.wanderingmarket.gui.sell;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.config.Configuration;
import com.convallyria.wanderingmarket.gui.BaseGUI;
import com.convallyria.wanderingmarket.market.item.MarketItem;
import com.convallyria.wanderingmarket.translation.Translations;
import com.convallyria.wanderingmarket.util.ItemStackBuilder;
import com.convallyria.wanderingmarket.util.SignMenuFactory;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SellGUI extends BaseGUI {

    private final ChestGui gui;

    public SellGUI(final WanderingMarket plugin, final ItemStack sell, final WanderingTrader wanderingTrader) {
        this.gui = new ChestGui(3, "Sell An Item!");
        final OutlinePane outlinePane = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOW);
        final Mask mask = new Mask(
                "111111111",
                "111011011",
                "111111111"
        );
        outlinePane.applyMask(mask);
        outlinePane.setRepeat(true);
        outlinePane.setOnClick(click -> click.setCancelled(true));
        outlinePane.addItem(new GuiItem(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)));
        gui.addPane(outlinePane);

        final StaticPane infoPane = new StaticPane(3, 1, 1, 1);
        infoPane.addItem(new GuiItem(new ItemStackBuilder(Material.OAK_SIGN)
                .withName("&#FE8661Select return item")
                .withLore("&#4169E1Click to select what item you want in return!")
                .build(), event -> {
            final Player player = (Player) event.getWhoClicked();
            player.sendMessage(Translations.ENTER_AMOUNT.color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));

            // Initialise and open our sign menu with an example
            SignMenuFactory.Menu menu = plugin.getSignMenuFactory().newMenu(new ArrayList<>(List.of("APPLEx10", "", "", "")));
            menu.open(player);
            menu.reopenIfFail(true);
            //TODO: this is a mess
            menu.response(((clicker, strings) -> {
                // Multi-line support
                String[] text = String.join("", strings).split("x");
                infoPane.removeItem(0, 0);
                final Optional<Material> material = Enums.getIfPresent(Material.class, text[0].trim().toUpperCase(Locale.ROOT));
                if (material.isPresent() && material.get().isItem() && !Configuration.BLACKLISTED_ITEMS.getMaterialList().contains(material.get())) {
                    try {
                        // Check the amount they're asking for. Obviously it can't be less than 1 or bigger than the item max stack size.
                        int count = Integer.parseInt(text[1].trim());
                        if (count > material.get().getMaxStackSize() || count < 1) throw new NumberFormatException();

                        // Create the items
                        final ItemStack itemStack = new ItemStack(material.get(), count);
                        final ItemStack buyItem = new ItemStack(itemStack.getType(), itemStack.getAmount());
                        final ItemStack sellItem = sell.clone();

                        // Nice try
                        if (itemStack.getType() == sell.getType() && itemStack.getAmount() < sell.getAmount()) {
                            if (Configuration.BROADCAST_DUPE_ATTEMPT.getBoolean()) {
                                Bukkit.broadcast(Translations.DUPE_ATTEMPT.args(Component.text(player.getName()).color(NamedTextColor.RED)));
                            }
                            return false;
                        }

                        MarketItem marketItem = new MarketItem(sellItem, buyItem, player);
                        // Do a check to see if a trade of this exact type already exists - todo: is this needed?
                        for (MarketItem item : plugin.getGlobalMarket().getActiveMarketItems()) {
                            if (sellItem.equals(item.sell()) && buyItem.equals(item.buy())) {
                                player.sendMessage(Translations.TRADE_ALREADY_EXISTS.color(NamedTextColor.RED));
                                return true;
                            }
                        }

                        //todo trade complete gui?

                        // Add it to the market
                        plugin.getGlobalMarket().addMarketItem(marketItem);
                        Bukkit.broadcast(Translations.TRADE_ADDED.args(Component.text(player.getName())).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));

                        // Play a sound to everyone to let them know!
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                        }

                        // Add it to the player's merchant, so they can see it has been properly added
                        final List<MerchantRecipe> recipes = new ArrayList<>(wanderingTrader.getRecipes());
                        MerchantRecipe merchantRecipe = new MerchantRecipe(sellItem, 1);
                        merchantRecipe.addIngredient(buyItem);
                        recipes.add(merchantRecipe);
                        wanderingTrader.setRecipes(recipes);

                        // Delay by a tick, otherwise we get errors...
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            clicker.closeInventory();
                            clicker.getInventory().remove(sell);
                        });
                    } catch (NumberFormatException e) {
                        player.sendMessage("Not a valid amount: " + text[1] + ".");
                        return true;
                    }
                } else {
                    player.sendMessage("Not a valid item type: " + text[0] + ".");
                    return true;
                }

                return true;
            }));
            //todo make user set item via chat
            event.setCancelled(true);
        }), 0, 0);
        gui.addPane(infoPane);

        final StaticPane sellPane = new StaticPane(6, 1, 1, 1);
        sellPane.addItem(new GuiItem(new ItemStackBuilder(sell)
                .build(), event -> event.setCancelled(true)), 0, 0);
        gui.addPane(sellPane);

        gui.setOnGlobalClick(clickEvent -> clickEvent.setCancelled(true));
        gui.setOnGlobalDrag(dragEvent -> dragEvent.setCancelled(true));
    }

    @Override
    public void open(Player player) {
        gui.show(player);
    }
}
