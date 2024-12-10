package org.referix.lotusOffSeasonV2.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.referix.lotusOffSeasonV2.armor.CustomItemManager;
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

        String mainCommand = args[0].toLowerCase();

        // Разделяем команды для armor и horder
        switch (mainCommand) {
            case "armor" -> handleArmorCommands(player, label, args);
            case "horder" -> handleHorderCommands(player, label, args);
            default -> player.sendMessage("Неизвестная команда. Используйте /" + label + " для помощи.");
        }

        return true;
    }

    private void handleArmorCommands(Player player, String label, String[] args) {
        if (args.length < 2) {
            sendArmorHelp(player, label);
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "give" -> handleGiveCommand(player, label, args);
            case "list" -> handleListCommand(player);
            case "help" -> sendArmorHelp(player, label);
            default -> player.sendMessage("Неизвестная команда для armor. Используйте /" + label + " armor help.");
        }
    }

    private void handleHorderCommands(Player player, String label, String[] args) {
        if (args.length < 2) {
            sendHorderHelp(player, label);
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "create" -> handleCreateHolder(player, args);
            case "remove" -> handleRemoveHolder(player, args);
            case "info" -> handleHolderInfo(player, args);
            case "reload" -> handleReloadConfig(player);
            case "help" -> sendHorderHelp(player, label);
            default -> player.sendMessage("Неизвестная команда для horder. Используйте /" + label + " horder help.");
        }
    }

    private void handleGiveCommand(Player player, String label, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Использование: /" + label + " armor give <itemKey>");
            return;
        }

        String itemKey = args[2];
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

    private void sendMainHelp(Player player, String label) {
        player.sendMessage("Основные команды:");
        player.sendMessage("/" + label + " armor - Управление предметами.");
        player.sendMessage("/" + label + " horder - Управление Holder.");
    }

    private void sendArmorHelp(Player player, String label) {
        player.sendMessage("Команды для armor:");
        player.sendMessage("/" + label + " armor give <itemKey> - Выдать предмет.");
        player.sendMessage("/" + label + " armor list - Показать список доступных предметов.");
        player.sendMessage("/" + label + " armor help - Показать помощь для armor.");
    }

    private void sendHorderHelp(Player player, String label) {
        player.sendMessage("Команды для horder:");
        player.sendMessage("/" + label + " horder create <name> [description] - Создать Holder.");
        player.sendMessage("/" + label + " horder remove <name> - Удалить Holder.");
        player.sendMessage("/" + label + " horder info <name> - Показать информацию о Holder.");
        player.sendMessage("/" + label + " horder reload - Перезагрузить конфигурацию Holder.");
    }

    private void handleCreateHolder(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Использование: /<command> horder create <name> [description]");
            return;
        }

        String name = args[2];
        String description = args.length > 3 ? args[3] : "Без описания";

        if (holderManager.getHolderByName(name) != null) {
            player.sendMessage("Holder с именем " + name + " уже существует.");
            return;
        }

        holderManager.addHolder(new Holder(name, name, description));
        holderManager.spawnHolderVillager(holderManager.getHolderByName(name), player.getLocation());
        player.sendMessage("Holder '" + name + "' создан.");
    }

    private void handleRemoveHolder(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Использование: /<command> horder remove <name>");
            return;
        }

        String name = args[2];
        if (holderManager.getHolderByName(name) == null) {
            player.sendMessage("Holder с именем " + name + " не найден.");
            return;
        }

        holderManager.removeHolder(name);
        player.sendMessage("Holder '" + name + "' удалён.");
    }

    private void handleHolderInfo(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Использование: /<command> horder info <name>");
            return;
        }

        String name = args[2];
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

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(List.of("armor", "horder"));
        } else if (args.length == 2) {
            if ("armor".equalsIgnoreCase(args[0])) {
                suggestions.addAll(List.of("give", "list", "help"));
            } else if ("horder".equalsIgnoreCase(args[0])) {
                suggestions.addAll(List.of("create", "remove", "info", "reload", "help"));
            }
        } else if (args.length == 3 && "armor".equalsIgnoreCase(args[0]) && "give".equalsIgnoreCase(args[1])) {
            suggestions.addAll(itemManager.getItems().keySet());
        } else if (args.length == 3 && "horder".equalsIgnoreCase(args[0]) && List.of("remove", "info").contains(args[1])) {
            suggestions.addAll(holderManager.getHolderNames());
        }

        return suggestions;
    }
}
