package org.breakthebot.townyWarp.MetaData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.breakthebot.townyWarp.TownyWarp;
import org.breakthebot.townyWarp.Warp;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.Base64;

public final class MetaDataHelper {
    private static final Gson gson = new Gson();
    private static MetaDataHelper instance;
    private static final String WARP_METADATA_KEY = "town_warps";
    private static final Logger logger = TownyWarp.getInstance().getLogger();

    public static String getWarpMetadataKey() {
        return WARP_METADATA_KEY;
    }

    private MetaDataHelper() {}

    public static MetaDataHelper getInstance() {
        if (instance == null) {
            instance = new MetaDataHelper();
        }
        return instance;
    }

    public void setTownWarps(@NotNull Town town, @NotNull List<Warp> value) {
        String json = gson.toJson(value);
        String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        StringDataField field = new StringDataField(WARP_METADATA_KEY, base64);
        town.addMetaData(field);
    }

    public static List<Warp> getTownWarps(@NotNull Town town) {
        StringDataField warpData = (StringDataField) town.getMetadata(WARP_METADATA_KEY);
        if (warpData == null || warpData.getValue().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String base64 = warpData.getValue();
            String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Warp>>() {}.getType();
            List<Warp> warps = gson.fromJson(json, listType);
            return warps != null ? warps : new ArrayList<>();
        } catch (Exception e) {
            logger.warning("Failed to parse warp metadata for town " + town.getName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static int getWarpCount(@NotNull Town town) {
        return getTownWarps(town).size();
    }

    public static Optional<Warp> getWarp(@NotNull Town town, @NotNull String warpName) {
        return getTownWarps(town)
                .stream()
                .filter(w -> w.getName().equalsIgnoreCase(warpName))
                .findFirst();
    }

    public static boolean addWarp(@NotNull Town town, @NotNull Warp newWarp) {
        List<Warp> warps = getTownWarps(town);
        for (Warp w : warps) {
            if (w.getName().equalsIgnoreCase(newWarp.getName())) {
                return false;
            }
        }
        warps.add(newWarp);
        getInstance().setTownWarps(town, warps);
        return true;
    }

    public static boolean removeWarp(@NotNull Town town, @NotNull String warpName) {
        List<Warp> warps = getTownWarps(town);
        for (int i = 0; i < warps.size(); i++) {
            if (warps.get(i).getName().equalsIgnoreCase(warpName)) {
                warps.remove(i);
                getInstance().setTownWarps(town, warps);
                return true;
            }
        }
        return false;
    }

    public static boolean updateWarp(@NotNull Town town, @NotNull String warpName, @NotNull Warp updatedWarp) {
        List<Warp> warps = getTownWarps(town);
        for (int i = 0; i < warps.size(); i++) {
            if (warps.get(i).getName().equalsIgnoreCase(warpName)) {
                warps.set(i, updatedWarp);
                getInstance().setTownWarps(town, warps);
                return true;
            }
        }
        return false;
    }

    public static boolean hasWarp(@NotNull Town town, @NotNull String warpName) {
        return getWarp(town, warpName).isPresent();
    }
}
