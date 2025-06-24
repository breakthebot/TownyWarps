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

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Resident;
import org.breakthebot.townyWarp.MetaData.MetaDataHelper;
import org.breakthebot.townyWarp.Warp;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.NoSuchElementException;

public class warp implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }

        switch (args[0]) {
            case "add" -> addWarp.onCommand(sender, command, label, args);
            case "remove" -> deleteWarp.onCommand(sender, command, label, args);
        }


        if (args.length == 2) {
            String townName = args[0];
            String warpName = args[1];

            TownyAPI townyApi = TownyAPI.getInstance();

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
                    townyApi.requestTeleport(player, loc, TownySettings.getTeleportWarmupTime());
                    return true;

                } else if (permLvl.name().equals("RESIDENT")) {
                    assert res != null;
                    if (!res.hasTown() || !res.getTown().getName().equals(townName)) {
                        TownyMessaging.sendErrorMsg(player, "This warp is only open for residents.");
                        return false;
                    }
                    Location loc = targetWarp.toLocation();
                    townyApi.requestTeleport(player, loc, TownySettings.getTeleportWarmupTime());
                    return true;
                }


            } catch (NoSuchElementException e) {
                TownyMessaging.sendErrorMsg(player, townName + " has no warp named " + warpName);
            } catch (NotRegisteredException e) {
                TownyMessaging.sendErrorMsg(player, townName + " does not exist");
            }

            return true;
        }

        if (args.length < 3) {
            TownyMessaging.sendErrorMsg(player, "Usage: /t warp <town> <name>");
            return false;
        }


        return false;
    }

}


