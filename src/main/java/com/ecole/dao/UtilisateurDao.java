package com.ecole.dao;

import com.ecole.model.Role;
import com.ecole.model.Utilisateur;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'entité Utilisateur.
 * Étend GenericDao avec des méthodes spécifiques aux utilisateurs.
 */
public interface UtilisateurDao extends GenericDao<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son email.
     * 
     * @param email L'email de l'utilisateur
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Trouve un utilisateur par son schoolId.
     * 
     * @param schoolId L'identifiant scolaire
     * @return Optional contenant l'utilisateur si trouvé
     */
    Optional<Utilisateur> findBySchoolId(String schoolId);

    /**
     * Trouve tous les utilisateurs par rôle.
     * 
     * @param role Le rôle à rechercher
     * @return Liste des utilisateurs avec ce rôle
     */
    List<Utilisateur> findByRole(Role role);

    /**
     * Authentifie un utilisateur par email et mot de passe.
     * 
     * @param email    L'email de l'utilisateur
     * @param password Le mot de passe
     * @return Optional contenant l'utilisateur si authentifié
     */
    Optional<Utilisateur> authenticate(String email, String password);

    /**
     * Vérifie si un email existe déjà.
     * 
     * @param email L'email à vérifier
     * @return true si l'email existe
     */
    boolean emailExists(String email);
}
