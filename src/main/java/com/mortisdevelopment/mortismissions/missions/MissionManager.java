package com.mortisdevelopment.mortismissions.missions;

import com.mortisdevelopment.mortiscorespigot.managers.CoreManager;
import com.mortisdevelopment.mortiscorespigot.utils.Randomizer;
import com.mortisdevelopment.mortismissions.data.MissionData;
import com.mortisdevelopment.mortismissions.MortisMissions;
import com.mortisdevelopment.mortismissions.missions.addons.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MissionManager extends CoreManager {

    private final HashMap<String, Mission> missionById;
    private final Randomizer<MissionCategory> categories;

    public MissionManager() {
        categories = new Randomizer<>();
        missionById = new HashMap<>();
        MortisMissions plugin = MortisMissions.getInstance();
        plugin.getServer().getPluginManager().registerEvents(new MissionListener(this), plugin);
        if (plugin.hasCrackShot()) {
            plugin.getServer().getPluginManager().registerEvents(new CrackShotListener(this), plugin);
        }
        if (plugin.hasMythicMobs()) {
            plugin.getServer().getPluginManager().registerEvents(new MythicMobListener(this), plugin);
        }
        if (plugin.hasQav()) {
            plugin.getServer().getPluginManager().registerEvents(new QualityArmoryVehicleListener(this), plugin);
        }
        if (plugin.hasWeaponMechanics()) {
            plugin.getServer().getPluginManager().registerEvents(new WeaponMechanicListener(this), plugin);
        }
        if (plugin.hasBrewery()) {
            plugin.getServer().getPluginManager().registerEvents(new BreweryListener(this), plugin);
        }
    }

    public ItemStack getItem(Player player, MissionType type) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }
            MissionData data = new MissionData(meta);
            if (data.isComplete()) {
                continue;
            }
            Mission mission = missionById.get(data.getId());
            if (mission == null || !mission.getType().equals(type)) {
                continue;
            }
            return item;
        }
        return null;
    }

    public Mission getMission(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String missionId = new MissionData(meta).getId();
        if (missionId == null) {
            return null;
        }
        return missionById.get(missionId);
    }

    public List<Mission> getMissions() {
        return new ArrayList<>(missionById.values());
    }

    public HashMap<String, Mission> getMissionById() {
        return missionById;
    }

    public Randomizer<MissionCategory> getCategories() {
        return categories;
    }
}
