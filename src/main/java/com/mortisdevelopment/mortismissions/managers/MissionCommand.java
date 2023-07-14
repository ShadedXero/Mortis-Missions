package com.mortisdevelopment.mortismissions.managers;

import com.mortisdevelopment.mortiscorespigot.MortisCoreSpigot;
import com.mortisdevelopment.mortismissions.missions.MissionCategory;
import com.mortisdevelopment.mortismissions.missions.Mission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MissionCommand implements TabExecutor {

    private final Manager manager;

    public MissionCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mortismissions.reload")) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("NO_PERMISSION"));
                return false;
            }
            manager.reload();
            MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("RELOADED"));
            return true;
        }
        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("mortismissions.give")) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("NO_PERMISSION"));
                return false;
            }
            if (args.length < 3) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("GIVE_USAGE"));
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("INVALID_TARGET"));
                return false;
            }
            Mission mission = manager.getMissionManager().getMissionById().get(args[2]);
            if (mission == null) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("MISSION_NOT_FOUND"));
                return false;
            }
            if (args.length > 3) {
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                }catch (IllegalArgumentException exp) {
                    MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("INVALID_NUMBER"));
                    return false;
                }
                mission.giveMission(target, amount);
            }else {
                mission.giveMission(target);
            }
            MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("MISSION_RECEIVED"));
            return true;
        }
        if (args[0].equalsIgnoreCase("random")) {
            if (!sender.hasPermission("mortismissions.random")) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("NO_PERMISSION"));
                return false;
            }
            if (args.length < 2) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("RANDOM_USAGE"));
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("INVALID_TARGET"));
                return false;
            }
            MissionCategory category = manager.getMissionManager().getCategories().getRandom();
            if (category == null) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("MISSION_NOT_FOUND"));
                return false;
            }
            String missionId = category.getRandom();
            if (missionId == null) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("MISSION_NOT_FOUND"));
                return false;
            }
            Mission mission = manager.getMissionManager().getMissionById().get(missionId);
            if (mission == null) {
                MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("MISSION_NOT_FOUND"));
                return false;
            }
            mission.giveMission(target);
            MortisCoreSpigot.getAdventure().sender(sender).sendMessage(manager.getMissionManager().getMessage("MISSION_RECEIVED"));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("give");
            arguments.add("random");
            arguments.add("reload");
            return arguments;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return new ArrayList<>(manager.getMissionManager().getMissionById().keySet());
            }
        }
        return null;
    }
}
