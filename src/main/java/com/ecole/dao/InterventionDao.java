package com.ecole.dao;

import com.ecole.model.Intervention;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Intervention.
 * Étend GenericDao avec des méthodes spécifiques aux interventions.
 */
public interface InterventionDao extends GenericDao<Intervention, Long> {

    /**
     * Trouve l'intervention liée à une alerte.
     * 
     * @param alerteId L'ID de l'alerte
     * @return Optional contenant l'intervention si trouvée
     */
    Optional<Intervention> findByAlerteId(Long alerteId);

    /**
     * Trouve les interventions d'un élève.
     * 
     * @param eleveId L'ID de l'élève
     * @return Liste des interventions de l'élève
     */
    List<Intervention> findByEleveId(Long eleveId);

    /**
     * Trouve les interventions par statut.
     * 
     * @param statut Le statut (ex: "En cours", "Terminée")
     * @return Liste des interventions avec ce statut
     */
    List<Intervention> findByStatut(String statut);

    /**
     * Trouve les interventions d'un conseiller.
     * 
     * @param conseillerId L'ID du conseiller
     * @return Liste des interventions du conseiller
     */
    List<Intervention> findByConseillerId(Long conseillerId);

    /**
     * Trouve les interventions dans une période donnée.
     * 
     * @param debut Date de début
     * @param fin   Date de fin
     * @return Liste des interventions dans cette période
     */
    List<Intervention> findByPeriode(LocalDateTime debut, LocalDateTime fin);

    /**
     * Trouve les interventions en cours.
     * 
     * @return Liste des interventions actives
     */
    List<Intervention> findInterventionsEnCours();

    /**
     * Trouve les interventions terminées.
     * 
     * @return Liste des interventions terminées
     */
    List<Intervention> findInterventionsTerminees();

    /**
     * Compte les interventions par statut.
     * 
     * @param statut Le statut à compter
     * @return Le nombre d'interventions
     */
    long countByStatut(String statut);
}
