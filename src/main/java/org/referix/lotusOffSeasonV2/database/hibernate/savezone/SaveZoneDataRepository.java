package org.referix.lotusOffSeasonV2.database.hibernate.savezone;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.referix.lotusOffSeasonV2.database.hibernate.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class SaveZoneDataRepository {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();



    public void removeProtectZone(int id) {
        if (sessionFactory == null) {
            getLogger().severe("SessionFactory is not initialized. Cannot remove protect zone.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            SaveZoneData saveZoneData = session.createQuery("FROM SaveZoneData WHERE id = :id", SaveZoneData.class)
                    .setParameter("id", id) // Підставляємо числовий ідентифікатор
                    .uniqueResult();


            if (saveZoneData != null) {
                session.delete(saveZoneData); // Видаляємо знайдений запис
                transaction.commit();
                getLogger().info("Protect zone '" + id + "' has been removed successfully.");
            } else {
                getLogger().warning("Protect zone '" + id + "' not found.");
                if (transaction != null) transaction.rollback();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                getLogger().warning("Transaction rolled back due to an error.");
            }
            e.printStackTrace();
        }
    }


    public void saveStructureData(SaveZoneData saveZoneData) {
        if (sessionFactory == null) {
            getLogger().severe("SessionFactory is not initialized. Cannot save structure data.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(saveZoneData);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                getLogger().warning("Transaction rolled back due to an error.");
            }
            e.printStackTrace();
        }
    }

    public List<SaveZoneData> getAllStructuresData() {
        List<SaveZoneData> saveZoneData = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            saveZoneData = session.createQuery("from SaveZoneData", SaveZoneData.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Ошибка при получении всех данных структур: " + e.getMessage());
        }
        return saveZoneData;
    }

    public List<Pair<Location, Location>> getSaveZoneDataZones() {
        List<Pair<Location, Location>> zones = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<SaveZoneData> structures = session.createQuery(
                            "from SaveZoneData",
                            SaveZoneData.class)
                    .getResultList();

            for (SaveZoneData structure : structures) {
                String[] coordinates = structure.getLocationValue().split("\\|");
                if (coordinates.length == 2) {
                    String[] loc1 = coordinates[0].split(",");
                    String[] loc2 = coordinates[1].split(",");
                    Location location1 = new Location(null, // world can be added dynamically
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


}
