package com.mortisdevelopment.mortismissions.missions;

import com.mortisdevelopment.mortiscorespigot.MortisCoreSpigot;
import com.mortisdevelopment.mortismissions.data.MissionData;
import com.mortisdevelopment.mortismissions.missions.requirements.MaterialRequirement;
import com.mortisdevelopment.mortismissions.MortisMissions;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;

public class MissionListener implements Listener {

    private final MortisMissions plugin = MortisMissions.getInstance();
    private final MissionManager missionManager;
    private final HashMap<Player, Integer> walkByPlayer;
    private final HashMap<Player, Integer> swimByPlayer;
    private final HashMap<Player, Integer> glideByPlayer;

    public MissionListener(MissionManager missionManager) {
        this.missionManager = missionManager;
        this.walkByPlayer = new HashMap<>();
        this.swimByPlayer = new HashMap<>();
        this.glideByPlayer = new HashMap<>();
        check();
    }

    private void check() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkWalk();
                checkSwim();
                checkGlide();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void checkWalk() {
        Iterator<Player> walkPlayers = walkByPlayer.keySet().iterator();
        while (walkPlayers.hasNext()) {
            Player player = walkPlayers.next();
            if (player == null) {
                return;
            }
            Integer blocks = walkByPlayer.get(player);
            if (blocks == null || blocks == 0) {
                walkPlayers.remove();
                return;
            }
            walkPlayers.remove();
            check(player, MissionType.WALK, null, blocks);
        }
    }

    private void checkSwim() {
        Iterator<Player> walkPlayers = swimByPlayer.keySet().iterator();
        while (walkPlayers.hasNext()) {
            Player player = walkPlayers.next();
            if (player == null) {
                return;
            }
            Integer blocks = swimByPlayer.get(player);
            if (blocks == null || blocks == 0) {
                walkPlayers.remove();
                return;
            }
            walkPlayers.remove();
            check(player, MissionType.SWIM, null, blocks);
        }
    }

    private void checkGlide() {
        Iterator<Player> walkPlayers = glideByPlayer.keySet().iterator();
        while (walkPlayers.hasNext()) {
            Player player = walkPlayers.next();
            if (player == null) {
                return;
            }
            Integer blocks = glideByPlayer.get(player);
            if (blocks == null || blocks == 0) {
                walkPlayers.remove();
                return;
            }
            walkPlayers.remove();
            check(player, MissionType.GLIDE, null, blocks);
        }
    }

    private void check(Player player, MissionType type, Object object, int amount) {
        ItemStack item = missionManager.getItem(player, type);
        if (item == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || mission.isDisabledWorld(player.getWorld()) || !mission.isRequirement(object)) {
            return;
        }
        mission.updateMission(item, amount);
    }

