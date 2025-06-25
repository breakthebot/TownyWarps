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

package org.breakthebot.TownyWarps.utils;

import org.breakthebot.TownyWarps.TownyWarps;
import org.bukkit.configuration.file.FileConfiguration;

public class config {

    public final int maxWarps;
    public final int firstWarpCost;
    public final float priceMultiplier;

    public config(TownyWarps plugin) {
        FileConfiguration cfg = plugin.getConfig();
        this.maxWarps = cfg.getInt("maxWarps", 5);
        this.firstWarpCost = cfg.getInt("firstWarpCost", 3);
        this.priceMultiplier = (float) cfg.getDouble("warpPriceMultiplier", 2);
    }

}
