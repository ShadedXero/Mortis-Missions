package me.none030.mortismissions.config;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.none030.mortismissions.MortisMissions;
import me.none030.mortismissions.missions.Mission;
import me.none030.mortismissions.missions.MissionManager;
import me.none030.mortismissions.utils.ItemBuilder;
import me.none030.mortismissions.utils.MissionType;
import me.none030.mortismissions.utils.Requirement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.none030.mortismissions.missions.MissionMessages.*;
import static me.none030.mortismissions.utils.MessageUtils.colorMessage;

public class ConfigManager {

    private final MortisMissions plugin = MortisMissions.getInstance();
    private final MissionManager manager;

    public ConfigManager(MissionManager manager) {
        this.manager = manager;
        loadConfig();
    }

    private void loadConfig() {
        File file = saveConfig();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages == null) {
            return;
        }
        NO_PERMISSION = colorMessage(messages.getString("no-permission"));
        TARGET_NOT_FOUND = colorMessage(messages.getString("target-not-found"));
        MISSION_NOT_FOUND = colorMessage(messages.getString("mission-not-found"));
        MISSION_COMPLETE_TITLE = colorMessage(messages.getString("mission-complete-title"));
        MISSION_COMPLETE_SUBTITLE = colorMessage(messages.getString("mission-complete-subtitle"));
        MISSION_RECEIVED = colorMessage(messages.getString("mission-received"));
        ConfigurationSection categorySection = config.getConfigurationSection("categories");
        if (categorySection == null) {
            return;
        }
        for (String category : categorySection.getKeys(false)) {
            manager.getCategories().add(category);
            manager.getMissionIdsByCategory().put(category, new ArrayList<>());
            manager.getChancesByCategory().put(category, categorySection.getInt(category));
        }
        ConfigurationSection missions = config.getConfigurationSection("missions");
        if (missions == null) {
            return;
        }
        for (String key : missions.getKeys(false)) {
            ConfigurationSection section = missions.getConfigurationSection(key);
            if (section == null) {
                continue;
            }
            String category = section.getString("category");
            manager.getMissionIdsByCategory().get(category).add(key);
            MissionType type = MissionType.valueOf(section.getString("type"));
            Requirement requirement = null;
            if (section.contains("object")) {
                if (!type.equals(MissionType.KILL) && !type.equals(MissionType.KILL_MYTHIC) && !type.equals(MissionType.DAMAGE_CRACKSHOT) && !type.equals(MissionType.RIDE)) {
                    Material material = Material.valueOf(section.getString("object"));
                    requirement = new Requirement(material);
                } else {
                    if (type.equals(MissionType.KILL) || type.equals(MissionType.RIDE)) {
                        EntityType entity = EntityType.valueOf(section.getString("object"));
                        requirement = new Requirement(entity);
                    }
                    if (type.equals(MissionType.KILL_MYTHIC)) {
                        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(section.getString("object")).orElse(null);
                        requirement = new Requirement(mob);
                    }
                    if (type.equals(MissionType.DAMAGE_CRACKSHOT)) {
                        String weaponTitle = section.getString("object");
                        requirement = new Requirement(weaponTitle);
                    }
                }
            }
            int min = section.getInt("min-requirement");
            int max = section.getInt("max-requirement");
            String title = colorMessage(section.getString("title"));
            String material = section.getString("material");
            String name = colorMessage(section.getString("name"));
            List<String> lore = section.getStringList("lore");
            lore.forEach(s -> lore.set(lore.indexOf(s), colorMessage(s)));
            ItemBuilder builder = new ItemBuilder(material, 1, name, lore, null, null);
            ItemStack item = builder.Build();
            List<String> rewards = section.getStringList("rewards");
            List<World> worlds = new ArrayList<>();
            for (String worldName : section.getStringList("enabled-worlds")) {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    continue;
                }
                worlds.add(world);
            }
            Mission mission = new Mission(key, title, type, item, requirement, min, max, worlds, rewards);
            manager.getMissions().add(mission);
            manager.getMissionById().put(key, mission);
        }
    }

    private File saveConfig() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }
        return file;
    }
}
