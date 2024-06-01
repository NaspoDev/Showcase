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
    // Player UUID as string : Showcase contents (ItemStack[])
    public static HashMap<String, ItemStack[]> invs = new HashMap<>();

    Showcase plugin;
    public Data(Showcase plugin) {
        this.plugin = plugin;

        mkdirs();
    }

    //Creates the PlayerData folder.
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
        for (Map.Entry<String, ItemStack[]> entry : invs.entrySet()) {

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
    public void restoreInvs() {
        if (dir.length() == 0) {
            return;
        }
        for (File file : dirListings) {
            playerConfig = YamlConfiguration.loadConfiguration(file);
            List<ItemStack> content = new ArrayList<>();
            playerConfig.getList("data").stream().forEach(item -> content.add((ItemStack) item));
            invs.put(Utils.removeExtension(file.getName()), content.toArray(new ItemStack[0]));
        }
    }


}
