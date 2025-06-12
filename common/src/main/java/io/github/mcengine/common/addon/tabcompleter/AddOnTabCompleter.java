package io.github.mcengine.common.addon.tabcompleter;

import io.github.mcengine.common.addon.command.AddonCommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddOnTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Set<String> projects = AddonCommandUtil.getAvailableProjects();

        if (args.length == 1) {
            List<String> base = new ArrayList<>(projects);
            base.add("list");
            return base.stream()
                       .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                       .sorted()
                       .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (projects.contains(args[0])) {
                return Collections.singletonList("list").stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}
