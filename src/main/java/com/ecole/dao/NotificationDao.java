package com.ecole.dao;

import com.ecole.model.Notification;

import java.util.List;

/**
 * Interface DAO pour l'entité Notification.
 * Étend GenericDao avec des méthodes spécifiques aux notifications.
 */
public interface NotificationDao extends GenericDao<Notification, Long> {

    /**
     * Trouve toutes les notifications d'un utilisateur.
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @return Liste des notifications de l'utilisateur
     */
    List<Notification> findByUtilisateurId(Long utilisateurId);

    /**
     * Trouve les notifications non lues d'un utilisateur.
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @return Liste des notifications non lues
     */
    List<Notification> findNonLuesByUtilisateurId(Long utilisateurId);

    /**
     * Trouve les notifications par type.
     * 
     * @param type Le type de notification
     * @return Liste des notifications de ce type
     */
    List<Notification> findByType(String type);

    /**
     * Marque toutes les notifications d'un utilisateur comme lues.
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @return Le nombre de notifications mises à jour
     */
    int marquerToutesCommeLues(Long utilisateurId);

    /**
     * Compte les notifications non lues d'un utilisateur.
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @return Le nombre de notifications non lues
     */
    long countNonLues(Long utilisateurId);

    /**
     * Supprime les anciennes notifications (plus de X jours).
     * 
     * @param joursRetention Nombre de jours de rétention
     * @return Le nombre de notifications supprimées
     */
    int supprimerAnciennesNotifications(int joursRetention);
}
