package org.referix.lotusOffSeasonV2.item.eat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StandardEatItem extends EatItem {
    private final Material material;
    private final String displayName;
    private final List<String> lore;

    public StandardEatItem(Material material, String displayName, List<String> lore, double radiationValue, double temperatureValue) {
        super(radiationValue, temperatureValue);
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    @Override
    public ItemStack getItemStack() {
        if (material == null) return null;
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
