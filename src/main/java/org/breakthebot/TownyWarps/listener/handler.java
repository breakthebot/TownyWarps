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

package org.breakthebot.TownyWarps.listener;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.event.teleport.CancelledTownyTeleportEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TeleportRequest;
import com.palmergames.bukkit.towny.object.Town;
import org.breakthebot.TownyWarps.MetaData.MetaDataHelper;
import org.breakthebot.TownyWarps.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class handler implements Listener {
    private final TownyAPI API = TownyAPI.getInstance();

    @EventHandler
    public void onNewDay(NewDayEvent event){
        for (Town town : API.getTowns()) {
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

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        Resident res = API.getResident(player);
        if (res != null && res.hasRequestedTeleport()) {
            API.abortTeleportRequest(res, CancelledTownyTeleportEvent.CancelledTeleportReason.DAMAGE);
            TownyMessaging.sendMsg(player, "Teleportation canceled because of damage.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (
                event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
        ) {
            Resident res = API.getResident(player);

            if (res != null && res.hasRequestedTeleport()) {
                API.abortTeleportRequest(res, CancelledTownyTeleportEvent.CancelledTeleportReason.MOVEMENT);
                TownyMessaging.sendErrorMsg(player, "Teleportation canceled because of movement.");
            }
        }
    }
}
