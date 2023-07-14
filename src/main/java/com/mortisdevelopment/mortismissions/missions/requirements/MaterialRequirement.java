package com.mortisdevelopment.mortismissions.missions.requirements;

import com.mortisdevelopment.mortismissions.MortisMissions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MaterialRequirement extends MissionRequirement {

    private final MortisMissions plugin = MortisMissions.getInstance();
    private final List<Material> materials;

    public MaterialRequirement(List<Material> materials) {
        this.materials = materials;
    }

    @Override
    public boolean isRequirement(Object object) {
        if (!(object instanceof Material)) {
            return false;
        }
        Material material = (Material) object;
        return materials.contains(material);
    }

    public int removeRequirement(Player player, int max) {
        int removed = 0;
        for (Material material : materials) {
            ItemStack item = new ItemStack(material, 1);
            while (player.getInventory().contains(item)) {
                if (removed >= max) {
                    break;
                }
                player.getInventory().removeItem(item);
                removed++;
            }
            if (removed >= max) {
                break;
            }
        }
        return removed;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
