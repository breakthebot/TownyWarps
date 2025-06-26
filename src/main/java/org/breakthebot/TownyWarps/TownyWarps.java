package org.breakthebot.TownyWarps;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import org.breakthebot.TownyWarps.commands.warp;
import org.breakthebot.TownyWarps.listener.handler;
import org.breakthebot.TownyWarps.utils.config;
import org.bukkit.plugin.java.JavaPlugin;


public final class TownyWarps extends JavaPlugin {
    private static TownyWarps instance;
    private config conf;


    @Override
    public void onEnable() {
        instance = this;
        conf = new config(this);

        AddonCommand warpCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "warp", new warp());
        TownyCommandAddonAPI.addSubCommand(warpCommand);
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