package io.github.mcengine.api.addon;

import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code MCEngineAddOnApi} class is an API for handling the downloading of addons from Git platforms.
 * It integrates with multiple Git platforms (GitHub, GitLab).
 * For more information, visit: http://github.com/MCEngine/addon-website/api
 */
public class MCEngineAddOnApi {
    private Plugin plugin;
    private List<Addon> addons;

    public MCEngineAddOnApi(Plugin plugin) {
        this.plugin = plugin;
        this.addons = new ArrayList<>();
        initialize();
    }

    /**
     * Initializes the API based on the plugin's configuration and loads addons from the addons.json file.
     */
    public void initialize() {
        loadAddonsFromFile();

        // Retrieve the enable flags from the plugin's configuration
        boolean isGitHubEnabled = plugin.getConfig().getBoolean("github.enable", false);
        boolean isGitLabEnabled = plugin.getConfig().getBoolean("gitlab.enable", false);

        // Check if GitHub is enabled
        if (isGitHubEnabled) {
            MCEngineAddOnApiGitHub gitHubApi = new MCEngineAddOnApiGitHub(plugin);
            plugin.getLogger().info("GitHub API is enabled.");
        }

        // Check if GitLab is enabled
        if (isGitLabEnabled) {
            MCEngineAddOnApiGitLab gitLabApi = new MCEngineAddOnApiGitLab(plugin);
            plugin.getLogger().info("GitLab API is enabled.");
        }

        // Handle the case where both APIs are enabled
        if (isGitHubEnabled && isGitLabEnabled) {
            plugin.getLogger().info("Both GitHub and GitLab APIs are enabled.");
        }
    }

    /**
     * Loads the addons from the addons.json file into memory.
     */
    private void loadAddonsFromFile() {
        // Path to the addons.json file
        File addonsFile = new File(plugin.getDataFolder(), "addons.json");

        if (!addonsFile.exists()) {
            plugin.getLogger().severe("addons.json file not found.");
            return;
        }

        try {
            // Read the file content
            String jsonContent = new String(Files.readAllBytes(Paths.get(addonsFile.toURI())));
            JSONArray addonsArray = new JSONArray(jsonContent);

            // Loop through the addons and add them to the list
            for (int i = 0; i < addonsArray.length(); i++) {
                JSONObject addon = addonsArray.getJSONObject(i);
                Addon addonObj = new Addon(
                    addon.getString("name"),
                    addon.getString("platform"),
                    addon.getString("owner"),
                    addon.getString("repository"),
                    addon.optString("token", null)
                );
                addons.add(addonObj);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred while reading the addons.json file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Downloads the add-on based on the name from the loaded addons list.
     *
     * @param addonName the name of the add-on to be downloaded.
     */
    public void downloadAddOn(String addonName) {
        // Find the matching addon by name
        Addon addon = addons.stream()
            .filter(a -> a.getName().equals(addonName))
            .findFirst()
            .orElse(null);

        if (addon == null) {
            plugin.getLogger().severe("Addon with name " + addonName + " not found.");
            return;
        }

        String platform = addon.getPlatform();
        String token = addon.getToken();
        String owner = addon.getOwner();
        String repository = addon.getRepository();

        // Choose the appropriate API based on the platform
        if (platform.equalsIgnoreCase("github")) {
            downloadFromGitHub(owner, repository, addonName, token);
        } else if (platform.equalsIgnoreCase("gitlab")) {
            downloadFromGitLab(owner, repository, addonName, token);
        } else {
            plugin.getLogger().severe("Unknown platform: " + platform);
        }
    }

    /**
     * Downloads the add-on from GitHub using the owner and repository.
     *
     * @param owner     The GitHub repository owner.
     * @param repository The GitHub repository name.
     * @param addonName The name of the add-on.
     * @param token     The GitHub token (if available).
     */
    private void downloadFromGitHub(String owner, String repository, String addonName, String token) {
        MCEngineAddOnApiGitHub gitHubApi = new MCEngineAddOnApiGitHub(plugin);

        // Provide the necessary details
        String file = addonName + ".jar";      // Assuming the file format is .jar
        String path = plugin.getDataFolder().getPath();

        // Call the download function
        gitHubApi.downloadAddOn(owner, repository, file, path, token);
    }

    /**
     * Downloads the add-on from GitLab using the owner and repository.
     *
     * @param owner     The GitLab repository owner.
     * @param repository The GitLab repository name.
     * @param addonName The name of the add-on.
     * @param token     The GitLab token (if available).
     */
    private void downloadFromGitLab(String owner, String repository, String addonName, String token) {
        MCEngineAddOnApiGitLab gitLabApi = new MCEngineAddOnApiGitLab(plugin);

        // Provide the necessary details
        String file = addonName + ".jar";      // Assuming the file format is .jar
        String path = plugin.getDataFolder().getPath();

        // Call the download function
        gitLabApi.downloadAddOn(owner, repository, file, path, token);
    }
}
