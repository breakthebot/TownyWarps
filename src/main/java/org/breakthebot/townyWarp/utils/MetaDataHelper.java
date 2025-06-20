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

import com.google.gson.Gson;
import org.breakthebot.townyWarp.TownyWarp;
import org.breakthebot.townyWarp.Warp;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class MetaDataHelper {
    private static final String WARP_METADATA_KEY = "town_warps";
    private static final Gson gson = new Gson();
    private static final Logger logger = TownyWarp.getInstance().getLogger();

    public static List<Warp> getTownWarps(Town town) {
        StringDataField warpData = (StringDataField) town.getMetadata(WARP_METADATA_KEY);
        if (warpData == null || warpData.getValue().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = new TypeToken<List<Warp>>(){}.getType();
            List<Warp> warps = gson.fromJson(warpData.getValue(), listType);
            return warps != null ? warps : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveTownWarps(Town town, List<Warp> warps) {
        try {
            String serializedWarps = gson.toJson(warps);
            town.addMetaData(new StringDataField(WARP_METADATA_KEY, serializedWarps));
            town.save();
        } catch (Exception e) {
            logger.severe("Failed to save warps for town " + town.getName() + ": " + e.getMessage());
        }
    }

    public static boolean addWarp(Town town, Warp warp) {
        List<Warp> warps = getTownWarps(town);

        if (warps.stream().anyMatch(w -> w.getName().equalsIgnoreCase(warp.getName()))) {
            return false;
        }

        warps.add(warp);
        saveTownWarps(town, warps);
        return true;
    }

    public static boolean removeWarp(Town town, String warpName) {
        List<Warp> warps = getTownWarps(town);
        boolean removed = warps.removeIf(w -> w.getName().equalsIgnoreCase(warpName));

        if (removed) {
            saveTownWarps(town, warps);
        }

        return removed;
    }

    public static Optional<Warp> getWarp(Town town, String warpName) {
        List<Warp> warps = getTownWarps(town);
        return warps.stream()
                .filter(w -> w.getName().equalsIgnoreCase(warpName))
                .findFirst();
    }

    public static boolean updateWarp(Town town, String warpName, Warp updatedWarp) {
        List<Warp> warps = getTownWarps(town);

        for (int i = 0; i < warps.size(); i++) {
            if (warps.get(i).getName().equalsIgnoreCase(warpName)) {
                warps.set(i, updatedWarp);
                saveTownWarps(town, warps);
                return true;
            }
        }

        return false;
    }

    public static boolean hasWarp(Town town, String warpName) {
        return getWarp(town, warpName).isPresent();
    }


    public static int getWarpCount(Town town) {
        return getTownWarps(town).size();
    }


    public static void clearWarps(Town town) {
        saveTownWarps(town, new ArrayList<>());
    }
}