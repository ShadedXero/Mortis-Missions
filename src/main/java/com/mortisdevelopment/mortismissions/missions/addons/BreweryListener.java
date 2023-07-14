package com.mortisdevelopment.mortismissions.missions.addons;

import com.dre.brewery.api.events.brew.BrewDrinkEvent;
import com.mortisdevelopment.mortismissions.missions.MissionManager;
import com.mortisdevelopment.mortismissions.missions.Mission;
import com.mortisdevelopment.mortismissions.missions.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BreweryListener implements Listener {

    private final MissionManager missionManager;

    public BreweryListener(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @EventHandler
    public void onBrew(BrewDrinkEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack item = missionManager.getItem(player, MissionType.BREWERY);
        if (item == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || mission.isDisabledWorld(player.getWorld()) || !mission.isRequirement(null)) {
            return;
        }
        mission.updateMission(item, e.getAddedAlcohol());
    }
}
