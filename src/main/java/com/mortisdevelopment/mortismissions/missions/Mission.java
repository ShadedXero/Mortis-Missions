package com.mortisdevelopment.mortismissions.missions;

import com.mortisdevelopment.mortiscorespigot.utils.ItemEditor;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import com.mortisdevelopment.mortismissions.data.MissionData;
import com.mortisdevelopment.mortismissions.missions.requirements.MissionRequirement;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class Mission {

    private final String id;
    private final String title;
    private final String category;
    private final MissionType type;
    private final ItemStack item;
    private final MissionRequirement requirement;
    private final int min;
    private final int max;
    private final List<String> disabledWorldNames;
    private final List<String> rewards;

    public Mission(String id, String title, String category, MissionType type, ItemStack item, MissionRequirement requirement, int min, int max, List<String> disabledWorldNames, List<String> rewards) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.type = type;
        this.item = item;
        this.requirement = requirement;
        this.min = min;
        this.max = max;
        this.disabledWorldNames = disabledWorldNames;
        this.rewards = rewards;
    }

    private ItemStack createMission(int amount) {
        ItemStack item = this.item.clone();
        if (item.getItemMeta() == null) {
            return null;
        }
        MissionData data = new MissionData(item.getItemMeta());
        data.create(id, amount, 0, false);
        item.setItemMeta(data.getMeta());
        ItemEditor editor = new ItemEditor(item);
        editor.setPlaceholder("%amount%", String.valueOf(amount));
        editor.setPlaceholder("%progress%", "0");
        return editor.getItem();
    }

    public void giveMission(Player player) {
        Random random = new Random();
        ItemStack item = createMission(random.nextInt(min, max));
        if (item == null) {
            return;
        }
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    public void giveMission(Player player, int amount) {
        ItemStack item = createMission(amount);
        if (item == null) {
            return;
        }
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    public void updateMission(ItemStack item, int amount) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta == null || item.getItemMeta() == null) {
            return;
        }
        MissionData data = new MissionData(item.getItemMeta());
        data.setCurrent(data.getCurrent() + amount);
        if (data.getCurrent() >= data.getMax()) {
            data.setCurrent(data.getMax());
            data.setComplete(true);
        }
        item.setItemMeta(data.getMeta());
        ItemEditor editor = new ItemEditor(item);
        editor.setLore(meta.getLore());
        if (data.isComplete()) {
            List<String> lore = editor.getLore();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                if (line.contains("%amount%") || line.contains("%progress%")) {
                    lore.set(i, MessageUtils.color(MessageUtils.getComponent(strike(line))));
                }
            }
            editor.setLore(lore);
        }
        editor.setPlaceholder("%amount%", String.valueOf(data.getMax()));
        editor.setPlaceholder("%progress%", String.valueOf(data.getCurrent()));
    }
    private String strike(String input) {
        StringBuilder modifiedString = new StringBuilder();
        boolean previousSection = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '§' && (i + 1) < input.length()) {
                char nextChar = input.charAt(i + 1);
                if (!previousSection) {
                    modifiedString.append("§");
                }
                modifiedString.append(nextChar).append("§m");
                i++;
                previousSection = true;
            } else {
                modifiedString.append(c);
                previousSection = false;
            }
        }
        return modifiedString.toString();
    }

    public boolean isRequirement(Object object) {
        if (requirement == null) {
            return true;
        }
        return requirement.isRequirement(object);
    }

    public boolean isDisabledWorld(World world) {
        return disabledWorldNames.contains(world.getName());
    }

    public void giveReward(Player player) {
        for (String command : this.rewards) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", player.getName()));
        }
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public MissionType getType() {
        return type;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public MissionRequirement getRequirement() {
        return requirement;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public List<String> getDisabledWorldNames() {
        return disabledWorldNames;
    }

    public List<String> getRewards() {
        return rewards;
    }
}
