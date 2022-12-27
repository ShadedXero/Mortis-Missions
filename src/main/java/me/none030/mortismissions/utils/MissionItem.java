package me.none030.mortismissions.utils;

import me.none030.mortismissions.missions.Mission;
import org.bukkit.inventory.ItemStack;

public class MissionItem {

    private final ItemStack item;
    private final Mission mission;

    public MissionItem(ItemStack item, Mission mission) {
        this.item = item;
        this.mission = mission;
    }

    public ItemStack getItem() {
        return item;
    }

    public Mission getMission() {
        return mission;
    }
}
