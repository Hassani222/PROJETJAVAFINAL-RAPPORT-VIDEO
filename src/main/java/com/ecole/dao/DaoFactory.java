package com.ecole.dao;

import com.ecole.dao.impl.*;

/**
 * Factory pour créer et obtenir les instances des DAOs.
 * Utilise le pattern Singleton pour chaque DAO.
 */
public class DaoFactory {

    private static UtilisateurDao utilisateurDao;
    private static EleveDao eleveDao;
    private static ConseillerDao conseillerDao;
    private static AlerteEmotionnelleDao alerteEmotionnelleDao;
    private static InterventionDao interventionDao;
    private static NotificationDao notificationDao;
    private static RapportDao rapportDao;

    private DaoFactory() {
    }

    public static synchronized UtilisateurDao getUtilisateurDao() {
        if (utilisateurDao == null) {
            utilisateurDao = new UtilisateurDaoImpl();
        }
        return utilisateurDao;
    }

    public static synchronized EleveDao getEleveDao() {
        if (eleveDao == null) {
            eleveDao = new EleveDaoImpl();
        }
        return eleveDao;
    }

    public static synchronized ConseillerDao getConseillerDao() {
        if (conseillerDao == null) {
            conseillerDao = new ConseillerDaoImpl();
        }
        return conseillerDao;
    }

    public static synchronized AlerteEmotionnelleDao getAlerteEmotionnelleDao() {
        if (alerteEmotionnelleDao == null) {
            alerteEmotionnelleDao = new AlerteEmotionnelleDaoImpl();
        }
        return alerteEmotionnelleDao;
    }

    public static synchronized InterventionDao getInterventionDao() {
        if (interventionDao == null) {
            interventionDao = new InterventionDaoImpl();
        }
        return interventionDao;
    }

    public static synchronized NotificationDao getNotificationDao() {
        if (notificationDao == null) {
            notificationDao = new NotificationDaoImpl();
        }
        return notificationDao;
    }

    public static synchronized RapportDao getRapportDao() {
        if (rapportDao == null) {
            rapportDao = new RapportDaoImpl();
        }
        return rapportDao;
    }
}
