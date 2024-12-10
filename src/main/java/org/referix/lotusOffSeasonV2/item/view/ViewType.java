package org.referix.lotusOffSeasonV2.item.view;

import org.bukkit.inventory.ItemStack;


public abstract class ViewType {
    private final String viewType;

    protected ViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getViewType() {
        return viewType;
    }

    public abstract ItemStack getItemStack();
}


