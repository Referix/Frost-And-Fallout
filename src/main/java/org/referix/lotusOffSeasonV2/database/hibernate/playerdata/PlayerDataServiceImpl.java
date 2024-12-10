package org.referix.lotusOffSeasonV2.database.hibernate.playerdata;

import java.util.HashMap;

public class PlayerDataServiceImpl implements PlayerDataService {
    private final PlayerDataRepository playerDataRepository = new PlayerDataRepository();

    @Override
    public void savePlayer(String playerName, double freezeValue, double radiationValue) {
        PlayerDataBase playerDataBase = new PlayerDataBase(playerName, freezeValue, radiationValue);
        playerDataRepository.savePlayerData(playerDataBase);
    }

    @Override
    public PlayerDataBase getPlayer(String playerName) {
        return playerDataRepository.getPlayerData(playerName);
    }

    @Override
    public void updatePlayer(String playerName, double freezeValue, double radiationValue) {
        PlayerDataBase playerDataBase = getPlayer(playerName);
        if (playerDataBase != null) {
            playerDataBase.setFreezeValue(freezeValue);
            playerDataBase.setRadiationValue(radiationValue);
            playerDataRepository.updatePlayerData(playerDataBase);
        }
    }

    @Override
    public void deletePlayer(String playerName) {
        playerDataRepository.deletePlayerData(playerName);
    }

    @Override
    public HashMap<String, Double> getValues(String playerName) {
        PlayerDataBase playerDataBase = getPlayer(playerName);
        if (playerDataBase != null) {
            HashMap<String, Double> values = new HashMap<>();
            values.put("freezeValue", playerDataBase.getFreezeValue());
            values.put("radiationValue", playerDataBase.getRadiationValue());
            return values;
        }
        return null; // або можна повернути порожній HashMap
    }
}

