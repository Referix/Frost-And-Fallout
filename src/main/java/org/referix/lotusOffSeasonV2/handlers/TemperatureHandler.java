package org.referix.lotusOffSeasonV2.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;
import org.referix.lotusOffSeasonV2.helpers.CampfireObserver;
import org.referix.lotusOffSeasonV2.playerdata.PlayerManager;

import java.util.Random;

public class TemperatureHandler {

    private double MAX_TEMPERATURE = 30;
    private double MIN_PLAYER_TEMPERATURE = 0;
    private double MIN_TEMPERATURE = -40;

    private double HEIGHT_LOW = 50;
    private double HEIGHT_HIGH = 80;

    private double MAX_HEIGHT_SPEED = -6;

    private double RADIUS_HIGH = 80;
    private double RADIUS_MIDDLE = 64;
    private double RADIUS_LOW = 48;
    private double RADIUS_NEUTRAL = 32;

    private double RADIUS_HIGH_SPEED = -8;
    private double RADIUS_LOW_SPEED = -2;
    private double RADIUS_NEUTRAL_SPEED = -1;

    private double MAX_COLD_SPEED = MAX_HEIGHT_SPEED + RADIUS_HIGH_SPEED;

    //Furnace
    private double FURNACE_SPEED = 1;


    private static final TemperatureHandler instance = new TemperatureHandler();

    public static TemperatureHandler getInstance() {
        return instance;
    }

    public double calculate(Player player, double value, double resistance){
        double speed = 0;
        double new_value = value + resistance;


        double y = player.getLocation().getBlockY();
        double heightColdSpeed = Math.clamp(
                (y - HEIGHT_LOW) / (HEIGHT_HIGH - HEIGHT_LOW) * MAX_HEIGHT_SPEED,
                MAX_HEIGHT_SPEED,
                0
        );
        double radiusColdSpeed = heightColdSpeed < 0 ? getRadiusColdSpeed(player) : 0;
        //System.out.println("radiusColdSpeed:" + radiusColdSpeed);
        double coldSpeed = 0 + 1 * heightColdSpeed + 1 *radiusColdSpeed;
        //System.out.println("coldSpeed:" + coldSpeed);

        double heatingSpeed = surroundingBlocksTemperature(player);
        if (heatingSpeed > 0) {
            double coldImpactRatio = Math.clamp(coldSpeed / MAX_COLD_SPEED, 0, 1); // 0-1
            double bonfireMinSpeed = +0.5;
            speed = heatingSpeed - (heatingSpeed - bonfireMinSpeed) * coldImpactRatio;
            //System.out.println("Speed+:" + speed);
        } else {
            speed = coldSpeed;
            //System.out.println("Speed-:" + speed);
        }

        new_value = new_value + speed;
        new_value = Math.clamp(new_value, MIN_TEMPERATURE, MAX_TEMPERATURE);
        return new_value;
    }

    public double getRadiusColdSpeed(Player player) {
        double x = player.getX();
        double z = player.getZ();
        double distance = Math.sqrt(x*x + z*z);

        double radiusColdSpeed = 0;
        double RADIUS_MIDDLE_SPEED = -4;
        if (distance > RADIUS_HIGH) radiusColdSpeed = RADIUS_HIGH_SPEED;
        else if (distance > RADIUS_MIDDLE) radiusColdSpeed = RADIUS_MIDDLE_SPEED;
        else if (distance > RADIUS_LOW) radiusColdSpeed = RADIUS_LOW_SPEED;
        else if (distance > RADIUS_NEUTRAL) radiusColdSpeed = RADIUS_NEUTRAL_SPEED;
        else radiusColdSpeed = 0;

        return radiusColdSpeed;
    }




