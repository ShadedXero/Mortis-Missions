package me.none030.mortismissions.missions;

import me.none030.mortismissions.MortisMissions;
import me.none030.mortismissions.config.ConfigManager;
import me.none030.mortismissions.utils.ChanceContainer;
import me.none030.mortismissions.utils.MissionData;
import me.none030.mortismissions.utils.MissionItem;
import me.none030.mortismissions.utils.MissionType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MissionManager {

    private final MortisMissions plugin = MortisMissions.getInstance();

    private ConfigManager configManager;
    private List<Mission> missions;
    private ChanceContainer<String> categories;
    private HashMap<String, List<String>> missionIdsByCategory;
    private HashMap<String, Mission> missionById;
    private HashMap<String, Integer> chancesByCategory;

    public MissionManager() {
        missions = new ArrayList<>();
        categories = new ChanceContainer<>();
        missionIdsByCategory = new HashMap<>();
        missionById = new HashMap<>();
        chancesByCategory = new HashMap<>();
        configManager = new ConfigManager(this);
        MissionCommand missionCommand = new MissionCommand(this);
        plugin.getServer().getPluginCommand("missions").setExecutor(missionCommand);
        plugin.getServer().getPluginManager().registerEvents(new MissionListener(this), plugin);
        if (plugin.crackShot) {
            plugin.getServer().getPluginManager().registerEvents(new CrackShotListener(this), plugin);
        }
        if (plugin.mythicMobs) {
            plugin.getServer().getPluginManager().registerEvents(new MythicMobListener(this), plugin);
        }
        if (plugin.qav) {
            plugin.getServer().getPluginManager().registerEvents(new QualityArmoryVehicleListener(this), plugin);
        }
    }

    public void reload() {
        missions = new ArrayList<>();
        categories = new ChanceContainer<>();
        missionIdsByCategory = new HashMap<>();
        missionById = new HashMap<>();
        chancesByCategory = new HashMap<>();
        configManager = new ConfigManager(this);
    }

    public MissionItem getMissionItem(Player player, MissionType type) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }
            MissionData data = getNBTData(item);
            if (data == null) {
                continue;
            }
            if (data.isComplete()) {
                continue;
            }
            if (!type.equals(MissionType.valueOf(data.getType()))) {
                continue;
            }
            Mission mission = missionById.get(data.getId());
            if (mission == null) {
                continue;
            }
            return new MissionItem(item, mission);
        }
        return null;
    }

    public MissionItem getMissionItem(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) {
            return null;
        }
        MissionData data = getNBTData(item);
        if (data == null) {
            return null;
        }
        Mission mission = missionById.get(data.getId());
        if (mission == null) {
            return null;
        }
        return new MissionItem(item, mission);
    }

    private MissionData getNBTData(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container =  meta.getPersistentDataContainer();
        String id = container.get(new NamespacedKey(plugin, "MissionId"), PersistentDataType.STRING);
        String type = container.get(new NamespacedKey(plugin, "MissionType"), PersistentDataType.STRING);
        boolean complete = Boolean.parseBoolean(container.get(new NamespacedKey(plugin, "MissionComplete"), PersistentDataType.STRING));
        Integer max = container.get(new NamespacedKey(plugin, "MissionMax"), PersistentDataType.INTEGER);
        Integer current = container.get(new NamespacedKey(plugin, "MissionCurrent"), PersistentDataType.INTEGER);
        if (id == null || type == null) {
            return null;
        }
        return new MissionData(id, type, max, current, complete);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public ChanceContainer<String> getCategories() {
        return categories;
    }

    public HashMap<String, List<String>> getMissionIdsByCategory() {
        return missionIdsByCategory;
    }

    public HashMap<String, Mission> getMissionById() {
        return missionById;
    }

    public HashMap<String, Integer> getChancesByCategory() {
        return chancesByCategory;
    }
}
