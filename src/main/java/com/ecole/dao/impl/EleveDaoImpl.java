package com.ecole.dao.impl;

import com.ecole.dao.EleveDao;
import com.ecole.model.Eleve;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Eleve.
 */
public class EleveDaoImpl extends GenericDaoImpl<Eleve, Long> implements EleveDao {

    public EleveDaoImpl() {
        super(Eleve.class);
    }

    @Override
    public List<Eleve> findByClasse(String classe) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Eleve e WHERE e.classe = :classe", Eleve.class)
                    .setParameter("classe", classe)
                    .list();
        }
    }

    @Override
    public Optional<Eleve> findBySchoolId(String schoolId) {
        try (Session session = getSession()) {
            Eleve eleve = session.createQuery(
                    "FROM Eleve e WHERE e.schoolId = :schoolId", Eleve.class)
                    .setParameter("schoolId", schoolId)
                    .uniqueResult();
            return Optional.ofNullable(eleve);
        }
    }

    @Override
    public List<Eleve> findByNiveauRisqueSuperieur(int seuil) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Eleve e WHERE e.niveauRisqueMoyen > :seuil ORDER BY e.niveauRisqueMoyen DESC", Eleve.class)
                    .setParameter("seuil", seuil)
                    .list();
        }
    }

    @Override
    public List<Eleve> findByAgeRange(int ageMin, int ageMax) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Eleve e WHERE e.age BETWEEN :ageMin AND :ageMax ORDER BY e.age", Eleve.class)
                    .setParameter("ageMin", ageMin)
                    .setParameter("ageMax", ageMax)
                    .list();
        }
    }

    @Override
    public List<Eleve> findElevesAvecAlertes() {
        try (Session session = getSession()) {
            return session.createQuery(
                    "SELECT DISTINCT e FROM Eleve e JOIN e.historiqueAlertes a", Eleve.class)
                    .list();
        }
    }
}
