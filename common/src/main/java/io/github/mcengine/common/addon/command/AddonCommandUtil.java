package io.github.mcengine.common.addon.command;

import io.github.mcengine.api.mcengine.util.MCEngineApiUtilExtension;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class AddonCommandUtil {

    /**
     * Returns all MCEngine project base names (e.g., MCEngineCurrency)
     * based on plugin names that start with "MCEngine".
     */
    public static Set<String> getAvailableProjects() {
        Set<String> result = new HashSet<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String rawName = plugin.getDescription().getName(); // e.g., MCEngineCurrency-SpigotMC-1.0.0
            String baseName = rawName.split("-")[0];            // → MCEngineCurrency
            if (baseName.startsWith("MCEngine")) {
                result.add(baseName);
            }
        }
        return result;
    }

    /**
     * Returns the internal loadedExtensions map from MCEngineApiUtilExtension.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<String>> getLoadedExtensions() {
        try {
            Field field = MCEngineApiUtilExtension.class.getDeclaredField("loadedExtensions");
            field.setAccessible(true);
            return (Map<String, List<String>>) field.get(null);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Unable to access loadedExtensions.");
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    /**
     * Returns true if the given project key exists in loadedExtensions.
     */
    public static boolean isValidProject(String name) {
        return getLoadedExtensions().containsKey(name);
    }
}
