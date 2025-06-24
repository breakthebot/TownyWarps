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


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import org.breakthebot.townyWarp.TownyWarp;
import org.breakthebot.townyWarp.Warp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;


public class WarpListDataField extends CustomDataField<List<Warp>> {
    private Gson gson = new Gson();

    private static final Logger LOGGER = TownyWarp.getInstance().getLogger();

    public WarpListDataField(String key, List<Warp> value, String label){
        super(key, value, label);
    }
    public static @NotNull String typeID() {
        return MetaDataHelper.Warp_LIST_KEY;
    }

    @Override
    public @NotNull String getTypeID() {
        return typeID();
    }

    public WarpListDataField(String key, List<Warp> value) {
        super(key, value);
    }

    @Override
    public void setValueFromString(String string) {
        Type WarpListType = new TypeToken<List<Warp>>(){}.getType();
        try {
            setValue(gson.fromJson(string, WarpListType));
        } catch (JsonSyntaxException e) {
            LOGGER.severe("Failed to set value of Warp list from string" + string);
            setValue(new CopyOnWriteArrayList<>());
        }
    }

    @Override
    protected String displayFormattedValue() {
        return getValue().toString();
    }

    @Override
    protected @Nullable String serializeValueToString() {
        String json = gson.toJson(getValue());
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    @Override
    public @NotNull CustomDataField<List<Warp>> clone() {
        return new WarpListDataField(getKey(), new CopyOnWriteArrayList<>(getValue()), hasLabel() ? getLabel() : null);
    }

}