package org.referix.lotusOffSeasonV2.database.hibernate.savezone;

import jakarta.persistence.*;


@Entity
@Table(name = "save_zone_data")
public class SaveZoneData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "save_zone_id", nullable = false, unique = true)
    private int structureId;

    @Column(name = "save_zone_name", nullable = false)
    private String structureName;

    @Column(name = "save_zone_location", nullable = false)
    private String locationValue;

    // Конструктори, геттери та сеттери

    public SaveZoneData() {
    }
    public SaveZoneData(String name, String locationValue) {
        this.structureName = name;
        this.locationValue = locationValue;
    }

    public String getStructureName() {
        return structureName;
    }

    public String getLocationValue() {
        return locationValue;
    }

    public int getStructureId() {
        return structureId;
    }

}
