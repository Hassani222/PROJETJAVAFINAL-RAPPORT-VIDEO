package com.ecole;

import com.ecole.dao.*;
import com.ecole.model.*;
import com.ecole.util.HibernateUtil;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {

    // DAOs
    private static final UtilisateurDao utilisateurDao = DaoFactory.getUtilisateurDao();
    private static final EleveDao eleveDao = DaoFactory.getEleveDao();
    private static final AlerteEmotionnelleDao alerteDao = DaoFactory.getAlerteEmotionnelleDao();
    private static final InterventionDao interventionDao = DaoFactory.getInterventionDao();
    private static final NotificationDao notificationDao = DaoFactory.getNotificationDao();

    public static void main(String[] args) {
        System.out.println(">>> Démarrage du système Eco-Emotion-Guard V2...");

        // Configurer le serveur API avec Javalin
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "frontend";
                staticFileConfig.location = Location.EXTERNAL;
            });
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        }).start(7000);

        // --- ENDPOINTS API ---

        // Récupérer tous les utilisateurs
        app.get("/api/users", ctx -> {
            List<Utilisateur> users = utilisateurDao.findAll();
            ctx.json(users);
        });

        // Récupérer toutes les alertes
        app.get("/api/alerts", ctx -> {
            List<AlerteEmotionnelle> alerts = alerteDao.findAll();
            ctx.json(alerts);
        });

        // Ajouter une alerte
        app.post("/api/alerts", ctx -> {
            AlerteEmotionnelle alerte = ctx.bodyAsClass(AlerteEmotionnelle.class);
            AlerteEmotionnelle created = alerteDao.create(alerte);
            ctx.status(201).json(created);
        });

        // Ajouter un utilisateur
        app.post("/api/users", ctx -> {
            Utilisateur input = ctx.bodyAsClass(Administrateur.class);
            Utilisateur realUser;
            switch (input.getRole()) {
                case ELEVE:
                    realUser = new Eleve(input.getNom(), input.getPrenom(), input.getEmail());
                    break;
                case CONSEILLER:
                    realUser = new Conseiller(input.getNom(), input.getPrenom(), input.getEmail(), "Spécialiste");
                    break;
                case ENSEIGNANT:
                    realUser = new Enseignant(input.getNom(), input.getPrenom(), input.getEmail(), "Matière");
                    break;
                default:
                    realUser = new Administrateur(input.getNom(), input.getPrenom(), input.getEmail());
                    break;
            }
            realUser.setEtatAcces(input.getEtatAcces());
            realUser.setSchoolId(input.getSchoolId());

            Utilisateur created = utilisateurDao.create(realUser);
            ctx.status(201).json(created);
        });

        // Supprimer une alerte
        app.delete("/api/alerts/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            alerteDao.deleteById(id);
            ctx.status(204);
        });

        // Modifier une alerte
        app.put("/api/alerts/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            AlerteEmotionnelle input = ctx.bodyAsClass(AlerteEmotionnelle.class);

            Optional<AlerteEmotionnelle> existingOpt = alerteDao.findById(id);
            if (existingOpt.isPresent()) {
                AlerteEmotionnelle existing = existingOpt.get();
                existing.setEmotionDetectee(input.getEmotionDetectee());
                existing.setGravite(input.getGravite());
                existing.setContexteScolaire(input.getContexteScolaire());
                AlerteEmotionnelle updated = alerteDao.update(existing);
                ctx.json(updated);
            } else {
                ctx.status(404).result("Alerte non trouvée");
            }
        });

        // Supprimer un utilisateur
        app.delete("/api/users/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            utilisateurDao.deleteById(id);
            ctx.status(204);
        });

        // Modifier un utilisateur
        app.put("/api/users/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Administrateur input = ctx.bodyAsClass(Administrateur.class);

            Optional<Utilisateur> existingOpt = utilisateurDao.findById(id);
            if (existingOpt.isPresent()) {
                Utilisateur existing = existingOpt.get();
                existing.setNom(input.getNom());
                existing.setPrenom(input.getPrenom());
                existing.setEmail(input.getEmail());
                existing.setRole(input.getRole());
                existing.setEtatAcces(input.getEtatAcces());
                existing.setSchoolId(input.getSchoolId());
                Utilisateur updated = utilisateurDao.update(existing);
                ctx.json(updated);
            } else {
                ctx.status(404).result("Utilisateur non trouvé");
            }
        });

        // Authentification Étudiant
        app.post("/api/login", ctx -> {
            String sid = ctx.queryParam("sid");
            Optional<Utilisateur> userOpt = utilisateurDao.findBySchoolId(sid);

            if (userOpt.isPresent()) {
                ctx.json(userOpt.get());
            } else {
                ctx.status(401).result("Identifiant inconnu");
            }
        });

        // Inscription Administrateur
        app.post("/api/admin/register", ctx -> {
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> data = ctx.bodyAsClass(java.util.Map.class);
            String nom = data.get("nom");
            String prenom = data.get("prenom");
            String email = data.get("email");
            String password = data.get("password");
            String roleStr = data.get("role");

            Utilisateur user;
            Role role = Role.valueOf(roleStr != null ? roleStr : "ADMINISTRATEUR");

            switch (role) {
                case ELEVE:
                    Eleve eleve = new Eleve(nom, prenom, email);
                    eleve.setPassword(password);
                    user = eleve;
                    break;
                case CONSEILLER:
                    Conseiller conseiller = new Conseiller(nom, prenom, email, "Spécialiste");
                    conseiller.setPassword(password);
                    user = conseiller;
                    break;
                case ENSEIGNANT:
                    Enseignant enseignant = new Enseignant(nom, prenom, email, "Matière");
                    enseignant.setPassword(password);
                    user = enseignant;
                    break;
                default:
                    Administrateur admin = new Administrateur(nom, prenom, email);
                    admin.setPassword(password);
                    user = admin;
                    break;
            }

            Utilisateur created = utilisateurDao.create(user);
            ctx.status(201).json(created);
        });

        // Authentification Administrateur
        app.post("/api/admin/login", ctx -> {
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> credentials = ctx.bodyAsClass(java.util.Map.class);
            String email = credentials.get("email");
            String password = credentials.get("password");

            Optional<Utilisateur> userOpt = utilisateurDao.authenticate(email, password);

            if (userOpt.isPresent()) {
                ctx.json(userOpt.get());
            } else {
                ctx.status(401).result("Email ou mot de passe incorrect");
            }
        });

        // Ajouter une notification
        app.post("/api/notifications", ctx -> {
            Notification input = ctx.bodyAsClass(Notification.class);

            Optional<Utilisateur> userOpt = utilisateurDao.findById(input.getUtilisateur().getId());
            if (userOpt.isPresent()) {
                Notification notif = new Notification(userOpt.get(), input.getMessage(), input.getType());
                Notification created = notificationDao.create(notif);
                ctx.status(201).json(created);
            } else {
                ctx.status(404).result("Utilisateur non trouvé");
            }
        });

        // Récupérer les notifications d'un utilisateur
        app.get("/api/notifications", ctx -> {
            String userIdParam = ctx.queryParam("userId");
            if (userIdParam != null) {
                Long userId = Long.parseLong(userIdParam);
                List<Notification> notifs = notificationDao.findByUtilisateurId(userId);
                ctx.json(notifs);
            } else {
                ctx.status(400).result("UserId requis");
            }
        });

        // Créer une intervention directement (depuis protocole de crise)
        app.post("/api/interventions", ctx -> {
            Intervention input = ctx.bodyAsClass(Intervention.class);

            // Si une alerte est fournie, on la lie
            AlerteEmotionnelle alerte = null;
            if (input.getAlerte() != null && input.getAlerte().getId() != null) {
                Optional<AlerteEmotionnelle> alerteOpt = alerteDao.findById(input.getAlerte().getId());
                alerte = alerteOpt.orElse(null);
            }

            Intervention inter = new Intervention();
            inter.setAlerte(alerte);
            inter.setDescription(input.getDescription());
            inter.setStatut(input.getStatut() != null ? input.getStatut() : "En cours");
            inter.setDateDebut(LocalDateTime.now());

            // Créer l'intervention
            Intervention created = interventionDao.create(inter);

            // Lier l'intervention à l'alerte (relation bidirectionnelle)
            if (alerte != null) {
                alerte.setIntervention(created);
                alerteDao.update(alerte);
            }

            ctx.status(201).json(created);
        });

        // Récupérer toutes les interventions
        app.get("/api/interventions", ctx -> {
            List<Intervention> interventions = interventionDao.findAll();
            ctx.json(interventions);
        });

        // Récupérer les interventions en cours
        app.get("/api/interventions/en-cours", ctx -> {
            List<Intervention> interventions = interventionDao.findInterventionsEnCours();
            ctx.json(interventions);
        });

        // Récupérer les interventions d'un élève
        app.get("/api/interventions/eleve/{eleveId}", ctx -> {
            Long eleveId = Long.parseLong(ctx.pathParam("eleveId"));
            List<Intervention> interventions = interventionDao.findByEleveId(eleveId);
            ctx.json(interventions);
        });

        // Récupérer les alertes non traitées
        app.get("/api/alerts/non-traitees", ctx -> {
            List<AlerteEmotionnelle> alertes = alerteDao.findAlertesNonTraitees();
            ctx.json(alertes);
        });

        // Récupérer les alertes par gravité
        app.get("/api/alerts/gravite/{gravite}", ctx -> {
            String graviteStr = ctx.pathParam("gravite");
            Gravite gravite = Gravite.valueOf(graviteStr.toUpperCase());
            List<AlerteEmotionnelle> alertes = alerteDao.findByGravite(gravite);
            ctx.json(alertes);
        });

        // Récupérer les alertes d'un élève
        app.get("/api/alerts/eleve/{eleveId}", ctx -> {
            Long eleveId = Long.parseLong(ctx.pathParam("eleveId"));
            List<AlerteEmotionnelle> alertes = alerteDao.findByEleveId(eleveId);
            ctx.json(alertes);
        });

        // Récupérer les élèves d'une classe
        app.get("/api/eleves/classe/{classe}", ctx -> {
            String classe = ctx.pathParam("classe");
            List<Eleve> eleves = eleveDao.findByClasse(classe);
            ctx.json(eleves);
        });

        // Statistiques du dashboard
        app.get("/api/stats", ctx -> {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalUsers", utilisateurDao.count());
            stats.put("totalAlerts", alerteDao.count());
            stats.put("alertesNonTraitees", alerteDao.findAlertesNonTraitees().size());
            stats.put("interventionsEnCours", interventionDao.countByStatut("En cours"));
            stats.put("alertesElevees", alerteDao.countByGravite(Gravite.ELEVE));
            stats.put("alertesMoyennes", alerteDao.countByGravite(Gravite.MOYEN));
            ctx.json(stats);
        });

        // Mettre à jour une intervention
        app.put("/api/interventions/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Intervention input = ctx.bodyAsClass(Intervention.class);
            Optional<Intervention> interOpt = interventionDao.findById(id);
            if (interOpt.isPresent()) {
                Intervention inter = interOpt.get();
                if (input.getDescription() != null)
                    inter.setDescription(input.getDescription());
                if (input.getStatut() != null)
                    inter.setStatut(input.getStatut());
                if ("Terminée".equals(input.getStatut()))
                    inter.setDateFin(LocalDateTime.now());
                ctx.json(interventionDao.update(inter));
            } else {
                ctx.status(404).result("Intervention non trouvée");
            }
        });

        System.out.println(">>> Serveur prêt sur http://localhost:7000");

        // Initialisation de données test si vide
        initData();
    }

    private static void initData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long count = utilisateurDao.count();
            if (count == 0) {
                Transaction tx = session.beginTransaction();

                // Admins & Conseillers
                Administrateur admin = new Administrateur("Admin", "Principal", "admin@ecole.com");
                admin.setPassword("admin123");
                Conseiller psy = new Conseiller("Lemoine", "Sophie", "s.lemoine@ecole.com", "Psychologue Scolaire");

                // Élèves
                Eleve e1 = new Eleve("Dupont", "Jean", "j.dupont@ecole.com");
                e1.setClasse("3ème A");
                e1.setAge(14);
                e1.setSchoolId("EDU-JEAN-001");
                e1.setPassword("jean123");

                Eleve e2 = new Eleve("Martin", "Léa", "l.martin@ecole.com");
                e2.setClasse("4ème B");
                e2.setAge(13);
                e2.setSchoolId("EDU-LEA-002");
                e2.setPassword("lea123");

                Eleve e3 = new Eleve("Bernard", "Lucas", "l.bernard@ecole.com");
                e3.setClasse("3ème A");
                e3.setAge(15);
                e3.setSchoolId("EDU-LUCAS-003");
                e3.setPassword("lucas123");

                session.persist(admin);
                session.persist(psy);
                session.persist(e1);
                session.persist(e2);
                session.persist(e3);

                // Alertes
                AlerteEmotionnelle a1 = new AlerteEmotionnelle(e1, "Détresse", 0.92, "Urgence émotionnelle");
                a1.setGravite(Gravite.ELEVE);
                a1.setContexteScolaire("Isolement prolongé en récréation");

                AlerteEmotionnelle a2 = new AlerteEmotionnelle(e2, "Anxiété", 0.78, "Suivi recommandé");
                a2.setGravite(Gravite.MOYEN);
                a2.setContexteScolaire("Stress visible avant examen");

                AlerteEmotionnelle a3 = new AlerteEmotionnelle(e3, "Retrait", 0.85, "Entretien à prévoir");
                a3.setGravite(Gravite.MOYEN);
                a3.setContexteScolaire("Changement brusque de comportement");

                session.persist(a1);
                session.persist(a2);
                session.persist(a3);

                // Interventions
                Intervention i1 = new Intervention(a1, psy, "Session de relaxation et écoute active");
                i1.setStatut("En cours");
                session.persist(i1);

                a1.setIntervention(i1);

                tx.commit();
                System.out.println(">>> Données de test initialisées.");
            } else {
                // S'assurer que l'admin a un mot de passe pour la démo
                Transaction tx = session.beginTransaction();
                Optional<Utilisateur> existingAdminOpt = utilisateurDao.findByEmail("admin@ecole.com");
                if (existingAdminOpt.isPresent()) {
                    Utilisateur existingAdmin = existingAdminOpt.get();
                    existingAdmin.setPassword("admin123");
                    session.merge(existingAdmin);
                }

                // S'assurer que les élèves existants ont un schoolId pour la démo
                List<Eleve> eleves = session.createQuery("from Eleve where schoolId is null", Eleve.class).list();
                for (Eleve e : eleves) {
                    e.setSchoolId("EDU-" + e.getPrenom().toUpperCase() + "-" + e.getId());
                }
                tx.commit();
            }
        }
    }
}