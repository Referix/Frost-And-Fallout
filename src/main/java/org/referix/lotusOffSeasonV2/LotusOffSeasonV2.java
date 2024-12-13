package org.referix.lotusOffSeasonV2;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.referix.lotusOffSeasonV2.database.hibernate.playerdata.PlayerDataServiceImpl;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneDataService;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneDataServiceImpl;
import org.referix.lotusOffSeasonV2.database.hibernate.structures.StructureDataService;
import org.referix.lotusOffSeasonV2.database.hibernate.structures.StructureDataServiceImpl;
import org.referix.lotusOffSeasonV2.event.EatEvent;
import org.referix.lotusOffSeasonV2.event.GoatDropEvent;
import org.referix.lotusOffSeasonV2.event.JoinLeaveEvent;
import org.referix.lotusOffSeasonV2.item.CustomItemManager;
import org.referix.lotusOffSeasonV2.command.MainCommand;
import org.referix.lotusOffSeasonV2.database.hibernate.HibernateUtil;
import org.referix.lotusOffSeasonV2.event.ArmorEvent;
import org.referix.lotusOffSeasonV2.playerdata.PlayerBar;
import org.referix.lotusOffSeasonV2.playerdata.PlayerManager;
import org.referix.lotusOffSeasonV2.safezone.SafeZoneManager;
import org.referix.lotusOffSeasonV2.trader.hoarder.HoarderConfig;
import org.referix.lotusOffSeasonV2.trader.hoarder.HolderManager;

public final class LotusOffSeasonV2 extends JavaPlugin {

    private static LotusOffSeasonV2 instance;
    private CustomItemManager itemManager;


    private HoarderConfig horderConfig;

    private HolderManager holderManager;

    private PlayerDataServiceImpl playerDataBase;
    private SaveZoneDataService saveZoneDataService;



    private StructureDataService structureDataService;

    @Override
    public void onEnable() {

        instance = this;
        PlayerManager.getInstance().startGlobalTask();
        //cfg
        itemManager = new CustomItemManager();
        this.playerDataBase = new PlayerDataServiceImpl();
        this.saveZoneDataService = new SaveZoneDataServiceImpl();

        itemManager.loadItems();
        getServer().getPluginManager().registerEvents(new ArmorEvent(itemManager),this);
        getServer().getPluginManager().registerEvents(new EatEvent(itemManager),this);
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(playerDataBase),this);
        getLogger().info("Предметы загружены из items.yml.");
        getServer().getPluginManager().registerEvents(new GoatDropEvent(),this);

        new PlayerBar(this,itemManager);


        horderConfig = new HoarderConfig();
        holderManager = new HolderManager(horderConfig);
        holderManager.loadHoldersFromFile();

        new MainCommand("lotus", itemManager,holderManager,saveZoneDataService);

        SafeZoneManager.getInstance().initializeZones(saveZoneDataService);

        //test
        structureDataService = new StructureDataServiceImpl();
    }

    @Override
    public void onDisable() {
       PlayerManager.getInstance().getGlobalTask().cancel();
        // Закриття SessionFactory Hibernate при вимкненні плагіна
        if (HibernateUtil.getSessionFactory() != null) {
            HibernateUtil.getSessionFactory().close();
        }
        getLogger().info("LotusOffSeason plugin disabled!");
        HandlerList.unregisterAll(this);
    }
    public PlayerDataServiceImpl getPlayerDataBase() {
        return playerDataBase;
    }

    public HoarderConfig getHorderConfig() {
        return horderConfig;
    }

    public static LotusOffSeasonV2 getInstance() {
        return instance;
    }

    public HolderManager getHolderManager() {
        return holderManager;
    }
}
