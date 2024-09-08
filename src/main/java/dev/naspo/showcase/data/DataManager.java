package dev.naspo.showcase.data;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.models.PlayerShowcase;
import dev.naspo.showcase.models.ShowcaseItem;
import dev.naspo.showcase.support.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

// Contains hashmap that is used to work with data, and data related methods.
public class DataManager {
    private File dir;
    private File[] dirListings;
    private File playerFile;
    private YamlConfiguration playerConfig;

    // Main working HashMap that stores showcase data in runtime.
    // Player UUID : PlayerShowcase
    private final HashMap<UUID, PlayerShowcase> playerShowcases;

    private final Showcase plugin;
    public DataManager(Showcase plugin) {
        this.plugin = plugin;
        this.playerShowcases = new HashMap<>();

        mkdirs();
    }

    // Creates the PlayerData folder.
    private void mkdirs() {
        dir = new File(plugin.getDataFolder(), "PlayerData");
        if (!(dir.exists())) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create PlayerData folder! The plugin functionally" +
                        "will not work without it!");
                plugin.getLogger().log(Level.SEVERE, "Disabling plugin.");
                plugin.getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        if (dir.isDirectory()) {
            dirListings = dir.listFiles();
        }
    }

    // Saves hashmap data to files.
    public void saveData() {
        for (Map.Entry<String, ItemStack[]> entry : playerShowcases.entrySet()) {

            playerFile = new File(dir, entry.getKey() + ".yml");
            if (!(playerFile.exists())) {
                try {
                    playerFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not create playerdata file for player uuid " + entry.getKey());
                    e.printStackTrace();
                    return;
                }
            }

            playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            playerConfig.set("data", entry.getValue());
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save playerdata for player uuid " + entry.getKey());
                e.printStackTrace();
            }
        }
    }

    // Restores file data to hashmap.
    public void restoreData() {
        if (dir.length() == 0) {
            return;
        }
        for (File file : dirListings) {
            playerConfig = YamlConfiguration.loadConfiguration(file);
            List<ItemStack> content = new ArrayList<>();
            playerConfig.getList("data").stream().forEach(item -> content.add((ItemStack) item));
            playerShowcases.put(Utils.removeExtension(file.getName()), content.toArray(new ItemStack[0]));
        }
    }

    // --- Methods for working with playerShowcases (data hashmap). ---

    // Returns true if the player has a showcase.
    public boolean playerHasShowcase(UUID playerUuid) {
        return playerShowcases.containsKey(playerUuid);
    }

    // Puts a player into the showcase hashmap.
    public void putPlayerShowcase(UUID playerUUID, PlayerShowcase showcase) {
        playerShowcases.put(playerUUID, showcase);
    }

    // Returns the players showcase data.
    public PlayerShowcase getPlayerShowcase(UUID playerUUID) {
        return playerShowcases.getOrDefault(playerUUID, null);
    }

    // Returns the size of the showcases hashmap.
    public int getAmountOfShowcases() {
        return playerShowcases.size();
    }
}
