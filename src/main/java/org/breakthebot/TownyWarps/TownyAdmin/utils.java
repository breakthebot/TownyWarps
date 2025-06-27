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
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.TownyWarps;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class utils {

    public static boolean addWarp(CommandSender sender, String townName, String[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }
        TownyAPI API = TownyAPI.getInstance();
        Town town = API.getTown(townName);
        String name = args[0];
        String permLevel = args[1];

        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Town not found.");
            return false;
        }

        if (!(permLevel.equalsIgnoreCase("resident") || permLevel.equalsIgnoreCase("outsider"))){
            TownyMessaging.sendErrorMsg(player, "Invalid Permission level. You may only choose resident or outsider.");
            return false;
        }
        if (!name.matches("[A-Za-z0-9_]{1,16}")) {
            TownyMessaging.sendErrorMsg(player, "Names may only contain letters, numbers, and underscores, up to 16 chars.");
            return false;
        }

        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(player.getLocation());
        if (townBlock == null) {
            TownyMessaging.sendErrorMsg(player, "You cannot create warps in wilderness.");
            return false;
        }
        if (townBlock.getTownOrNull() != town) {
            TownyMessaging.sendErrorMsg(player, "You cannot create warps outside of selected town.");
            return false;
        }

        if (TownyWarps.getInstance().getConf().maxWarps == MetaDataHelper.getWarpCount(town)){
            TownyMessaging.sendErrorMsg(player, "Warning! This town has already reached the allowed limit of warps on this server.");
            TownyMessaging.sendMsg(player, "Warp will be created regardless due to TownyAdmin privileges.");
        }

        double bal = town.getAccount().getHoldingBalance();
        int next = Warp.calculateWarpCost(MetaDataHelper.getWarpCount(town) + 1);

        if (next > bal){
            TownyMessaging.sendErrorMsg(player, "Town does not have enough gold for this warp. Amount needed: " + next);
            return false;
        }

        if (MetaDataHelper.hasWarp(town, name)){
            TownyMessaging.sendErrorMsg(player, "A warp with that name already exists.");
            return false;
        }

        Warp warp = new Warp(
                name,
                player.getLocation(),
                sender.getName()
        );

        warp.setPermLevel(Warp.AccessLevel.valueOf(permLevel.toUpperCase()));

        boolean success = MetaDataHelper.addWarp(town, warp);
        if (success) {
            TownyMessaging.sendMsg(player, "Warp added successfully");
            town.getAccount().withdraw(next, "New Warp creation");
        } else {
            TownyMessaging.sendErrorMsg(player, "Warp not added; An error occurred");
        }
        return success;
    }

    public static boolean removeWarp(CommandSender sender, String townName,  String[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }
        TownyAPI API = TownyAPI.getInstance();
        Town town = API.getTown(townName);
        String name = args[0];

        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Town not found.");
            return false;
        }

        boolean success = MetaDataHelper.removeWarp(town, name);
        if (success) {
            TownyMessaging.sendMsg(player, "Warp removed successfully");
        } else {
            TownyMessaging.sendErrorMsg(player, "Warp not removed; An error occurred");
        }
        return success;
    }

    public static boolean levelWarp(CommandSender sender, String townName, String[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }
        TownyAPI API = TownyAPI.getInstance();
        Town town = API.getTown(townName);
        String name = args[0];
        Warp.AccessLevel permLevel = Warp.AccessLevel.valueOf(args[1].toUpperCase());

        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Town not found.");
            return false;
        }
        Optional<Warp> optionalWarp = MetaDataHelper.getWarp(town, name);
        if (optionalWarp.isEmpty()) {
            TownyMessaging.sendErrorMsg(player, "Warp not found.");
            return false;
        }
        Warp warp = optionalWarp.get();
        warp.setPermLevel(permLevel);

        boolean success = MetaDataHelper.updateWarp(town, warp.getName(), warp);
        if (success) {
            TownyMessaging.sendMsg(player, "Warp modified successfully.");
        } else {
            TownyMessaging.sendErrorMsg(player, "Warp not modified; An error occurred.");
        }
        return success;
    }

    public static boolean renameWarp(CommandSender sender, String townName, String[] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }
        TownyAPI API = TownyAPI.getInstance();
        Town town = API.getTown(townName);
        String oldName = args[0];
        String newName = args[1];

        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Town not found.");
            return false;
        }
        Optional<Warp> optionalWarp = MetaDataHelper.getWarp(town, oldName);
        if (optionalWarp.isEmpty()) {
            TownyMessaging.sendErrorMsg(player, "Warp not found.");
            return false;
        }
        Warp warp = optionalWarp.get();
        warp.setName(newName);

        boolean success = MetaDataHelper.updateWarp(town, oldName, warp);
        if (success) {
            TownyMessaging.sendMsg(player, "Warp modified successfully.");
        } else {
            TownyMessaging.sendErrorMsg(player, "Warp not modified; An error occurred.");
        }
        return success;
    }
}
