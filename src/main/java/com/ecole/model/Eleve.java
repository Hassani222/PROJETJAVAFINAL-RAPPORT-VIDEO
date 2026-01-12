package com.ecole.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eleves")
public class Eleve extends Utilisateur {
    @Column(name = "niveau_risque_moyen")
    private int niveauRisqueMoyen;

    private String classe;
    private int age;

    @ElementCollection
    @JsonIgnore
    private List<String> observationsComportementales = new ArrayList<>();

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AlerteEmotionnelle> historiqueAlertes = new ArrayList<>();

    public Eleve() {
    }

    public Eleve(String nom, String prenom, String email) {
        super(nom, prenom, email, Role.ELEVE);
        this.niveauRisqueMoyen = 0;
    }

    // Getters et Setters
    public int getNiveauRisqueMoyen() {
        return niveauRisqueMoyen;
    }

    public void setNiveauRisqueMoyen(int niveauRisqueMoyen) {
        this.niveauRisqueMoyen = niveauRisqueMoyen;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getObservationsComportementales() {
        return observationsComportementales;
    }

    public void addObservation(String observation) {
        this.observationsComportementales.add(observation);
    }

    public List<AlerteEmotionnelle> getHistoriqueAlertes() {
        return historiqueAlertes;
    }

    public void addAlerte(AlerteEmotionnelle alerte) {
        historiqueAlertes.add(alerte);
        alerte.setEleve(this);
    }
}