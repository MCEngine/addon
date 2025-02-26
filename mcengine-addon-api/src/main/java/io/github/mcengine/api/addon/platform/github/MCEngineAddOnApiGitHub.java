import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class provides functionality for downloading add-ons from a GitHub repository.
 */
public class MCEngineAddOnApiGitHub {
    private Plugin plugin;
    private String token;

    /**
     * Constructs a new MCEngineAddOnApiGitHub instance.
     *
     * @param plugin The plugin instance which is calling this API.
     * @param token  The GitHub personal access token for authentication.
     */
    public MCEngineAddOnApiGitHub(Plugin plugin) {
        this.plugin = plugin;
        this.token = plugin.getConfig().getString("github.token");
    }

    /**
     * Downloads a file from a specified GitHub repository release.
     *
     * @param owner       The GitHub username or organization owning the repository.
     * @param repository  The name of the repository.
     * @param file        The name of the file to be downloaded from the release.
     * @param path        The path where the downloaded add-on should be installed.
     */
    public void downloadAddOn(String owner, String repository, String file, String path) {
        try {
            // GitHub API URL for fetching release assets
            String urlString = "https://api.github.com/repos/" + owner + "/" + repository + "/releases";
            URI uri = new URI(urlString); // Use URI instead of URL constructor
            URL url = uri.toURL(); // Convert URI to URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set authorization header
            connection.setRequestProperty("Authorization", "token " + token);
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestMethod("GET");

            // Handle server response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response and parse the JSON
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JSONArray releases = new JSONArray(response.toString());

                    // Find the asset by file name
                    boolean fileFound = false;
                    for (int i = 0; i < releases.length(); i++) {
                        JSONObject release = releases.getJSONObject(i);
                        JSONArray assets = release.getJSONArray("assets");

                        for (int j = 0; j < assets.length(); j++) {
                            JSONObject asset = assets.getJSONObject(j);
                            if (asset.getString("name").equals(file)) {
                                fileFound = true;
                                String downloadUrl = asset.getString("browser_download_url");

                                // Now download the file
                                downloadFile(downloadUrl, file, path);
                                break;
                            }
                        }
                        if (fileFound) break;
                    }

                    if (!fileFound) {
                        plugin.getLogger().severe("File not found in the release: " + file);
                    }
                }
            } else {
                plugin.getLogger().severe("Failed to fetch releases. Response Code: " + responseCode);
            }
        } catch (Exception e) { // Catch URI and other exceptions
            plugin.getLogger().severe("An error occurred while downloading the add-on: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Downloads the file from the given URL to the specified path.
     *
     * @param downloadUrl The URL to download the file from.
     * @param file        The name of the file to be saved.
     * @param path        The path where the downloaded file should be installed.
     */
    private void downloadFile(String downloadUrl, String file, String path) {
        try {
            // Use URI to create the URL in a non-deprecated way
            URI uri = new URI(downloadUrl);  // Create URI from the string
            URL url = uri.toURL();           // Convert URI to URL
    
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
            // Handle server response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the input stream (file content)
                InputStream inputStream = connection.getInputStream();
    
                // Create the file where the add-on will be installed
                File outputFile = new File(path, file);
                outputFile.getParentFile().mkdirs(); // Ensure directory exists
                try (ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                     FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }
    
                plugin.getLogger().info("Successfully downloaded " + file + " to " + path);
            } else {
                plugin.getLogger().severe("Failed to download file: " + file + ". Response Code: " + responseCode);
            }
        } catch (Exception e) {  // Catch URI and other exceptions
            plugin.getLogger().severe("An error occurred while downloading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }    
}
