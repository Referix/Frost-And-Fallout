package org.referix.lotusOffSeasonV2.database.hibernate.savezone;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;

import java.util.List;

public interface SaveZoneDataService {

    void saveProtectZone(String name, String location);

    void removeProtectZone(int id);

    List<SaveZoneData> getAllSaveZones();

    boolean isProtectZone(Location playerLocation);

    List<Pair<Location, Location>> getSaveZones();

}
