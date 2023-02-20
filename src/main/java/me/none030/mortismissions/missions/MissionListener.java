package me.none030.mortismissions.missions;

import me.none030.mortismissions.utils.MissionItem;
import me.none030.mortismissions.utils.MissionType;
import me.none030.mortismissions.utils.Requirement;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class MissionListener implements Listener {

    private final MissionManager manager;

    public MissionListener(MissionManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (!e.getAction().isRightClick()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(item);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.getType().equals(MissionType.COLLECT)) {
            return;
        }
        mission.Check(player, item);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player player = entity.getKiller();
        if (e.isCancelled()) {
            return;
        }
        if (player == null) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.KILL);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(entity.getType())) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onWalk(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (player.isFlying() || player.isInsideVehicle()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.WALK);
        if (player.isGliding()) {
            missionItem = manager.getMissionItem(player, MissionType.GLIDE);
        }
        if (player.isSwimming()) {
            missionItem = manager.getMissionItem(player, MissionType.SWIM);
        }
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

    @EventHandler
    public void onRide(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (player.isFlying() || !player.isInsideVehicle() || player.getVehicle() == null) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.RIDE);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(player.getVehicle().getType())) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Material material = e.getBlock().getType();
        if (e.isCancelled()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.BREAK);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(material)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Material material = e.getBlockPlaced().getType();
        if (e.isCancelled()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.PLACE);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(material)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Player player = e.getEnchanter();
        Material material = e.getItem().getType();
        if (e.isCancelled()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.ENCHANT);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(material)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (!(e.getCaught() instanceof Item)) {
            return;
        }
        Item caught = (Item) e.getCaught();
        if (caught == null) {
            return;
        }
        Material material = caught.getItemStack().getType();
        MissionItem missionItem = manager.getMissionItem(player, MissionType.FISH);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(material)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onHarvest(PlayerHarvestBlockEvent e) {
        Player player = e.getPlayer();
        Material material = e.getHarvestedBlock().getType();
        if (e.isCancelled()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.HARVEST);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(material)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, 1);
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent e) {
        Player player = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.SHEAR);
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

    @EventHandler
    public void onCollect(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        MissionItem missionItem = manager.getMissionItem(item);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (!mission.getType().equals(MissionType.COLLECT)) {
            return;
        }
        if (mission.Check(player, item)) {
            return;
        }
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        Requirement requirement = mission.getRequirements();
        if (!player.getInventory().contains(requirement.getMaterials().get(0))) {
            return;
        }
        mission.collect(player, item, requirement.getMaterials().get(0));
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.isCancelled()) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        Material material = e.getCurrentItem().getType();
        int amount = e.getCurrentItem().getAmount();
        if (e.getRawSlot() != 0) {
            return;
        }
        MissionItem missionItem = manager.getMissionItem(player, MissionType.CRAFT);
        if (missionItem == null) {
            return;
        }
        Mission mission = missionItem.getMission();
        if (mission.isWorld(player.getWorld())) {
            return;
        }
        if (!mission.hasRequirement(material)) {
            return;
        }
        ItemStack item = missionItem.getItem();
        mission.updateMission(player, item, amount);
    }
}
