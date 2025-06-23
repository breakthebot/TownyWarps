package org.breakthebot.townyWarp;

import org.breakthebot.townyWarp.utils.config;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWarp extends JavaPlugin {

    private static TownyWarp instance;
    private config conf;
    @Override
    public void onEnable() {
        instance = this;
        conf = new config(this);

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