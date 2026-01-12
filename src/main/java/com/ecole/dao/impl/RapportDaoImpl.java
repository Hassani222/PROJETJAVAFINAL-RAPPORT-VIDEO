package com.ecole.dao.impl;

import com.ecole.dao.RapportDao;
import com.ecole.model.Rapport;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du DAO pour l'entité Rapport.
 */
public class RapportDaoImpl extends GenericDaoImpl<Rapport, Long> implements RapportDao {

    public RapportDaoImpl() {
        super(Rapport.class);
    }

    @Override
    public List<Rapport> findByType(String type) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Rapport r WHERE r.type = :type ORDER BY r.dateGeneration DESC", Rapport.class)
                    .setParameter("type", type)
                    .list();
        }
    }

    @Override
    public List<Rapport> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Rapport r WHERE r.dateGeneration BETWEEN :debut AND :fin ORDER BY r.dateGeneration DESC",
                    Rapport.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    @Override
    public List<Rapport> findByAuteurId(Long utilisateurId) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Rapport r WHERE r.auteur.id = :auteurId ORDER BY r.dateGeneration DESC", Rapport.class)
                    .setParameter("auteurId", utilisateurId)
                    .list();
        }
    }

    @Override
    public List<Rapport> findDerniersRapports(int limit) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Rapport r ORDER BY r.dateGeneration DESC", Rapport.class)
                    .setMaxResults(limit)
                    .list();
        }
    }
}
