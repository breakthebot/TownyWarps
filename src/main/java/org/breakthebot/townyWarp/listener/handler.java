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

package org.breakthebot.townyWarp.listener;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.townyWarp.MetaData.MetaDataHelper;
import org.breakthebot.townyWarp.Warp;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class handler implements Listener {

    @EventHandler
    public void onNewDay(NewDayEvent event){
        for (Town town : TownyAPI.getInstance().getTowns()) {
            List<Warp> warps = MetaDataHelper.getTownWarps(town);
            int warpCount = warps.size();
            double balance = town.getAccount().getHoldingBalance();
            int cost = Warp.calculateTotalCost(warpCount);
            if (balance < cost){
                while (warpCount > 0 && Warp.calculateTotalCost(warpCount) > balance) {
                    warps.remove(warpCount - 1);
                    warpCount--;
                    TownyMessaging.sendPrefixedTownMessage(town, "Your town has lost some warps due to insufficient funds.");
                }
                MetaDataHelper.getInstance().setTownWarps(town, warps);
                return;
            }

            town.getAccount().withdraw(cost, "Warp cost");
        }
    }
}
