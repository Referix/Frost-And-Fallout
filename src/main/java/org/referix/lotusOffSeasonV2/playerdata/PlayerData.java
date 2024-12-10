package org.referix.lotusOffSeasonV2.playerdata;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

public class PlayerData {
    private final Player player;

    // Радіація
    private double radiationResistance;
    private double radiationValue;

    // Зима
    private double temperatureResistance;
    private double temperatureValue;

//    public PlayerData(Player player, double radiationSpeed,
//                      double radiationResistance, double radiationValue,
//                       double temperatureResistance, double temperatureValue) {
//        this.player = player;
//        this.radiationSpeed = radiationSpeed;
//        this.radiationResistance = radiationResistance;
//        this.radiationValue = radiationValue;
//        this.temperatureResistance = temperatureResistance;
//        this.temperatureValue = temperatureValue;
//    }

    public PlayerData(Player player) {
            this.player = player;
            this.radiationResistance = 0;
            this.radiationValue = 0;
            this.temperatureResistance = 0;
            this.temperatureValue = 0;
    }



    public void setRadiationResistance(double radiationResistance) {
        this.radiationResistance = radiationResistance;
    }

    public void setRadiationValue(double radiationValue) {
        player.setMetadata("radiationValue", new FixedMetadataValue(LotusOffSeasonV2.getInstance(), Math.max(radiationValue, 0)));
        this.radiationValue = radiationValue;
    }


    public void setTemperatureResistance(double temperatureResistance) {
        this.temperatureResistance = temperatureResistance;
    }

    public void setTemperatureValue(double temperatureValue) {
        player.setMetadata("temperatureValue", new FixedMetadataValue(LotusOffSeasonV2.getInstance(), Math.max(temperatureValue, 0)));
        this.temperatureValue = temperatureValue;
    }



    public double getRadiationResistance() {
        return radiationResistance;
    }

    public double getRadiationValue() {
        return radiationValue;
    }


    public double getTemperatureResistance() {
        return temperatureResistance;
    }

    public double getTemperatureValue() {
        return temperatureValue;
    }

}
