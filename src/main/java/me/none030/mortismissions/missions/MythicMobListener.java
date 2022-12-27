package me.none030.mortismissions.missions;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.none030.mortismissions.utils.MissionItem;
import me.none030.mortismissions.utils.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MythicMobListener implements Listener {

    private final MissionManager manager;

    public MythicMobListener(MissionManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        if (e.getKiller() == null) {
            return;
        }
        if (!(e.getKiller() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getKiller();
        MythicMob mythicMob = e.getMobType();
        MissionItem missionItem = manager.getMissionItem(player, MissionType.KILL_MYTHIC);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(mythicMob)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }
}
