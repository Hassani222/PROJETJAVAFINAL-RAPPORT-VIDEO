package com.ecole.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique pour les opérations CRUD de base.
 * 
 * @param <T>  Le type d'entité
 * @param <ID> Le type de l'identifiant
 */
public interface GenericDao<T, ID> {

    /**
     * Persiste une nouvelle entité dans la base de données.
     * 
     * @param entity L'entité à créer
     * @return L'entité persistée avec son ID généré
     */
    T create(T entity);

    /**
     * Trouve une entité par son identifiant.
     * 
     * @param id L'identifiant de l'entité
     * @return Optional contenant l'entité si trouvée
     */
    Optional<T> findById(ID id);

    /**
     * Récupère toutes les entités.
     * 
     * @return Liste de toutes les entités
     */
    List<T> findAll();

    /**
     * Met à jour une entité existante.
     * 
     * @param entity L'entité à mettre à jour
     * @return L'entité mise à jour
     */
    T update(T entity);

    /**
     * Supprime une entité.
     * 
     * @param entity L'entité à supprimer
     */
    void delete(T entity);

    /**
     * Supprime une entité par son identifiant.
     * 
     * @param id L'identifiant de l'entité à supprimer
     * @return true si l'entité a été supprimée, false sinon
     */
    boolean deleteById(ID id);

    /**
     * Compte le nombre total d'entités.
     * 
     * @return Le nombre d'entités
     */
    long count();

    /**
     * Vérifie si une entité existe par son identifiant.
     * 
     * @param id L'identifiant à vérifier
     * @return true si l'entité existe
     */
    boolean existsById(ID id);
}
