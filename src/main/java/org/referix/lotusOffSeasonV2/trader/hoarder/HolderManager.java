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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolderManager implements Listener {
    private final Map<String, Holder> holders = new HashMap<>();
    private final NamespacedKey UNIQUE_ID_KEY; // Ключ для PersistentDataContainer
    private final HoarderConfig hoarderConfig; // Посилання на конфігурацію

    // Конструктор HolderManager із передачею конфігурації
    public HolderManager(HoarderConfig hoarderConfig) {
        this.hoarderConfig = hoarderConfig;
        this.UNIQUE_ID_KEY = new NamespacedKey(LotusOffSeasonV2.getInstance(), "custom_holder");

        // Регистрация событий
        Bukkit.getPluginManager().registerEvents(this, LotusOffSeasonV2.getInstance());
    }

    // Додавання Holder до мапи
    public void addHolder(Holder holder) {
        String id = holder.getName(); // Використовуємо ім'я як унікальний ID
        if (holders.containsKey(id)) {
            Bukkit.getLogger().warning("Holder з ім'ям (ID) " + id + " вже існує.");
            return;
        }
        holders.put(id, holder);
        Bukkit.getLogger().info("Holder доданий: " + holder);
    }

    // Видалення Holder із мапи та видалення пов'язаного Villager
    public void removeHolder(String name) {
        Holder removedHolder = holders.remove(name);
        if (removedHolder != null) {
            Bukkit.getLogger().info("Holder видалений: " + removedHolder);

            // Видаляємо Villager, пов'язаного з Holder
            Bukkit.getWorlds().forEach(world ->
                    world.getEntitiesByClass(Villager.class).forEach(villager -> {
                        PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
                        if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                            String villagerName = dataContainer.get(UNIQUE_ID_KEY, PersistentDataType.STRING);
                            if (villagerName != null && villagerName.equals(name)) {
                                villager.remove();
                                Bukkit.getLogger().info("Villager з ім'ям (ID) " + name + " видалений.");
                            }
                        }
                    })
            );
        } else {
            Bukkit.getLogger().warning("Holder з ім'ям (ID) " + name + " не знайдено.");
        }
    }

    // Пошук Holder за ім'ям (ID)
    public Holder getHolderByName(String name) {
        return holders.get(name);
    }

    // Спавн Villager, пов'язаного з Holder
    public Villager spawnHolderVillager(Holder holder, Location location) {
        if (holder == null || location == null) {
            Bukkit.getLogger().warning("Holder або локація не можуть бути null.");
            return null;
        }

        // Створення Villager
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        // Устанавливаем уникальный ID в PersistentDataContainer
        PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
        dataContainer.set(UNIQUE_ID_KEY, PersistentDataType.STRING, holder.getName());

        configureVillager(villager, holder);

        Bukkit.getLogger().info("Villager створений для Holder " + holder.getName() + ".");
        return villager;
    }

    // Конфігурація Villager
    private void configureVillager(Villager villager, Holder holder) {
        // Встановлюємо кастомне ім'я
        villager.setCustomName(holder.getName());
        villager.setCustomNameVisible(true);

        // Робимо невразливим, нерухомим, без інтелекту
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setVillagerType(Villager.Type.SWAMP);
        villager.setHealth(20); // Мінімальне HP
        villager.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        villager.setCollidable(false);

        // Завантажуємо трейди для Holder із конфігурації
        List<MerchantRecipe> recipes = getHolderRecipes(holder);
        if (recipes != null && !recipes.isEmpty()) {
            villager.setRecipes(recipes);
            Bukkit.getLogger().info("Трейди для Holder " + holder.getName() + " встановлені.");
        } else {
            Bukkit.getLogger().warning("Не вдалося знайти трейди для Holder " + holder.getName() + ".");
        }
    }

    // Метод для отримання трейдів із HoarderConfig
    private List<MerchantRecipe> getHolderRecipes(Holder holder) {
        return hoarderConfig.getTraderRecipes();
    }

    // Захист жителів від пошкоджень
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            // Проверяем наличие уникального ID в PersistentDataContainer
            PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
            if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                event.setCancelled(true);
                Bukkit.getLogger().info("Попытка повредить защищённого жителя заблокирована.");
            }
        }
    }

    // Перезавантаження конфігурації та перевірка жителів
    public void reloadConfigAndVillagers() {
        // Перезавантаження конфігурації
        hoarderConfig.reloadConfig();
        Bukkit.getLogger().info("Конфігурацію hoarder_trader.yml успішно перезавантажено.");

        // Перевірка всіх жителів у світі
        for (World world : Bukkit.getWorlds()) {
            for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
                if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                    String holderName = dataContainer.get(UNIQUE_ID_KEY, PersistentDataType.STRING);
                    if (holderName != null) {
                        Holder holder = holders.get(holderName);
                        if (holder != null) {
                            configureVillager(villager, holder);
                            Bukkit.getLogger().info("Житель з ID '" + holderName + "' успішно оновлений.");
                        } else {
                            Bukkit.getLogger().warning("Житель з ID '" + holderName + "' не має відповідного Holder.");
                        }
                    }
                }
            }
        }
    }

    // Повертає список усіх імен Holder
    public List<String> getHolderNames() {
        return new ArrayList<>(holders.keySet());
    }

    public void saveHoldersToFile() {
        File file = new File(LotusOffSeasonV2.getInstance().getDataFolder(), "holders.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, Holder> entry : holders.entrySet()) {
            String holderName = entry.getKey();
            Holder holder = entry.getValue();

            // Сохраняем данные о Holder
            ConfigurationSection section = config.createSection(holderName);
            section.set("name", holder.getName());
            section.set("description", holder.getDescription());
        }

        try {
            config.save(file);
            Bukkit.getLogger().info("Данные Holders успешно сохранены.");
        } catch (IOException e) {
            Bukkit.getLogger().severe("Ошибка при сохранении данных Holders: " + e.getMessage());
        }
    }

    public void loadHoldersFromFile() {
        File file = new File(LotusOffSeasonV2.getInstance().getDataFolder(), "holders.yml");
        if (!file.exists()) {
            Bukkit.getLogger().warning("Файл holders.yml не найден. Список Holders пуст.");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String holderName : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(holderName);
            if (section != null) {
                String name = section.getString("name");
                String description = section.getString("description");

                // Восстанавливаем Holder и добавляем в карту
                Holder holder = new Holder(name, name, description);
                holders.put(name, holder);
            }
        }

        Bukkit.getLogger().info("Данные Holders успешно загружены.");
    }

    public void refreshVillagerTrades() {
        Bukkit.getScheduler().runTaskTimer(LotusOffSeasonV2.getInstance(), () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Villager villager : world.getEntitiesByClass(Villager.class)) {
                    PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
                    if (dataContainer.has(UNIQUE_ID_KEY, PersistentDataType.STRING)) {
                        String holderName = dataContainer.get(UNIQUE_ID_KEY, PersistentDataType.STRING);
                        Holder holder = holders.get(holderName);

                        if (holder != null) {
                            List<MerchantRecipe> recipes = villager.getRecipes();
                            boolean updated = false; // Флаг, чтобы понять, были ли обновлены рецепты

                            for (MerchantRecipe recipe : recipes) {
                                if (recipe.getUses() >= recipe.getMaxUses()) { // Проверяем, исчерпан ли maxUsage
                                    recipe.setUses(0); // Сбрасываем использование
                                    updated = true;
                                }
                            }

                            if (updated) { // Обновляем рецепты только если что-то изменилось
                                villager.setRecipes(recipes);
                                Bukkit.getLogger().info("Ресурсы трейдов для жителя '" + holderName + "' обновлены.");
                            }
                        }
                    }
                }
            }
        }, 0L, 2000L); // Проверка каждые 10 секунд (200 тиков)
    }



}
