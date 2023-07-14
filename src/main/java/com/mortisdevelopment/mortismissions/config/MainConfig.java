package com.mortisdevelopment.mortismissions.config;

import com.mortisdevelopment.mortiscorespigot.configs.Config;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import com.mortisdevelopment.mortismissions.missions.MissionCategory;
import com.mortisdevelopment.mortismissions.missions.MissionType;
import com.mortisdevelopment.mortismissions.missions.requirements.*;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import com.mortisdevelopment.mortismissions.missions.Mission;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainConfig extends Config {

    private final ConfigManager configManager;

    public MainConfig(ConfigManager configManager) {
        super("config.yml");
        this.configManager = configManager;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        saveConfig();
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        loadMissions(config.getConfigurationSection("missions"));
        loadCategories(config.getConfigurationSection("categories"));
        configManager.getManager().getMissionManager().addMessages(loadMessages(config.getConfigurationSection("messages")));
    }

    private MissionRequirement getRequirement(ConfigurationSection section, MissionType type) {
        if (!section.contains("objects")) {
            return null;
        }
        List<String> objects = section.getStringList("objects");
        if (objects.contains("ANYTHING")) {
            return null;
        }
        if (type.equals(MissionType.COLLECT) || type.equals(MissionType.BREAK) || type.equals(MissionType.PLACE) || type.equals(MissionType.CRAFT) || type.equals(MissionType.ENCHANT) || type.equals(MissionType.FISH) || type.equals(MissionType.HARVEST) || type.equals(MissionType.BUCKET_FILL) || type.equals(MissionType.BUCKET_EMPTY)) {
            List<Material> materials = new ArrayList<>();
            for (String object : objects) {
                Material material;
                try {
                    material = Material.valueOf(object);
                } catch (IllegalArgumentException exp) {
                    continue;
                }
                materials.add(material);
            }
            return new MaterialRequirement(materials);
        }
        if (type.equals(MissionType.KILL) || type.equals(MissionType.RIDE) || type.equals(MissionType.TAME) || type.equals(MissionType.INTERACT_ENTITY) || type.equals(MissionType.BREED)) {
            List<EntityType> entities = new ArrayList<>();
            for (String object : objects) {
                EntityType entity;
                try {
                    entity = EntityType.valueOf(object);
                } catch (IllegalArgumentException exp) {
                    continue;
                }
                entities.add(entity);
            }
            return new EntityRequirement(entities);
        }
        if (type.equals(MissionType.KILL_MYTHIC)) {
            List<MythicMob> mythicMobs = new ArrayList<>();
            for (String object : objects) {
                MythicMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(object).orElse(null);
                if (mythicMob == null) {
                    continue;
                }
                mythicMobs.add(mythicMob);
            }
            return new MythicMobRequirement(mythicMobs);
        }
        if (type.equals(MissionType.DAMAGE_CRACKSHOT) || type.equals(MissionType.DAMAGE_WM)) {
            return new WeaponRequirement(objects);
        }
        return null;
    }

    private void loadMissions(ConfigurationSection missions) {
        if (missions == null) {
            return;
        }
        for (String id : missions.getKeys(false)) {
            ConfigurationSection section = missions.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            String title = MessageUtils.color(section.getString("title"));
            String category = section.getString("category");
            MissionType type = MissionType.valueOf(section.getString("type"));
            MissionRequirement requirement = getRequirement(section, type);
            int min = section.getInt("min-requirement");
            int max = section.getInt("max-requirement");
            ItemStack item = loadItem(section);
            if (item == null) {
               continue;
            }
            List<String> rewards = section.getStringList("rewards");
            List<String> disabledWorldNames = section.getStringList("disabled-worlds");
            Mission mission = new Mission(id, title, category, type, item, requirement, min, max, disabledWorldNames, rewards);
            configManager.getManager().getMissionManager().getMissionById().put(id, mission);
        }
    }

    private void loadCategories(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        for (String id : section.getKeys(false)) {
            if (id == null) {
                continue;
            }
            double chance = section.getDouble(id);
            List<String> missionIds = new ArrayList<>();
            for (Mission mission : configManager.getManager().getMissionManager().getMissionById().values()) {
                if (!mission.getCategory().equalsIgnoreCase(id)) {
                    continue;
                }
                missionIds.add(id);
            }
            MissionCategory category = new MissionCategory(id, missionIds);
            configManager.getManager().getMissionManager().getCategories().addEntry(category, chance);
        }
    }
}
