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

package org.breakthebot.TownyWarps.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.NoSuchElementException;

import static org.breakthebot.TownyWarps.MetaData.MetaDataHelper.getTownWarps;


public class warp implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {
        TownyAPI townyApi = TownyAPI.getInstance();

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        if (!player.hasPermission("townywarps.warp.use")) {
            TownyMessaging.sendErrorMsg(player, "You are not permitted to perform this command.");
            return false;
        }

       String townName;
       String warpName;
        if (args.length < 1 || args.length > 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warp <town> <name>");
            return false;
        }
        if (args.length == 1) {
           townName = townyApi.getTownName(player);
           warpName = args[0];
       } else {
           townName = args[0];
           warpName = args[1];
       }

       Resident res = townyApi.getResident(player);
       Town town = townyApi.getTown(townName);
        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Town not found.");
            return false;
        }
        if (town.getOutlaws().contains(res)) {
            TownyMessaging.sendErrorMsg(player, "You are outlawed in that town.");
            return false;
        }

       try {
           Warp targetWarp = MetaDataHelper.getWarp(town, warpName).orElseThrow();
           Warp.AccessLevel permLvl = targetWarp.getPermLevel();
           if (permLvl.name().equals("OUTSIDER")) {
               assert res != null;
               if (!res.hasTown()) {
                   TownyMessaging.sendErrorMsg(player, "You must be part of a town.");
                   return false;
               }
               Location loc = targetWarp.toLocation();
               TownyMessaging.sendMsg(player, "&bWaiting to teleport...");
               townyApi.requestTeleport(player, loc, TownySettings.getTeleportWarmupTime());
               return true;

           } else if (permLvl.name().equals("RESIDENT")) {
               assert res != null;
               if (!res.hasTown() || !res.getTown().getName().equalsIgnoreCase(townName)) {
                   TownyMessaging.sendErrorMsg(player, "This warp is only open for residents.");
                   return false;
               }
               Location loc = targetWarp.toLocation();
               TownyMessaging.sendMsg(player, "&bWaiting to teleport...");
               if (player.hasPermission("towny.admin.spawn.nowarmup")) {
                   townyApi.requestTeleport(player, loc, 0);
                   return true;
               }
               int warmup;
               if (player.hasPermission("towny.admin.spawn.nowarmup")) {
                   warmup = 0;
               } else {
                   warmup = TownySettings.getTeleportWarmupTime();
               }
               townyApi.requestTeleport(player, loc, warmup);
               return true;
           }


       } catch (NoSuchElementException e) {
           TownyMessaging.sendErrorMsg(player, townName + " has no warp named " + warpName);
       } catch (NotRegisteredException e) {
           TownyMessaging.sendErrorMsg(player, townName + " does not exist");
       }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (args.length == 1) {
            return TownyUniverse.getInstance().getTowns().stream()
                    .map(Town::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted()
                    .toList();
        }

        if (args.length == 2) {
            Town town = TownyUniverse.getInstance().getTown(args[0]);
            if (town != null) {
                return getTownWarps(town).stream()
                        .map(Warp::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .sorted()
                        .toList();
            }
        }

        return List.of();
    }

}


