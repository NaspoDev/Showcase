package dev.naspo.showcase;

import dev.naspo.showcase.commandstuff.Commands;
import dev.naspo.showcase.commandstuff.TabCompleter;
import dev.naspo.showcase.data.DataManager;
import dev.naspo.showcase.data.Events;
import dev.naspo.showcase.models.PlayerShowcase;
import dev.naspo.showcase.models.ShowcaseItem;
import dev.naspo.showcase.support.OpenShowcaseService;
import dev.naspo.showcase.support.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public final class Showcase extends JavaPlugin {
    private DataManager dataManager;
    private OpenShowcaseService openShowcaseService;
    private BukkitTask repeatSaveInvsTask;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Showcase has been enabled!");

        dataManager = new DataManager(this);
        openShowcaseService = new OpenShowcaseService(this, dataManager);

        // initialize utility class
        Utils.initialize(this);
        dependencyCheck();
        softDependencyCheck();
        registerEvents();
        registerCommands();

        // Call to restore showcase data
        dataManager.restoreData();
        // Start scheduled data saves.
        repeatSaveInvs();
    }

    @Override
    public void onDisable() {
        repeatSaveInvsTask.cancel(); // stop the repeat save invs task.

        // If there are showcases...
        if (dataManager.getAmountOfShowcases() > 0) {
            // Remove the cooldown lore from all showcase items in all player showcases.
            // (We don't want to save the cooldown countdown in their lore).
            for (PlayerShowcase playerShowcase : dataManager.getPlayerShowcases()) {
                for (ShowcaseItem showcaseItem : playerShowcase.getShowcaseItems()) {
                    showcaseItem.removeCooldownLore();
                }
            }
            // Save data.
            dataManager.saveData();
        }
        this.getLogger().info("Showcase has been disabled!");
    }

    private void dependencyCheck() {
        //Vault
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().log(Level.SEVERE, "Could not locate Vault which is a dependency of this plugin!");
            this.getLogger().log(Level.SEVERE, "Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void softDependencyCheck() {
        //PlaceholderAPI
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.getLogger().log(Level.WARNING, "Could not locate PlaceholderAPI which is a soft dependency" +
                    " of this plugin! Some features/functionality may be limited.");
        }
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new Events(this, dataManager), this);
    }

    private void registerCommands() {
        this.getCommand("showcase").setExecutor(new Commands(this, dataManager, openShowcaseService));
        this.getCommand("showcase").setTabCompleter(new TabCompleter());
    }

    // Saves showcases to data file (asynchronously) every 5 minutes to prevent data loss on server crash.
    private void repeatSaveInvs() {
        repeatSaveInvsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this, (() -> dataManager.saveData()), 6000L, 6000L);
    }
}
