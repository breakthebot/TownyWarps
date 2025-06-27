/*
 * This file is part of TownyWarp.
 *
 * TownyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TownyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TownyWarp. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breakthebot.TownyWarps.TownyAdmin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyObject;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.breakthebot.TownyWarps.MetaData.MetaDataHelper.getTownWarps;

public class command implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        if (args.length < 2) {
            TownyMessaging.sendErrorMsg(player,
                    "Usage: /ta warps <town> <add|remove|level|rename> [warpName] [resident|outsider|newName]");
            return false;
        }

        String townName   = args[0];
        String identifier = args[1].toLowerCase();

        String[] subArgs = Arrays.copyOfRange(args, 2, args.length);

        Town town = TownyAPI.getInstance().getTown(townName);
        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Unknown town: " + townName);
            return false;
        }

        switch (identifier) {
            case "add" -> {
                if (!player.hasPermission("towny.command.townyadmin.warp.create")) {
                    TownyMessaging.sendErrorMsg(player, "You do not have permission to create warps.");
                    return false;
                }
                if (subArgs.length != 2) {
                    TownyMessaging.sendErrorMsg(player,
                            "Usage: /ta warps " + townName + " add <warpName> <resident|outsider>");
                    return false;
                }
                return utils.addWarp(sender, townName, subArgs);
            }

            case "remove" -> {
                if (!player.hasPermission("towny.command.townyadmin.warp.delete")) {
                    TownyMessaging.sendErrorMsg(player, "You do not have permission to delete warps.");
                    return false;
                }
                if (subArgs.length != 1) {
                    TownyMessaging.sendErrorMsg(player,
                            "Usage: /ta warps " + townName + " remove <warpName>");
                    return false;
                }
                return utils.removeWarp(sender, townName, subArgs);
            }

            case "level" -> {
                if (!player.hasPermission("towny.command.townyadmin.warp.level")) {
                    TownyMessaging.sendErrorMsg(player, "You do not have permission to change warp levels.");
                    return false;
                }
                if (subArgs.length != 2) {
                    TownyMessaging.sendErrorMsg(player,
                            "Usage: /ta warps " + townName + " level <warpName> <resident|outsider>");
                    return false;
                }
                return utils.levelWarp(sender, townName, subArgs);
            }

            case "rename" -> {
                if (!player.hasPermission("towny.command.townyadmin.warp.rename")) {
                    TownyMessaging.sendErrorMsg(player, "You do not have permission to rename warps.");
                    return false;
                }
                if (subArgs.length != 2) {
                    TownyMessaging.sendErrorMsg(player,
                            "Usage: /ta warps " + townName + " rename <oldName> <newName>");
                    return false;
                }
                return utils.renameWarp(sender, townName, subArgs);
            }

            default -> {
                TownyMessaging.sendErrorMsg(player,
                        "Invalid subcommand. Use: add, remove, level, or rename.");
                return false;
            }
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 1) {
            return TownyAPI.getInstance().getTowns().stream()
                    .map(TownyObject::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted()
                    .toList();
        }

        List<String> subcommands = List.of("add", "remove", "level", "rename");

        if (args.length == 2) {
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        String sub = args[1].toLowerCase();
        if (!subcommands.contains(sub)) {
            return List.of();
        }

        Town town = TownyAPI.getInstance().getTown(args[0]);
        if (town == null) {
            return List.of();
        }

        List<String> warps = getTownWarps(town).stream()
                .map(Warp::getName)
                .sorted()
                .toList();

        if (args.length == 3) {
            return switch (sub) {
                case "remove", "level", "rename" ->
                        warps.stream()
                                .filter(w -> w.toLowerCase().startsWith(args[2].toLowerCase()))
                                .toList();
                default -> List.of();
            };
        }

        if (args.length == 4) {
            return switch (sub) {
                case "add", "level" ->
                        Stream.of("resident", "outsider")
                                .filter(r -> r.startsWith(args[3].toLowerCase()))
                                .toList();
                default -> List.of();
            };
        }

        return List.of();
    }
}