package org.referix.lotusOffSeasonV2.safezone;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneData;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneDataService;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneDataServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class SafeZoneManager {

    private static final SafeZoneManager instance = new SafeZoneManager();
    private final List<SafeZone> safeZones = new ArrayList<>();
    private final Plugin plugin = LotusOffSeasonV2.getInstance();
    public static SafeZoneManager getInstance() {
        return instance;
    }

    // Добавление новой зоны
    public void addSafeZone(SafeZone safeZone) {
        safeZones.add(safeZone);
    }

    // Удаление зоны по ID
    public boolean removeSafeZone(int zoneID) {
        return safeZones.removeIf(zone -> zone.getZoneID() == zoneID);
    }

    // Поиск зоны по имени
    public SafeZone getSafeZoneByName(String name) {
        return safeZones.stream()
                .filter(zone -> zone.getZoneName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Проверка, находится ли игрок в какой-либо зоне
    public SafeZone getZoneContainingPlayer(Player player) {
        Location playerLocation = player.getLocation();
        return safeZones.stream()
                .filter(zone -> isLocationInZone(playerLocation,
                        zone.getLocationPair().getLeft(),
                        zone.getLocationPair().getRight()))
                .findFirst()
                .orElse(null);
    }

    // Проверка, находится ли игрок в какой-либо зоне
    public boolean isPlayerInAnyZone(Player player) {
        Location playerLocation = player.getLocation();
        return safeZones.stream()
                .anyMatch(zone -> isLocationInZone(playerLocation,
                        zone.getLocationPair().getLeft(),
                        zone.getLocationPair().getRight()));
    }

    public void initializeZones(SaveZoneDataService saveZoneDataService) {
        List<SaveZoneData> zonesData = saveZoneDataService.getAllSaveZones();

        try {
            for (SaveZoneData data : zonesData) {
                try {
                    // Парсим данные координат
                    List<Pair<Location, Location>> parsedZones = parseZonesFromString(data.getLocationValue());
                    if (!parsedZones.isEmpty()) {
                        // Берём первую пару координат для SafeZone
                        Pair<Location, Location> zoneCoordinates = parsedZones.get(0);
                        SafeZone safeZone = new SafeZone(
                                data.getSafeZoneName(),
                                zoneCoordinates
                        );
                        addSafeZone(safeZone); // Добавляем загруженную зону в менеджер

                        // Успешная инициализация
                        plugin.getLogger().info("Успешно инициализирована зона: "
                                + data.getSafeZoneName() + " (ID: " + data.getSafeZoneId() + ")");
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Ошибка при инициализации зоны: "
                            + data.getSafeZoneName() + " (ID: " + data.getSafeZoneId() + ")");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при загрузке данных зон безопасности.");
            e.printStackTrace();
        } finally {
            plugin.getLogger().info("Процесс инициализации зон завершён.");
        }
    }

    public List<Pair<Location, Location>> parseZonesFromString(String data) {
        List<Pair<Location, Location>> zones = new ArrayList<>();
        try {
            String[] zoneEntries = data.split(";"); // Разделяем по ';', чтобы получить отдельные зоны
            for (String entry : zoneEntries) {
                String[] coordinates = entry.split("\\|"); // Разделяем первую и вторую координаты
                if (coordinates.length == 2) {
                    String[] loc1 = coordinates[0].split(","); // Координаты первой точки
                    String[] loc2 = coordinates[1].split(","); // Координаты второй точки

                    Location location1 = new Location(null, // `null` - добавьте мир, если необходимо
                            Integer.parseInt(loc1[0].trim()),
                            Integer.parseInt(loc1[1].trim()),
                            Integer.parseInt(loc1[2].trim()));

                    Location location2 = new Location(null,
                            Integer.parseInt(loc2[0].trim()),
                            Integer.parseInt(loc2[1].trim()),
                            Integer.parseInt(loc2[2].trim()));

                    zones.add(Pair.of(location1, location2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zones;
    }



    // Проверка, находится ли местоположение внутри зоны
    private boolean isLocationInZone(Location playerLoc, Location posLoc1, Location posLoc2) {
        int x1 = Math.min(posLoc1.getBlockX(), posLoc2.getBlockX());
        int y1 = Math.min(posLoc1.getBlockY(), posLoc2.getBlockY());
        int z1 = Math.min(posLoc1.getBlockZ(), posLoc2.getBlockZ());

        int x2 = Math.max(posLoc1.getBlockX(), posLoc2.getBlockX());
        int y2 = Math.max(posLoc1.getBlockY(), posLoc2.getBlockY());
        int z2 = Math.max(posLoc1.getBlockZ(), posLoc2.getBlockZ());

        int px = playerLoc.getBlockX();
        int py = playerLoc.getBlockY();
        int pz = playerLoc.getBlockZ();

        return (px >= x1 && px <= x2) &&
                (py >= y1 && py <= y2) &&
                (pz >= z1 && pz <= z2);
    }

    // Получение всех зон
    public List<SafeZone> getAllSafeZones() {
        return new ArrayList<>(safeZones);
    }
}
