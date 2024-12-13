
package org.referix.lotusOffSeasonV2.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.referix.lotusOffSeasonV2.safezone.SafeZoneManager;

public class RadiationHandler {

    private double HEIGHT_LOW = -60;
    private double HEIGHT_HIGH = 50;

    private double MIN_RADIATION_SPEED = 0;
    private double MAX_RADIATION_SPEED = 2;

    private double MIN_RADIATION_VALUE = 0;
    private double MAX_PLAYER_RADIATION_VALUE = 20;
    private double MAX_RADIATION_VALUE = 30;

    private double MAX_DAMAGE_PER_SEC = 4;


    private static final RadiationHandler instance = new RadiationHandler();

    public static RadiationHandler getInstance() {
        return instance;
    }

    public double calculate(Player player, double value, double resistance){
        if (SafeZoneManager.getInstance().isPlayerInAnyZone(player)){
            return Math.max(value - (MAX_RADIATION_SPEED * 0.5), MIN_RADIATION_VALUE);
        }
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

    public Component createProgressBar(double absoluteValue, String label, boolean showValue) {
        int totalBars = 30; // Общая длина полоски

        double relValue = (MIN_RADIATION_VALUE - absoluteValue) / (MIN_RADIATION_VALUE - MAX_RADIATION_VALUE);
        relValue = Math.clamp(relValue, 0, 1);

        int filledBars = (int) Math.round(relValue * totalBars); // Количество заполненных
        int damageBars = (int) Math.round((MAX_RADIATION_VALUE - MAX_PLAYER_RADIATION_VALUE) / (MAX_RADIATION_VALUE - MIN_RADIATION_VALUE) * totalBars);
        int safeBars = totalBars - damageBars;

        // Начало компонента прогресс-бара

        Component progressBar = Component.text(label);

        progressBar = progressBar.append(Component.text(" ["));

        for (int i = 0; i < totalBars; i++) {
            String c = i < filledBars ? "|" : "."; // Заполненный или пустой сегмент
            TextColor color = (i < safeBars) ? TextColor.color(0x00DD77) : TextColor.color(0xCC0066); // Красный или синий
            progressBar = progressBar.append(Component.text(c).color(color));
        }

        progressBar = progressBar.append(Component.text("]"));

        if (showValue) {
            String numberValue = String.format(" %2.0f%%", relValue * 100);
            progressBar = progressBar.append(Component.text(numberValue).color(TextColor.color(0xFFFFFF)));
        }

        return progressBar;
    }

    public double clamped(double value){
        return Math.clamp(value, MIN_RADIATION_VALUE, MAX_RADIATION_VALUE);
    }


    public void applyDamageEffect(Player player, double radiationValue) {
        if (radiationValue < MAX_PLAYER_RADIATION_VALUE) return;
        PotionEffect slowEffect = new PotionEffect(PotionEffectType.SLOWNESS, 200, 1);
        PotionEffect weaknessEffect = new PotionEffect(PotionEffectType.WEAKNESS, 200, 1);
        PotionEffect poisonEffect = new PotionEffect(PotionEffectType.POISON, 200, 1);

        double hp = player.getHealth();
        double gamage = Math.max(0.5, MAX_DAMAGE_PER_SEC * (radiationValue - MAX_PLAYER_RADIATION_VALUE) / (MAX_RADIATION_VALUE - MAX_PLAYER_RADIATION_VALUE));
        hp = Math.max(0, hp - gamage);

        player.setHealth(hp);
        if (!player.hasPotionEffect(PotionEffectType.SLOWNESS) &&
                !player.hasPotionEffect(PotionEffectType.WEAKNESS) &&
                !player.hasPotionEffect(PotionEffectType.POISON)) {

            player.addPotionEffect(slowEffect);
            player.addPotionEffect(weaknessEffect);
            player.addPotionEffect(poisonEffect);
        }
    }
}
