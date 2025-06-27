package org.breakthebot.TownyWarps.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static org.breakthebot.TownyWarps.MetaData.MetaDataHelper.getTownWarps;

public class warps implements CommandExecutor, TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        switch (args[0]) {
            case "add" -> {
                return addWarp.onCommand(sender, command, label, args);
            }
            case "remove" -> {
                return deleteWarp.onCommand(sender, command, label, args);
            }
            case "list" -> {
                return listWarp.onCommand(sender, command, label, args);
            }
            case "level" -> {
                return editWarp.onCommand(sender, command, label, args);
            }
            case "info" -> {
                return info.onCommand(sender, command, label, args);
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String @NotNull[] args) {
        if (args.length == 1) {
            return Stream.of("add", "remove", "list", "level", "info")
                    .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "remove", "level" -> {
                    if (sender instanceof Player player) {
                        try {
                            Resident resident = TownyAPI.getInstance().getResident(player);
                            if (resident != null && resident.hasTown()) {
                                Town town = resident.getTown();
                                return getTownWarps(town).stream()
                                        .map(Warp::getName)
                                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                        .sorted()
                                        .toList();
                            }
                        } catch (NotRegisteredException ignored) {}
                    }
                }
                case "list", "info" -> {
                    return TownyUniverse.getInstance().getTowns().stream()
                            .map(Town::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .sorted()
                            .toList();
                }
                case "add" -> {
                    return List.of();
                }
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "add", "level" -> {
                    return Stream.of("resident", "outsider")
                            .filter(s -> s.startsWith(args[2].toLowerCase()))
                            .toList();
                }
                case "info" -> {
                    Town town = TownyAPI.getInstance().getTown(args[1]);
                    if (town != null) {
                        return getTownWarps(town).stream()
                                .map(Warp::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .sorted()
                                .toList();
                    }
                }
            }
        }

        return List.of();
    }


}
