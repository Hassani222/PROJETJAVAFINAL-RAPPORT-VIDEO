package com.ecole.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertes_emotionnelles")
public class AlerteEmotionnelle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    @JsonIgnoreProperties({ "historiqueAlertes", "observationsComportementales" })
    private Eleve eleve;

    @OneToOne(mappedBy = "alerte", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({ "alerte" })
    private Intervention intervention;

    private String emotionDetectee;
    private double scoreConfianceIA;
    private LocalDateTime dateDetection;
    private String actionRecommandee;

    @Enumerated(EnumType.STRING)
    private Gravite gravite;

    private String contexteScolaire;

    public AlerteEmotionnelle() {
    }

    public AlerteEmotionnelle(Eleve eleve, String emotion, double score, String action) {
        this.eleve = eleve;
        this.emotionDetectee = emotion;
        this.scoreConfianceIA = score;
        this.actionRecommandee = action;
        this.dateDetection = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public String getEmotionDetectee() {
        return emotionDetectee;
    }

    public void setEmotionDetectee(String emotionDetectee) {
        this.emotionDetectee = emotionDetectee;
    }

    public double getScoreConfianceIA() {
        return scoreConfianceIA;
    }

    public void setScoreConfianceIA(double scoreConfianceIA) {
        this.scoreConfianceIA = scoreConfianceIA;
    }

    public LocalDateTime getDateDetection() {
        return dateDetection;
    }

    public void setDateDetection(LocalDateTime dateDetection) {
        this.dateDetection = dateDetection;
    }

    public String getActionRecommandee() {
        return actionRecommandee;
    }

    public void setActionRecommandee(String actionRecommandee) {
        this.actionRecommandee = actionRecommandee;
    }

    public Gravite getGravite() {
        return gravite;
    }

    public void setGravite(Gravite gravite) {
        this.gravite = gravite;
    }

    public String getContexteScolaire() {
        return contexteScolaire;
    }

    public void setContexteScolaire(String contexteScolaire) {
        this.contexteScolaire = contexteScolaire;
    }
}