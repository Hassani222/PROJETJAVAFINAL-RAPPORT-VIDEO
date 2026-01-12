package com.ecole.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateurs")
public class Administrateur extends Utilisateur {
    public Administrateur() {
    }

    public Administrateur(String nom, String prenom, String email) {
        super(nom, prenom, email, Role.ADMINISTRATEUR);
    }
}
