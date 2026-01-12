package com.ecole.dao.impl;

import com.ecole.dao.NotificationDao;
import com.ecole.model.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implémentation du DAO pour l'entité Notification.
 */
public class NotificationDaoImpl extends GenericDaoImpl<Notification, Long> implements NotificationDao {

    public NotificationDaoImpl() {
        super(Notification.class);
    }

    @Override
    public List<Notification> findByUtilisateurId(Long utilisateurId) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Notification n WHERE n.utilisateur.id = :uid ORDER BY n.dateCreation DESC",
                    Notification.class)
                    .setParameter("uid", utilisateurId)
                    .list();
        }
    }

    @Override
    public List<Notification> findNonLuesByUtilisateurId(Long utilisateurId) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Notification n WHERE n.utilisateur.id = :uid AND n.lue = false ORDER BY n.dateCreation DESC",
                    Notification.class)
                    .setParameter("uid", utilisateurId)
                    .list();
        }
    }

    @Override
    public List<Notification> findByType(String type) {
        try (Session session = getSession()) {
            return session.createQuery(
                    "FROM Notification n WHERE n.type = :type ORDER BY n.dateCreation DESC", Notification.class)
                    .setParameter("type", type)
                    .list();
        }
    }

    @Override
    public int marquerToutesCommeLues(Long utilisateurId) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            int updated = session.createMutationQuery(
                    "UPDATE Notification n SET n.lue = true WHERE n.utilisateur.id = :uid AND n.lue = false")
                    .setParameter("uid", utilisateurId)
                    .executeUpdate();
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à jour des notifications", e);
        }
    }

    @Override
    public long countNonLues(Long utilisateurId) {
        try (Session session = getSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(n) FROM Notification n WHERE n.utilisateur.id = :uid AND n.lue = false", Long.class)
                    .setParameter("uid", utilisateurId)
                    .uniqueResult();
            return count != null ? count : 0;
        }
    }

    @Override
    public int supprimerAnciennesNotifications(int joursRetention) {
        Transaction transaction = null;
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            LocalDateTime dateLimit = LocalDateTime.now().minusDays(joursRetention);
            int deleted = session.createMutationQuery(
                    "DELETE FROM Notification n WHERE n.dateCreation < :dateLimit")
                    .setParameter("dateLimit", dateLimit)
                    .executeUpdate();
            transaction.commit();
            return deleted;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression des anciennes notifications", e);
        }
    }
}
