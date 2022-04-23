package me.naspo.showcase;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("showcase")) {
            if (!(sender.hasPermission("showcase.use"))) {
                return null;
            }
            List<String> arguments = new ArrayList<>();

            arguments.add("help");
            if (sender.hasPermission("showcase.edit")) {
                arguments.add("edit");
            }
            if (sender.hasPermission("showcase.reload")) {
                arguments.add("reload");
            }
            Bukkit.getOnlinePlayers().forEach(p -> {
                arguments.add(p.getName());
            });

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String s : arguments) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                        result.add(s);
                    }
                }
                return result;
            }
        }
        return null;
    }
}
