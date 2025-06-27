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
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class listWarp {
    public static boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            TownyMessaging.sendErrorMsg(sender, "Only players may use this command.");
            return false;
        }
        if (!player.hasPermission("townywarps.warp.use")) {
            TownyMessaging.sendErrorMsg(player, "You are not permitted to perform this command.");
            return false;
        }
        TownyAPI townyApi = TownyAPI.getInstance();
        String townName;
        if (args.length == 2) {

            townName = String.valueOf(townyApi.getTown(args[1]));
        }
        else townName = townyApi.getTownName(player);

        List<String> warpNames = MetaDataHelper.getTownWarps(townName).stream().map(Warp::getName).toList();
        String warpList = String.join(", ", warpNames);

        TownyMessaging.sendMsg(player, "&b" + townName + "Warps: \n" + warpList);
        return true;
    }

}
