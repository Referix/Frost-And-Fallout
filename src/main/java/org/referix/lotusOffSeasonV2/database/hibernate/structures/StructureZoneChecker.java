package org.referix.lotusOffSeasonV2.database.hibernate.structures;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class StructureZoneChecker {

    private final List<Triple<Location, Location, Double>> radiationZones;

    public StructureZoneChecker(List<Triple<Location, Location, Double>> radiationZones) {
        this.radiationZones = radiationZones;
    }

    /**
     * Перевіряє, чи знаходиться гравець у зоні.
     * @param player Гравець для перевірки.
     * @return Значення Integer радіації, якщо гравець у зоні, інакше null.
     */
    public Double getPlayerZoneRadiation(Player player) {
        Location playerLocation = player.getLocation();
        for (Triple<Location, Location, Double> zone : radiationZones) {
            if (isLocationInZone(playerLocation, zone.getLeft(), zone.getMiddle())) {
                System.out.println(zone);
                return zone.getRight(); // Повертає рівень радіації
            }
        }
        return null; // Якщо гравець не у зоні
    }

    /**
     * Логіка перевірки, чи знаходиться точка в зоні.
     * @param location Локація для перевірки.
     * @param loc1 Перша точка зони.
     * @param loc2 Друга точка зони.
     * @return true, якщо точка знаходиться в зоні.
     */
    private boolean isLocationInZone(Location location, Location loc1, Location loc2) {
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int px = location.getBlockX();
        int py = location.getBlockY();
        int pz = location.getBlockZ();

        return (px >= x1 && px <= x2) &&
                (py >= y1 && py <= y2) &&
                (pz >= z1 && pz <= z2);
    }
}
