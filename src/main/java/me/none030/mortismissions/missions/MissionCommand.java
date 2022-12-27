package me.none030.mortismissions.missions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.none030.mortismissions.missions.MissionMessages.*;
import static me.none030.mortismissions.utils.MessageUtils.colorMessage;

public class MissionCommand implements TabExecutor {

    private final MissionManager manager;

    public MissionCommand(MissionManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mortismissions.reload")) {
                sender.sendMessage(NO_PERMISSION);
                return false;
            }
            manager.reload();
            sender.sendMessage(RELOAD);
        }
        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("mortismissions.give")) {
                sender.sendMessage(NO_PERMISSION);
                return false;
            }
            if (args.length >= 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(TARGET_NOT_FOUND);
                    return false;
                }
                if (manager.getMissionById().get(args[2]) == null) {
                    sender.sendMessage(MISSION_NOT_FOUND);
                    return false;
                }
                Mission mission = manager.getMissionById().get(args[2]);
                if (mission == null) {
                    sender.sendMessage(MISSION_NOT_FOUND);
                    return false;
                }
                if (args.length == 3) {
                    mission.giveMission(target);
                } else {
                    int amount = Integer.parseInt(args[3]);
                    mission.giveMission(target, amount);
                }
            }
        }
        if (args[0].equalsIgnoreCase("random")) {
            if (!sender.hasPermission("mortismissions.random")) {
                sender.sendMessage(NO_PERMISSION);
                return false;
            }
            if (args.length != 2) {
                sender.sendMessage(colorMessage("&cUsage: /missions give <player>"));
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(TARGET_NOT_FOUND);
                return false;
            }
            List<String> categories = manager.getCategories();
            Collections.shuffle(categories);
            List<String> ids = new ArrayList<>(manager.getMissionIdsByCategory().get(categories.get(0)));
            Collections.shuffle(ids);
            Mission mission = manager.getMissionById().get(ids.get(0));
            if (mission == null) {
                sender.sendMessage(MISSION_NOT_FOUND);
                return false;
            }
            mission.giveMission(target);
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
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length != 3) {
                return null;
            }
            return new ArrayList<>(manager.getMissionById().keySet());
        }
        return null;
    }
}
