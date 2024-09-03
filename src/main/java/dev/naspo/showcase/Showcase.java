package dev.naspo.showcase;

import dev.naspo.showcase.commandstuff.Commands;
import dev.naspo.showcase.support.OpenShowcaseService;
import dev.naspo.showcase.commandstuff.TabCompleter;
import dev.naspo.showcase.data.DataManager;
import dev.naspo.showcase.data.Events;
import dev.naspo.showcase.support.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Showcase extends JavaPlugin {
    private DataManager dataManager;
    private OpenShowcaseService openShowcaseService;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Showcase has been enabled!");

        dataManager = new DataManager(this);
        openShowcaseService = new OpenShowcaseService(this);

        // initialize utility class
        Utils.initialize(this);
        dependencyCheck();
        softDependencyCheck();
        registerEvents();
        registerCommands();

        // Call to restore showcase data
        dataManager.restoreInvs();
        // Start scheduled data saves.
        repeatSaveInvs();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Showcase has been disabled!");
        if (!(dataManager.invs.isEmpty())) {
            dataManager.saveInvs();
        }
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
        this.getServer().getPluginManager().registerEvents(new Events(), this);
    }

    private void registerCommands() {
        this.getCommand("showcase").setExecutor(new Commands(this, dataManager, openShowcaseService));
        this.getCommand("showcase").setTabCompleter(new TabCompleter());
    }

    //Saves invs from hashmap to file every 5 minutes to prevent data loss on server crash.
    private void repeatSaveInvs() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                dataManager.saveInvs();
            }
        }, 6000L, 6000L);
    }
}
