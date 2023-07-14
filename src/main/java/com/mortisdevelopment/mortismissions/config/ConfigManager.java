package com.mortisdevelopment.mortismissions.config;

import com.mortisdevelopment.mortismissions.managers.Manager;

public class ConfigManager {

    private final Manager manager;
    private final MainConfig mainConfig;

    public ConfigManager(Manager manager) {
        this.manager = manager;
        this.mainConfig = new MainConfig(this);
    }

    public Manager getManager() {
        return manager;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
