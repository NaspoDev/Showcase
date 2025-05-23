package dev.naspo.showcase.listeners;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PlayerInteractListener implements Listener {

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
                - Check if its a showcase sign (text is "'s Showcase")
                - Get the player's name from the sign and validate it.
                - Then open the showcase for the player who clicked.
                - If any errors, display.
                 */
            }
        }
    }

    private boolean isShowcaseSign(Sign sign) {
        final String SHOWCASE_SIGN_ENDING = "'sShowcase";
        String[] lines = sign.getSide(Side.FRONT).getLines();
        // Concatenate all lines into one string (no delimiter).
        String concatenatedLines = String.join("", lines);

        // If the sign ends with "'sShowcase", try getting the player's name.
        if (concatenatedLines.endsWith(SHOWCASE_SIGN_ENDING)) {
            // The player name should be everything before "'sShowcase".
            String playerName = concatenatedLines.substring(0, concatenatedLines.indexOf(SHOWCASE_SIGN_ENDING));

            // TODO: try getting the player, be it offline or online.
        }
    }
}

