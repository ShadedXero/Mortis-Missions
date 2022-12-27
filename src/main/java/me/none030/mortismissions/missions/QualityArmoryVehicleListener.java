package me.none030.mortismissions.missions;

import me.none030.mortismissions.utils.MissionItem;
import me.none030.mortismissions.utils.MissionType;
import me.zombie_striker.qg.exp.cars.api.QualityArmoryVehicles;
import me.zombie_striker.qg.exp.cars.api.events.PlayerEnterQAVehicleEvent;
import me.zombie_striker.qg.exp.cars.api.events.PlayerExitQAVehicleEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QualityArmoryVehicleListener implements Listener {

    private final List<UUID> playersInVehicle;
    private final MissionManager manager;

    public QualityArmoryVehicleListener(MissionManager manager) {
        playersInVehicle = new ArrayList<>();
        this.manager = manager;
    }

    @EventHandler
    public void onVehicleEnter(PlayerEnterQAVehicleEvent e) {
        Player player = e.getPlayer();
        playersInVehicle.add(player.getUniqueId());
    }
    @EventHandler
    public void onVehicleExit(PlayerExitQAVehicleEvent e) {
        Player player = e.getPlayer();
        playersInVehicle.remove(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        playersInVehicle.remove(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.getVehicle() == null) {
            return;
        }
        if (!QualityArmoryVehicles.isVehicle(player.getVehicle())) {
            return;
        }
        playersInVehicle.remove(player.getUniqueId());
    }

    @EventHandler
    public void onVehicleRide(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!playersInVehicle.contains(player.getUniqueId())) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.RIDE_QAV);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }
}
