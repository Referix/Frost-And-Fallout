package org.referix.lotusOffSeasonV2.playerdata;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;
import org.referix.lotusOffSeasonV2.handlers.RadiationHandler;
import org.referix.lotusOffSeasonV2.handlers.TemperatureHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayerManager {

    private static final PlayerManager instance = new PlayerManager();
    private Map<Player, PlayerData> playerDataMap = new HashMap<>();

    private BukkitTask globalTask;

    public static PlayerManager getInstance() {
        return instance;
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player, PlayerData::new);
    }

    public void removePlayerData(Player player) {
        playerDataMap.remove(player);
    }


    public void startGlobalTask() {
        globalTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()){
                    PlayerData data = getPlayerData(player);
                    //winter
                    double temperatureValue = data.getTemperatureValue();
                    double temperatureResistance = data.getTemperatureResistance();
                    temperatureValue = TemperatureHandler.getInstance().calculate(player,temperatureValue,temperatureResistance);
                    data.setTemperatureValue(temperatureValue);
                    System.out.println("Winter: " + temperatureValue + " "  + temperatureResistance);

                    //radiation
                    double radiationValue = data.getRadiationValue();
                    double radiationResistance = data.getRadiationResistance();
                    radiationValue = RadiationHandler.getInstance().calculate(player,radiationValue,radiationResistance);
                    data.setRadiationValue(radiationValue);
                    System.out.println("Radiation: " + radiationValue + " "  + radiationResistance);


                    TemperatureHandler.getInstance().applyDamageEffect(player,temperatureValue);
                    RadiationHandler.getInstance().applyDamageEffect(player,radiationValue);

                }
            }
        }.runTaskTimer(LotusOffSeasonV2.getInstance(), 0, 20);
    }




    public BukkitTask getGlobalTask() {
        return globalTask;
    }





}
