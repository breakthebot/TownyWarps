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


import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompletion extends AddonCommand {

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // First argument: return list of all town names
            return TownyUniverse.getInstance().getTowns().stream()
                    .map(Town::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // Second argument: return warps from the specified town (args[0])
            try {
                Town town = TownyUniverse.getInstance().getTown(args[0]);
                if (town != null) {
                    return Arrays.asList(War.getTownWarps()); // Replace with actual warp names
                }
            } catch (NotRegisteredException ignored) {}
        }
        return Collections.emptyList();
    }
}
