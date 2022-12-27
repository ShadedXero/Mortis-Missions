package me.none030.mortismissions.missions;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import me.none030.mortismissions.utils.MissionItem;
import me.none030.mortismissions.utils.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CrackShotListener implements Listener {

    private final MissionManager manager;

    public CrackShotListener(MissionManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onDamage(WeaponDamageEntityEvent e) {
        Player player = e.getPlayer();
        int damage = (int) e.getDamage();
        if (e.isCancelled()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.DAMAGE_CRACKSHOT);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(e.getWeaponTitle())) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, damage);
    }
}
