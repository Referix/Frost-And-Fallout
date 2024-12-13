package org.referix.lotusOffSeasonV2.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.referix.lotusOffSeasonV2.database.hibernate.playerdata.PlayerDataBase;
import org.referix.lotusOffSeasonV2.database.hibernate.playerdata.PlayerDataService;
import org.referix.lotusOffSeasonV2.playerdata.PlayerData;
import org.referix.lotusOffSeasonV2.playerdata.PlayerManager;

public class JoinLeaveEvent implements Listener {
    PlayerDataService playerDataService;

    public JoinLeaveEvent(PlayerDataService playerDataService) {
        this.playerDataService = playerDataService;
    }

    @EventHandler
    public void playerDeath(PlayerRespawnEvent e){
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(e.getPlayer());
        playerData.setRadiationValue(0);
        playerData.setTemperatureValue(20);
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent e){
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(e.getPlayer());
        playerDataService.savePlayer(e.getPlayer().getName(),playerData.getTemperatureValue(),playerData.getRadiationValue());
    }


    @EventHandler
    public void playerJoin(PlayerJoinEvent e){
        PlayerDataBase playerDataBase =  playerDataService.getPlayer(e.getPlayer().getName());
        PlayerData playerData = PlayerManager.getInstance().getPlayerData(e.getPlayer());
        playerData.setTemperatureValue(playerDataBase.getFreezeValue());
        playerData.setRadiationValue(playerDataBase.getRadiationValue());
        System.out.println(" RAD : " + playerDataBase.getRadiationValue() + " WIN: " + playerDataBase.getFreezeValue() );
        System.out.println(" RAD : " + playerData.getRadiationValue() + " WIN: " + playerData.getTemperatureValue() );
    }

}
