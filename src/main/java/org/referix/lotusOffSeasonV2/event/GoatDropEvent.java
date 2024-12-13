package org.referix.lotusOffSeasonV2.event;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.referix.lotusOffSeasonV2.utils.OraxenUtils;

import java.util.Random;

public class GoatDropEvent implements Listener {

    @EventHandler
    public void onGoatDeath(EntityDeathEvent event) {
        if (!OraxenUtils.isOraxenEnable()) { return; }
        if (new Random().nextDouble() >= 0.1) {
            System.out.println("Шанс не сработал, скипаем");
            return;
        }
        if (event.getEntity().getType() == EntityType.GOAT) {
            String oraxenItemId = "goat_fur";
            ItemStack oraxenItem = OraxenItems.getItemById(oraxenItemId) != null
                    ? OraxenItems.getItemById(oraxenItemId).build()
                    : null;

            if (oraxenItem != null) {
                event.getDrops().add(oraxenItem);
            }
        }
    }
}
