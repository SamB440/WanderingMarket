package com.convallyria.wanderingmarket;

import com.convallyria.wanderingmarket.config.Configuration;
import com.convallyria.wanderingmarket.gson.ItemStackAdapter;
import com.convallyria.wanderingmarket.gson.OfflinePlayerAdapter;
import com.convallyria.wanderingmarket.listener.JoinListener;
import com.convallyria.wanderingmarket.listener.WanderingMarketListener;
import com.convallyria.wanderingmarket.market.GlobalMarket;
import com.convallyria.wanderingmarket.task.WanderingMarketSpawner;
import com.convallyria.wanderingmarket.util.FileSystemUtils;
import com.convallyria.wanderingmarket.util.SignMenuFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class WanderingMarket extends ExtendedJavaPlugin {

    private BukkitAudiences adventure;
    private GlobalMarket globalMarket;
    private SignMenuFactory signMenuFactory;

    private static final File TRADES_FILE = new File("." + File.separator + "plugins" + File.separator + "WanderingMarket" + File.separator + "trades.json");

    @NonNull
    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public GlobalMarket getGlobalMarket() {
        return globalMarket;
    }

    public SignMenuFactory getSignMenuFactory() {
        return signMenuFactory;
    }

    @Override
    public void enable() {
        this.adventure = BukkitAudiences.create(this);
        if (TRADES_FILE.exists()) {
            try (FileReader reader = new FileReader(TRADES_FILE)) {
                this.globalMarket = getGson().fromJson(reader, GlobalMarket.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.globalMarket = new GlobalMarket();
        }
        this.signMenuFactory = new SignMenuFactory(this);
        this.registerTranslations();
        this.saveDefaultConfig();
        this.registerListeners();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new WanderingMarketSpawner(this), 20L, Configuration.DELAY.getInt());
    }

    @Override
    public void disable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        try (FileWriter writer = new FileWriter(TRADES_FILE)) {
            getGson().toJson(this.globalMarket, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new WanderingMarketListener(this), this);
        pm.registerEvents(new JoinListener(this), this);
    }

    private void registerTranslations() {
        final TranslationRegistry translationRegistry = TranslationRegistry.create(Key.key("wanderingmarket", "translations"));
        translationRegistry.defaultLocale(Locale.US);
        try {
            FileSystemUtils.visitResources(WanderingMarket.class, path -> {
                this.getLogger().info("Loading localisations...");

                try (final Stream<Path> stream = Files.walk(path)) {
                    stream.forEach(file -> {
                        if (!Files.isRegularFile(file)) {
                            return;
                        }

                        final String filename = com.google.common.io.Files.getNameWithoutExtension(file.getFileName().toString());
                        final String localeName = filename
                                .replace("messages_", "")
                                .replace("messages", "")
                                .replace('_', '-');
                        final Locale locale = localeName.isEmpty() ? Locale.US : Locale.forLanguageTag(localeName);

                        translationRegistry.registerAll(locale, ResourceBundle.getBundle("l10n/messages",
                                locale, UTF8ResourceBundleControl.get()), false);
                    });
                } catch (final IOException e) {
                    this.getLogger().log(Level.WARNING, "Encountered an I/O error whilst loading translations", e);
                }
            }, "l10n");
        } catch (final IOException e) {
            this.getLogger().log(Level.WARNING, "Encountered an I/O error whilst loading translations", e);
            return;
        }

        GlobalTranslator.get().addSource(translationRegistry);
    }

    private Gson getGson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeHierarchyAdapter(OfflinePlayer.class, new OfflinePlayerAdapter())
                .create();
    }
}
