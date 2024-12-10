package org.referix.lotusOffSeasonV2.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.referix.lotusOffSeasonV2.item.CustomItemManager;
import org.referix.lotusOffSeasonV2.trader.hoarder.Holder;
import org.referix.lotusOffSeasonV2.trader.hoarder.HolderManager;

import java.util.ArrayList;
import java.util.List;

public class MainCommand extends AbstractCommand {
    private final CustomItemManager itemManager;
    private final HolderManager holderManager;

    public MainCommand(String command, CustomItemManager itemManager, HolderManager holderManager) {
        super(command);
        this.itemManager = itemManager;
        this.holderManager = holderManager;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду можно выполнять только как игрок.");
            return true;
        }

        if (args.length < 1) {
            sendMainHelp(player, label);
            return true;
        }

        String category = args[0].toLowerCase();

        switch (category) {
            case "item" -> handleItemCommands(player, label, args);
            case "villager" -> handleVillagerCommands(player, label, args);
            default -> player.sendMessage("Неизвестная категория. Используйте /" + label + " для помощи.");
        }

        return true;
    }


    // --- Main help command ----
    private void sendMainHelp(Player player, String label) {
        player.sendMessage("§aОсновные команды:");
        player.sendMessage("§7/" + label + " armor - Управление предметами (броня).");
        player.sendMessage("§7/" + label + " horder - Управление торговцами (Holder).");
        player.sendMessage("§7/" + label + " help - Показать эту справку.");
    }

    //---  item ---
    private void handleItemCommands(Player player, String label, String[] args) {
        if (args.length < 2) {
            sendItemHelp(player, label);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "armor" -> handleArmorCommands(player, label, args);
            case "view" -> handleViewCommands(player, label, args);
            case "eat" -> handleEatCommands(player, label, args);
            default -> player.sendMessage("Неизвестная команда для item. Используйте /" + label + " item help.");
        }
    }

    private void sendItemHelp(Player player, String label) {
        player.sendMessage("§aКоманды для item:");
        player.sendMessage("§7/" + label + " item armor - Управление предметами брони.");
        player.sendMessage("§7/" + label + " item view - Управление view item.");
        player.sendMessage("§7/" + label + " item eat - Управление съедобными предметами.");
        player.sendMessage("§7/" + label + " item help - Показать помощь для item.");
    }



    // --- View Commands ---
    private void handleViewCommands(Player player, String label, String[] args) {
        if (args.length < 3) {
            sendViewHelp(player, label);
            return;
        }

        String action = args[2].toLowerCase();

        switch (action) {
            case "give" -> handleViewGiveCommand(player, label, args);
            case "list" -> handleViewListCommand(player);
            case "help" -> sendViewHelp(player, label);
            default -> player.sendMessage("Неизвестная команда для view. Используйте /" + label + " view help.");
        }
    }

    private void handleViewGiveCommand(Player player, String label, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Использование: /" + label + " view give <itemKey>");
            return;
        }

        String itemKey = args[3];
        var viewItem = itemManager.getViewItem(itemKey);

        if (viewItem == null) {
            player.sendMessage("View item с ключом '" + itemKey + "' не найден.");
            return;
        }

        ItemStack itemStack = viewItem.getItemStack();
        if (itemStack == null) {
            player.sendMessage("View item с ключом '" + itemKey + "' имеет некорректный ItemStack.");
            return;
        }

        player.getInventory().addItem(itemStack);
        player.sendMessage("Вам выдан view item: " + (itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName() : "Без имени"));
    }


    private void handleViewListCommand(Player player) {
        player.sendMessage("Доступные view items:");
        itemManager.getViewItems().keySet().forEach(key -> player.sendMessage("- " + key));
    }

    private void sendViewHelp(Player player, String label) {
        player.sendMessage("Команды для view:");
        player.sendMessage("/" + label + " view give <itemKey> - Выдать view item.");
        player.sendMessage("/" + label + " view list - Показать список доступных view items.");
        player.sendMessage("/" + label + " view help - Показать помощь для view.");
    }



    // --- Armor Commands ---
    private void handleArmorCommands(Player player, String label, String[] args) {
        if (args.length < 4) {
            sendArmorHelp(player, label);
            return;
        }

        String action = args[2].toLowerCase();

        switch (action) {
            case "give" -> handleGiveCommand(player, label, args);
            case "list" -> handleListCommand(player);
            case "help" -> sendArmorHelp(player, label);
            default -> player.sendMessage("Неизвестная команда для armor. Используйте /" + label + " armor help.");
        }
    }

    private void handleGiveCommand(Player player, String label, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Использование: /" + label + " armor give <itemKey>");
            return;
        }

        String itemKey = args[3];
        var customItem = itemManager.getItem(itemKey);

        if (customItem == null) {
            player.sendMessage("Предмет с ключом '" + itemKey + "' не найден.");
            return;
        }

        player.getInventory().addItem(customItem.getItemStack());
        player.sendMessage("Вам выдан предмет: " + customItem.getItemStack().getItemMeta().getDisplayName());
    }

    private void handleListCommand(Player player) {
        player.sendMessage("Доступные предметы:");
        itemManager.getItems().keySet().forEach(key -> player.sendMessage("- " + key));
    }

    private void sendArmorHelp(Player player, String label) {
        player.sendMessage("Команды для armor:");
        player.sendMessage("/" + label + " armor give <itemKey> - Выдать предмет.");
        player.sendMessage("/" + label + " armor list - Показать список доступных предметов.");
        player.sendMessage("/" + label + " armor help - Показать помощь для armor.");
    }

    // --- Eat Commands ---

    private void handleEatCommands(Player player, String label, String[] args) {
        if (args.length < 4) {
            sendEatHelp(player, label);
            return;
        }

        String action = args[2].toLowerCase();

        switch (action) {
            case "give" -> handleEatGiveCommand(player, label, args);
            case "list" -> handleEatListCommand(player);
            case "help" -> sendEatHelp(player, label);
            default -> player.sendMessage("Неизвестная команда для eat. Используйте /" + label + " eat help.");
        }
    }
    private void handleEatGiveCommand(Player player, String label, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Использование: /" + label + " eat give <itemKey>");
            return;
        }

        String itemKey = args[3];
        var eatItem = itemManager.getEatItem(itemKey);

        if (eatItem == null) {
            player.sendMessage("Eat item с ключом '" + itemKey + "' не найден.");
            return;
        }

        ItemStack itemStack = eatItem.getItemStack();
        if (itemStack == null) {
            player.sendMessage("Eat item с ключом '" + itemKey + "' имеет некорректный ItemStack.");
            return;
        }

        player.getInventory().addItem(itemStack);
        player.sendMessage("Вам выдан eat item: " + (itemStack.getItemMeta() != null ? itemStack.getItemMeta().getDisplayName() : "Без имени"));
    }

    private void handleEatListCommand(Player player) {
        player.sendMessage("Доступные eat items:");
        itemManager.getEatItems().keySet().forEach(key -> player.sendMessage("- " + key));
    }

    private void sendEatHelp(Player player, String label) {
        player.sendMessage("Команды для eat:");
        player.sendMessage("/" + label + " eat give <itemKey> - Выдать eat item.");
        player.sendMessage("/" + label + " eat list - Показать список доступных eat items.");
        player.sendMessage("/" + label + " eat help - Показать помощь для eat.");
    }


    // -- Villager ---
    private void handleVillagerCommands(Player player, String label, String[] args) {
        if (args.length < 2) {
            sendVillagerHelp(player, label);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "horder" -> handleHorderCommands(player, label, args);
            default -> player.sendMessage("Неизвестная команда для villager. Используйте /" + label + " villager help.");
        }
    }

    private void sendVillagerHelp(Player player, String label) {
        player.sendMessage("§aКоманды для villager:");
        player.sendMessage("§7/" + label + " villager horder - Управление торговцами (Holder).");
        player.sendMessage("§7/" + label + " villager help - Показать помощь для villager.");
    }


    // --- Horder Commands ---
    private void handleHorderCommands(Player player, String label, String[] args) {
        if (args.length < 4) {
            sendHorderHelp(player, label);
            return;
        }

        String action = args[2].toLowerCase();

        switch (action) {
            case "create" -> handleCreateHolder(player, args);
            case "remove" -> handleRemoveHolder(player, args);
            case "info" -> handleHolderInfo(player, args);
            case "reload" -> handleReloadConfig(player);
            case "help" -> sendHorderHelp(player, label);
            default -> player.sendMessage("Неизвестная команда для horder. Используйте /" + label + " horder help.");
        }
    }

    private void handleCreateHolder(Player player, String[] args) {
        if (args.length < 5) {
            player.sendMessage("Использование: /<command> horder create <name> [preset]");
            return;
        }

        String name = args[3];
        String preset = args.length > 3 ? args[4] : null;

        if (holderManager.getHolderByName(name) != null) {
            player.sendMessage("Holder с именем " + name + " уже существует.");
            return;
        }

        holderManager.spawnHolderVillager(name, preset, player.getLocation());
        player.sendMessage("Holder '" + name + "' создан.");
    }

    private void handleRemoveHolder(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Использование: /<command> horder remove <name>");
            return;
        }

        String name = args[3];
        if (holderManager.getHolderByName(name) == null) {
            player.sendMessage("Holder с именем " + name + " не найден.");
            return;
        }

        holderManager.removeHolder(name);
        player.sendMessage("Holder '" + name + "' удалён.");
    }

    private void handleHolderInfo(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Использование: /<command> horder info <name>");
            return;
        }

        String name = args[3];
        Holder holder = holderManager.getHolderByName(name);
        if (holder == null) {
            player.sendMessage("Holder с именем " + name + " не найден.");
            return;
        }

        player.sendMessage("Информация о Holder:");
        player.sendMessage("Имя: " + holder.getName());
        player.sendMessage("Описание: " + holder.getDescription());
    }

    private void handleReloadConfig(Player player) {
        holderManager.reloadConfigAndVillagers();
        player.sendMessage("Конфигурация перезагружена.");
    }

    private void sendHorderHelp(Player player, String label) {
        player.sendMessage("Команды для horder:");
        player.sendMessage("/" + label + " horder create <name> [preset] - Создать Holder с пресетом рецептов.");
        player.sendMessage("/" + label + " horder remove <name> - Удалить Holder.");
        player.sendMessage("/" + label + " horder info <name> - Показать информацию о Holder.");
        player.sendMessage("/" + label + " horder reload - Перезагрузить конфигурацию Holder.");
    }





    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Предлагаем категории верхнего уровня
            suggestions.addAll(List.of("item", "villager"));
        } else if (args.length == 2) {
            String category = args[0].toLowerCase();
            if ("item".equals(category)) {
                suggestions.addAll(List.of("armor", "view", "eat", "help"));
            } else if ("villager".equals(category)) {
                suggestions.addAll(List.of("horder", "help"));
            }
        } else if (args.length == 3) {
            String category = args[0].toLowerCase();
            String subCommand = args[1].toLowerCase();

            if ("item".equals(category)) {
                suggestions.addAll(List.of("give")); // Добавить сюда команду "give"
                switch (subCommand) {
                    case "armor", "view", "eat" -> suggestions.addAll(List.of("give", "list", "help"));
                }
            } else if ("villager".equals(category) && "horder".equals(subCommand)) {
                suggestions.addAll(List.of("create", "remove", "info", "list"));
            }
        } else if (args.length == 4) {
            String category = args[0].toLowerCase();
            String subCommand = args[1].toLowerCase();
            String action = args[2].toLowerCase();

            if ("item".equals(category)) {
                switch (subCommand) {
                    case "armor" -> {
                        if ("give".equals(action)) {
                            suggestions.addAll(itemManager.getItems().keySet());
                        }
                    }
                    case "view" -> {
                        if ("give".equals(action)) {
                            suggestions.addAll(itemManager.getViewItems().keySet());
                        }
                    }
                    case "eat" -> {
                        if ("give".equals(action)) {
                            suggestions.addAll(itemManager.getEatItems().keySet());
                        }
                    }
                }
            } else if ("villager".equals(category) && "horder".equals(subCommand)) {
                if ("remove".equals(action) || "info".equals(action)) {
                    suggestions.addAll(holderManager.getHolderNames());
                }
            }
        }

        return suggestions;
    }



}
