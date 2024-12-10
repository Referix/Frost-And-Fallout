package org.referix.lotusOffSeasonV2.database.hibernate.savezone;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;

import java.util.List;

public class SaveZoneDataServiceImpl implements SaveZoneDataService {

    private final SaveZoneDataRepository saveZoneDataRepository = new SaveZoneDataRepository();


    @Override
    public void saveProtectZone(String name, String location) {
        SaveZoneData saveZoneData = new SaveZoneData(name, location);
        saveZoneDataRepository.saveStructureData(saveZoneData);
    }

    @Override
    public void removeProtectZone(int id) {
        saveZoneDataRepository.removeProtectZone(id);
    }

    @Override
    public List<SaveZoneData> getAllSaveZones() {
        return saveZoneDataRepository.getAllStructuresData();
    }

    @Override
    public boolean isProtectZone(Location playerLocation) {
        List<Pair<Location, Location>> saveZoneDataZones = saveZoneDataRepository.getSaveZoneDataZones();
        for (Pair<Location, Location> zone : saveZoneDataZones) {
            if (isLocationInZone(playerLocation, zone.getLeft(), zone.getRight())) {
                return true;
            }
        }
        return false;
    }

    // Логіка перевірки, чи знаходиться точка в зоні
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

    @Override
    public List<Pair<Location, Location>> getSaveZones() {
        return saveZoneDataRepository.getSaveZoneDataZones();
    }
}
