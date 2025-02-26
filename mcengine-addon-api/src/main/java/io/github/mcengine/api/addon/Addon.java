package io.github.mcengine.api.addon;

public class Addon {
    private final String name;
    private final String platform;
    private final String owner;
    private final String repository;
    private final String token;

    public Addon(String name, String platform, String owner, String repository, String token) {
        this.name = name;
        this.platform = platform;
        this.owner = owner;
        this.repository = repository;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepository() {
        return repository;
    }

    public String getToken() {
        return token;
    }
}
