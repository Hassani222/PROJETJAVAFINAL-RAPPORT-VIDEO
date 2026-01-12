package com.ecole.service;

import com.ecole.model.AlerteEmotionnelle;
import com.ecole.model.Eleve;
import com.ecole.model.Gravite;
import java.util.Random;

public class SurveillanceService {
    private final Random random = new Random();

    public AlerteEmotionnelle simulerDetection(Eleve eleve) {
        String[] emotions = { "Détresse", "Anxiété", "Retrait Social", "Stress Élevé" };
        String emotion = emotions[random.nextInt(emotions.length)];
        double score = 0.75 + (0.2 * random.nextDouble());

        AlerteEmotionnelle alerte = new AlerteEmotionnelle(eleve, emotion, score, "Analyse comportementale requise");
        alerte.setGravite(score > 0.9 ? Gravite.ELEVE : Gravite.MOYEN);
        alerte.setContexteScolaire("Détecté via analyse vidéo en classe");

        return alerte;
    }

    public void analyserFlux() {
        System.out.println("[IA] Analyse du flux vidéo en temps réel...");
    }
}
