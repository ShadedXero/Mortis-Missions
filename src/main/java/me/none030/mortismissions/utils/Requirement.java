package me.none030.mortismissions.utils;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Requirement {
    private List<Material> materials;
    private List<EntityType> entities;
    private List<MythicMob> mythicMobs;
    private List<String> weaponTitles;

    public Requirement(List<String> objects, RequirementType type) {
        if (type.equals(RequirementType.MATERIAL)) {
            List<Material> materials = new ArrayList<>();
            for (String object : objects) {
                Material material;
                try {
                    material = Material.valueOf(object);
                }catch (IllegalArgumentException exp) {
                    continue;
                }
                materials.add(material);
            }
            this.materials = materials;
        }
        if (type.equals(RequirementType.ENTITY)) {
            List<EntityType> entities = new ArrayList<>();
            for (String object : objects) {
                EntityType entity;
                try {
                    entity = EntityType.valueOf(object);
                }catch (IllegalArgumentException exp) {
                    continue;
                }
                entities.add(entity);
            }
            this.entities = entities;
        }
        if (type.equals(RequirementType.MYTHIC_MOB)) {
            List<MythicMob> mythicMobs = new ArrayList<>();
            for (String object : objects) {
                MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(object).orElse(null);
                if (mob == null) {
                    continue;
                }
                mythicMobs.add(mob);
            }
            this.mythicMobs = mythicMobs;
        }
        if (type.equals(RequirementType.WEAPON)) {
            this.weaponTitles = objects;
        }
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public List<EntityType> getEntities() {
        return entities;
    }

    public List<MythicMob> getMythicMobs() {
        return mythicMobs;
    }

    public List<String> getWeaponTitles() {
        return weaponTitles;
    }
}
