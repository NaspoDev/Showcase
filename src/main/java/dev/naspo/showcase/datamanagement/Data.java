package dev.naspo.showcase.datamanagement;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class Data {
    private File dir;
    private File[] dirListings;
    private File playerFile;
    private YamlConfiguration playerConfig;

    // Main working HashMap that stores showcase data in runtime.
    public static ArrayList<PlayerShowcase> showcases = new ArrayList<>();

    Showcase plugin;
    public Data(Showcase plugin) {
        this.plugin = plugin;

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
    public void saveInvs() {
        for (PlayerShowcase showcase : showcases) {

            // Define the player file.
            playerFile = new File(dir, showcase.getOwnerUUID() + ".yml");
            // If the player file doesn't already exist, create it.
            if (!(playerFile.exists())) {
                try {
                    playerFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not create playerdata file for player uuid " +
                            showcase.getOwnerUUID());
                    e.printStackTrace();
                    return;
                }
            }

            // Define the YAML config from the player file.
            playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            // Set the showcase item data under a key called "data".
            playerConfig.set("data", showcase.getItems());

            // Save the file.
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save playerdata for player uuid " + showcase.getOwnerUUID());
                e.printStackTrace();
            }
        }
    }

    // Restores file data to hashmap.
    public void restoreInvs() {
        if (dir.length() == 0) {
            return;
        }
        for (File file : dirListings) {
            playerConfig = YamlConfiguration.loadConfiguration(file);
            List<ItemStack> items = new ArrayList<>();
            playerConfig.getList("data").stream().forEach(item -> items.add((ItemStack) item));
            showcases.add(new PlayerShowcase(UUID.fromString(Utils.removeExtension(file.getName())),
                    items.toArray(new ItemStack[0])));
        }
    }

    // Finds and returns a showcase based on the owner uuid.
    public static PlayerShowcase getShowcase(UUID ownerUUID) {
        for (PlayerShowcase showcase : showcases) {
            if (showcase.getOwnerUUID() == ownerUUID) {
                return showcase;
            }
        }
        return null;
    }
}
