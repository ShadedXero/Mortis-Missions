package me.none030.mortismissions.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemBuilder {

    private final Material material;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final HashMap<Enchantment, Integer> enchants;
    private final List<ItemFlag> flags;

    public ItemBuilder(String material, int amount, String name, List<String> lore, List<String> enchants, List<String> flags) {
        this.material = Material.valueOf(material);
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.enchants = new HashMap<>();
        if (enchants != null) {
            for (String enchant : enchants) {
                String[] raw = enchant.split(":");
                this.enchants.put(Enchantment.getByName(raw[0]), Integer.parseInt(raw[1]));
            }
        }
        this.flags = new ArrayList<>();
        if (flags != null) {
            for (String flag : flags) {
                this.flags.add(ItemFlag.valueOf(flag));
            }
        }
    }

    public ItemStack Build() {
        if (material == null|| amount == 0) {
            return null;
        }
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        if (enchants != null) {
            for (Enchantment enchant : enchants.keySet()) {
                meta.addEnchant(enchant, enchants.get(enchant), true);
            }
        }
        if (flags != null) {
            for (ItemFlag flag : flags) {
                meta.addItemFlags(flag);
            }
        }
        item.setItemMeta(meta);
        return item;
    }
}