    private  double surroundingBlocksTemperature(Player player) {
        Location playerLocation = player.getLocation();
        int px = playerLocation.getBlockX();
        int py = playerLocation.getBlockY();
        int pz = playerLocation.getBlockZ();
        double heatingSum = 0;

        for (int x = px - 3; x <= px + 3; x++) {
            for (int y = py -3; y <= py + 3; y++) {
                for (int z = pz - 3; z <= pz + 3; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    Material type = block.getType();
                    // Check if the block is a furnace
                    if (type == Material.FURNACE || type == Material.BLAST_FURNACE || type == Material.SMOKER ) {
                        // Get block data and check if it is lit
                        if (block.getBlockData() instanceof org.bukkit.block.data.type.Furnace furnaceData && furnaceData.isLit()) {
                            heatingSum += FURNACE_SPEED;
                        }
                    }
                    if (type == Material.CAMPFIRE || type == Material.SOUL_CAMPFIRE) {
                        Campfire campfire = (Campfire) block.getBlockData();
                        if (campfire.isLit()){
                            heatingSum += FURNACE_SPEED;
                            new CampfireObserver(block).campfireBehaviour(campfire);
                        }
                    }
                }
            }
        }
        heatingSum = Math.min(heatingSum,4);
        return heatingSum;
    }



    public double calculateRelativeValue(double value) {
        double relValue = (MIN_TEMPERATURE - value) / (MIN_TEMPERATURE - MAX_TEMPERATURE);
        return Math.clamp(relValue, 0, 1);
    }



    public String createProgressBar(double absoluteValue, String label,boolean showValue) {
        int totalBars = 10; // Общая длина полоски
        double relValue = 1 - (MIN_TEMPERATURE - absoluteValue) / (MIN_TEMPERATURE - MAX_TEMPERATURE);
        relValue = Math.clamp(relValue, 0, 1);
        int filledBars = (int) Math.round(relValue * totalBars); // Количество заполненных

        StringBuilder bar = new StringBuilder(label + ": ");
        for (int i = 0; i < totalBars; i++) {
            bar.append(i < filledBars ? "█" : "░");
        }
        if (showValue) {
            return String.format("%s %2.1f", bar, absoluteValue);
        } else return String.format("%s", bar);
    }

    public double clamped(double value){
        return Math.clamp(value,MIN_TEMPERATURE,MAX_TEMPERATURE);
    }

//    private double MAX_TEMPERATURE = 30;
//    private double MIN_PLAYER_TEMPERATURE = 0;
//    private double MIN_TEMPERATURE = -40;

    public void applyDamageEffect(Player player,double temperatureValue) {
        if (temperatureValue > MIN_PLAYER_TEMPERATURE) return;
        PotionEffect slowEffect = new PotionEffect(PotionEffectType.SLOWNESS, 200, 1);
        PotionEffect weaknessEffect = new PotionEffect(PotionEffectType.WEAKNESS, 200, 1);
        double hp = player.getHealth();
        hp = Math.max(0, hp - 1);
        player.setHealth(hp);
        if (!player.hasPotionEffect(PotionEffectType.SLOWNESS) && !player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
            player.sendMessage(ChatColor.RED + "Вы замерзаете!");
            player.addPotionEffect(slowEffect);
            player.addPotionEffect(weaknessEffect);
            player.setFreezeTicks(600); // 30 секунд замороження
            int random = new Random().nextInt(100);
            if (random < 30) {
                if (!player.getInventory().getItemInMainHand().getType().isAir()) {
                    // Отримуємо місцезнаходження гравця для дропу предмета
                    Location playerLocation = player.getLocation();

                    // Дропаємо предмет на місцезнаходження гравця
                    player.getWorld().dropItemNaturally(playerLocation, player.getInventory().getItemInMainHand());

                    // Забираємо предмет з руки гравця
                    player.getInventory().setItemInMainHand(null);
                }
            }
        }
    }

//    private static boolean isCampfire(Block block) {
//        if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE) {
//            Campfire campfire = (Campfire) block.getBlockData();
//            // Проверяем, зажжён ли костёр
//            return campfire.isLit();
//        }
//        return false;
//    }

}
