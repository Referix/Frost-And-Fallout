package org.referix.lotusOffSeasonV2.playerdata;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;
import org.referix.lotusOffSeasonV2.handlers.RadiationHandler;
import org.referix.lotusOffSeasonV2.handlers.TemperatureHandler;
import org.referix.lotusOffSeasonV2.item.CustomItemManager;
import org.referix.lotusOffSeasonV2.item.view.ViewType;

public class PlayerBar {

    private final LotusOffSeasonV2 plugin;
    private final CustomItemManager itemManager;

    public PlayerBar(LotusOffSeasonV2 plugin, CustomItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        startActionBarTask();
    }

    private void startActionBarTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateActionBar(player);
                }
            }
        }.runTaskTimer(plugin, 0, 20); // Выполнять каждую секунду
    }

    private void updateActionBar(Player player) {
        ViewType customItem = itemManager.getViewItemByItemStack(player.getInventory().getItemInMainHand());

        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
        double radiation = playerData.getRadiationValue();
        double temperature = playerData.getTemperatureValue();

        String radiationBar = "";
        String temperatureBar = "";

        if (customItem != null) {

            if ("RADIATION".equalsIgnoreCase(customItem.getViewType())) {
                radiationBar = RadiationHandler.getInstance().createProgressBar(radiation,"Радиация:");
            }

            else if ("TEMPERATURE".equalsIgnoreCase(customItem.getViewType())) {
                temperatureBar = TemperatureHandler.getInstance().createProgressBar(temperature, "Температура",true);
            }
        }

        if (radiationBar.isEmpty() && temperatureBar.isEmpty()) {
            temperatureBar = TemperatureHandler.getInstance().createProgressBar(temperature, "Температура",false);
        }

        String actionBarMessage = String.join("  ", radiationBar, temperatureBar).trim();

        player.sendActionBar(Component.text(actionBarMessage));
    }



}
