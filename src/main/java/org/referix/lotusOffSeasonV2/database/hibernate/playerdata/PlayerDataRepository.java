package org.referix.lotusOffSeasonV2.database.hibernate.playerdata;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.referix.lotusOffSeasonV2.database.hibernate.HibernateUtil;

public class PlayerDataRepository {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public void savePlayerData(PlayerDataBase playerDataBase) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(playerDataBase); // saveOrUpdate замість save
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public PlayerDataBase getPlayerData(String playerName) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(PlayerDataBase.class, playerName);
        }
    }

    public void updatePlayerData(PlayerDataBase playerDataBase) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(playerDataBase);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deletePlayerData(String playerName) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            PlayerDataBase playerDataBase = session.get(PlayerDataBase.class, playerName);
            if (playerDataBase != null) {
                session.delete(playerDataBase);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}

