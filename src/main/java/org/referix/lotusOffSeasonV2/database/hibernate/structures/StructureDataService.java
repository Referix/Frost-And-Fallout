package org.referix.lotusOffSeasonV2.database.hibernate.structures;


import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;

import java.util.List;

public interface StructureDataService {
    void structureSave(String structureName, String location, double isRadiation);

    void structureSave(String structureName, String location);

    int getAllStructuresDataByName(String name);

    List<Triple<Location, Location, Double>> getRadiationZones();

    List<StructuresData> getAllStructuresData();
}
