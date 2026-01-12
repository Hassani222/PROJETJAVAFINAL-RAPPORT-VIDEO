package com.ecole.dao.impl;

import com.ecole.dao.InterventionDao;
import com.ecole.model.Intervention;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Intervention.
 */
public class InterventionDaoImpl extends GenericDaoImpl<Intervention, Long> implements InterventionDao {

    public InterventionDaoImpl() {
        super(Intervention.class);
    }

    @Override
    public Optional<Intervention> findByAlerteId(Long alerteId) {
        try (Session session = getSession()) {
            Intervention intervention = session.createQuery(
                    "FROM Intervention i WHERE i.alerte.id = :alerteId", Intervention.class)
                    .setParameter("alerteId", alerteId)
                    .uniqueResult();
            return Optional.ofNullable(intervention);
        }
    }

    @Override
    public List<Intervention> findByEleveId(Long eleveId) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Intervention i WHERE i.alerte.eleve.id = :eleveId ORDER BY i.dateDebut DESC",
                    Intervention.class)
                    .setParameter("eleveId", eleveId)
                    .list();
        }
    }

    @Override
    public List<Intervention> findByStatut(String statut) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Intervention i WHERE i.statut = :statut ORDER BY i.dateDebut DESC", Intervention.class)
                    .setParameter("statut", statut)
                    .list();
        }
    }

    @Override
    public List<Intervention> findByConseillerId(Long conseillerId) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Intervention i WHERE i.conseiller.id = :conseillerId ORDER BY i.dateDebut DESC",
                    Intervention.class)
                    .setParameter("conseillerId", conseillerId)
                    .list();
        }
    }

    @Override
    public List<Intervention> findByPeriode(LocalDateTime debut, LocalDateTime fin) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Intervention i WHERE i.dateDebut BETWEEN :debut AND :fin ORDER BY i.dateDebut DESC",
                    Intervention.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .list();
        }
    }

    @Override
    public List<Intervention> findInterventionsEnCours() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Intervention i WHERE i.statut = 'En cours' ORDER BY i.dateDebut DESC", Intervention.class)
                    .list();
        }
    }

    @Override
    public List<Intervention> findInterventionsTerminees() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Intervention i WHERE i.statut = 'Terminée' OR i.dateFin IS NOT NULL ORDER BY i.dateFin DESC",
                    Intervention.class)
                    .list();
        }
    }

    @Override
    public long countByStatut(String statut) {
        try (Session session = getSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(i) FROM Intervention i WHERE i.statut = :statut", Long.class)
                    .setParameter("statut", statut)
                    .uniqueResult();
            return count != null ? count : 0;
        }
    }
}
