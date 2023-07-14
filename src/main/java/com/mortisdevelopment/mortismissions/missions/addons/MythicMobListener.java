package com.mortisdevelopment.mortismissions.missions.addons;

import com.mortisdevelopment.mortismissions.missions.MissionManager;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import com.mortisdevelopment.mortismissions.missions.Mission;
import com.mortisdevelopment.mortismissions.missions.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MythicMobListener implements Listener {

    private final MissionManager missionManager;

    public MythicMobListener(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        if (e.getKiller() == null || !(e.getKiller() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getKiller();
        ItemStack item = missionManager.getItem(player, MissionType.KILL_MYTHIC);
        if (item == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || mission.isDisabledWorld(player.getWorld()) || !mission.isRequirement(e.getMobType())) {
            return;
        }
        mission.updateMission(item, 1);
    }
}
