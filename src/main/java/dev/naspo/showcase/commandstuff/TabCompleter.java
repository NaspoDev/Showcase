package dev.naspo.showcase.commandstuff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Negative permission check.
        if (!sender.hasPermission("showcase.use")) {
            return null;
        }

        // Defining a list of valid of arguments to tab-complete.
        List<String> arguments = new ArrayList<>();

        // "help" is available to anyone with the base "showcase.use" permission.
        arguments.add("help");

        // If they have the reload permission, add that to the list of arguments.
        if (sender.hasPermission("showcase.reload")) {
            arguments.add("reload");
        }

        // Add each online player's name as an argument.
        Bukkit.getOnlinePlayers().forEach(p -> {
            arguments.add(p.getName());
        });

        // Tab-completing logic.
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String s : arguments) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }
            return result;
        }
        return null;
    }
}
