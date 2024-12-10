package org.referix.lotusOffSeasonV2.database.hibernate.playerdata;

import java.util.HashMap;

public interface PlayerDataService {
    void savePlayer(String playerName, double freezeValue, double radiationValue);
    PlayerDataBase getPlayer(String playerName);
    void updatePlayer(String playerName, double freezeValue, double radiationValue);
    void deletePlayer(String playerName);
    HashMap<String, Double> getValues(String playerName);
}
