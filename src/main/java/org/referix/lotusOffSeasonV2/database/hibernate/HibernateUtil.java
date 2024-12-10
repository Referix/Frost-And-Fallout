package org.referix.lotusOffSeasonV2.database.hibernate;



import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;
import org.referix.lotusOffSeasonV2.database.hibernate.playerdata.PlayerDataBase;
import org.referix.lotusOffSeasonV2.database.hibernate.savezone.SaveZoneData;
import org.referix.lotusOffSeasonV2.database.hibernate.structures.StructuresData;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();

            // Налаштування бази даних
            configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
            configuration.setProperty("hibernate.connection.url", "jdbc:sqlite:plugins/LotusOffSeasonV2/database.db");
            configuration.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");

            // Показати SQL-запити в консолі
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.format_sql", "false");

            // Додаємо анотовані класи
            configuration.addAnnotatedClass(PlayerDataBase.class);
            configuration.addAnnotatedClass(StructuresData.class); // Додайте сюди інші сутності
            configuration.addAnnotatedClass(SaveZoneData.class); // Додайте сюди інші сутності

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            getLogger().info("Hibernate initialized successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize Hibernate!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(LotusOffSeasonV2.getInstance());
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed" + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

