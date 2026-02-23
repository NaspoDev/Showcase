package dev.naspo.showcase.datamanagement;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.types.PlayerShowcase;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

    // YAML paths.
    private final String ITEM_DATA_YAML_PATH = "data";
    private final String SLOT_COOLDOWN_YAML_PATH = "slot-cooldowns";

    // The Bukkit scheduler task that repeatedly auto saves data to disk on set intervals.
    private BukkitTask autoSaveTask;

    // Main working HashMap that stores showcase data in runtime.
    // Player UUID : PlayerShowcase
    private final HashMap<UUID, PlayerShowcase> playerShowcases;

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
        for (Map.Entry<UUID, PlayerShowcase> entry : playerShowcases.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerShowcase showcase = entry.getValue();

            // Initialize the player file.
            File playerFile = new File(playerDataDirectory, uuidString + ".yml");

            // If it doesn't exist, try and create it.
            if (!(playerFile.exists())) {
                try {
                    playerFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not create playerdata file for player uuid " + uuidString);
                    e.printStackTrace();
                    return;
                }
            }

            // Load the player's YAML file.
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(playerFile);
            // Set item data to showcase contents (ItemStack[]).
            yamlConfig.set(ITEM_DATA_YAML_PATH, showcase.getItems());
            // Set slow cooldown data.
            yamlConfig.set(SLOT_COOLDOWN_YAML_PATH, showcase.getSlotCooldowns());

            // Save.
            try {
                yamlConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save playerdata for player uuid " + uuidString);
                e.printStackTrace();
            }
        }
    }

    // Restores file data to hashmap.
    public void restoreShowcaseData() {
        for (File file : playerDataFiles) {
            // Load the YAML file.
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);

            // Initialize the PlayerShowcase.
            UUID playerUUID = UUID.fromString(Utils.removeFileExtension(file.getName()));
            PlayerShowcase showcase = new PlayerShowcase(playerUUID);

            // Load and process showcase items.
            List<ItemStack> items = new ArrayList<>();
            yamlConfig.getList("data").forEach(item -> items.add((ItemStack) item));
            showcase.setItems(items.toArray(new ItemStack[0]));

            // Load and process slot cooldowns.
            HashMap<Integer, Long> slotCooldowns = new HashMap<>();
            ConfigurationSection configurationSection = yamlConfig.getConfigurationSection(SLOT_COOLDOWN_YAML_PATH);
            // Checking if a slot cooldown section exists before proceeding. This is
            // done for backwards compatibility for before the cooldowns feature was added.
            if (configurationSection != null) {
                Map<String, Object> slotCooldownsRaw = configurationSection.getValues(false);
                // Convert generic YAMLConfiguration Map types to my original types.
                for (Map.Entry<String, Object> entry : slotCooldownsRaw.entrySet()) {
                    Integer slot = Integer.parseInt(entry.getKey());
                    Long unlockTimeEpoch = (Long) entry.getValue();
                    slotCooldowns.put(slot, unlockTimeEpoch);
                }
            }
            showcase.setSlotCooldowns(slotCooldowns);

            // Add the showcase to the playerShowcases hashmap.
            playerShowcases.put(playerUUID, showcase);
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

    // Getter
    public HashMap<UUID, PlayerShowcase> getPlayerShowcases() {
        return playerShowcases;
    }
}
