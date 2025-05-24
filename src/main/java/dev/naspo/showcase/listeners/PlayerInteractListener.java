package dev.naspo.showcase.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PlayerInteractListener implements Listener {

    // A showcase sign will always end with this text.
    private final String SHOWCASE_SIGN_ENDING = "'sShowcase";

    // Checks and handles when a showcase sign is right-clicked.
    @EventHandler
    private void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() instanceof Sign) {
                event.setCancelled(true); // Stop the player from editing the sign.
                Sign sign = (Sign) event.getClickedBlock();



                /*
                TODO:
                - Get the player's name from the sign and validate it.
                - Then open the showcase for the player who clicked.
                - If any errors, display.
                 */
            }
        }
    }

    // Will return the Player on the showcase sign if the sign is a showcase sign, and a player can be found.
    private Player getPlayerFromShowcaseSign(Sign sign) {
        String[] lines = sign.getSide(Side.FRONT).getLines();
        // Concatenate all lines into one string.
        String concatenatedLines = String.join("", lines);

        // If the sign ends with the SHOWCASE_SING_ENDING, we can assume it is a showcase sign.
        if (concatenatedLines.endsWith(SHOWCASE_SIGN_ENDING)) {
            // We can assume that text before the SHOWCASE_SIGN_ENDING is the player's name.
            String playerName = concatenatedLines.substring(0, concatenatedLines.indexOf(SHOWCASE_SIGN_ENDING));

            // Check if the player is online.
            List<Player> onlinePlayers = Bukkit.getOnlinePlayers();
            
        }
        return null;
    }
}

