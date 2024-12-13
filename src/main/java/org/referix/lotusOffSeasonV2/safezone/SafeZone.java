package org.referix.lotusOffSeasonV2.safezone;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;

public class SafeZone {

    private String zoneName;
    private Pair<Location,Location> locationPair;
    private int zoneID;

    public SafeZone(String zoneName, Pair<Location, Location> locationPair) {
        this.zoneName = zoneName;
        this.locationPair = locationPair;

    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void setZoneID(int zoneID) {
        this.zoneID = zoneID;
    }

    public Pair<Location, Location> getLocationPair() {
        return locationPair;
    }

    public void setLocationPair(Pair<Location, Location> locationPair) {
        this.locationPair = locationPair;
    }

    public String getZoneName() {
        return zoneName;
    }

    public int getZoneID() {
        return zoneID;
    }



}



