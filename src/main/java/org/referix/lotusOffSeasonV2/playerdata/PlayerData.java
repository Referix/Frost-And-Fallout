package org.referix.lotusOffSeasonV2.playerdata;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;
import org.referix.lotusOffSeasonV2.handlers.RadiationHandler;
import org.referix.lotusOffSeasonV2.handlers.TemperatureHandler;

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
        double value = RadiationHandler.getInstance().clamped(radiationValue);
        player.setMetadata("radiationValue", new FixedMetadataValue(LotusOffSeasonV2.getInstance(), value));
        this.radiationValue = radiationValue;
    }


    public void setTemperatureResistance(double temperatureResistance) {
        this.temperatureResistance = temperatureResistance;
    }

    public void setTemperatureValue(double temperatureValue) {
        double value = TemperatureHandler.getInstance().clamped(temperatureValue);
        player.setMetadata("temperatureValue", new FixedMetadataValue(LotusOffSeasonV2.getInstance(), value));
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
