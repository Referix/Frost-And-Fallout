package org.referix.lotusOffSeasonV2.item.armor;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
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
        // Получение предмета через Oraxen API
        ItemBuilder itemBuilder = OraxenItems.getItemById(oraxenId);
        if (itemBuilder == null) {
            throw new IllegalArgumentException("Oraxen предмет с ID '" + oraxenId + "' не найден.");
        }
        return itemBuilder.build();
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

