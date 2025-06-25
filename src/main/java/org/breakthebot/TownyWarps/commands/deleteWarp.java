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


import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;

public class deleteWarp{

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return true;
        }

        if (args.length != 2) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warp remove <name>");
            return false;
        }

        String name = args[1];

        try {
            TownyUniverse towny = TownyUniverse.getInstance();

            Resident resident = towny.getResident(player.getName());

            assert resident != null;
            if (!resident.hasTown()) {
                TownyMessaging.sendErrorMsg(player, "You are not in a town.");
                return true;
            }

            Town town = resident.getTown();
            assert town != null;
            if (town.getMayor().equals(resident)) {

               boolean success = MetaDataHelper.removeWarp(town, name);
               if (success) {
                    TownyMessaging.sendMsg(player, "Warp removed successfully");
               } else {
                   TownyMessaging.sendErrorMsg(player, "Warp not found or an error occurred");
               }
                return success;

            } else {
               TownyMessaging.sendMsg(player,"You must be the town’s Mayor to run this.");
                return false;
            }

        } catch (NotRegisteredException e) {
           TownyMessaging.sendErrorMsg(player,"Towny data not found. Try again later.");
        }

        return true;
    }
}
