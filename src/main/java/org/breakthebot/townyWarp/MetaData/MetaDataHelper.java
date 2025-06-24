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

package org.breakthebot.townyWarp.MetaData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.breakthebot.townyWarp.TownyWarp;
import org.breakthebot.townyWarp.Warp;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public final class MetaDataHelper {
    private static final Gson gson = new Gson();
    private static MetaDataHelper instance;
    private static final String WARP_METADATA_KEY = "town_warps";
    private static final Logger logger = TownyWarp.getInstance().getLogger();

    public static final String Warp_LIST_KEY = WARP_METADATA_KEY + "warp_list";

    private MetaDataHelper() {}

    public static MetaDataHelper getInstance() {
        if (instance == null) {
            instance = new MetaDataHelper();
        }
        return instance;
    }

    public void setWarpList(@NotNull Town town, @NotNull List<Warp> value) {
        WarpListDataField field = new WarpListDataField(Warp_LIST_KEY, value);
        town.addMetaData(field);
    }

    public @NotNull List<Warp> getWarpList(@NotNull Town town) {
        WarpListDataField field = (WarpListDataField) town.getMetadata(Warp_LIST_KEY);
        if (field == null || field.getValue() == null) {
            return new CopyOnWriteArrayList<>();
        }
        return field.getValue();

    }
    public static boolean updateWarp(@NotNull Town town, @NotNull String warpName, @NotNull Warp updatedWarp) {
        List<Warp> warps = getInstance().getWarpList(town);
        for (int i = 0; i < warps.size(); i++) {
            if (warps.get(i).getName().equalsIgnoreCase(warpName)) {
                warps.set(i, updatedWarp);
                getInstance().setWarpList(town, warps);
                return true;
            }
        }
        return false;
    }


    public static boolean hasWarp(@NotNull Town town, @NotNull String warpName) {
        return getWarp(town, warpName).isPresent();
    }

    public static int getWarpCount(@NotNull Town town) {
        return getInstance().getWarpList(town).size();
    }

    public static Optional<Warp> getWarp(@NotNull Town town, @NotNull String warpName) {
        return getInstance()
                .getWarpList(town)
                .stream()
                .filter(w -> w.getName().equalsIgnoreCase(warpName))
                .findFirst();
    }

    public static List<Warp> getTownWarps(@NotNull Town town) {
        StringDataField warpData = (StringDataField) town.getMetadata(WARP_METADATA_KEY);
        if (warpData == null || warpData.getValue().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = new TypeToken<List<Warp>>(){}.getType();
            List<Warp> warps = gson.fromJson(warpData.getValue(), listType);
            return warps != null ? warps : new ArrayList<>();
        } catch (Exception e) {
            logger.warning("Failed to parse warp metadata for town " + town.getName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public static boolean removeWarp(@NotNull Town town, @NotNull String warpName) {
        List<Warp> warps =  getTownWarps(town);
        for (int i = 0; i < warps.size(); i++) {
            if (warps.get(i).getName().equalsIgnoreCase(warpName)) {
                warps.remove(i);
                getInstance().setWarpList(town, warps);
                return true;
            }
        }
        return false;
    }

    public static boolean addWarp(@NotNull Town town, @NotNull Warp newWarp) {
        List<Warp> warps = getInstance().getWarpList(town);
        for (Warp w : warps) {
            if (w.getName().equalsIgnoreCase(newWarp.getName())) {
                return false;
            }
        }
        warps.add(newWarp);
        getInstance().setWarpList(town, warps);
        return true;
    }
}
