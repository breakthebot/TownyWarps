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
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class levelWarp {

    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        TownyAPI API = TownyAPI.getInstance();

        if (args.length != 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warps level <name> <permLevel>");
            return false;
        }

        Resident res = API.getResident(player);
        assert res != null;

        if (!res.hasTown()) {
            TownyMessaging.sendErrorMsg(player, "You must be part of a town.");
            return false;
        }

        String name = args[1];
        String permLevel = args[2];

        if (!(permLevel.equalsIgnoreCase("resident") || permLevel.equalsIgnoreCase("outsider"))) {
            TownyMessaging.sendErrorMsg(player, "Invalid Permission level. You may only choose resident or outsider.");
            return false;
        }

        if (!player.hasPermission("townywarps.warp.level")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
            return false;
        }


        Town town = API.getTown(player);
        assert town != null;
        if (!MetaDataHelper.hasWarp(town, name)) {
            TownyMessaging.sendErrorMsg(player, "Warp not found");
            return false;
        }

        Optional<Warp> optionalWarp = MetaDataHelper.getWarp(town, name);
        if (optionalWarp.isPresent()) {
            Warp warp = optionalWarp.get();
            warp.setPermLevel(Warp.AccessLevel.valueOf(permLevel.toUpperCase()));
            MetaDataHelper.updateWarp(town, warp.getName(), warp);
            TownyMessaging.sendMsg(player, "Warp permLevel successfully updated");
            return true;
        }




        return false;
    }
}