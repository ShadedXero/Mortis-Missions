package com.mortisdevelopment.mortismissions.missions.addons;

import com.mortisdevelopment.mortismissions.missions.MissionManager;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponDamageEntityEvent;
import com.mortisdevelopment.mortismissions.missions.Mission;
import com.mortisdevelopment.mortismissions.missions.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class WeaponMechanicListener implements Listener {

    private final MissionManager missionManager;

    public WeaponMechanicListener(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @EventHandler
    public void onDamage(WeaponDamageEntityEvent e) {
        if (e.isCancelled() || !(e.getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getShooter();
        int damage = (int) e.getFinalDamage();
        if (damage <= 0) {
            return;
        }
        ItemStack item = missionManager.getItem(player, MissionType.DAMAGE_CRACKSHOT);
        if (item == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || mission.isDisabledWorld(player.getWorld()) || !mission.isRequirement(e.getWeaponTitle())) {
            return;
        }
        mission.updateMission(item, damage);
    }
}
