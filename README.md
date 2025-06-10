# 📦 Addon Downloader

This project provides a way to fetch available addons from a central website and download them directly in-game using the command:

```
/addon {project} {addon_id}
```

## ⚠️ Requirements

This project requires a server to host addon JAR files.

It is recommended to use a URL structure like:

```
https://addon.example.com/{project}/{addon_id}/{addon_version}/{file_name}.jar
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

## 🔐 Important

Always think about **security** when downloading and using addons.

Any addon you load onto your server should have a clearly identifiable **author**.

This helps determine whether the addon can be trusted and ensures the security and stability of your server.

Avoid duplicate content by following a standard and unified addon hosting structure.
