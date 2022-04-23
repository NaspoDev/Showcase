package me.naspo.showcase;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Showcase extends JavaPlugin {
    private Utils utils;
    private Data data;
    private Commands commands;
    private TabCompleter tabCompleter;
    private Events events;

    @Override
    public void onEnable() {
        this.getLogger().info("Showcase has been enabled!");
        this.saveDefaultConfig();

        instantiateClasses();

        data.restoreInvs();

        this.getCommand("showcase").setExecutor(commands);
        this.getCommand("showcase").setTabCompleter(tabCompleter);
        this.getServer().getPluginManager().registerEvents(events, this);

        repeatSaveInvs();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Showcase has been disabled!");
        if (!(Data.invs.isEmpty())) {
            data.saveInvs();
        }
    }

    private void instantiateClasses() {
        utils = new Utils(this);
        data = new Data(this);
        commands = new Commands(this, data);
        tabCompleter = new TabCompleter();
        events = new Events();
    }

    //save invs from hashmap to file every 5 minutes to prevent data loss on server crash
    public void repeatSaveInvs() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                data.saveInvs();
            }
        }, 6000L, 6000L);
    }
}
