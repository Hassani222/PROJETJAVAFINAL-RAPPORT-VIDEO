# 🚀 Guide de Test - Emotional Guard V2

## ✅ Le Serveur est Démarré
**URL**: http://localhost:7000

---

## 📋 Scénarios de Test

### 1️⃣ **Test Inscription Étudiant**
1. Aller sur: `http://localhost:7000`
2. Cliquer sur "S'enregistrer"
3. Remplir le formulaire:
   - Nom: `Martin`
   - Prénom: `Sophie`
   - Email: `sophie@ecole.com`
   - **Rôle**: `🎓 Étudiant`
   - Mot de passe: `sophie123`
4. Cliquer sur "Créer mon compte"
5. ✅ **Résultat attendu**: Redirection automatique vers `student.html`

---

### 2️⃣ **Test Inscription Admin**
1. Aller sur: `http://localhost:7000`
2. Cliquer sur "S'enregistrer"
3. Remplir le formulaire:
   - Nom: `Durand`
   - Prénom: `Marc`
   - Email: `marc@ecole.com`
   - **Rôle**: `👨‍💼 Administrateur`
   - Mot de passe: `marc123`
4. Cliquer sur "Créer mon compte"
5. ✅ **Résultat attendu**: Accès au Dashboard Admin

---

### 3️⃣ **Test Portail Étudiant**
1. Aller sur: `http://localhost:7000/student.html`
2. Se connecter avec:
   - Email: `j.dupont@ecole.com`
   - Mot de passe: `jean123`
3. Cliquer sur "🆘 Demander de l'aide"
4. Répondre aux 5 questions du test
5. ✅ **Résultat attendu**: 
   - Message de confirmation
   - Alerte créée automatiquement
   - Visible chez l'admin en temps réel (5s max)

---

### 4️⃣ **Test Admin → Intervention**
1. Se connecter en tant qu'admin: `admin@ecole.com` / `admin123`
2. Attendre qu'une alerte apparaisse (polling automatique)
3. Aller dans "Interventions"
4. Cliquer sur "Plan d'action"
5. ✅ **Résultat attendu**: 
   - Modal avec suggestions IA personnalisées
   - Possibilité de sélectionner une intervention
   - Mise à jour du statut

---

## 🔑 Comptes de Test Disponibles

### Administrateurs
- `admin@ecole.com` / `admin123`

### Étudiants
- `j.dupont@ecole.com` / `jean123`
- `l.martin@ecole.com` / `lea123`
- `l.bernard@ecole.com` / `lucas123`

---

## 🎯 Fonctionnalités Clés

✅ **Inscription avec sélection de rôle**
✅ **Redirection automatique selon le rôle**
✅ **Portail étudiant dédié**
✅ **Test d'auto-évaluation**
✅ **Alertes en temps réel (polling 5s)**
✅ **Suggestions IA personnalisées**
✅ **Gestion des interventions**

---

## 🐛 En cas de Problème

1. Vérifier que le serveur tourne sur le port 7000
2. Vider le cache du navigateur (Ctrl + Shift + Delete)
3. Vérifier la console du navigateur (F12)
4. Redémarrer le serveur si nécessaire

---

**Bon test ! 🎉**
