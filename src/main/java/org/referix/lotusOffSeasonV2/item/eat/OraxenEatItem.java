package org.referix.lotusOffSeasonV2.item.eat;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class OraxenEatItem extends EatItem {

    private final String oraxenId;

    public OraxenEatItem(String oraxenId, double radiationValue, double temperatureValue) {
        super(radiationValue, temperatureValue);
        this.oraxenId = oraxenId;
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

    public String getOraxenId() {
        return oraxenId;
    }
}
