package com.ecole.dao;

import com.ecole.model.Eleve;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Eleve.
 * Étend GenericDao avec des méthodes spécifiques aux élèves.
 */
public interface EleveDao extends GenericDao<Eleve, Long> {

    /**
     * Trouve tous les élèves d'une classe.
     * 
     * @param classe Le nom de la classe
     * @return Liste des élèves de cette classe
     */
    List<Eleve> findByClasse(String classe);

    /**
     * Trouve un élève par son schoolId.
     * 
     * @param schoolId L'identifiant scolaire
     * @return Optional contenant l'élève si trouvé
     */
    Optional<Eleve> findBySchoolId(String schoolId);

    /**
     * Trouve les élèves avec un niveau de risque supérieur à un seuil.
     * 
     * @param seuil Le niveau de risque minimum
     * @return Liste des élèves à risque
     */
    List<Eleve> findByNiveauRisqueSuperieur(int seuil);

    /**
     * Trouve les élèves par tranche d'âge.
     * 
     * @param ageMin Âge minimum
     * @param ageMax Âge maximum
     * @return Liste des élèves dans cette tranche d'âge
     */
    List<Eleve> findByAgeRange(int ageMin, int ageMax);

    /**
     * Trouve les élèves qui ont des alertes actives.
     * 
     * @return Liste des élèves avec alertes
     */
    List<Eleve> findElevesAvecAlertes();
}
