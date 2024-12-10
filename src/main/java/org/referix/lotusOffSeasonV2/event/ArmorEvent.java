package org.referix.lotusOffSeasonV2.event;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.referix.lotusOffSeasonV2.armor.CustomItem;
import org.referix.lotusOffSeasonV2.armor.CustomItemManager;
import org.referix.lotusOffSeasonV2.playerdata.PlayerManager;

public class ArmorEvent implements Listener {
    private final CustomItemManager customItemManager;

    public ArmorEvent(CustomItemManager customItemManager) {
        this.customItemManager = customItemManager;
    }

    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        onArmorChanged(player);
    }

    public void onArmorChanged(Player player) {
        double temperatureResistance = 0;

        ItemStack[] armorContents = player.getInventory().getArmorContents(); // Получаем предметы брони

        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null) continue;

            // Проверяем, зарегистрирован ли предмет в CustomItemManager
            CustomItem customItem = customItemManager.getCustomItemByItemStack(armorPiece);
            if (customItem != null) {
                temperatureResistance += customItem.getTemperatureResistance();
            }
        }

        // Устанавливаем сопротивление для игрока
        PlayerManager.getInstance().getPlayerData(player).setTemperatureResistance(temperatureResistance);
        System.out.println("Сопротивление температуре: " + temperatureResistance);
    }
}
