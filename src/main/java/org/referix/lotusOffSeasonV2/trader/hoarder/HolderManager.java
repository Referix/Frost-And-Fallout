package org.referix.lotusOffSeasonV2.trader.hoarder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

import java.io.File;
import java.util.*;

public class HolderManager implements Listener {
    private final Map<String, Holder> holders = new HashMap<>();
    private final NamespacedKey UNIQUE_ID_KEY; // Ключ для PersistentDataContainer

    private final HoarderConfig hoarderConfig; // Ссылка на конфигурацию

    // Конструктор HolderManager с передачей конфигурации

    public HolderManager(HoarderConfig hoarderConfig) {
        this.hoarderConfig = hoarderConfig;
        this.UNIQUE_ID_KEY = new NamespacedKey(LotusOffSeasonV2.getInstance(), "custom_holder");

        // Регистрация событий
        Bukkit.getPluginManager().registerEvents(this, LotusOffSeasonV2.getInstance());
    }
    public void loadHoldersFromFile() {
        File file = new File(LotusOffSeasonV2.getInstance().getDataFolder(), "holders.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning("Файл holders.yml не найден. Список Holders пуст.");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String holderId : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(holderId);
            if (section != null) {
                String name = section.getString("name", holderId); // Если имя не указано, используем ID
                String description = section.getString("description", "Без описания");
                String villagerPreset = section.getString("villagerPreset", "default"); // Значение по умолчанию

                // Используем addHolder для добавления
                addHolder(new Holder(holderId, name, description, villagerPreset));
            }
        }

        Bukkit.getLogger().info("Данные Holders успешно загружены из holders.yml.");
    }



    // Добавление Holder в карту

    public void addHolder(Holder holder) {
        if (!holder.hasPreset()) {
            Bukkit.getLogger().warning("Пресет '" + holder.getVillagerPreset() + "' для Holder '" + holder.getName() + "' не существует.");
            return;
        }

        String id = holder.getName();
        if (holders.containsKey(id)) {
            Bukkit.getLogger().warning("Holder с именем (ID) " + id + " уже существует.");
            return;
        }

        holders.put(id, holder);
        Bukkit.getLogger().info("Holder добавлен: " + holder);
    }

    // Удаление Holder из карты и удаление связанного Villager

