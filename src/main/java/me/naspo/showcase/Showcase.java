package me.naspo.showcase;

import me.naspo.showcase.commandstuff.Commands;
import me.naspo.showcase.commandstuff.OpenShowcase;
import me.naspo.showcase.commandstuff.TabCompleter;
import me.naspo.showcase.datamanagement.Data;
import me.naspo.showcase.datamanagement.Events;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Showcase extends JavaPlugin {
    private Utils utils;
    private Data data;
    private OpenShowcase openShowcase;
    private Commands commands;
    private TabCompleter tabCompleter;
    private Events events;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Showcase has been enabled!");

        dependencyCheck();
        softDependencyCheck();
        instantiateClasses();
        registerEvents();
        registerCommands();

        data.restoreInvs();
        repeatSaveInvs();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Showcase has been disabled!");
        if (!(Data.invs.isEmpty())) {
            data.saveInvs();
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

    private void instantiateClasses() {
        utils = new Utils(this);
        data = new Data(this);
        openShowcase = new OpenShowcase(this);
        commands = new Commands(this, data, openShowcase);
        tabCompleter = new TabCompleter();
        events = new Events();
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(events, this);
    }

    private void registerCommands() {
        this.getCommand("showcase").setExecutor(commands);
        this.getCommand("showcase").setTabCompleter(tabCompleter);
    }

    //Saves invs from hashmap to file every 5 minutes to prevent data loss on server crash.
    private void repeatSaveInvs() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                data.saveInvs();
            }
        }, 6000L, 6000L);
    }
}
