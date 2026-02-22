package dev.naspo.showcase.datamanagement;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

// Handles data management and in-memory data such as showcase item data and cooldown data.
public class DataManager {
    private final Showcase plugin;
    private File playerDataDirectory;
    private File[] playerDataFiles;
    // The Bukkit scheduler task that repeatedly auto saves data to disk on set intervals.
    private BukkitTask autoSaveTask;

    // Main working HashMap that stores showcase data in runtime.
    // Player UUID as string : Showcase contents (ItemStack[])
    private final HashMap<String, ItemStack[]> playerShowcases;

    public DataManager(Showcase plugin) {
        this.plugin = plugin;
        playerShowcases = new HashMap<>();

        createPlayerDataFolder();
    }

    // Creates the PlayerData folder.
    private void createPlayerDataFolder() {
        playerDataDirectory = new File(plugin.getDataFolder(), "PlayerData");

        if (!(playerDataDirectory.exists())) {
            try {
                playerDataDirectory.mkdirs();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create PlayerData folder! The plugin functionally" +
                        "will not work without it!");
                plugin.getLogger().log(Level.SEVERE, "Disabling plugin.");
                plugin.getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        // If it already exists, or once its created, ensure it's a directory and store its files.
        if (playerDataDirectory.isDirectory()) {
            playerDataFiles = playerDataDirectory.listFiles();
        }
    }

    // Saves in-memory showcase data to files.
    public void saveShowcaseData() {
        for (Map.Entry<String, ItemStack[]> entry : playerShowcases.entrySet()) {
            // Initialize the player file.
            File playerFile = new File(playerDataDirectory, entry.getKey() + ".yml");

            // If it doesn't exist, try and create it.
            if (!(playerFile.exists())) {
                try {
                    playerFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not create playerdata file for player uuid " + entry.getKey());
                    e.printStackTrace();
                    return;
                }
            }

            // Load the player's YAML file.
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(playerFile);
            // Set "data" key to showcase contents (ItemStack).
            yamlConfig.set("data", entry.getValue());

            // Save.
            try {
                yamlConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save playerdata for player uuid " + entry.getKey());
                e.printStackTrace();
            }
        }
    }

    // Restores file data to hashmap.
    public void restoreShowcaseData() {
        if (playerDataDirectory.length() == 0) {
            return;
        }
        for (File file : playerDataFiles) {
            // Load the YAML file.
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
            // Add each item to the content list.
            List<ItemStack> content = new ArrayList<>();
            yamlConfig.getList("data").forEach(item -> content.add((ItemStack) item));
            // Add the data to the hashmap.
            playerShowcases.put(Utils.removeFileExtension(file.getName()), content.toArray(new ItemStack[0]));
        }
    }

    // Writes im-memory showcase data every 5 minutes to prevent data loss on server crash.
    public void startAutoSave() {
        // If task is already running, return;
        if (autoSaveTask != null && !autoSaveTask.isCancelled()) {
            return;
        }

        // Start task.
        autoSaveTask = Bukkit.getScheduler().runTaskTimer(plugin, this::saveShowcaseData, 6000L, 6000L);
    }
}
