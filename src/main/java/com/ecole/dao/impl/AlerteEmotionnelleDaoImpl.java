package com.ecole.dao.impl;

import com.ecole.dao.AlerteEmotionnelleDao;
import com.ecole.model.AlerteEmotionnelle;
import com.ecole.model.Gravite;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du DAO pour l'entité AlerteEmotionnelle.
 */
public class AlerteEmotionnelleDaoImpl extends GenericDaoImpl<AlerteEmotionnelle, Long>
        implements AlerteEmotionnelleDao {

    public AlerteEmotionnelleDaoImpl() {
        super(AlerteEmotionnelle.class);
    }

    @Override
    public List<AlerteEmotionnelle> findByEleveId(Long eleveId) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.eleve.id = :eleveId ORDER BY a.dateDetection DESC",
                    AlerteEmotionnelle.class)
                    .setParameter("eleveId", eleveId)
                    .list();
        }
    }

    @Override
    public List<AlerteEmotionnelle> findByGravite(Gravite gravite) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.gravite = :gravite ORDER BY a.dateDetection DESC",
                    AlerteEmotionnelle.class)
                    .setParameter("gravite", gravite)
                    .list();
        }
    }

    @Override
    public List<AlerteEmotionnelle> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.dateDetection BETWEEN :debut AND :fin ORDER BY a.dateDetection DESC",
                    AlerteEmotionnelle.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    @Override
    public List<AlerteEmotionnelle> findAlertesNonTraitees() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.intervention IS NULL ORDER BY a.dateDetection DESC",
                    AlerteEmotionnelle.class)
                    .list();
        }
    }

    @Override
    public List<AlerteEmotionnelle> findAlertesTraitees() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.intervention IS NOT NULL ORDER BY a.dateDetection DESC",
                    AlerteEmotionnelle.class)
                    .list();
        }
    }

    @Override
    public List<AlerteEmotionnelle> findByEmotion(String emotion) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.emotionDetectee LIKE :emotion ORDER BY a.dateDetection DESC",
                    AlerteEmotionnelle.class)
                    .setParameter("emotion", "%" + emotion + "%")
                    .list();
        }
    }

    @Override
    public List<AlerteEmotionnelle> findByScoreConfianceSuperieur(double seuil) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM AlerteEmotionnelle a WHERE a.scoreConfianceIA > :seuil ORDER BY a.scoreConfianceIA DESC",
                    AlerteEmotionnelle.class)
                    .setParameter("seuil", seuil)
                    .list();
        }
    }

    @Override
    public long countByGravite(Gravite gravite) {
        try (Session session = getSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(a) FROM AlerteEmotionnelle a WHERE a.gravite = :gravite", Long.class)
                    .setParameter("gravite", gravite)
                    .uniqueResult();
            return count != null ? count : 0;
        }
    }
}
