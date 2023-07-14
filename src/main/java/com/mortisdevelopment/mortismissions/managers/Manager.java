package com.mortisdevelopment.mortismissions.managers;

import com.mortisdevelopment.mortismissions.MortisMissions;
import com.mortisdevelopment.mortismissions.config.ConfigManager;
import com.mortisdevelopment.mortismissions.missions.MissionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class Manager {

    private final MortisMissions plugin = MortisMissions.getInstance();
    private MissionManager missionManager;
    private ConfigManager configManager;

    public Manager() {
        this.missionManager = new MissionManager();
        this.configManager = new ConfigManager(this);
        plugin.getServer().getPluginCommand("missions").setExecutor(new MissionCommand(this));
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        Bukkit.getScheduler().cancelTasks(plugin);
        setMissionManager(new MissionManager());
        setConfigManager(new ConfigManager(this));
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public void setMissionManager(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}
