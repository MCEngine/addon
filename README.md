# 📦 Addon Downloader

This project provides a way to fetch available addons from a central website and download them directly in-game using the command:

```
/addon {project} {addon_id}
```

## ⚠️ Requirements

This project requires a server to host addon JAR files.

It is recommended to use a URL structure like:

```
https://addon.example.com/{project}/{addon_id}/{addon_version}
```

✅ This allows everyone to use the same path format—only the domain needs to be changed.

## 💡 Usage

- To download the **latest version** of an addon:
  ```
  /addon {project} {addon_id}
  ```

- To download a **specific version**:
  ```
  /addon {project} {addon_id} {version}
  ```
