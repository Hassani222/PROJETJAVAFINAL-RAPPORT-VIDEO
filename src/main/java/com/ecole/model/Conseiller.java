package com.ecole.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conseillers")
public class Conseiller extends Utilisateur {
    private String specialite;

    @OneToMany(mappedBy = "conseiller", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Intervention> interventions = new ArrayList<>();

    public Conseiller() {
    }

    public Conseiller(String nom, String prenom, String email, String specialite) {
        super(nom, prenom, email, Role.CONSEILLER);
        this.specialite = specialite;
    }

    // Getters et Setters
    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }

    public void addIntervention(Intervention intervention) {
        interventions.add(intervention);
        intervention.setConseiller(this);
    }
}