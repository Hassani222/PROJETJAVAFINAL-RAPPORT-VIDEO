package com.ecole.dao;

import com.ecole.model.AlerteEmotionnelle;
import com.ecole.model.Gravite;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface DAO pour l'entité AlerteEmotionnelle.
 * Étend GenericDao avec des méthodes spécifiques aux alertes.
 */
public interface AlerteEmotionnelleDao extends GenericDao<AlerteEmotionnelle, Long> {

    /**
     * Trouve toutes les alertes d'un élève.
     * 
     * @param eleveId L'ID de l'élève
     * @return Liste des alertes de l'élève
     */
    List<AlerteEmotionnelle> findByEleveId(Long eleveId);

    /**
     * Trouve les alertes par niveau de gravité.
     * 
     * @param gravite Le niveau de gravité
     * @return Liste des alertes avec cette gravité
     */
    List<AlerteEmotionnelle> findByGravite(Gravite gravite);

    /**
     * Trouve les alertes dans une période donnée.
     * 
     * @param debut Date de début
     * @param fin   Date de fin
     * @return Liste des alertes dans cette période
     */
    List<AlerteEmotionnelle> findByPeriode(LocalDateTime debut, LocalDateTime fin);

    /**
     * Trouve les alertes sans intervention.
     * 
     * @return Liste des alertes non traitées
     */
    List<AlerteEmotionnelle> findAlertesNonTraitees();

    /**
     * Trouve les alertes avec intervention.
     * 
     * @return Liste des alertes traitées
     */
    List<AlerteEmotionnelle> findAlertesTraitees();

    /**
     * Trouve les alertes par type d'émotion détectée.
     * 
     * @param emotion Le type d'émotion
     * @return Liste des alertes pour cette émotion
     */
    List<AlerteEmotionnelle> findByEmotion(String emotion);

    /**
     * Trouve les alertes avec un score de confiance supérieur à un seuil.
     * 
     * @param seuil Le score minimum
     * @return Liste des alertes à haute confiance
     */
    List<AlerteEmotionnelle> findByScoreConfianceSuperieur(double seuil);

    /**
     * Compte les alertes par gravité.
     * 
     * @param gravite Le niveau de gravité
     * @return Le nombre d'alertes
     */
    long countByGravite(Gravite gravite);
}
