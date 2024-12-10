package org.referix.lotusOffSeasonV2;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.referix.lotusOffSeasonV2.armor.CustomItemManager;
import org.referix.lotusOffSeasonV2.command.MainCommand;
import org.referix.lotusOffSeasonV2.event.ArmorEvent;
import org.referix.lotusOffSeasonV2.playerdata.PlayerManager;
import org.referix.lotusOffSeasonV2.trader.hoarder.HoarderConfig;
import org.referix.lotusOffSeasonV2.trader.hoarder.HolderManager;

public final class LotusOffSeasonV2 extends JavaPlugin {

    private static LotusOffSeasonV2 instance;
    private CustomItemManager itemManager;

    public HolderManager holderManager;

    @Override
    public void onEnable() {

        instance = this;
        PlayerManager.getInstance().startGlobalTask();
        //cfg
        itemManager = new CustomItemManager();


        itemManager.loadItems();
        getServer().getPluginManager().registerEvents(new ArmorEvent(itemManager),this);
        getLogger().info("Предметы загружены из items.yml.");



        holderManager = new HolderManager(new HoarderConfig());
        holderManager.loadHoldersFromFile();

        new MainCommand("lotus", itemManager,holderManager);
    }

    @Override
    public void onDisable() {
       PlayerManager.getInstance().getGlobalTask().cancel();
       HandlerList.unregisterAll(this);
    }

    public static LotusOffSeasonV2 getInstance() {
        return instance;
    }
}
