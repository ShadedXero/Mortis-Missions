package me.none030.mortismissions.utils;

import io.lumine.mythic.api.mobs.MythicMob;
import me.zombie_striker.qg.exp.cars.VehicleEntity;
import me.zombie_striker.qg.exp.cars.api.QualityArmoryVehicles;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Requirement {
    private Material material;
    private EntityType entity;
    private MythicMob mythicMob;
    private String weaponTitle;

    public Requirement(Material material) {
        this.material = material;
    }

    public Requirement(EntityType entity) {
        this.entity = entity;
    }

    public Requirement(MythicMob mythicMob) {
        this.mythicMob = mythicMob;
    }

    public Requirement(String weaponTitle) {
        this.weaponTitle = weaponTitle;
    }

    public Material getMaterial() {
        return material;
    }

    public EntityType getEntity() {
        return entity;
    }

    public MythicMob getMythicMob() {
        return mythicMob;
    }

    public String getWeaponTitle() {
        return weaponTitle;
    }
}
