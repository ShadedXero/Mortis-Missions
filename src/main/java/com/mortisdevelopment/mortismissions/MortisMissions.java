package com.mortisdevelopment.mortismissions;

import com.mortisdevelopment.mortiscorespigot.MortisCoreSpigot;
import com.mortisdevelopment.mortismissions.managers.Manager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MortisMissions extends JavaPlugin {

    private static MortisMissions Instance;
    private Manager manager;
    private boolean crackShot;
    private boolean mythicMobs;
    private boolean qav;
    private boolean weaponMechanics;
    private boolean brewery;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        MortisCoreSpigot.register(this);
        crackShot = getServer().getPluginManager().getPlugin("CrackShot") != null;
        mythicMobs = getServer().getPluginManager().getPlugin("MythicMobs") != null;
        qav = getServer().getPluginManager().getPlugin("QualityArmoryVehicles") != null;
        weaponMechanics = getServer().getPluginManager().getPlugin("WeaponMechanics") != null;
        brewery = getServer().getPluginManager().getPlugin("Brewery") != null;
        manager = new Manager();
    }

    public static MortisMissions getInstance() {
        return Instance;
    }

    public Manager getManager() {
        return manager;
    }

    public boolean hasCrackShot() {
        return crackShot;
    }

    public boolean hasMythicMobs() {
        return mythicMobs;
    }

    public boolean hasQav() {
        return qav;
    }

    public boolean hasWeaponMechanics() {
        return weaponMechanics;
    }

    public boolean hasBrewery() {
        return brewery;
    }
}
