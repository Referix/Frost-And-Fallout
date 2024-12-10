package org.referix.lotusOffSeasonV2.item.armor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StandardArmor implements CustomItem{
    private final ItemStack itemStack;
    private final double temperatureResistance;
    private final double radiationResistance;

    public StandardArmor(Material material, String displayName, List<String> lore, double temperatureResistance, double radiationResistance) {
        this.itemStack = new ItemStack(material);
        this.temperatureResistance = temperatureResistance;
        this.radiationResistance = radiationResistance;

        ItemMeta meta = this.itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            this.itemStack.setItemMeta(meta);
        }
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack.clone();
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
