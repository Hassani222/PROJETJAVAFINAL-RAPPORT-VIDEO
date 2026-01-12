package com.ecole.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();

                // Priorité à la variable d'environnement pour Docker/Propriétés
                String dbUrl = System.getenv("DB_URL");
                if (dbUrl != null && !dbUrl.isEmpty()) {
                    configuration.setProperty("hibernate.connection.url", dbUrl);
                }

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                System.err.println("ERREUR HIBERNATE INITIALISATION : " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Échec de l'initialisation de Hibernate", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}