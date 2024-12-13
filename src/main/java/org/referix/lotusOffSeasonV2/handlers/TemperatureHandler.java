package org.referix.lotusOffSeasonV2.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.referix.lotusOffSeasonV2.helpers.CampfireObserver;
import org.referix.lotusOffSeasonV2.playerdata.PlayerData;
import org.referix.lotusOffSeasonV2.playerdata.PlayerManager;
import org.referix.lotusOffSeasonV2.safezone.SafeZoneManager;

import java.util.Random;

public class TemperatureHandler {

    private double MAX_TEMPERATURE = 30;
    private double MIN_PLAYER_TEMPERATURE = 0;
    private double MIN_TEMPERATURE = -40;

    private double HEIGHT_LOW = 50;
    private double HEIGHT_HIGH = 100;

    private double MAX_HEIGHT_SPEED = -2;

    private double RADIUS_HIGH = 2000;
    private double RADIUS_MIDDLE = 1200;
    private double RADIUS_LOW = 800;
    private double RADIUS_NEUTRAL = 500;

    private double RADIUS_HIGH_SPEED = -4;
    private double RADIUS_MIDDLE_SPEED = -2;
    private double RADIUS_LOW_SPEED = -1;
    private double RADIUS_NEUTRAL_SPEED = -0.25;

    private double MAX_COLD_SPEED = MAX_HEIGHT_SPEED + RADIUS_HIGH_SPEED;

    //Furnace
    private double FURNACE_SPEED = 1;



    private  double MAX_DAMAGE_PER_SEC = 2.0;


    private static final TemperatureHandler instance = new TemperatureHandler();

    public static TemperatureHandler getInstance() {
        return instance;
    }

    public double calculate(Player player, double value, double resistance){
        if (SafeZoneManager.getInstance().isPlayerInAnyZone(player)){
            return Math.clamp(value + (2 * FURNACE_SPEED), MIN_TEMPERATURE,MAX_TEMPERATURE);
        }
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



//    public double calculateRelativeValue(double value) {
//        double relValue = (MIN_TEMPERATURE - value) / (MIN_TEMPERATURE - MAX_TEMPERATURE);
//        return Math.clamp(relValue, 0, 1);
//    }




    public Component createProgressBar(double absoluteValue, String label, boolean showValue) {
        int totalBars = 30; // Общая длина полоски

        double relValue = (MIN_TEMPERATURE - absoluteValue) / (MIN_TEMPERATURE - MAX_TEMPERATURE);
        relValue = Math.clamp(relValue, 0, 1);

        int filledBars = (int) Math.round(relValue * totalBars); // Количество заполненных
        int coldBars = (int) Math.round((MIN_PLAYER_TEMPERATURE - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE) * totalBars);
        // int hotBars = totalBars - coldBars;

        // Начало компонента прогресс-бара
        Component progressBar = Component.text(label);

        progressBar = progressBar.append(Component.text(" ["));

        for (int i = 0; i < totalBars; i++) {
            String c = i < filledBars ? "|" : "."; // Заполненный или пустой сегмент
            TextColor color = (i < coldBars) ? TextColor.color(0x22CCFF) : TextColor.color(0xFF7733); // Красный или синий
            progressBar = progressBar.append(Component.text(c).color(color));
        }

        progressBar = progressBar.append(Component.text("]"));

        if (showValue) {
            String numberValue = String.format("%2.1f", absoluteValue);
            progressBar = progressBar.append(Component.text(numberValue).color(TextColor.color(0xFFFFFF)));
        }

        return progressBar;
    }


    public double clamped(double value){
        return Math.clamp(value,MIN_TEMPERATURE,MAX_TEMPERATURE);
    }

//    private double MAX_TEMPERATURE = 30;
//    private double MIN_PLAYER_TEMPERATURE = 0;
//    private double MIN_TEMPERATURE = -40;

    //


    public void applyDamageEffect(Player player,double temperatureValue) {
        if (temperatureValue > MIN_PLAYER_TEMPERATURE) return;

        PotionEffect slowEffect = new PotionEffect(PotionEffectType.SLOWNESS, 200, 1);
        PotionEffect weaknessEffect = new PotionEffect(PotionEffectType.WEAKNESS, 200, 1);

        double hp = player.getHealth();
        double gamage = Math.max(0.5, MAX_DAMAGE_PER_SEC * temperatureValue / (MIN_PLAYER_TEMPERATURE - MIN_TEMPERATURE));
        hp = Math.max(0, hp - gamage);


        player.setHealth(hp);
        if (!player.hasPotionEffect(PotionEffectType.SLOWNESS) && !player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
            player.sendMessage(ChatColor.RED + "Вы замерзаете!");
            player.addPotionEffect(slowEffect);
            player.addPotionEffect(weaknessEffect);
            player.setFreezeTicks(20*5); // 30 секунд замороження
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

//    public void applySafeEffects(Player player){
//        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
//        double temperatureValue = playerData.getTemperatureValue()+1;
//        playerData.setTemperatureValue(temperatureValue);
//    }

//    private static boolean isCampfire(Block block) {
//        if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE) {
//            Campfire campfire = (Campfire) block.getBlockData();
//            // Проверяем, зажжён ли костёр
//            return campfire.isLit();
//        }
//        return false;
//    }

}
