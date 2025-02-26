package io.github.mcengine.api.addon.platform.gitlab;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * This class provides functionality for downloading add-ons from a GitLab repository.
 */
public class MCEngineAddOnApiGitLab {
    private Plugin plugin;
    private String token;

    /**
     * Constructs a new MCEngineAddOnApiGitLab instance.
     *
     * @param plugin The plugin instance which is calling this API.
     * @param token  The GitLab personal access token for authentication.
     */
    public MCEngineAddOnApiGitLab(Plugin plugin) {
        this.plugin = plugin;
        this.token = plugin.getConfig().getString("gitlab.token");
    }

    /**
     * Downloads a file from a specified GitLab repository release.
     *
     * @param owner       The GitLab username or group owning the repository.
     * @param repository The name of the repository.
     * @param file        The name of the file to be downloaded from the release.
     * @param path        The path where the downloaded add-on should be installed.
     * @param directToken The GitLab personal access token to use for authentication. If null, the default token will be used.
     */
    public void downloadAddOn(String owner, String repository, String file, String path, String directToken) {
        // Use the provided token if available; otherwise, fall back to the default token
        String tokenToUse = (directToken != null) ? directToken : token;

        try {
            // First, get the project ID from GitLab's API using the owner and repository
            String projectIdUrl = "https://gitlab.com/api/v4/projects/" + encodeRepository(owner + "/" + repository);
            URI uri = new URI(projectIdUrl);  // Using URI instead of URL constructor
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            // Set authorization header with the appropriate token
            connection.setRequestProperty("Authorization", "Bearer " + tokenToUse);
            connection.setRequestMethod("GET");

            // Handle server response to get the project ID
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response to get the project ID
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Extract the project ID from the JSON response
                String projectId = extractProjectId(response.toString());

                // Now that we have the project ID, use it to download the release asset
                downloadFile(projectId, file, path);
            } else {
                plugin.getLogger().severe("Failed to fetch project ID. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred while downloading the add-on: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Downloads the file from the GitLab release using the provided project ID.
     *
     * @param projectId The project ID.
     * @param file      The file to download.
     * @param path      The path where to save the downloaded file.
     */
    private void downloadFile(String projectId, String file, String path) {
        try {
            String downloadUrl = "https://gitlab.com/api/v4/projects/" + projectId + "/releases/assets?name=" + file;
            URI downloadUri = new URI(downloadUrl);  // Using URI for the download URL
            HttpURLConnection downloadConnection = (HttpURLConnection) downloadUri.toURL().openConnection();
            downloadConnection.setRequestProperty("Authorization", "Bearer " + token);
            downloadConnection.setRequestMethod("GET");

            // Handle the download of the asset file
            int downloadResponseCode = downloadConnection.getResponseCode();
            if (downloadResponseCode == HttpURLConnection.HTTP_OK) {
                // Get the input stream (file content)
                InputStream inputStream = downloadConnection.getInputStream();

                // Create the file where the add-on will be installed
                File outputFile = new File(path, file);
                outputFile.getParentFile().mkdirs(); // Ensure directory exists
                try (ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                     FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }

                plugin.getLogger().info("Successfully downloaded " + file + " to " + path);
            } else {
                plugin.getLogger().severe("Failed to download file: " + file + ". Response Code: " + downloadResponseCode);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred while downloading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract the project ID from the GitLab API response JSON.
     *
     * @param jsonResponse The JSON response from the API.
     * @return The project ID.
     */
    private String extractProjectId(String jsonResponse) {
        // Extract project ID from the JSON response (you can use a JSON parsing library like Jackson or Gson)
        // For simplicity, assuming it's in the format {"id": <project_id>}
        int startIndex = jsonResponse.indexOf("\"id\":") + 5;
        int endIndex = jsonResponse.indexOf(",", startIndex);
        return jsonResponse.substring(startIndex, endIndex).trim();
    }

    /**
     * Encodes the repository name for the URL.
     *
     * @param repository The repository name to encode.
     * @return The encoded repository name.
     */
    private String encodeRepository(String repository) {
        try {
            return java.net.URLEncoder.encode(repository, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            plugin.getLogger().severe("Failed to encode repository name: " + e.getMessage());
            return repository;
        }
    }
}
