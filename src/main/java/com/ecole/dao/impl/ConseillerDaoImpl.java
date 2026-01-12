package com.ecole.dao.impl;

import com.ecole.dao.ConseillerDao;
import com.ecole.model.Conseiller;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Conseiller.
 */
public class ConseillerDaoImpl extends GenericDaoImpl<Conseiller, Long> implements ConseillerDao {

    public ConseillerDaoImpl() {
        super(Conseiller.class);
    }

    @Override
    public Optional<Conseiller> findByEmail(String email) {
        try (Session session = getSession()) {
            Conseiller conseiller = session.createQuery(
                    "FROM Conseiller c WHERE c.email = :email", Conseiller.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(conseiller);
        }
    }

    @Override
    public List<Conseiller> findBySpecialite(String specialite) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Conseiller c WHERE c.specialite LIKE :specialite", Conseiller.class)
                    .setParameter("specialite", "%" + specialite + "%")
                    .list();
        }
    }

    @Override
    public List<Conseiller> findConseillersDisponibles() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "SELECT c FROM Conseiller c WHERE c.id NOT IN " +
                            "(SELECT DISTINCT i.conseiller.id FROM Intervention i WHERE i.statut = 'En cours' AND i.conseiller IS NOT NULL)",
                    Conseiller.class)
                    .list();
        }
    }

    @Override
    public List<Conseiller> findConseillersAvecInterventionsActives() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "SELECT DISTINCT c FROM Conseiller c JOIN Intervention i ON i.conseiller.id = c.id " +
                            "WHERE i.statut = 'En cours'",
                    Conseiller.class)
                    .list();
        }
    }
}
