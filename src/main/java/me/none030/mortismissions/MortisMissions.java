package me.none030.mortismissions;

import me.none030.mortismissions.missions.MissionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MortisMissions extends JavaPlugin {

    private static MortisMissions Instance;
    private MissionManager missionManager;
    public boolean crackShot = true;
    public boolean mythicMobs = true;
    public boolean qav = true;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        missionManager = new MissionManager();
        if (getServer().getPluginManager().getPlugin("CrackShot") == null) {
            crackShot = false;
        }
        if (getServer().getPluginManager().getPlugin("MythicMobs") == null) {
            mythicMobs = false;
        }
        if (getServer().getPluginManager().getPlugin("QualityArmoryVehicles") == null) {
            qav = false;
        }
    }

    public static MortisMissions getInstance() {
        return Instance;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public void setMissionManager(MissionManager missionManager) {
        this.missionManager = missionManager;
    }
}
