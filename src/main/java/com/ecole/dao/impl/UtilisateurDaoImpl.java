package com.ecole.dao.impl;

import com.ecole.dao.UtilisateurDao;
import com.ecole.model.Role;
import com.ecole.model.Utilisateur;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du DAO pour l'entité Utilisateur.
 */
public class UtilisateurDaoImpl extends GenericDaoImpl<Utilisateur, Long> implements UtilisateurDao {

    public UtilisateurDaoImpl() {
        super(Utilisateur.class);
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        try (Session session = getSession()) {
            Utilisateur user = session.createQuery(
                    "FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<Utilisateur> findBySchoolId(String schoolId) {
        try (Session session = getSession()) {
            Utilisateur user = session.createQuery(
                    "FROM Utilisateur u WHERE u.schoolId = :schoolId", Utilisateur.class)
                    .setParameter("schoolId", schoolId)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }

    @Override
    public List<Utilisateur> findByRole(Role role) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Utilisateur u WHERE u.role = :role", Utilisateur.class)
                    .setParameter("role", role)
                    .list();
        }
    }

    @Override
    public Optional<Utilisateur> authenticate(String email, String password) {
        try (Session session = getSession()) {
            Utilisateur user = session.createQuery(
                    "FROM Utilisateur u WHERE u.email = :email AND u.password = :password", Utilisateur.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }

    @Override
    public boolean emailExists(String email) {
        try (Session session = getSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(u) FROM Utilisateur u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}
