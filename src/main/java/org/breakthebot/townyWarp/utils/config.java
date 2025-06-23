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

package org.breakthebot.townyWarp.utils;

import org.breakthebot.townyWarp.TownyWarp;
import org.bukkit.configuration.file.FileConfiguration;

public class config {
    private final TownyWarp plugin;
    private FileConfiguration cfg;

    public final int maxWarps;
    public final double firstWarpCost;
    public final double priceMultiplier;
    public final int minSize;
    public final int sizeMultiplier;

    public config(TownyWarp plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        plugin.saveDefaultConfig();
        this.maxWarps = cfg.getInt("maxWarps", 5);
        this.firstWarpCost = cfg.getDouble("firstWarpCost", 3);
        this.priceMultiplier = cfg.getDouble("warpPriceMultiplier", 2);
        this.minSize = cfg.getInt("minSize", -1);
        this.sizeMultiplier = cfg.getInt("minSizePerWarp", -1);
    }

}
