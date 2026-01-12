package com.ecole.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "alerte_id")
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private AlerteEmotionnelle alerte;

    @ManyToOne
    @JoinColumn(name = "conseiller_id")
    private Conseiller conseiller;

    @OneToOne(mappedBy = "intervention", cascade = CascadeType.ALL)
    private Rapport rapport;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private String description;

    public Intervention() {
    }

    public Intervention(AlerteEmotionnelle alerte, Conseiller conseiller, String description) {
        this.alerte = alerte;
        this.conseiller = conseiller;
        this.description = description;
        this.dateDebut = LocalDateTime.now();
        this.statut = "En cours";
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlerteEmotionnelle getAlerte() {
        return alerte;
    }

    public void setAlerte(AlerteEmotionnelle alerte) {
        this.alerte = alerte;
    }

    public Conseiller getConseiller() {
        return conseiller;
    }

    public void setConseiller(Conseiller conseiller) {
        this.conseiller = conseiller;
    }

    public Rapport getRapport() {
        return rapport;
    }

    public void setRapport(Rapport rapport) {
        this.rapport = rapport;
        if (rapport != null)
            rapport.setIntervention(this);
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
