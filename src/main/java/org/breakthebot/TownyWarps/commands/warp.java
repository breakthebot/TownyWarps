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
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.utils.SpawnUtil;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.Warp;
import org.breakthebot.TownyWarps.utils.TeleportUtil;
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


public class warp implements CommandExecutor, TabExecutor {
    private TeleportUtil tpUtil = TeleportUtil.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {
        TownyAPI townyApi = TownyAPI.getInstance();

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
                return listWarp.onCommand(sender,command,label,args);
            }
        }

       String townName;
       String warpName;

       if (args.length == 1){
           townName = townyApi.getTownName(player);
           warpName = args[0];
       } else {
           townName = args[0];
           warpName = args[1];
       }

       try {
           Warp targetWarp = MetaDataHelper.getWarp(townyApi.getTown(townName), warpName).orElseThrow();
           Warp.AccessLevel permLvl = targetWarp.getPermLevel();
           Resident res = townyApi.getResident(player.getName());
           if (permLvl.name().equals("OUTSIDER")) {
               assert res != null;
               if (!res.hasTown()) {
                   TownyMessaging.sendErrorMsg(player, "You must be part of a town.");
                   return false;
               }
               Location loc = targetWarp.toLocation();
               tpUtil.teleport(player, loc, TownySettings.getTeleportWarmupTime());
               return true;

           } else if (permLvl.name().equals("RESIDENT")) {
               assert res != null;
               if (!res.hasTown() || !res.getTown().getName().equalsIgnoreCase(townName)) {
                   TownyMessaging.sendErrorMsg(player, "This warp is only open for residents.");
                   return false;
               }
               Location loc = targetWarp.toLocation();
               tpUtil.teleport(player, loc, TownySettings.getTeleportWarmupTime());
               return true;
           }


       } catch (NoSuchElementException e) {
           TownyMessaging.sendErrorMsg(player, townName + " has no warp named " + warpName);
       } catch (NotRegisteredException e) {
           TownyMessaging.sendErrorMsg(player, townName + " does not exist");
       }

        if (args.length > 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warp <town> <name>");
            return false;
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
        return List.of();
    }
}


