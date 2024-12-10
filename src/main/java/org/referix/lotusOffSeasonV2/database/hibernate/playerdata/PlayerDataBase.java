package org.referix.lotusOffSeasonV2.database.hibernate.playerdata;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_data")
public class PlayerDataBase {

    @Id
    @Column(name = "player_name", nullable = false, unique = true)
    private String playerName;

    @Column(name = "freeze_value", nullable = false)
    private double freezeValue;

    @Column(name = "radiation_value", nullable = false)
    private double radiationValue;

    // Конструктори, геттери та сеттери
    public PlayerDataBase() {
    }

    public PlayerDataBase(String playerName, double freezeValue, double radiationValue) {
        this.playerName = playerName;
        this.freezeValue = freezeValue;
        this.radiationValue = radiationValue;
    }

    public double getFreezeValue() {
        return freezeValue;
    }

    public void setFreezeValue(double freezeValue) {
        this.freezeValue = freezeValue;
    }

    public double getRadiationValue() {
        return radiationValue;
    }

    public void setRadiationValue(double radiationValue) {
        this.radiationValue = radiationValue;
    }
}
