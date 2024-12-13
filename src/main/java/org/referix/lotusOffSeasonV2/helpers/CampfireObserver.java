package org.referix.lotusOffSeasonV2.helpers;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.scheduler.BukkitRunnable;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

public class CampfireObserver {
    private final Block block;

    private int CAMPFIRE_INT = 20 * 60 * 2;
    //task
    private boolean isExtinguishingScheduled = false;

    public CampfireObserver(Block block) {
        this.block = block;
    }

    public void campfireBehaviour(Campfire campfire){
        isExtinguishingScheduled = true;

        new BukkitRunnable() {
            @Override
            public void run() {

                // Проверяем ещё раз, горит ли костёр, перед его тушением
                if (campfire.isLit()) {

                    // Тушим костёр
                    campfire.setLit(false);
                    block.setBlockData(campfire, false);
                }

                // Сбрасываем флаг после выполнения задачи
                isExtinguishingScheduled = false;
            }
        }.runTaskLater(LotusOffSeasonV2.getInstance(), CAMPFIRE_INT); // Тушим через заданное время
    }


}
