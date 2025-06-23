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

package org.breakthebot.townyWarp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Bukkit;

import java.util.Objects;


public class Warp {
    private String name;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private long createdAt;
    private String createdBy;
    private AccessLevel permLevel = AccessLevel.RESIDENT;
    public Warp() {}

    public Warp(String name, Location location, String createdBye) {
        this.name = name;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.createdBy = createdBy;
        this.createdAt = System.currentTimeMillis();
    }

    public enum AccessLevel { RESIDENT, OUTSIDER }

    public String getName() { return name; }
    public String getWorldName() { return worldName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public long getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public AccessLevel getPermLevel() {return this.permLevel;}

    public void setPermLevel(AccessLevel perm) {this.permLevel = perm;}

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Warp warp = (Warp) obj;
        return Objects.equals(name, warp.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Warp{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", AccessLevel=" + permLevel +
                '}';

    }
    public static int calculateTotalCost(int warpCount){
        int base = TownyWarp.getInstance().getConf().firstWarpCost;
        int total = base;
        int lastCost = base;
        int result;
        for(int i = 2; i <= warpCount; i++){
            result = (int) Math.ceil(lastCost * TownyWarp.getInstance().getConf().priceMultiplier);
            total += result;
            lastCost = result;
        }
        return total;
    }

    public static int calculateWarpCost(int warpNumber){
        int base = TownyWarp.getInstance().getConf().firstWarpCost;
        int result = base;
        int lastCost = base;

        for(int i = 2; i <= warpNumber; i++){
            result = (int) Math.ceil(lastCost * TownyWarp.getInstance().getConf().priceMultiplier);
            lastCost = result;
        }
        return result;
    }
}