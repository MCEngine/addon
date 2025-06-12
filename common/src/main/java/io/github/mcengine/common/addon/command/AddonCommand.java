package io.github.mcengine.common.addon.command;

import io.github.mcengine.api.mcengine.MCEngineApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

public class AddonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Get all available project base names (e.g., MCEngineCurrency)
        Set<String> availableProjects = AddonCommandUtil.getAvailableProjects();

        // If no arguments or just "list", show all loaded addons grouped by project
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            boolean found = false;
            for (String project : availableProjects) {
                List<String> addons = MCEngineApi.getLoadedExtensionFileNames(project);
                if (addons != null && !addons.isEmpty()) {
                    if (!found) {
                        sender.sendMessage("§6Loaded AddOns:");
                        found = true;
                    }
                    sender.sendMessage("§e- " + project);
                    for (String addon : addons) {
                        sender.sendMessage("§7  -- " + addon);
                    }
                }
            }
            if (!found) {
                sender.sendMessage("§cNo MCEngine addons loaded.");
            }
            return true;
        }

        // If two arguments and second is "list", list addons for specific project
        if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
            String project = args[0];
            if (!AddonCommandUtil.isValidProject(project)) {
                sender.sendMessage("§cProject '" + project + "' not found or not loaded.");
                return true;
            }

            List<String> addons = MCEngineApi.getLoadedExtensionFileNames(project);
            if (addons == null || addons.isEmpty()) {
                sender.sendMessage("§cNo addons found for project: " + project);
                return true;
            }

            sender.sendMessage("§6AddOns for §e" + project + "§6:");
            for (String addon : addons) {
                sender.sendMessage("§7- " + addon);
            }
            return true;
        }

        // Invalid usage
        sender.sendMessage("§cUsage:");
        sender.sendMessage("§e/addon list");
        sender.sendMessage("§e/addon <project> list");
        return true;
    }
}
