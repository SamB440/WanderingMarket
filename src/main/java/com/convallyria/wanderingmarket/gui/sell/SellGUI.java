package com.convallyria.wanderingmarket.gui.sell;

import com.convallyria.wanderingmarket.WanderingMarket;
import com.convallyria.wanderingmarket.gui.BaseGUI;
import com.convallyria.wanderingmarket.market.item.MarketItem;
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
            player.sendMessage("Enter item and amount, e.g: applex10");
            SignMenuFactory.Menu menu = plugin.getSignMenuFactory().newMenu(new ArrayList<>(List.of("applex10")));
            menu.open(player);
            menu.reopenIfFail(true);
            menu.response(((clicker, strings) -> {
                String[] text = strings[0].split("x");
                infoPane.removeItem(0, 0);
                final Optional<Material> material = Enums.getIfPresent(Material.class, text[0].trim().toUpperCase(Locale.ROOT));
                if (material.isPresent() && material.get().isItem() && !material.get().isAir()) {
                    try {
                        int count = Integer.parseInt(text[1].trim());
                        if (count > 64 || count < 1) throw new NumberFormatException();
                        final ItemStack itemStack = new ItemStack(material.get(), count);
                        final ItemStack buyItem = new ItemStack(itemStack.getType(), itemStack.getAmount());
                        final ItemStack sellItem = sell.clone();
                        if (itemStack.getType() == sell.getType() && itemStack.getAmount() < sell.getAmount()) {
                            player.sendMessage(Component.text("No duping for you!").color(NamedTextColor.RED));
                            Bukkit.getServer().broadcast(Component.text(player.getName() + " tried to dupe using wandering market! SHAME!").color(NamedTextColor.RED));
                            return false;
                        }

                        MarketItem marketItem = new MarketItem(sellItem, buyItem, player);
                        for (MarketItem item : plugin.getGlobalMarket().getMarketItems()) {
                            if (sellItem.equals(item.sell()) && buyItem.equals(item.buy())) {
                                player.sendMessage(Component.text("The market already has the exact same items!").color(NamedTextColor.RED));
                                return true;
                            }
                        }

                        //todo trade complete gui?

                        plugin.getGlobalMarket().addMarketItem(marketItem);
                        Bukkit.broadcast(Component.text(player.getName() + " has added a trade to the wandering market...").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                        }
                        final List<MerchantRecipe> recipes = new ArrayList<>(wanderingTrader.getRecipes());
                        MerchantRecipe merchantRecipe = new MerchantRecipe(sellItem, 1);
                        merchantRecipe.addIngredient(buyItem);
                        recipes.add(merchantRecipe);
                        wanderingTrader.setRecipes(recipes);

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
