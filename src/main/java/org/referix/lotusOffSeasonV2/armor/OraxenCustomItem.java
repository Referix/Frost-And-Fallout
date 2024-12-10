package org.referix.lotusOffSeasonV2.armor;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenCustomItem implements CustomItem {
    private final String oraxenId;
    private final double temperatureResistance;
    private final double radiationResistance;

    public OraxenCustomItem(String oraxenId, double temperatureResistance, double radiationResistance) {
        this.oraxenId = oraxenId;
        this.temperatureResistance = temperatureResistance;
        this.radiationResistance = radiationResistance;
    }

    @Override
    public ItemStack getItemStack() {
        // Предполагаем, что Oraxen предоставляет метод для получения предмета
        return OraxenItems.getItemById(oraxenId).build();
    }

    @Override
    public double getTemperatureResistance() {
        return temperatureResistance;
    }

    @Override
    public double getRadiationResistance() {
        return radiationResistance;
    }
}

