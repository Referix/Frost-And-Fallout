package org.referix.lotusOffSeasonV2.trader.hoarder;


import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HoarderConfig {
    private final File horderConfigFile;
    private FileConfiguration horderConfig;

    public HoarderConfig() {
        // Ініціалізуємо файл trader.yml
        this.horderConfigFile = new File(LotusOffSeasonV2.getInstance().getDataFolder(), "hoarder_trader.yml");

        if (!horderConfigFile.exists()) {
            try {
                // Якщо файл не існує, копіюємо його з ресурсу
                InputStream resourceStream = LotusOffSeasonV2.getInstance().getResource("hoarder_trader.yml");
                if (resourceStream != null) {
                    java.nio.file.Files.copy(resourceStream, horderConfigFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Завантажуємо конфігурацію
        this.horderConfig = YamlConfiguration.loadConfiguration(horderConfigFile);
    }

    /**
     * Отримує список рецептів з trader.yml
     *
     * @return Список MerchantRecipe
     */
    public List<MerchantRecipe> getTraderRecipes() {
        List<MerchantRecipe> recipes = new ArrayList<>();

        // Зчитуємо секцію рецептів з файлу
        if (horderConfig.contains("recipes")) {
            for (String key : horderConfig.getConfigurationSection("recipes").getKeys(false)) {
                String path = "recipes." + key;

                // Отримуємо результат
                String resultId = horderConfig.getString(path + ".result.item");
                int resultAmount = horderConfig.getInt(path + ".result.amount", 1);
                ItemStack result = getItemFromConfig(resultId, resultAmount);

                if (result == null) {
                    System.err.println("ConfigTrader: Рецепт " + key + " пропущено. Некоректний результат: " + resultId);
                    continue; // Пропускаємо цей рецепт
                }

                // Створюємо рецепт
                MerchantRecipe recipe = new MerchantRecipe(result, horderConfig.getInt(path + ".maxUses", 10));

                // Зчитуємо інгредієнти
                List<?> ingredients = horderConfig.getList(path + ".ingredients");
                boolean validIngredients = true;

                if (ingredients != null) {
                    for (Object ingredientObj : ingredients) {
                        String[] parts = ingredientObj.toString().split(":");
                        if (parts.length == 2) {
                            try {
                                int ingredientAmount = Integer.parseInt(parts[1]);
                                ItemStack ingredient = getItemFromConfig(parts[0], ingredientAmount);

                                if (ingredient != null) {
                                    recipe.addIngredient(ingredient);
                                } else {
                                    System.err.println("ConfigTrader: Некоректний інгредієнт " + parts[0] + " у рецепті " + key);
                                    validIngredients = false;
                                }
                            } catch (NumberFormatException ex) {
                                System.err.println("ConfigTrader: Некоректний формат кількості для " + parts[0] + " у рецепті " + key);
                                validIngredients = false;
                            }
                        } else {
                            System.err.println("ConfigTrader: Некоректний формат інгредієнта " + ingredientObj + " у рецепті " + key);
                            validIngredients = false;
                        }
                    }
                }

                // Якщо інгредієнти валідні, додаємо рецепт
                if (validIngredients) {
                    recipes.add(recipe);
                } else {
                    System.err.println("ConfigTrader: Рецепт " + key + " пропущено через некоректні інгредієнти.");
                }
            }
        }

        return recipes;
    }


    /**
     * Отримує ItemStack з конфігурації, підтримуючи Oraxen ID.
     *
     * @param itemId Ідентифікатор предмета (Material або Oraxen ID)
     * @param amount Кількість предметів
     * @return ItemStack
     */
    private ItemStack getItemFromConfig(String itemId, int amount) {
        if (itemId == null || itemId.isEmpty()) {
            System.err.println("ConfigTrader: Передан пустой или null itemId.");
            return null;
        }

        // Проверяем наличие Oraxen
        if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            try {
                ItemBuilder itemBuilder = OraxenItems.getItemById(itemId);
                if (itemBuilder != null) {
                    ItemStack oraxenItem = itemBuilder.build();
                    oraxenItem.setAmount(amount);
                    return oraxenItem;
                }
            } catch (Exception e) {
                System.err.println("ConfigTrader: Ошибка при загрузке Oraxen-предмета с ID '" + itemId + "': " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Проверяем как стандартный предмет
        Material material = null;
        try {
            material = Material.valueOf(itemId.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            // Логирование для пропущенного материала
            System.err.println("ConfigTrader: Неверный ID материала или неизвестный предмет: " + itemId);
        }

        if (material != null) {
            return new ItemStack(material, amount);
        }

        // Если ни один вариант не сработал
        System.err.println("ConfigTrader: Не удалось найти предмет с ID: " + itemId);
        return null;
    }




    /**
     * Зберігає конфігурацію trader.yml
     */
    public void saveConfig() {
        try {
            horderConfig.save(horderConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Перезапускає конфігурацію trader.yml
     */
    public void reloadConfig() {
        // Завантажуємо існуючий файл конфігурації з диска
        this.horderConfig = YamlConfiguration.loadConfiguration(horderConfigFile);
        System.out.println("ConfigTrader: Конфігурацію успішно перезапущено.");
    }
}

