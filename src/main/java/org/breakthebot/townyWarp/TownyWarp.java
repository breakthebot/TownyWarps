package org.breakthebot.townyWarp;

import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWarp extends JavaPlugin {

    private static TownyWarp instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("TownyWarp has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TownyWarp has been disabled!");
        instance = null;
    }

    public static TownyWarp getInstance() {
        return instance;
    }
}