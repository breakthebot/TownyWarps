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

package org.breakthebot.townyWarp.commands;


import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.townyWarp.TownyWarp;
import org.breakthebot.townyWarp.Warp;
import org.breakthebot.townyWarp.utils.MetaDataHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class addWarp implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg("Only players may use this command.");
            return true;
        }

        if (args.length != 1) {
            TownyMessaging.sendErrorMsg(ChatColor.RED + "Usage: /" + label + " <name>");
            return false;
        }

        String name = args[0];

        if (!name.matches("[A-Za-z0-9_]{1,16}")) {
            TownyMessaging.sendErrorMsg(ChatColor.RED + "Invalid name! Names may only contain letters, numbers, and underscores, up to 16 chars.");
            return true;
        }

        try {
            TownyUniverse towny = TownyUniverse.getInstance();

            Resident resident = towny.getResident(player.getUniqueId());

            if (!resident.hasTown()) {
                TownyMessaging.sendErrorMsg(ChatColor.RED + "You are not in a town.");
                return true;
            }

            Town town = resident.getTown();

            if (town.getMayor().equals(resident)) {
                int price = Warp.calculateWarpPrice(MetaDataHelper.getWarpCount(town));
                double bal = town.getAccount().getHoldingBalance();

                if (price > bal){
                    TownyMessaging.sendErrorMsg("You do not have enough gold for this warp");
                    return false;
                }
                if (TownyWarp.getInstance().getConf().maxWarps == MetaDataHelper.getWarpCount(town) && TownyWarp.getInstance().getConf().maxWarps != 1){
                    TownyMessaging.sendErrorMsg("You have reached the allowed limit of warps on this server.");
                    return false;
                }
                Warp warp = new Warp(
                        name,
                        player.getLocation(),
                        sender.getName(),
                        price
                );

                MetaDataHelper.addWarp(town, warp);
                TownyMessaging.sendMsg(ChatColor.GREEN + "Warp added successfully");
                return true;

            } else {
                TownyMessaging.sendErrorMsg(ChatColor.RED + "You must be the town’s Mayor to run this.");
                return false;
            }

        } catch (NotRegisteredException e) {
            TownyMessaging.sendErrorMsg(ChatColor.RED + "Towny data not found. Try again later.");
        }

        return true;
    }


}
