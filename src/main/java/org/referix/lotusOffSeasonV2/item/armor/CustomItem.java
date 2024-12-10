package org.referix.lotusOffSeasonV2.item.armor;

import org.bukkit.inventory.ItemStack;

public interface CustomItem {
    ItemStack getItemStack(); // Отримання предмета
    double getTemperatureResistance(); // Отримання теплоємності
    double getRadiationResistance();
}