    @EventHandler
    public void onWalk(PlayerMoveEvent e) {
        if (e.isCancelled() || e.getTo() == null) {
            return;
        }
        Player player = e.getPlayer();
        if (player.isFlying() || player.isInsideVehicle()) {
            return;
        }
        int distance = (int) Math.round(e.getFrom().distance(e.getTo()));
        if (distance <= 0) {
            return;
        }
        if (player.isGliding()) {
            glideByPlayer.put(player, glideByPlayer.getOrDefault(player, 0) + distance);
            return;
        }
        if (player.isSwimming()) {
            swimByPlayer.put(player, swimByPlayer.getOrDefault(player, 0) + distance);
            return;
        }
        walkByPlayer.put(player, walkByPlayer.getOrDefault(player, 0) + distance);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        EquipmentSlot hand = e.getHand();
        if (hand == null || !hand.equals(EquipmentSlot.HAND)) {
            return;
        }
        Action action = e.getAction();
        if (!action.name().contains("RIGHT")) {
            return;
        }
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || mission.getType().equals(MissionType.COLLECT)) {
            return;
        }
        MissionData data = new MissionData(meta);
        if (!data.isComplete()) {
            return;
        }
        Player player = e.getPlayer();
        player.getInventory().removeItem(item);
        mission.giveReward(player);
        MortisCoreSpigot.getAdventure().player(player).sendTitlePart(TitlePart.TITLE, missionManager.getMessage("MISSION_COMPLETE_TITLE").replaceText(TextReplacementConfig.builder().match("%mission_name%").replacement(mission.getTitle()).build()));
        MortisCoreSpigot.getAdventure().player(player).sendTitlePart(TitlePart.SUBTITLE, missionManager.getMessage("MISSION_COMPLETE_SUBTITLE").replaceText(TextReplacementConfig.builder().match("%mission_name%").replacement(mission.getTitle()).build()));
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player player = entity.getKiller();
        if (player == null) {
            return;
        }
        check(player, MissionType.KILL, entity.getType(), 1);
    }

    @EventHandler
    public void onRide(PlayerMoveEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player player = e.getPlayer();
        if (player.isFlying() || !player.isInsideVehicle() || player.getVehicle() == null) {
            return;
        }
        check(player, MissionType.RIDE, player.getVehicle().getType(), 1);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.BREAK, e.getBlock().getType(), 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.PLACE, e.getBlockPlaced().getType(), 1);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getEnchanter(), MissionType.ENCHANT, e.getItem().getType(), 1);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.isCancelled() || !(e.getCaught() instanceof Item)) {
            return;
        }
        Item caught = (Item) e.getCaught();
        if (caught == null) {
            return;
        }
        check(e.getPlayer(), MissionType.FISH, caught.getItemStack().getType(), 1);
    }

    @EventHandler
    public void onHarvest(PlayerHarvestBlockEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.HARVEST, e.getHarvestedBlock().getType(), 1);
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.SHEAR, null, 1);
    }

    @EventHandler
    public void onCollect(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        Mission mission = missionManager.getMission(item);
        if (mission == null || !mission.getType().equals(MissionType.COLLECT)) {
            return;
        }
        MissionData data = new MissionData(meta);
        if (data.isComplete()) {
            player.getInventory().removeItem(item);
            mission.giveReward(player);
            MortisCoreSpigot.getAdventure().player(player).sendTitlePart(TitlePart.TITLE, missionManager.getMessage("MISSION_COMPLETE_TITLE").replaceText(TextReplacementConfig.builder().match("%mission_name%").replacement(mission.getTitle()).build()));
            MortisCoreSpigot.getAdventure().player(player).sendTitlePart(TitlePart.SUBTITLE, missionManager.getMessage("MISSION_COMPLETE_SUBTITLE").replaceText(TextReplacementConfig.builder().match("%mission_name%").replacement(mission.getTitle()).build()));
        }else {
            if (mission.isDisabledWorld(player.getWorld())) {
                return;
            }
            int amount = ((MaterialRequirement) mission.getRequirement()).removeRequirement(player, data.getMax());
            if (amount <= 0) {
                return;
            }
            mission.updateMission(item, amount);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.isCancelled() || !e.getSlotType().equals(InventoryType.SlotType.RESULT)) {
            return;
        }
        ItemStack craftedItem = e.getCurrentItem();
        if (craftedItem == null) {
            return;
        }
        int amount = craftedItem.getAmount();
        check((Player) e.getWhoClicked(), MissionType.CRAFT, craftedItem.getType(), amount);
    }

    @EventHandler
    public void onTame(EntityTameEvent e) {
        if (e.isCancelled() || !(e.getOwner() instanceof Player)) {
            return;
        }
        check((Player) e.getOwner(), MissionType.TAME, e.getEntity().getType(), 1);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.INTERACT_ENTITY, e.getRightClicked().getType(), 1);
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent e) {
        if (e.isCancelled() || e.getEntity().getPassengers().size() == 0) {
            return;
        }
        Entity entity = e.getEntity().getPassengers().get(0);
        if (!(entity instanceof Player)) {
            return;
        }
        check((Player) entity, MissionType.HORSE_JUMP, null, 1);
    }

    @EventHandler
    public void onExpGain(PlayerExpChangeEvent e) {
        int amount = e.getAmount();
        if (amount <= 0) {
            return;
        }
        check(e.getPlayer(), MissionType.EXP_GAIN, null, amount);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        check(e.getEntity(), MissionType.DEATH, null, 1);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.BUCKET_FILL, e.getBlock().getType(), 1);
    }

    @EventHandler
    public void onBuketEmpty(PlayerBucketEmptyEvent e) {
        if (e.isCancelled()) {
            return;
        }
        check(e.getPlayer(), MissionType.BUCKET_EMPTY, e.getBlock().getType(), 1);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent e) {
        if (!(e.getBreeder() instanceof Player)) {
            return;
        }
        check((Player) e.getBreeder(), MissionType.BREED, e.getEntity().getType(), 1);
    }
}
