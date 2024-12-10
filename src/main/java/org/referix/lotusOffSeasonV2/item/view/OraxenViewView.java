package org.referix.lotusOffSeasonV2.item.view;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class OraxenViewView extends ViewType {
    private final String oraxenId;

    public OraxenViewView(String oraxenId, String viewType) {
        super(viewType);
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