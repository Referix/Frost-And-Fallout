package org.referix.lotusOffSeasonV2.handlers;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RadiationHandler {

    private double HEIGHT_LOW = -60;
    private double HEIGHT_HIGH = 50;


    private double MIN_RADIATION_SPEED = 0;
    private double MAX_RADIATION_SPEED = 2;

    private double MIN_RADIATION_VALUE = 0;
    private double MAX_RADIATION_VALUE = 10;


    private static final RadiationHandler instance = new RadiationHandler();

    public static RadiationHandler getInstance() {
        return instance;
    }

    public double calculate(Player player, double value, double resistance){
        double speed = 0;
        double new_value = value + (-resistance);


        double y = player.getLocation().getBlockY();
        double heightRadiationSpeed = MAX_RADIATION_SPEED + (y - HEIGHT_LOW) * (MIN_RADIATION_SPEED - MAX_RADIATION_SPEED) / (HEIGHT_HIGH - HEIGHT_LOW);
        heightRadiationSpeed = Math.clamp(heightRadiationSpeed, MIN_RADIATION_SPEED, MAX_RADIATION_SPEED);

        if (y > HEIGHT_HIGH) {
            heightRadiationSpeed = -MAX_RADIATION_SPEED / 10;
        }

        double radiationSpeed = 0 + 1 * heightRadiationSpeed;
        //System.out.format("radiationSpeed: %.2f%n", radiationSpeed);

        new_value += radiationSpeed;

        new_value = Math.clamp(new_value, MIN_RADIATION_VALUE, MAX_RADIATION_VALUE);

        return new_value;
    }

//    public double calculateRelativeValue(double value) {
//        double relValue = (MIN_RADIATION_VALUE - value) / (MIN_RADIATION_VALUE - MAX_RADIATION_VALUE);
//        return Math.clamp(relValue, 0, 1);
//    }


    public String createProgressBar(double absoluteValue, String label) {
        int totalBars = 10; // Общая длина полоски
        double relativeValue = (MIN_RADIATION_VALUE - absoluteValue) / (MIN_RADIATION_VALUE - MAX_RADIATION_VALUE);
        relativeValue = Math.clamp(relativeValue, 0, 1);
        int filledBars = (int) Math.round(relativeValue * totalBars); // Количество заполненных

        StringBuilder bar = new StringBuilder(label + ": ");
        for (int i = 0; i < totalBars; i++) {
            bar.append(i < filledBars ? "█" : "░");
        }

        return String.format("%s %2.1f", bar, absoluteValue);
    }


    public double clamped(double value){
        return Math.clamp(value, MIN_RADIATION_VALUE, MAX_RADIATION_VALUE);
    }


    public void applyDamageEffect(Player player, double radiationValue) {
        if (radiationValue < MAX_RADIATION_VALUE) return;
        PotionEffect slowEffect = new PotionEffect(PotionEffectType.SLOWNESS, 200, 1);
        PotionEffect weaknessEffect = new PotionEffect(PotionEffectType.WEAKNESS, 200, 1);
        PotionEffect poisonEffect = new PotionEffect(PotionEffectType.POISON, 200, 1);
        double hp = player.getHealth();
        hp = Math.max(0, hp - 1);
        player.setHealth(hp);
        if (!player.hasPotionEffect(PotionEffectType.SLOWNESS) &&
                !player.hasPotionEffect(PotionEffectType.WEAKNESS) &&
                !player.hasPotionEffect(PotionEffectType.POISON)) {

            player.addPotionEffect(slowEffect);
            player.addPotionEffect(weaknessEffect);
            player.addPotionEffect(poisonEffect);

            // Додатковий урон
            if (player.getHealth() > 2.0) {
                player.damage(6.0); // Завдає 3 серця урону
            }
        }
    }

}