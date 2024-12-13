package org.referix.lotusOffSeasonV2.command;


import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneData;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveZoneCommand {
    private final SaveZoneDataService service;

    private Location pos1 = null;
    private Location pos2 = null;

    public SaveZoneCommand(SaveZoneDataService service) {
        this.service = service;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("only players can execute this command");
            return true;
        }
        if (!player.hasPermission("lotusOffSeason.savezone")) {
            player.sendMessage("dont have permission");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("no valid command: /lotus savezone pos1|pos2|save <name>");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "pos1":
                pos1 = player.getLocation();
                player.sendMessage("fist point: " + formatLocation(pos1));
                break;

            case "pos2":
                pos2 = player.getLocation();
                player.sendMessage("second points: " + formatLocation(pos2));
                break;

            case "save":
                if (args.length < 3) {
                    player.sendMessage("set name to savezone: /lotus savezone save <назва>");
                    return true;
                }
                saveZoneToDB(player, pos1, pos2, args[1]);
                break;
            case "list":
                player.sendMessage("Укажіть назву для зони. Наприклад: /<команда> save <назва>");
                listZoneToDB(player);
                return true;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage("Укажіть назву для зони. Наприклад: /<команда> save <назва>");
                    return true;
                }
                removeZoneToDB(player, Integer.parseInt(args[1]));
                break;
            default:
                player.sendMessage("Невідома команда. Використовуйте: pos1, pos2 або save.");
        }
        return true;
    }

    private void removeZoneToDB(Player player, int id) {
        service.getAllSaveZones().forEach(saveZoneData -> {
            if (saveZoneData.getStructureId() == id){
                player.sendMessage("Сейв зону удаленно с названием: " + saveZoneData.getStructureName() + " ID: " + saveZoneData.getStructureId());
            }
        });
        service.removeProtectZone(id);
    }

    private void listZoneToDB(Player player) {
        // Отримуємо всі збережені зони з бази даних
        List<SaveZoneData> saveZoneDatas = service.getAllSaveZones();

        // Якщо зон немає, повідомляємо гравця
        if (saveZoneDatas.isEmpty()) {
            player.sendMessage("нет зон.");
            return;
        }

        // Формуємо мапу з імен зон і їх ідентифікаторів
        HashMap<String, Integer> saveId = new HashMap<>();
        saveZoneDatas.forEach(saveZoneData -> saveId.put(saveZoneData.getStructureName(), saveZoneData.getStructureId()));

        // Виводимо гравцю список зон
        player.sendMessage("Список зон:");
        saveId.forEach((name, id) ->
                player.sendMessage(" - Имя: " + name + ", ID: " + id)
        );
    }


    private void saveZoneToDB(Player player, Location pos1, Location pos2, String zoneName) {
        if (pos1 != null && pos2 != null) {
            String positionData = formatLocation(pos1) + "|" + formatLocation(pos2);
            service.saveProtectZone(zoneName, positionData);
            player.sendMessage("Зона '" + zoneName + "' успішно збережена.");
        } else {
            player.sendMessage("Будь ласка, спочатку встановіть обидві точки (pos1 і pos2).");
        }
    }

    private String formatLocation(Location location) {
        return location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

}
