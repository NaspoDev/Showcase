package dev.naspo.showcase;

import dev.naspo.showcase.commandstuff.Commands;
import dev.naspo.showcase.services.OpenShowcaseService;
import dev.naspo.showcase.commandstuff.TabCompleter;
import dev.naspo.showcase.datamanagement.DataManager;
import dev.naspo.showcase.listeners.InventoryListener;
import dev.naspo.showcase.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Showcase extends JavaPlugin {
    private DataManager dataManager;
    private OpenShowcaseService openShowcase;
    private Commands commands;
    private TabCompleter tabCompleter;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Showcase has been enabled!");

        dependencyCheck();
        softDependencyCheck();
        instantiateClasses();
        registerEvents();
        registerCommands();

        dataManager.restoreShowcaseData();
        dataManager.startAutoSave();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Showcase has been disabled!");
        dataManager.saveShowcaseData();
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
        // PlaceholderAPI
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.getLogger().log(Level.WARNING, "Could not locate PlaceholderAPI which is a soft dependency" +
                    " of this plugin! Some features/functionality may be limited.");
        }
    }

    private void instantiateClasses() {
        dataManager = new DataManager(this);
        openShowcase = new OpenShowcaseService(this, dataManager);
        commands = new Commands(this, openShowcase);
        tabCompleter = new TabCompleter();
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(dataManager), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(dataManager), this);
    }

    private void registerCommands() {
        this.getCommand("showcase").setExecutor(commands);
        this.getCommand("showcase").setTabCompleter(tabCompleter);
    }
}
