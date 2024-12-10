package org.referix.lotusOffSeasonV2.trader.hoarder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HoarderConfig {
    private final File configFile;
    private FileConfiguration config;

    public HoarderConfig() {
        // Инициализация файла конфигурации
        this.configFile = new File(LotusOffSeasonV2.getInstance().getDataFolder(), "hoarder_trader.yml");

        if (!configFile.exists()) {
            LotusOffSeasonV2.getInstance().saveResource("hoarder_trader.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Получить список рецептов по их идентификаторам.
     *
     * @param recipeIds Список идентификаторов рецептов.
     * @return Список MerchantRecipe.
     */
    public List<MerchantRecipe> getRecipesByIds(List<String> recipeIds) {
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (String recipeId : recipeIds) {
            String path = "recipes." + recipeId;

            // Проверяем существование рецепта
            if (!config.contains(path)) {
                Bukkit.getLogger().warning("HoarderConfig: Рецепт с ID '" + recipeId + "' не найден.");
                continue;
            }

            // Создаем результат
            String resultId = config.getString(path + ".result.item");
            int resultAmount = config.getInt(path + ".result.amount", 1);
            ItemStack result = getItemFromConfig(resultId, resultAmount);

            if (result == null) {
                Bukkit.getLogger().warning("HoarderConfig: Пропущен рецепт " + recipeId + ". Некорректный результат: " + resultId);
                continue;
            }

            MerchantRecipe recipe = new MerchantRecipe(result, config.getInt(path + ".maxUses", 10));

            // Добавляем ингредиенты
            List<?> ingredients = config.getList(path + ".ingredients");
            if (ingredients != null) {
                for (Object ingredientObj : ingredients) {
                    String[] parts = ingredientObj.toString().split(":");
                    if (parts.length == 2) {
                        try {
                            int ingredientAmount = Integer.parseInt(parts[1]);
                            ItemStack ingredient = getItemFromConfig(parts[0], ingredientAmount);
                            if (ingredient != null) {
                                recipe.addIngredient(ingredient);
                            }
                        } catch (NumberFormatException e) {
                            Bukkit.getLogger().warning("HoarderConfig: Некорректный формат количества для " + parts[0]);
                        }
                    }
                }
            }

            recipes.add(recipe);
        }

        return recipes;
    }

    /**
     * Получить список рецептов для конкретного жителя.
     *
     * @param villagerId Идентификатор жителя.
     * @return Список MerchantRecipe.
     */
    public List<MerchantRecipe> getRecipesForVillager(String villagerId) {
        String path = "villagers." + villagerId + ".recipes";

        if (!config.contains(path)) {
            Bukkit.getLogger().warning("HoarderConfig: Пресет для жителя '" + villagerId + "' не найден.");
            return new ArrayList<>();
        }

        List<String> recipeIds = config.getStringList(path);
        return getRecipesByIds(recipeIds);
    }

    /**
     * Получить ItemStack из конфигурации.
     *
     * @param itemId Идентификатор предмета.
     * @param amount Количество предметов.
     * @return ItemStack.
     */
    private ItemStack getItemFromConfig(String itemId, int amount) {
        if (itemId == null || itemId.isEmpty()) {
            return null;
        }

        Material material = Material.matchMaterial(itemId);
        if (material != null) {
            return new ItemStack(material, amount);
        }

        Bukkit.getLogger().warning("HoarderConfig: Не удалось найти предмет с ID: " + itemId);
        return null;
    }

    /**
     * Перезагрузка конфигурации.
     */
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
        Bukkit.getLogger().info("HoarderConfig: Конфигурация перезагружена.");
    }

    /**
     * Сохранение конфигурации.
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public boolean hasPreset(String presetName) {
        if (presetName == null || presetName.isEmpty()) {
            return false;
        }
        return LotusOffSeasonV2.getInstance().getHorderConfig().contains("villagers." + presetName);
    }

}
