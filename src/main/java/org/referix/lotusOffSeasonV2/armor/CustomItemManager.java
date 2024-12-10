package org.referix.lotusOffSeasonV2.armor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager {
    private final Map<String, CustomItem> items = new HashMap<>();
    private final boolean isOraxenEnabled;

    public CustomItemManager() {
        // Проверяем наличие Oraxen
        this.isOraxenEnabled = Bukkit.getPluginManager().getPlugin("Oraxen") != null;

        if (!isOraxenEnabled) {
            LotusOffSeasonV2.getInstance().getLogger().warning("Oraxen не найден. Предметы из Oraxen будут пропущены.");
        }
    }

    public void loadItems() {
        File file = new File(LotusOffSeasonV2.getInstance().getDataFolder(), "items.yml");
        if (!file.exists()) {
            LotusOffSeasonV2.getInstance().saveResource("items.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + key;

            try {
                String type = config.getString(path + ".type");
                if (type == null) {
                    throw new IllegalArgumentException("Не указан тип предмета для ключа '" + key + "'");
                }

                if ("standard".equalsIgnoreCase(type)) {
                    // Загрузка стандартного предмета
                    Material material = Material.valueOf(config.getString(path + ".material"));
                    String displayName = config.getString(path + ".displayName");
                    List<String> lore = config.getStringList(path + ".lore");
                    double temperatureResistance = config.getDouble(path + ".temperatureResistance");
                    double radiationResistance = config.getDouble(path + ".radiationResistance");

                    items.put(key, new StandardArmor(material, displayName, lore, temperatureResistance, radiationResistance));
                    LotusOffSeasonV2.getInstance().getLogger().info("Успешно загружен стандартный предмет: " + key);
                } else if ("oraxen".equalsIgnoreCase(type)) {
                    if (!isOraxenEnabled) {
                        LotusOffSeasonV2.getInstance().getLogger().warning("Oraxen-предмет '" + key + "' пропущен, так как Oraxen не активен.");
                        continue;
                    }

                    // Загрузка Oraxen-предмета
                    String oraxenId = config.getString(path + ".oraxenId");
                    if (oraxenId == null || oraxenId.isEmpty()) {
                        throw new IllegalArgumentException("Отсутствует 'oraxenId' для предмета с ключом '" + key + "'");
                    }
                    double temperatureResistance = config.getDouble(path + ".temperatureResistance");
                    double radiationResistance = config.getDouble(path + ".radiationResistance");

                    items.put(key, new OraxenCustomItem(oraxenId, temperatureResistance, radiationResistance));
                    LotusOffSeasonV2.getInstance().getLogger().info("Успешно загружен Oraxen-предмет: " + key);
                } else {
                    throw new IllegalArgumentException("Неизвестный тип предмета '" + type + "' для ключа '" + key + "'");
                }
            } catch (IllegalArgumentException e) {
                LotusOffSeasonV2.getInstance().getLogger().warning("Ошибка при загрузке предмета с ключом '" + key + "': " + e.getMessage());
            } catch (Exception e) {
                LotusOffSeasonV2.getInstance().getLogger().severe("Непредвиденная ошибка при загрузке предмета с ключом '" + key + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Map<String, CustomItem> getItems() {
        return items;
    }

    public CustomItem getItem(String key) {
        return items.get(key);
    }

    public CustomItem getCustomItemByItemStack(ItemStack itemStack) {
        for (CustomItem item : items.values()) {
            if (itemStack.isSimilar(item.getItemStack())) {
                return item;
            }
        }
        return null;
    }
}
