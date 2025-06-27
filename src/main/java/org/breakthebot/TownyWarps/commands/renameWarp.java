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

public class renameWarp {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        TownyAPI API = TownyAPI.getInstance();

        if (args.length != 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warps rename <warp> <name>");
            return false;
        }

        Resident res = API.getResident(player);
        assert res != null;

        if (!res.hasTown()) {
            TownyMessaging.sendErrorMsg(player, "You must be part of a town.");
            return false;
        }

        String name = args[1];
        String newName = args[2];

        if (!player.hasPermission("townywarps.warp.rename")) {
            TownyMessaging.sendErrorMsg(player, "You do not have permission to perform this command.");
            return false;
        }

        if (!newName.matches("[A-Za-z0-9_]{1,16}")) {
            TownyMessaging.sendErrorMsg(player, "Invalid name. Names may only contain letters, numbers, and underscores, up to 16 chars.");
            return false;
        }


        Town town = API.getTown(player);
        assert town != null;
        if (!MetaDataHelper.hasWarp(town, name)) {
            TownyMessaging.sendErrorMsg(player, "Warp not found");
            return false;
        }

        if (MetaDataHelper.hasWarp(town, newName)) {
            TownyMessaging.sendErrorMsg(player, "A warp with this name exists already");
            return false;
        }
        if (name.equals(newName)) {
            TownyMessaging.sendErrorMsg(player, "No name change detected");
            return false;
        }

        Optional<Warp> optionalWarp = MetaDataHelper.getWarp(town, name);
        if (optionalWarp.isPresent()) {
            Warp warp = optionalWarp.get();
            warp.setName(newName);
            MetaDataHelper.updateWarp(town, name, warp);
            TownyMessaging.sendMsg(player, "Warp successfully renamed");
            return true;
        }




        return false;
    }
}
