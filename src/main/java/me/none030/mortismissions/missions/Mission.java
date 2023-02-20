package me.none030.mortismissions.missions;

import io.lumine.mythic.api.mobs.MythicMob;
import me.none030.mortismissions.MortisMissions;
import me.none030.mortismissions.utils.MissionData;
import me.none030.mortismissions.utils.MissionType;
import me.none030.mortismissions.utils.Requirement;
import me.zombie_striker.qg.exp.cars.VehicleEntity;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.none030.mortismissions.missions.MissionMessages.*;

public class Mission {

    private final MortisMissions plugin = MortisMissions.getInstance();

    private final String id;
    private final String title;
    private final MissionType type;
    private final ItemStack mission;
    private final Requirement requirements;
    private final int min;
    private final int max;
    private final List<World> disabledWorlds;
    private final List<String> rewards;

    public Mission(String id, String title, MissionType type, ItemStack mission, Requirement requirements, int min, int max, List<World> disabledWorlds, List<String> rewards) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.mission = mission;
        this.requirements = requirements;
        this.min = min;
        this.max = max;
        this.disabledWorlds = disabledWorlds;
        this.rewards = rewards;
    }

    public boolean isWorld(World world) {
        return disabledWorlds.contains(world);
    }

    public boolean hasRequirement(String weaponTitle) {
        return requirements.getWeaponTitles().contains(weaponTitle);
    }

    public boolean hasRequirement(Material material) {
        return requirements.getMaterials().contains(material);
    }

    public boolean hasRequirement(EntityType entity) {
        return requirements.getEntities().contains(entity);
    }

    public boolean hasRequirement(MythicMob mythicMob) {
        return requirements.getMythicMobs().contains(mythicMob);
    }

    public boolean Check(Player player, ItemStack item) {
        MissionData data = getNBTData(item);
        if (data == null) {
            return false;
        }
        if (!data.isComplete()) {
            return false;
        }
        player.getInventory().remove(item);
        giveReward(player);
        return true;
    }

    public void giveMission(Player player) {
        Random random = new Random();
        int amount = random.nextInt(min, max);
        ItemStack item = createMission(amount);
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        player.sendMessage(MISSION_RECEIVED);
    }

    public void giveMission(Player player, int amount) {
        ItemStack item = createMission(amount);
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        player.sendMessage(MISSION_RECEIVED);
    }

    private ItemStack createMission(int amount) {
        ItemStack item = mission.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta.getLore() != null) {
            List<String> lore = meta.getLore();
            lore.replaceAll(s -> s.replace("%amount%", String.valueOf(amount)).replace("%progress%", "0"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        setNBTData(item, id, amount);
        return item;
    }

    public void updateMission(Player player, ItemStack item, int amount) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(mission.getItemMeta().getLore());
        MissionData data = getNBTData(item);
        assert data != null;
        if (meta.getLore() != null) {
            List<String> lore = meta.getLore();
            lore.replaceAll(s -> s.replace("%amount%", String.valueOf(data.getMax())).replace("%progress%", String.valueOf(data.getCurrent() + amount)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        addNBTData(player, item, data.getCurrent() + amount);
    }

    public void collect(Player player, ItemStack item, Material material) {
        MissionData data = getNBTData(item);
        if (data == null) {
            return;
        }
        int amount = data.getMax() - data.getCurrent();
        if (amount <= 0) {
            return;
        }
        int items = 0;
        HashMap<Integer, ItemStack> materials = new HashMap<>(player.getInventory().all(material));
        for (Integer slot : materials.keySet()) {
            items += materials.get(slot).getAmount();
        }
        int itemAmount;
        ItemStack collectItem = new ItemStack(material, 1);
        if (items >= amount) {
            itemAmount = amount;
            for (int i = 0; i < amount; i++) {
                player.getInventory().removeItem(collectItem);
            }
        }else {
            itemAmount = items;
            for (int i = 0; i < items; i++) {
                player.getInventory().removeItem(collectItem);
            }
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(mission.getItemMeta().getLore());
        if (meta.getLore() != null) {
            List<String> lore = meta.getLore();
            lore.replaceAll(s -> s.replace("%amount%", String.valueOf(data.getMax())).replace("%progress%", String.valueOf(data.getCurrent() + itemAmount)));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        addNBTData(player, item, data.getCurrent() + itemAmount);
    }

    private void giveReward(Player player) {
        for (String command : this.rewards) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", player.getName()));
        }
    }

    private void addNBTData(Player player, ItemStack item, int amount) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container =  meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "MissionCurrent"), PersistentDataType.INTEGER, amount);
        item.setItemMeta(meta);
        update(player, item);
    }

    private void update(Player player, ItemStack item) {
        MissionData data = getNBTData(item);
        if (data == null) {
            return;
        }
        if (data.getCurrent() < data.getMax()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(mission.getItemMeta().getLore());
        if (meta.getLore() != null) {
            List<String> lore = meta.getLore();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                if (!line.contains("%amount%") && !line.contains("%progress%")) {
                    continue;
                }
                line = line.replace("%amount%", String.valueOf(data.getMax())).replace("%progress%", String.valueOf(data.getMax()));
                for (ChatColor color : ChatColor.values()) {
                    line = line.replace(color.toString(), color.toString() + "" + ChatColor.STRIKETHROUGH);
                }
                lore.set(i, line);
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        setComplete(item);
        player.sendTitle(MISSION_COMPLETE_TITLE, MISSION_COMPLETE_SUBTITLE.replace("%mission_name%", title));
    }

    private void setNBTData(ItemStack item, String id, int max) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container =  meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "Mission"), PersistentDataType.STRING, UUID.randomUUID().toString());
        container.set(new NamespacedKey(plugin, "MissionComplete"), PersistentDataType.STRING, "false");
        container.set(new NamespacedKey(plugin, "MissionId"), PersistentDataType.STRING, id);
        container.set(new NamespacedKey(plugin, "MissionType"), PersistentDataType.STRING, type.toString());
        container.set(new NamespacedKey(plugin, "MissionMax"), PersistentDataType.INTEGER, max);
        container.set(new NamespacedKey(plugin, "MissionCurrent"), PersistentDataType.INTEGER, 0);
        item.setItemMeta(meta);
    }

    private void setComplete(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container =  meta.getPersistentDataContainer();
        container.set(new NamespacedKey(plugin, "MissionComplete"), PersistentDataType.STRING, "true");
        item.setItemMeta(meta);
    }

    public boolean isComplete(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container =  meta.getPersistentDataContainer();
        return Boolean.parseBoolean(container.get(new NamespacedKey(plugin, "MissionComplete"), PersistentDataType.STRING));
    }

    private MissionData getNBTData(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container =  meta.getPersistentDataContainer();
        String id = container.get(new NamespacedKey(plugin, "MissionId"), PersistentDataType.STRING);
        String type = container.get(new NamespacedKey(plugin, "MissionType"), PersistentDataType.STRING);
        boolean complete = Boolean.parseBoolean(container.get(new NamespacedKey(plugin, "MissionComplete"), PersistentDataType.STRING));
        Integer max = container.get(new NamespacedKey(plugin, "MissionMax"), PersistentDataType.INTEGER);
        Integer current = container.get(new NamespacedKey(plugin, "MissionCurrent"), PersistentDataType.INTEGER);
        if (id == null || type == null) {
            return null;
        }
        return new MissionData(id, type, max, current, complete);
    }

    public Requirement getRequirements() {
        return requirements;
    }

    public MissionType getType() {
        return type;
    }
}
