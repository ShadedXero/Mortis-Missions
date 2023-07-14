package com.mortisdevelopment.mortismissions.missions.addons;

import com.mortisdevelopment.mortismissions.missions.MissionManager;
import com.mortisdevelopment.mortismissions.missions.Mission;
import com.mortisdevelopment.mortismissions.missions.MissionType;
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
    private final MissionManager missionManager;

    public QualityArmoryVehicleListener(MissionManager missionManager) {
        playersInVehicle = new ArrayList<>();
        this.missionManager = missionManager;
    }

    @EventHandler
    public void onVehicleEnter(PlayerEnterQAVehicleEvent e) {
        if (e.isCanceled()) {
            return;
        }
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
        if (e.isCancelled()) {
            return;
        }
        Player player = e.getPlayer();
        if (!playersInVehicle.contains(player.getUniqueId())) {
            return;
        }
        ItemStack item = missionManager.getItem(player, MissionType.RIDE_QAV);
        if (item == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || mission.isDisabledWorld(player.getWorld()) || !mission.isRequirement(null)) {
            return;
        }
        mission.updateMission(item, 1);
    }
}
