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
import org.breakthebot.townyWarp.Warp;
import org.breakthebot.townyWarp.utils.MetaDataHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class warp implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args){
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <name>");
            return false;
        }
        String townName = args[0];
        String warpName = args[1];
        if (!townName.matches("[A-Za-z0-9_]{1,16}") && warpName.matches("[A-Za-z0-9_]{1,16}")) {
            sender.sendMessage(ChatColor.RED + "Invalid name! Names may only contain letters, numbers, and underscores, up to 16 chars.");
            return true;
        }
        try {
            Warp targetWarp = MetaDataHelper.getWarp(TownyAPI.getInstance().getTown(townName), warpName).orElseThrow();
            Warp.AccessLevel permLvl = targetWarp.getPermLevel();


        } catch (NoSuchElementException e) {
            sender.sendMessage(townName + " has no warp named " + warpName);
        }

        return true;
    }
}
