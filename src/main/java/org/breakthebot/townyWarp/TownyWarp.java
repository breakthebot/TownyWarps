package org.breakthebot.townyWarp;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.TownyObject;
import org.breakthebot.townyWarp.commands.addWarp;
import org.breakthebot.townyWarp.commands.warp;
import org.breakthebot.townyWarp.utils.config;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

public final class TownyWarp extends JavaPlugin {

    private static TownyWarp instance;
    private config conf;
    @Override
    public void onEnable() {
        instance = this;
        conf = new config(this);

        AddonCommand warpCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "warp", new warp());
//        warpCommand.setTabCompletion(0, TownyAPI.getInstance().getTowns().stream().map(TownyObject::getName).toList());
//        warpCommand.setTabCompletion(1, Arrays.asList("suggestions", "for", "second", "argument"));
        TownyCommandAddonAPI.addSubCommand(warpCommand);

//        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.TOWN, "warp", new warp());

        getLogger().info("TownyWarp has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TownyWarp has been disabled!");
        instance = null;
    }

    public config getConf() { return this.conf; }

    public static TownyWarp getInstance() {
        return instance;
    }
}