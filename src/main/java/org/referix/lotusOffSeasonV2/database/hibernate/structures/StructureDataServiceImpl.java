package org.referix.lotusOffSeasonV2.database.hibernate.structures;


import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;

import java.util.List;

public class StructureDataServiceImpl implements StructureDataService {


    private final StructureDataRepository structureDataRepository = new StructureDataRepository();
    @Override
    public void structureSave(String structureName, String location, double isRadiation) {
        StructuresData structuresData = new StructuresData(structureName,location, isRadiation);
        structureDataRepository.saveStructureData(structuresData);
    }

    @Override
    public void structureSave(String structureName, String location) {
        StructuresData structuresData = new StructuresData(structureName,location,0);
        structureDataRepository.saveStructureData(structuresData);
    }


    @Override
    public int getAllStructuresDataByName(String name) {
       List<StructuresData> arrayList = structureDataRepository.getAllStructuresDataByName(name);
       return arrayList.size();
    }


    @Override
    public List<Triple<Location, Location, Double>> getRadiationZones() {
        return structureDataRepository.getRadiationZones();
    }

    @Override
    public List<StructuresData> getAllStructuresData() {
        return structureDataRepository.getAllStructuresData();
    }

}
