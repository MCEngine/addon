package io.github.mcengine.api.addon;

import org.bukkit.plugin.Plugin;

/**
 * The {@code MCEngineAddOnApi} class is an API for handling the downloading of addons from Git platforms.
 * It integrates with multiple Git platforms (GitHub, GitLab).
 * For more information, visit: http://github.com/MCEngine/addon-website/api
 */
public class MCEngineAddOnApi {
    private Plugin plugin;

    public MCEngineAddOnApi(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        boolean isGitHubEnabled = plugin.getConfig().getBoolean("github.enable", false);
        boolean isGitLabEnabled = plugin.getConfig().getBoolean("gitlab.enable", false);

        // Check if GitHub is enabled
        if (isGitHubEnabled) {
            MCEngineAddOnApiGitHub gitHubApi = new MCEngineAddOnApiGitHub(plugin);
            // Call functions to interact with GitHub API
            plugin.getLogger().info("GitHub API is enabled.");
        }

        // Check if GitLab is enabled
        if (isGitLabEnabled) {
            MCEngineAddOnApiGitLab gitLabApi = new MCEngineAddOnApiGitLab(plugin);
            // Call functions to interact with GitLab API
            plugin.getLogger().info("GitLab API is enabled.");
        }

        // Handle the case where both APIs are enabled
        if (isGitHubEnabled && isGitLabEnabled) {
            plugin.getLogger().info("Both GitHub and GitLab APIs are enabled.");
        }
    }
}
