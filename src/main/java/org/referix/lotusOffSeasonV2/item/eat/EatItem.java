package org.referix.lotusOffSeasonV2.item.eat;

import org.bukkit.inventory.ItemStack;

public abstract class EatItem {

    private final double radiationValue;
    private final double temperatureValue;

    public EatItem(double radiationValue, double temperatureValue) {
        this.radiationValue = radiationValue;
        this.temperatureValue = temperatureValue;
    }

    public double getRadiationValue() {
        return radiationValue;
    }

    public double getTemperatureValue() {
        return temperatureValue;
    }

    public abstract ItemStack getItemStack();
}
