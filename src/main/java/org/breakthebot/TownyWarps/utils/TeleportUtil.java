package org.breakthebot.TownyWarps.utils;
import com.palmergames.bukkit.towny.TownyAPI;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.breakthebot.TownyWarps.TownyWarps;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe manager for scheduling and cancelling player teleports,
 
 */
public class TeleportUtil {
    private static final TeleportUtil INSTANCE = new TeleportUtil();

    private final ConcurrentMap<UUID, ScheduledTask> pendingTeleports = new ConcurrentHashMap<>();

    private final RegionScheduler scheduler;

    private TeleportUtil() {
        this.scheduler = TownyWarps.getInstance().getServer().getRegionScheduler();
    }

    public static TeleportUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Teleport a player, copies Towny's teleport logic, because the methods are private.
     *
     * @param player     the player to teleport
     * @param target     the target location
     * @param delayTicks delay in server ticks
     */
    public void teleport(@NotNull Player player, @NotNull Location target, long delayTicks) {
        UUID playerId = player.getUniqueId();

        ScheduledTask existing = pendingTeleports.remove(playerId);
        if (existing != null) {
            existing.cancel();
        }

        ScheduledTask task = player.getScheduler().runDelayed(
                TownyWarps.getInstance(),
                (s) -> {
                    pendingTeleports.remove(playerId);
                    TownyAPI.getInstance().requestTeleport(player, target);
                },
                null,
                delayTicks
        );
        pendingTeleports.put(playerId, task);
    }

    /**
     * Cancel a pending teleport for the given player UUID.
     *
     * @param playerId the UUID of the player
     * @return true if a teleport was cancelled, false otherwise
     */
    public boolean cancelTeleport(@NotNull UUID playerId) {
        ScheduledTask task = pendingTeleports.remove(playerId);
        if (task != null) {
            task.cancel();
            return true;
        }
        return false;
    }

    /**
     * Provides an unmodifiable view of pending teleports. Safe for concurrent access.
     */
    public Map<UUID, ScheduledTask> getPendingTeleports() {
        return Collections.unmodifiableMap(pendingTeleports);
    }
}
