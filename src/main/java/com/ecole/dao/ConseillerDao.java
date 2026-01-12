package com.ecole.dao;

import com.ecole.model.Conseiller;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Conseiller.
 * Étend GenericDao avec des méthodes spécifiques aux conseillers.
 */
public interface ConseillerDao extends GenericDao<Conseiller, Long> {

    /**
     * Trouve un conseiller par son email.
     * 
     * @param email L'email du conseiller
     * @return Optional contenant le conseiller si trouvé
     */
    Optional<Conseiller> findByEmail(String email);

    /**
     * Trouve les conseillers par spécialité.
     * 
     * @param specialite La spécialité recherchée
     * @return Liste des conseillers avec cette spécialité
     */
    List<Conseiller> findBySpecialite(String specialite);

    /**
     * Trouve les conseillers disponibles (sans intervention en cours).
     * 
     * @return Liste des conseillers disponibles
     */
    List<Conseiller> findConseillersDisponibles();

    /**
     * Trouve les conseillers avec leurs interventions actives.
     * 
     * @return Liste des conseillers ayant des interventions en cours
     */
    List<Conseiller> findConseillersAvecInterventionsActives();
}
