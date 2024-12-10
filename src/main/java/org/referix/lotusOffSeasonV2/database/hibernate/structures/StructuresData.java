package org.referix.lotusOffSeasonV2.database.hibernate.structures;


import jakarta.persistence.*;

@Entity
@Table(name = "structure_data")
public class StructuresData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "structure_id", nullable = false, unique = true)
    private int structureId;

    @Column(name = "structure_name", nullable = false)
    private String structureName;

    @Column(name = "location_value", nullable = false)
    private String locationValue;

    @Column(name = "isRadiation")
    private double isRadiation;


    // Конструктори, геттери та сеттери

    public StructuresData() {
    }
    public StructuresData( String name, String locationValue, double isRadiation) {
        this.structureName = name;
        this.locationValue = locationValue;
        this.isRadiation = isRadiation;
    }

    public double getRadiation() {
        return isRadiation;
    }


    public String getStructureName() {
        return structureName;
    }

    public String getLocationValue() {
        return locationValue;
    }

    public void setLocationValue(String locationValue) {
        this.locationValue = locationValue;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }
}