    public void removeHolder(String name) {
        Holder removedHolder = holders.remove(name);
        if (removedHolder != null) {
            Bukkit.getLogger().info("Holder удален: " + removedHolder);

            // Удаляем Villager, связанного с Holder
            Bukkit.getWorlds().forEach(world ->
                    world.getEntitiesByClass(Villager.class).forEach(villager -> {
                        PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
                        if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                            String villagerName = dataContainer.get(UNIQUE_ID_KEY, PersistentDataType.STRING);
                            if (villagerName != null && villagerName.equals(name)) {
                                villager.remove();
                                Bukkit.getLogger().info("Villager с именем (ID) " + name + " удален.");
                            }
                        }
                    })
            );
        } else {
            Bukkit.getLogger().warning("Holder с именем (ID) " + name + " не найден.");
        }
    }
    // Поиск Holder по имени (ID)

    public Holder getHolderByName(String name) {
        return holders.get(name);
    }
    public List<String> getHolderNames() {
        return new ArrayList<>(holders.keySet());
    }

    public void spawnHolderVillager(String customName, String villagerPreset, Location location) {
        if (villagerPreset == null || location == null || customName == null || customName.trim().isEmpty()) {
            Bukkit.getLogger().warning("Имя жителя, пресет или локация не могут быть null или пустыми.");
            return;
        }

        if (holders.containsKey(customName)) {
            Bukkit.getLogger().warning("Житель с именем '" + customName + "' уже существует.");
            return;
        }

        if (!hoarderConfig.hasPreset(villagerPreset)) {
            Bukkit.getLogger().warning("Пресет '" + villagerPreset + "' не существует.");
            return;
        }

        if (location.getWorld() == null) {
            Bukkit.getLogger().warning("Мир для локации не найден. Невозможно создать жителя.");
            return;
        }

        List<MerchantRecipe> recipes = hoarderConfig.getRecipesForVillager(villagerPreset);
        if (recipes.isEmpty()) {
            Bukkit.getLogger().warning("Не найдены рецепты для пресета жителя: " + villagerPreset);
            return;
        }

        // Создание Villager
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        // Устанавливаем уникальный ID в PersistentDataContainer
        PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
        dataContainer.set(UNIQUE_ID_KEY, PersistentDataType.STRING, customName);

        configureVillager(villager, recipes, customName);

        // Добавляем Holder
        addHolder(new Holder(customName, customName, "Житель, созданный автоматически.", villagerPreset));
        Bukkit.getLogger().info("Villager с именем '" + customName + "' создан для пресета: " + villagerPreset);
    }


    // Конфигурация Villager

    private void configureVillager(Villager villager, List<MerchantRecipe> recipes, String customName) {
        villager.setCustomName(customName);
        villager.setCustomNameVisible(true);

        // Делаем невосприимчивым к урону и отключаем AI
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setVillagerType(Villager.Type.SWAMP);
        villager.setHealth(20); // Минимальное HP
        villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        villager.setCollidable(false);

        // Устанавливаем трейды
        villager.setRecipes(recipes);
    }
    // Защита жителей от повреждений

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
            if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
                Bukkit.getLogger().info("Попытка повредить защищённого жителя заблокирована.");
            }
        }
    }
    public void updateHolderPreset(String name, String newVillagerPreset) {
        Holder holder = holders.get(name);
        if (holder == null) {
            Bukkit.getLogger().warning("Holder с именем '" + name + "' не найден.");
            return;
        }

        if (!hoarderConfig.hasPreset(newVillagerPreset)) {
            Bukkit.getLogger().warning("Пресет '" + newVillagerPreset + "' не существует. Обновление прервано.");
            return;
        }

        holder.setVillagerPreset(newVillagerPreset);
        Bukkit.getLogger().info("Пресет для Holder '" + name + "' обновлён на '" + newVillagerPreset + "'.");
    }



    public void reloadConfigAndVillagers() {
        hoarderConfig.reloadConfig();
        Bukkit.getLogger().info("Конфигурация hoarder_trader.yml успешно перезагружена.");

        // Обновляем пресеты в Holder
        for (Map.Entry<String, Holder> entry : holders.entrySet()) {
            String name = entry.getKey();
            Holder holder = entry.getValue();

            if (!holder.hasPreset()) {
                Bukkit.getLogger().warning("Пресет '" + holder.getVillagerPreset() + "' для Holder '" + name + "' больше не существует.");
            }
        }

        // Проверка всех жителей в мире
        for (World world : Bukkit.getWorlds()) {
            for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
                if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                    String holderName = dataContainer.get(UNIQUE_ID_KEY, PersistentDataType.STRING);
                    Holder holder = holders.get(holderName);

                    if (holder == null) {
                        Bukkit.getLogger().warning("Житель с именем '" + holderName + "' больше не привязан к существующему Holder.");
                        continue;
                    }

                    if (!holder.hasPreset()) {
                        Bukkit.getLogger().warning("Пресет '" + holder.getVillagerPreset() + "' для жителя '" + holderName + "' больше не существует.");
                        continue;
                    }

                    List<MerchantRecipe> recipes = hoarderConfig.getRecipesForVillager(holder.getVillagerPreset());
                    if (!recipes.isEmpty()) {
                        configureVillager(villager, recipes, holder.getName());
                        Bukkit.getLogger().info("Житель с именем '" + holderName + "' обновлён с пресетом '" + holder.getVillagerPreset() + "'.");
                    } else {
                        Bukkit.getLogger().warning("Житель с именем '" + holderName + "' не имеет доступных рецептов.");
                    }
                }
            }
        }
    }


    public HoarderConfig getHoarderConfig() {
        return hoarderConfig;
    }
}
