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
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.townyWarp.TownyWarp;
import org.breakthebot.townyWarp.Warp;
import org.breakthebot.townyWarp.utils.MetaDataHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class addWarp {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warp add <name> <permLevel>");
            return false;
        }

        String name = args[1];
        String permLevel = args[2];

        if (!(Warp.AccessLevel.OUTSIDER.name().toLowerCase().equals(permLevel) | Warp.AccessLevel.RESIDENT.name().toLowerCase().equals(permLevel))){
            TownyMessaging.sendErrorMsg(player, "Invalid Permission level. You may only choose resident or outsider.");
            return false;
        }
        if (!name.matches("[A-Za-z0-9_]{1,16}")) {
            TownyMessaging.sendErrorMsg(player, "Invalid name. Names may only contain letters, numbers, and underscores, up to 16 chars.");
            return false;
        }

        try {
            TownyUniverse towny = TownyUniverse.getInstance();

            Resident resident = towny.getResident(player.getUniqueId());

            if (!resident.hasTown()) {
                TownyMessaging.sendErrorMsg(player, "You are not in a town.");
                return false;
            }

            Town town = resident.getTown();

            if (town.getMayor().equals(resident)) {

                double bal = town.getAccount().getHoldingBalance();
                int next = Warp.calculateWarpCost(MetaDataHelper.getWarpCount(town) + 1);

                if (next > bal){
                    TownyMessaging.sendErrorMsg(player, "You do not have enough gold for this warp. Amount needed: " + next);
                    return false;
                }

                if (TownyWarp.getInstance().getConf().maxWarps == MetaDataHelper.getWarpCount(town)){
                    TownyMessaging.sendErrorMsg(player, "You have reached the allowed limit of warps on this server.");
                    return false;
                }

                Warp warp = new Warp(
                        name,
                        player.getLocation(),
                        sender.getName()
                );
                warp.setPermLevel(Warp.AccessLevel.valueOf(permLevel));
                MetaDataHelper.addWarp(town, warp);
                TownyMessaging.sendMsg(player, "Warp added successfully");
                return true;

            } else {
                TownyMessaging.sendErrorMsg(player, "You must be the Mayor to run this.");
                return false;
            }

        } catch (Exception e) {
            TownyMessaging.sendErrorMsg(player, "Towny data not found. Try again later.");
        }

        return true;
    }


}
