package com.ecole.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "enseignants")
public class Enseignant extends Utilisateur {
    private String matiere;

    public Enseignant() {
    }

    public Enseignant(String nom, String prenom, String email, String matiere) {
        super(nom, prenom, email, Role.ENSEIGNANT);
        this.matiere = matiere;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }
}
