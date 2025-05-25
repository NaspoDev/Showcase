package dev.naspo.showcase.listeners;

import dev.naspo.showcase.Showcase;
import dev.naspo.showcase.commandstuff.OpenShowcase;
import dev.naspo.showcase.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Optional;

public class PlayerInteractListener implements Listener {

    // A showcase sign will always end with this text.
    private final String SHOWCASE_SIGN_ENDING = "'sShowcase";
    private Showcase plugin;
    private OpenShowcase openShowcase;

    public PlayerInteractListener(Showcase plugin, OpenShowcase openShowcase) {
        this.plugin = plugin;
        this.openShowcase = openShowcase;
    }

    // Checks and handles when a showcase sign is right-clicked.
    @EventHandler
    private void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock();

                Player onlineTarget = getOnlinePlayerFromShowcaseSign(sign);
                if (onlineTarget != null) {
                    event.setCancelled(true); // Stop the player from editing the sign.
                    openShowcase.openOthersOnlineInv(player, onlineTarget);
                    return;
                }

                OfflinePlayer offlineTarget = getOfflinePlayerFromShowcaseSign(sign);
                if (offlineTarget != null) {
                    event.setCancelled(true); // Stop the player from editing the sign.
                    openShowcase.openOthersOfflineInv(player, offlineTarget);
                    return;
                }

                player.sendMessage(Utils.chatColor(
                        Utils.getPluginPrefix(plugin) + "Could not open showcase for target player."
                ));
            }
        }
    }

    // Will return the Player on the showcase sign if the sign is a showcase sign, and a player can be found.
    private Player getOnlinePlayerFromShowcaseSign(Sign sign) {
        String[] lines = sign.getSide(Side.FRONT).getLines();
        // Concatenate all lines into one string.
        String concatenatedLines = String.join("", lines);

        // If the sign ends with the SHOWCASE_SING_ENDING, we can assume it is a showcase sign.
        if (concatenatedLines.endsWith(SHOWCASE_SIGN_ENDING)) {
            // We can assume that text before the SHOWCASE_SIGN_ENDING is the player's name.
            String playerName = concatenatedLines.substring(0, concatenatedLines.indexOf(SHOWCASE_SIGN_ENDING));

            // Check if the player is online.
            Optional<Player> target = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getName().equals(playerName))
                    .findFirst()
                    .map(p -> (Player) p);

            // If they are, return them.
            if (target.isPresent()) {
                return target.get();
            }
        }
        return null;
    }

    private OfflinePlayer getOfflinePlayerFromShowcaseSign(Sign sign) {
        String[] lines = sign.getSide(Side.FRONT).getLines();
        // Concatenate all lines into one string.
        String concatenatedLines = String.join("", lines);

        // If the sign ends with the SHOWCASE_SING_ENDING, we can assume it is a showcase sign.
        if (concatenatedLines.endsWith(SHOWCASE_SIGN_ENDING)) {
            // We can assume that text before the SHOWCASE_SIGN_ENDING is the player's name.
            String playerName = concatenatedLines.substring(0, concatenatedLines.indexOf(SHOWCASE_SIGN_ENDING));
            return Bukkit.getOfflinePlayer(playerName);
        }
        return null;
    }
}

