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
        this.dir = new File(plugin.getDataFolder(), "PlayerData");
        this.playerShowcases = new HashMap<>();

        mkdirs();
    }

    // Creates the PlayerData folder.
    private void mkdirs() {
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
        for (Map.Entry<UUID, PlayerShowcase> entry : playerShowcases.entrySet()) {
            playerFile = new File(dir, entry.getKey().toString() + ".yml");
            playerConfig = YamlConfiguration.loadConfiguration(playerFile);

            // Backwards compatibility for before v1.7.0.
            // Checks for config list "data" and deletes it.
            if (playerConfig.getList("data") != null) {
                playerConfig.set("data", null);
            }

            // Collect the showcase data to write to the player's data file.
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (ShowcaseItem showcaseItem : entry.getValue().getShowcaseItems()) {
                Map<String, Object> dataValue = new HashMap<>();
                dataValue.put("itemStack", showcaseItem.getItem());
                dataValue.put("cooldownEndsEpoch", showcaseItem.getCooldownEndsEpoch());
                dataList.add(dataValue);
            }

            // Write the data to the player's yml data file.
            playerConfig.set("items", dataList);

            // Save the file.
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save player data for player uuid " + entry.getKey().toString());
                e.printStackTrace();
            }
        }
    }

    // Restores file data to hashmap.
    public void restoreData() {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            playerConfig = YamlConfiguration.loadConfiguration(file);
            PlayerShowcase playerShowcase = new PlayerShowcase(plugin);

            // Backwards compatibility for before v1.7.0.
            // Checks for config list "data" and restores from there.
            if (playerConfig.getList("data") != null) {
                playerConfig.getList("data").forEach(itemStack -> {
                    if (itemStack != null) {
                        playerShowcase.addShowcaseItem((ItemStack) itemStack, 0);
                    }
                });
            } else {
                // Otherwise there is no old "data" list of items, so restore using the post 1.7.0 way.
                playerConfig.getMapList("items").forEach(entry -> {
                    ItemStack itemStack = (ItemStack) entry.get("itemStack");
                    long cooldownEndsEpoch = (long) entry.get("cooldownEndsEpoch");
                    playerShowcase.addShowcaseItem(itemStack, cooldownEndsEpoch);
                });
            }

            UUID playerUUID = UUID.fromString(Utils.removeExtension(file.getName()));
            playerShowcases.put(playerUUID, playerShowcase);
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

    public List<PlayerShowcase> getPlayerShowcases() {
        return new ArrayList<>(playerShowcases.values());
    }
}
