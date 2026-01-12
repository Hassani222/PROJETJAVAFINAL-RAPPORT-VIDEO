package com.ecole.dao;

import com.ecole.model.Rapport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface DAO pour l'entité Rapport.
 * Étend GenericDao avec des méthodes spécifiques aux rapports.
 */
public interface RapportDao extends GenericDao<Rapport, Long> {

    /**
     * Trouve les rapports par type.
     * 
     * @param type Le type de rapport
     * @return Liste des rapports de ce type
     */
    List<Rapport> findByType(String type);

    /**
     * Trouve les rapports dans une période donnée.
     * 
     * @param debut Date de début
     * @param fin   Date de fin
     * @return Liste des rapports dans cette période
     */
    List<Rapport> findByPeriode(LocalDateTime debut, LocalDateTime fin);

    /**
     * Trouve les rapports générés par un utilisateur.
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @return Liste des rapports de l'utilisateur
     */
    List<Rapport> findByAuteurId(Long utilisateurId);

    /**
     * Trouve les derniers rapports générés.
     * 
     * @param limit Le nombre maximum de rapports à retourner
     * @return Liste des derniers rapports
     */
    List<Rapport> findDerniersRapports(int limit);
}
