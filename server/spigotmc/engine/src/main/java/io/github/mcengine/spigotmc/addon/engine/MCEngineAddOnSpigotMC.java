package io.github.mcengine.spigotmc.addon.engine;

import io.github.mcengine.api.mcengine.MCEngineApi;
import io.github.mcengine.api.mcengine.Metrics;
import io.github.mcengine.common.addon.command.AddonCommand;
import io.github.mcengine.common.addon.tabcompleter.AddOnTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineAddOnSpigotMC extends JavaPlugin {

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        new Metrics(this, 25751);
        saveDefaultConfig(); // Save config.yml if it doesn't exist

        boolean enabled = getConfig().getBoolean("enable", false);
        if (!enabled) {
            getLogger().warning("Plugin is disabled in config.yml (enable: false). Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("addon").setExecutor(new AddonCommand());
        getCommand("addon").setTabCompleter(new AddOnTabCompleter());

        MCEngineApi.checkUpdate(this, getLogger(), "github", "MCEngine", "addon-engine", getConfig().getString("github.token", "null"));
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {}
}
