package org.breakthebot.TownyWarps.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Optional;

public class info {

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

        if (args.length != 3) {
            TownyMessaging.sendMsg("Usage: /t warps info <town> <warp>");
            return false;
        }

        String townName = args[1];
        String warpName = args[2];

        Town town = TownyAPI.getInstance().getTown(townName);
        if (town == null) {
            TownyMessaging.sendErrorMsg(player, "Town not found.");
            return false;
        }

        Optional<Warp> optionalWarp = MetaDataHelper.getWarp(town, warpName);
        if (optionalWarp.isEmpty()) {
            TownyMessaging.sendErrorMsg(player, "Warp not found.");
            return false;
        }

        Warp warp = optionalWarp.get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created = sdf.format(warp.getCreatedAt());

        TownyMessaging.sendMsg(player, "&bWarp Info for '" + warp.getName() + "'");
        TownyMessaging.sendMsg(player, "Created by: " + warp.getCreatedBy());
        TownyMessaging.sendMsg(player, "Created at: " + created);
        TownyMessaging.sendMsg(player, "Permission: " + warp.getPermLevel());
        return true;
    }
}
