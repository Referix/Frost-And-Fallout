package org.referix.lotusOffSeasonV2.database.hibernate.structures;

import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.referix.lotusOffSeasonV2.database.hibernate.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class StructureDataRepository {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public void saveStructureData(StructuresData structureData) {
        if (sessionFactory == null) {
            getLogger().severe("SessionFactory is not initialized. Cannot save structure data.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(structureData);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
                getLogger().warning("Transaction rolled back due to an error.");
            }
            e.printStackTrace();
        }
    }

    public List<StructuresData> getAllStructuresData() {
        List<StructuresData> structures = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            structures = session.createQuery("from StructuresData", StructuresData.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Ошибка при получении всех данных структур: " + e.getMessage());
        }
        return structures;
    }

    public List<StructuresData> getAllStructuresDataByName(String name) {
        List<StructuresData> structures = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            structures = session.createQuery("from StructuresData where structureName = :name", StructuresData.class)
                    .setParameter("name", name)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return structures;
    }


    public List<Triple<Location, Location, Double>> getRadiationZones() {
        List<Triple<Location, Location, Double>> zones = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<StructuresData> structures = session.createQuery(
                            "from StructuresData where isRadiation >= 1", // Зміна умови
                            StructuresData.class)
                    .getResultList();

            for (StructuresData structure : structures) {
                String[] coordinates = structure.getLocationValue().split("\\|");
                if (coordinates.length == 2) {
                    String[] loc1 = coordinates[0].split(",");
                    String[] loc2 = coordinates[1].split(",");
                    Location location1 = new Location(null, // Світ можна додати динамічно
                            Integer.parseInt(loc1[0].trim()),
                            Integer.parseInt(loc1[1].trim()),
                            Integer.parseInt(loc1[2].trim()));
                    Location location2 = new Location(null,
                            Integer.parseInt(loc2[0].trim()),
                            Integer.parseInt(loc2[1].trim()),
                            Integer.parseInt(loc2[2].trim()));

                    // Додаємо зону разом із значенням isRadiation
                    zones.add(Triple.of(location1, location2, structure.getRadiation()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zones;
    }





}
