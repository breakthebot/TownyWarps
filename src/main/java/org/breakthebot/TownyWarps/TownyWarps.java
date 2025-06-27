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

package org.breakthebot.TownyWarps;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import org.breakthebot.TownyWarps.TownyAdmin.command;
import org.breakthebot.TownyWarps.commands.warp;
import org.breakthebot.TownyWarps.commands.warps;
import org.breakthebot.TownyWarps.listener.handler;
import org.breakthebot.TownyWarps.utils.config;
import org.bukkit.plugin.java.JavaPlugin;


public final class TownyWarps extends JavaPlugin {
    private static TownyWarps instance;
    private config conf;


    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        conf = new config(this);

        AddonCommand warpCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "warp", new warp());
        TownyCommandAddonAPI.addSubCommand(warpCommand);
        AddonCommand warpsCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "warps", new warps());
        TownyCommandAddonAPI.addSubCommand(warpsCommand);

        AddonCommand warpsAdminCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWNYADMIN, "warps", new command());
        TownyCommandAddonAPI.addSubCommand(warpsAdminCommand);

        getServer().getPluginManager().registerEvents(new handler(), this);

        getLogger().info("TownyWarp has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TownyWarp has been disabled!");
        instance = null;
    }



    public config getConf() { return this.conf; }

    public static TownyWarps getInstance() {
        return instance;
    }
}