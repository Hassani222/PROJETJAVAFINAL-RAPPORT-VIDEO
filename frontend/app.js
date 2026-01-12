// State management
let currentView = 'dashboard';
let alerts = [];
let users = [];
let loggedStudent = null;
let loggedAdmin = null;
let editingId = null;

// API Configuration
const API_URL = 'http://localhost:7000/api';

// UI Elements
const viewTitle = document.getElementById('view-title');
const tableTitle = document.getElementById('table-title');
const tableBody = document.getElementById('table-body');
const tableHead = document.querySelector('#main-table thead');
const navItems = document.querySelectorAll('.nav-item');
const crudModal = document.getElementById('crud-modal');
const closeModalBtn = document.getElementById('close-modal');
const addNewBtn = document.getElementById('add-new-btn');
const crudForm = document.getElementById('crud-form');
const formFields = document.getElementById('form-fields');

// Initialize
document.addEventListener('DOMContentLoaded', async () => {
  initAdminAuth();
  initStudentEvents();

  if (checkAdminSession()) {
    await refreshData();
    renderView('dashboard');
  }
});

function initAdminAuth() {
  const loginForm = document.getElementById('admin-login-form');
  if (loginForm) {
    loginForm.addEventListener('submit', attemptAdminLogin);
  }
  const registerForm = document.getElementById('admin-register-form');
  if (registerForm) {
    registerForm.addEventListener('submit', attemptAdminRegister);
  }
}

function showAuthView(view) {
  const loginView = document.getElementById('login-view');
  const registerView = document.getElementById('register-view');
  if (view === 'login') {
    loginView.style.display = 'block';
    registerView.style.display = 'none';
  } else {
    loginView.style.display = 'none';
    registerView.style.display = 'block';
  }
}

function checkAdminSession() {
  const session = localStorage.getItem('adminSession');
  if (session) {
    loggedAdmin = JSON.parse(session);
    showMainApp();
    return true;
  }
  return false;
}

function showMainApp() {
  document.getElementById('admin-auth-container').classList.add('hidden');
  document.getElementById('main-app-container').style.display = 'flex';
  if (loggedAdmin) {
    document.getElementById('user-name').textContent = `${loggedAdmin.nom} ${loggedAdmin.prenom}`;
    document.getElementById('user-role').textContent = loggedAdmin.role;
  }
}

function logoutAdmin() {
  localStorage.removeItem('adminSession');
  location.reload();
}

async function attemptAdminRegister(e) {
  e.preventDefault();
  const nom = document.getElementById('reg-nom').value;
  const prenom = document.getElementById('reg-prenom').value;
  const email = document.getElementById('reg-email').value;
  const role = document.getElementById('reg-role').value;
  const password = document.getElementById('reg-password').value;
  const errorDiv = document.getElementById('admin-register-error');

  if (!role) {
    errorDiv.textContent = "Veuillez sélectionner un rôle.";
    errorDiv.style.display = 'block';
    return;
  }

  try {
    const response = await fetch(`${API_URL}/admin/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nom, prenom, email, password, role })
    });

    if (response.ok) {
      const user = await response.json();

      // Redirect based on role
      if (user.role === 'ELEVE') {
        // Redirect to student portal
        localStorage.setItem('studentSession', JSON.stringify(user));
        window.location.href = 'student.html';
      } else {
        // Admin, Conseiller, Enseignant go to admin dashboard
        localStorage.setItem('adminSession', JSON.stringify(user));
        loggedAdmin = user;
        errorDiv.style.display = 'none';
        showMainApp();
        await refreshData();
        renderView('dashboard');
      }
    } else {
      errorDiv.textContent = await response.text() || "Erreur lors de l'inscription.";
      errorDiv.style.display = 'block';
    }
  } catch (err) {
    console.error("Register Error:", err);
    errorDiv.textContent = "Erreur de connexion au serveur.";
    errorDiv.style.display = 'block';
  }
}

async function attemptAdminLogin(e) {
  e.preventDefault();
  const email = document.getElementById('admin-email').value;
  const password = document.getElementById('admin-password').value;
  const errorDiv = document.getElementById('admin-login-error');

  try {
    const response = await fetch(`${API_URL}/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (response.ok) {
      const user = await response.json();

      // Redirect based on role
      if (user.role === 'ELEVE') {
        // Redirect to student portal
        localStorage.setItem('studentSession', JSON.stringify(user));
        window.location.href = 'student.html';
      } else {
        // Admin, Conseiller, Enseignant go to admin dashboard
        localStorage.setItem('adminSession', JSON.stringify(user));
        loggedAdmin = user;
        errorDiv.style.display = 'none';
        showMainApp();
        await refreshData();
        renderView('dashboard');
      }
    } else {
      errorDiv.style.display = 'block';
    }
  } catch (err) {
    console.error("Login Error:", err);
    errorDiv.textContent = "Erreur de connexion au serveur.";
    errorDiv.style.display = 'block';
  }
}

async function refreshData() {
  try {
    const [usersRes, alertsRes] = await Promise.all([
      fetch(`${API_URL}/users`),
      fetch(`${API_URL}/alerts`)
    ]);
    users = await usersRes.json();
    alerts = await alertsRes.json();
    updateStats();
  } catch (err) {
    console.error("Erreur de connexion au backend:", err);
  }
}

function updateStats() {
  document.getElementById('stat-alerts').textContent = alerts.length;
  document.getElementById('stat-interventions').textContent = alerts.filter(a => a.intervention).length;
}

// Navigation Handling
navItems.forEach(item => {
  item.addEventListener('click', (e) => {
    e.preventDefault();
    navItems.forEach(i => i.classList.remove('active'));
    item.classList.add('active');

    const view = item.getAttribute('data-view');
    renderView(view);
  });
});

function renderView(view) {
  currentView = view;
  viewTitle.textContent = view.charAt(0).toUpperCase() + view.slice(1);

  const statsGrid = document.querySelector('.stats-grid');
  const insightsPanel = document.querySelector('.insights-panel');
  const dashboardGridMain = document.querySelector('.dashboard-grid-main');
  const suggestionsDiv = document.getElementById('ai-suggestions-container');

  if (view === 'dashboard') {
    statsGrid.style.display = 'grid';
    if (insightsPanel) insightsPanel.style.display = 'block';
    if (monitoringSection) monitoringSection.style.display = 'block';
    if (testSection) testSection.style.display = 'block';
    if (suggestionsDiv) suggestionsDiv.style.display = 'none';
    if (dashboardGridMain) dashboardGridMain.style.gridTemplateColumns = '2fr 1fr';

    tableTitle.textContent = 'Alertes Récentes';
    renderAlertsTable();
    startLiveSimulation();
  } else {
    statsGrid.style.display = 'none';
    if (insightsPanel) insightsPanel.style.display = 'none';
    if (suggestionsDiv) suggestionsDiv.style.display = (view === 'interventions' ? 'grid' : 'none');
    if (dashboardGridMain) dashboardGridMain.style.gridTemplateColumns = '1fr';

    if (view === 'alerts') {
      tableTitle.textContent = 'Gestion des Alertes';
      renderAlertsTable(true);
    } else if (view === 'users') {
      tableTitle.textContent = 'Gestion des Utilisateurs';
      renderUsersTable();
    } else if (view === 'interventions') {
      tableTitle.textContent = 'Suivi des Interventions';
      renderInterventionsTable();
    } else if (view === 'settings') {
      tableTitle.textContent = 'Paramètres du Système';
      renderSettings();
    } else {
      tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 3rem;">Vue en cours de développement...</td></tr>';
      tableHead.innerHTML = '';
      tableTitle.textContent = 'Bientôt disponible';
    }
  }
}

// Table Rendering
function renderSettings() {
  tableHead.innerHTML = '';
  tableBody.innerHTML = `
    <tr>
      <td colspan="5" style="padding: 2rem;">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
          <!-- AI Config Section -->
          <div class="content-card" style="padding: 1.5rem; background: rgba(255,255,255,0.02);">
            <h3 style="margin-bottom: 1.5rem; color: var(--primary);">🤖 Intelligence Artificielle</h3>
            <div class="form-group">
              <label>Seuil de détection de détresse (%)</label>
              <input type="range" min="10" max="90" value="75" class="slider" id="ai-threshold">
              <div style="display: flex; justify-content: space-between; font-size: 0.8rem; margin-top: 0.5rem;">
                <span>Sensible (10%)</span>
                <span id="threshold-val">75%</span>
                <span>Strict (90%)</span>
              </div>
            </div>
            <div class="form-group">
              <label>Fréquence d'analyse (ms)</label>
              <select style="width: 100%; padding: 0.75rem; background: var(--bg); border: 1px solid var(--border); border-radius: 8px; color: white;">
                <option value="5000">Temps Réel (5s)</option>
                <option value="30000" selected>Standard (30s)</option>
                <option value="60000">Économique (1min)</option>
              </select>
            </div>
            <button class="btn btn-primary" onclick="animateSuccess()" style="width: 100%;">Sauvegarder l'IA</button>
          </div>

          <!-- System Info Section -->
          <div class="content-card" style="padding: 1.5rem; background: rgba(255,255,255,0.02);">
            <h3 style="margin-bottom: 1.5rem; color: var(--accent);">⚙️ État du Système</h3>
            <ul style="list-style: none; display: flex; flex-direction: column; gap: 1rem;">
              <li style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 0.5rem;">
                <span style="color: var(--text-muted);">Version Engine</span>
                <span class="status-pill success" style="background: rgba(16,185,129,0.1);">v2.4.0-Stable</span>
              </li>
              <li style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 0.5rem;">
                <span style="color: var(--text-muted);">Base de données</span>
                <span style="font-weight: 600;">MySQL (Connecté)</span>
              </li>
              <li style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 0.5rem;">
                <span style="color: var(--text-muted);">Temps d'exécution</span>
                <span id="uptime-val">Calcul en cours...</span>
              </li>
            </ul>
            <div style="margin-top: 1.5rem;">
               <button class="btn btn-ghost" style="width: 100%; border: 1px solid var(--danger); color: var(--danger);" onclick="alert('Action réservée au SuperAdmin')">Réinitialiser les Logs</button>
            </div>
          </div>

          <!-- User Profile Section -->
          <div class="content-card" style="padding: 1.5rem; background: rgba(255,255,255,0.02); grid-column: span 2;">
            <h3 style="margin-bottom: 1.5rem;">👤 Profil Administrateur</h3>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
              <div class="form-group">
                <label>Nom complet</label>
                <input type="text" value="${currentUser?.nom || 'Admin'} ${currentUser?.prenom || ''}" disabled>
              </div>
              <div class="form-group">
                <label>Email de secours</label>
                <input type="email" value="${currentUser?.email || 'admin@ecole.com'}">
              </div>
            </div>
          </div>
        </div>
      </td>
    </tr>
  `;

  // Small logic for the range slider
  const slider = document.getElementById('ai-threshold');
  const val = document.getElementById('threshold-val');
  if (slider && val) {
    slider.oninput = function () { val.textContent = this.value + '%'; };
  }

  // Uptime simulation
  const uptimeVal = document.getElementById('uptime-val');
  if (uptimeVal) {
    const startTime = Date.now() - 3600000 * 4; // 4 hours ago mocking
    setInterval(() => {
      const diff = Math.floor((Date.now() - startTime) / 1000);
      const h = Math.floor(diff / 3600);
      const m = Math.floor((diff % 3600) / 60);
      const s = diff % 60;
      uptimeVal.textContent = `${h}h ${m}m ${s}s`;
    }, 1000);
  }
}

function renderInterventionsTable() {
  tableHead.innerHTML = `
    <tr>
      <th>Alerte</th>
      <th>Intervenant</th>
      <th>Date Début</th>
      <th>Statut</th>
      <th>Actions</th>
    </tr>
  `;

  // Filter alerts that have interventions
  const interventions = alerts.filter(a => a.intervention).map(a => ({
    ...a.intervention,
    alerteEmotion: a.emotionDetectee,
    eleveNom: a.eleve ? a.eleve.nom : 'Inconnu'
  }));

  if (interventions.length === 0) {
    tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem;">Aucune intervention enregistrée.</td></tr>';
    return;
  }

  tableBody.innerHTML = interventions.map(inter => `
    <tr>
      <td><span style="font-weight:600;">${inter.alerteEmotion}</span> (${inter.eleveNom})</td>
      <td>${inter.conseiller ? inter.conseiller.nom : 'Non assigné'}</td>
      <td>${formatDate(inter.dateDebut)}</td>
      <td><span class="status-pill warning">${inter.statut || 'En cours'}</span></td>
      <td>
        <button class="btn btn-ghost" onclick="viewInterventionDetails(${inter.id})">Plan d'action</button>
      </td>
    </tr>
  `).join('');

  // Add AI Suggestions below the table
  const currentInterventions = tableBody.parentElement.parentElement;
  let suggestionsDiv = document.getElementById('ai-suggestions-container');
  if (!suggestionsDiv) {
    suggestionsDiv = document.createElement('div');
    suggestionsDiv.id = 'ai-suggestions-container';
    suggestionsDiv.className = 'suggestions-grid';
    currentInterventions.after(suggestionsDiv);
  }

  suggestionsDiv.innerHTML = `
    <div class="content-card suggestion-card">
      <div class="card-header"><h3>Exercices de Relaxation</h3></div>
      <div class="p-3">
        <p class="text-muted small mb-2">Suggéré pour : Stress & Anxiété</p>
        <ul class="suggestion-list">
          <li>Cohérence cardiaque (5 min)</li>
          <li>Méditation guidée "Calme"</li>
        </ul>
        <button class="btn btn-primary btn-sm mt-3" onclick="openRelaxationModal()">Assigner à un élève</button>
      </div>
    </div>
    <div class="content-card suggestion-card">
      <div class="card-header"><h3>Protocoles de Crise</h3></div>
      <div class="p-3">
        <p class="text-muted small mb-2">Suggéré pour : Détresse Critique</p>
        <ul class="suggestion-list">
          <li>Entretien immédiat avec Psy</li>
          <li>Alerte parentale automatique</li>
        </ul>
        <button class="btn btn-danger btn-sm mt-3" onclick="openCrisisModal()">Activer Protocole</button>
      </div>
    </div>
  `;
}

function openRelaxationModal() {
  const studentOptions = users.filter(u => u.role === 'ELEVE').map(s => `
    <option value="${s.id}">${s.nom} ${s.prenom}</option>
  `).join('');

  formFields.innerHTML = `
    <div class="form-group">
      <label>Sélectionner l'élève</label>
      <select id="rel-student-id">${studentOptions}</select>
    </div>
    <div class="form-group">
      <label>Exercice à assigner</label>
      <select id="rel-exercise">
        <option>Cohérence cardiaque (5 min)</option>
        <option>Méditation guidée "Calme"</option>
        <option>Respiration carrée</option>
      </select>
    </div>
  `;
  document.getElementById('modal-title').textContent = "Assigner Relaxation";
  crudModal.classList.add('active');

  const oldSubmit = crudForm.onsubmit;
  crudForm.onsubmit = async (e) => {
    e.preventDefault();
    const sid = document.getElementById('rel-student-id').value;
    const ex = document.getElementById('rel-exercise').value;

    try {
      const resp = await fetch(`${API_URL}/notifications`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          utilisateur: { id: parseInt(sid) },
          message: `Nouvel exercice : ${ex}`,
          type: 'RELAXATION',
          dateCreation: new Date().toISOString()
        })
      });

      if (resp.ok) {
        closeModal();
        animateSuccess();
        await refreshData();
        renderView(currentView);
      } else {
        alert("Erreur lors de l'assignation.");
      }
    } catch (err) {
      console.error(err);
    }
    crudForm.onsubmit = oldSubmit;
  };
}

function openCrisisModal() {
  const studentOptions = users.filter(u => u.role === 'ELEVE').map(s => `
    <option value="${s.id}">${s.nom} ${s.prenom}</option>
  `).join('');

  formFields.innerHTML = `
    <div class="form-group">
      <label>Sélectionner l'élève en crise</label>
      <select id="crisis-student-id">${studentOptions}</select>
    </div>
    <div class="form-group">
      <label>Protocole à activer</label>
      <select id="crisis-protocol">
        <option>Entretien immédiat avec Psy</option>
        <option>Alerte parentale automatique</option>
        <option>Hospitalisation / Urgence</option>
      </select>
    </div>
  `;
  document.getElementById('modal-title').textContent = "ACTIVER PROTOCOLE CRISE";
  crudModal.classList.add('active');

  const oldSubmit = crudForm.onsubmit;
  crudForm.onsubmit = async (e) => {
    e.preventDefault();
    const sid = document.getElementById('crisis-student-id').value;
    const proto = document.getElementById('crisis-protocol').value;

    try {
      // 1. Create a "Crisis Alert" first
      const alerteResp = await fetch(`${API_URL}/alerts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          eleve: { id: parseInt(sid) },
          emotionDetectee: 'CRRISE / URGENT',
          gravite: 'ELEVE',
          contexteScolaire: `Protocole de crise activé manuellement: ${proto}`,
          scoreConfianceIA: 1.0,
          actionRecommandee: proto
        })
      });

      if (!alerteResp.ok) throw new Error("Échec création alerte");
      const alerteCreated = await alerteResp.json();

      // 2. Create the Intervention linked to this alert
      const interResp = await fetch(`${API_URL}/interventions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          alerte: { id: alerteCreated.id },
          description: `PROTOCOLE CRISE ACTIVE : ${proto}`,
          statut: 'Urgent'
        })
      });

      if (interResp.ok) {
        closeModal();
        animateSuccess();
        await refreshData();
        renderView(currentView);
      } else {
        alert("Erreur lors de l'activation du protocole.");
      }
    } catch (err) {
      console.error(err);
      alert("Erreur : " + err.message);
    }
    crudForm.onsubmit = oldSubmit;
  };
}



function viewInterventionDetails(id) {
  // Find the intervention in the alerts list (since interventions are attached to alerts)
  // Or fetch it directly. For now, we look in local alerts.
  const interventionWithAlert = alerts.find(a => a.intervention && a.intervention.id === id);
  if (!interventionWithAlert) {
    alert("Intervention non trouvée.");
    return;
  }

  const alertData = interventionWithAlert;
  const intervention = alertData.intervention;
  const student = alertData.eleve;

  // AI Logic for Suggestions
  const suggestions = generateSmartSuggestions(alertData);

  formFields.innerHTML = `
    <div style="background: rgba(255,255,255,0.03); padding: 1rem; border-radius: 8px; margin-bottom: 1.5rem;">
      <h4 style="margin-bottom:0.5rem; color: var(--text-light);">Profil Étudiant</h4>
      <p><strong>Nom :</strong> ${student ? student.nom + ' ' + student.prenom : 'Inconnu'}</p>
      <p><strong>Émotion Détectée :</strong> <span class="status-pill warning">${alertData.emotionDetectee}</span></p>
      <p><strong>Contexte :</strong> ${alertData.contexteScolaire || 'Non spécifié'}</p>
    </div>

    <h4 style="margin-bottom:1rem; color: var(--primary);">✨ Suggestions IA Personnalisées</h4>
    <div class="suggestions-grid" style="grid-template-columns: 1fr; gap: 0.5rem; margin-bottom: 1.5rem;">
      ${suggestions.map(s => `
        <div class="suggestion-item" onclick="selectSuggestion(this, '${s.action}')" style="cursor:pointer; padding:0.75rem; border:1px solid rgba(255,255,255,0.1); border-radius:6px; transition:all 0.2s;">
          <div style="font-weight:600; color:var(--accent);">${s.title}</div>
          <div style="font-size:0.85rem; color:var(--text-muted);">${s.desc}</div>
        </div>
      `).join('')}
    </div>

    <div class="form-group">
      <label>Action / Protocole Choisi</label>
      <textarea id="field-intervention-desc" rows="3">${intervention.description || ''}</textarea>
    </div>
    
    <div class="form-group">
        <label>Statut</label>
        <select id="field-intervention-status">
            <option value="En cours" ${intervention.statut === 'En cours' ? 'selected' : ''}>En cours</option>
            <option value="Terminée" ${intervention.statut === 'Terminée' ? 'selected' : ''}>Terminée</option>
            <option value="Annulé" ${intervention.statut === 'Annulé' ? 'selected' : ''}>Annulé</option>
        </select>
    </div>
  `;

  document.getElementById('modal-title').textContent = "Plan d'Intervention Personnalisé";
  crudModal.classList.add('active');

  const oldSubmit = crudForm.onsubmit;
  crudForm.onsubmit = async (e) => {
    e.preventDefault();
    const newDesc = document.getElementById('field-intervention-desc').value;
    const newStatus = document.getElementById('field-intervention-status').value;

    try {
      const resp = await fetch(`${API_URL}/interventions/${intervention.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          description: newDesc,
          statut: newStatus
        })
      });

      if (resp.ok) {
        closeModal();
        animateSuccess();
        await refreshData();
        renderView('interventions');
      } else {
        alert("Erreur lors de la mise à jour.");
      }
    } catch (err) {
      console.error(err);
    }
    crudForm.onsubmit = oldSubmit;
  };
}

function generateSmartSuggestions(alertData) {
  const emotion = alertData.emotionDetectee?.toLowerCase() || '';
  const context = alertData.contexteScolaire?.toLowerCase() || '';
  const gravity = alertData.gravite;

  let suggestions = [];

  if (gravity === 'ELEVE' || gravity === 'CRITIQUE') {
    suggestions.push({
      title: "📞 Protocole d'Urgence",
      desc: "Contacter les parents immédiatement et isoler l'élève dans un espace calme.",
      action: "Activation du Protocole d'Urgence : Appel parents + Espace calme."
    });
  }

  if (emotion.includes('colère') || emotion.includes('agress')) {
    suggestions.push({
      title: "🛑 Désamorçage Tactique",
      desc: "Utiliser une voix basse, éviter le contact visuel direct, proposer une activité motrice.",
      action: "Technique de désamorçage : Voix basse, diversion motrice."
    });
    suggestions.push({
      title: "🏃 Pause Active",
      desc: "Envoyer l'élève faire une course administrative pour évacuer l'énergie.",
      action: "Pause active : Course administrative."
    });
  } else if (emotion.includes('tristesse') || emotion.includes('détresse')) {
    suggestions.push({
      title: "👂 Écoute Active",
      desc: "Entretien individuel de 10min axé sur l'expression des émotions sans jugement.",
      action: "Entretien d'écoute active (10min)."
    });
    suggestions.push({
      title: "🤝 Pair-Aidance",
      desc: "Proposer de travailler avec un camarade de confiance.",
      action: "Mise en place d'un binôme de confiance."
    });
  } else if (emotion.includes('anxiété') || emotion.includes('stress')) {
    suggestions.push({
      title: "🌬️ Cohérence Cardiaque",
      desc: "Guidage respiratoire 5-5 (Inspirer 5s, Expirer 5s) pendant 3 minutes.",
      action: "Exercice de cohérence cardiaque (3min)."
    });
    suggestions.push({
      title: "🎧 Isolement Sensoriel",
      desc: "Autoriser le port du casque anti-bruit pendant 20 minutes.",
      action: "Autorisation casque anti-bruit (20min)."
    });
  } else {
    suggestions.push({
      title: "🔍 Entretien Exploratoire",
      desc: "L'IA manque de données. Un entretien est nécessaire pour qualifier l'émotion.",
      action: "Programmation d'un entretien exploratoire."
    });
  }

  // Contextual suggestions
  if (context.includes('classe') || context.includes('cours')) {
    suggestions.push({
      title: "📚 Aménagement Pédagogique",
      desc: "Réduire temporairement la charge de travail ou le temps d'exposition au groupe.",
      action: "Allègement temporaire des tâches scolaires."
    });
  }

  return suggestions;
}

function selectSuggestion(el, actionText) {
  document.querySelectorAll('.suggestion-item').forEach(i => i.style.borderColor = 'rgba(255,255,255,0.1)');
  el.style.borderColor = 'var(--primary)';
  document.getElementById('field-intervention-desc').value = actionText;
}

function renderAlertsTable(full = false) {
  // ... (keeping existing logic but adding it here for the replacement block)
  tableHead.innerHTML = `
    <tr>
      <th>Étudiant</th>
      <th>Émotion</th>
      <th>Score IA</th>
      <th>Status</th>
      <th>Date</th>
      ${full ? '<th>Actions</th>' : ''}
    </tr>
  `;

  tableBody.innerHTML = alerts.map(alert => `
    <tr>
      <td style="font-weight: 600;">${alert.eleve ? alert.eleve.nom + ' ' + alert.eleve.prenom : 'Inconnu'}</td>
      <td>${alert.emotionDetectee}</td>
      <td>
        <div style="display:flex; align-items:center; gap:8px;">
          <div style="width:100px; height:6px; background:rgba(255,255,255,0.1); border-radius:10px; overflow:hidden;">
            <div style="width:${alert.scoreConfianceIA * 100}%; height:100%; background:var(--primary);"></div>
          </div>
          <span>${Math.round(alert.scoreConfianceIA * 100)}%</span>
        </div>
      </td>
      <td><span class="status-pill ${getStatusClass(alert.gravite)}">${alert.gravite || 'INCONNU'}</span></td>
      <td style="color: var(--text-muted); font-size: 0.75rem;">${formatDate(alert.dateDetection)}</td>
      ${full ? `
        <td>
          <div class="action-btns">
            <button class="btn btn-ghost" onclick="editItem(${alert.id})">
              <svg style="width:14px;height:14px" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path></svg>
            </button>
            <button class="btn btn-ghost" style="color:var(--danger);" onclick="deleteItem(${alert.id})">
              <svg style="width:14px;height:14px" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
            </button>
          </div>
        </td>
      ` : ''}
    </tr>
  `).join('');
}

function renderUsersTable() {
  tableHead.innerHTML = `
    <tr>
      <th>Nom</th>
      <th>Email</th>
      <th>Rôle</th>
      <th>Status</th>
      <th>Actions</th>
    </tr>
  `;

  tableBody.innerHTML = users.map(user => `
    <tr>
      <td style="font-weight: 600;">${user.nom} ${user.prenom}</td>
      <td>${user.email}</td>
      <td>${user.role || 'N/A'}</td>
      <td><span class="status-pill success">${user.etatAcces || 'ACTIF'}</span></td>
      <td>
        <div class="action-btns">
          <button class="btn btn-ghost" onclick="editItem(${user.id})">Edit</button>
          <button class="btn btn-ghost" style="color:var(--danger);" onclick="deleteUser(${user.id})">Delete</button>
        </div>
      </td>
    </tr>
  `).join('');
}

function formatDate(dateArr) {
  if (!dateArr) return 'N/A';
  if (Array.isArray(dateArr)) {
    const [year, month, day, hour, minute] = dateArr;
    return `${day}/${month.toString().padStart(2, '0')}/${year} ${hour}:${minute.toString().padStart(2, '0')}`;
  }
  return dateArr;
}

function getStatusClass(status) {
  if (!status) return 'success';
  switch (status) {
    case 'ELEVE': case 'Urgent': case 'CRITIQUE': return 'danger';
    case 'MOYEN': case 'Suivi': case 'MODERE': return 'warning';
    case 'FAIBLE': case 'Normal': return 'success';
    default: return 'success';
  }
}

// Modal & CRUD
addNewBtn.addEventListener('click', () => {
  openModal();
});

closeModalBtn.addEventListener('click', () => {
  closeModal();
});

function openModal(editId = null) {
  editingId = editId;
  const mode = editId ? 'Modifier' : 'Ajouter';
  document.getElementById('modal-title').textContent = `${mode} ${currentView === 'alerts' ? 'une Alerte' : 'un Utilisateur'}`;

  // ... (Génération des champs HTML comme avant) ...
  if (currentView === 'alerts' || currentView === 'dashboard') {
    const studentOptions = users.filter(u => u.role === 'ELEVE').map(s => `
      <option value="${s.id}">${s.nom} ${s.prenom}</option>
    `).join('') || '<option value="2">Jean Dupont (Démo)</option>';

    formFields.innerHTML = `
      <div class="form-group">
        <label>Étudiant concerné</label>
        <select id="field-student-id">
          ${studentOptions}
        </select>
      </div>
      <div class="form-group">
        <label>Gravité</label>
        <select id="field-gravite">
          <option value="FAIBLE">Faible</option>
          <option value="MOYEN">Moyen</option>
          <option value="ELEVE">Élevé (Urgent)</option>
        </select>
      </div>
      <div class="form-group">
        <label>Contexte / Observation</label>
        <textarea id="field-contexte" rows="3" placeholder="Décrivez la situation..."></textarea>
      </div>
      <div class="form-group">
        <label>Émotion détectée</label>
        <select id="field-mood">
          <option>Détresse</option>
          <option>Anxiété</option>
          <option>Colère</option>
          <option>Tristesse</option>
        </select>
      </div>
    `;
  } else if (currentView === 'users') {
    formFields.innerHTML = `
      <div class="form-group">
        <label>Nom complet</label>
        <input type="text" id="field-name" required>
      </div>
      <div class="form-group">
        <label>Email</label>
        <input type="email" id="field-email" required>
      </div>
      <div class="form-group">
        <label>Rôle</label>
        <select id="field-role">
          <option value="ADMINISTRATEUR">Administrateur</option>
          <option value="CONSEILLER">Conseiller / Psychologue</option>
          <option value="ENSEIGNANT">Enseignant</option>
          <option value="ELEVE">Élève</option>
        </select>
      </div>
      <div class="form-group">
        <label>État d'accès</label>
        <select id="field-status">
          <option value="ACTIF">Actif</option>
          <option value="RESTREINT">Restreint</option>
          <option value="DESACTIVE">Désactivé</option>
        </select>
      </div>
      <div class="form-group">
        <label>ID École (Optionnel pour Admin)</label>
        <input type="text" id="field-school-id" placeholder="Ex: EDU-JEAN-103">
      </div>
    `;
  }

  // Populate fields if editing
  if (editId) {
    if (currentView === 'alerts' || currentView === 'dashboard') {
      const alert = alerts.find(a => a.id === editId);
      if (alert) {
        document.getElementById('field-student-id').value = alert.eleve ? alert.eleve.id : '';
        document.getElementById('field-gravite').value = alert.gravite;
        document.getElementById('field-contexte').value = alert.contexteScolaire;
        document.getElementById('field-mood').value = alert.emotionDetectee;
        // Student selection might be disabled during edit to avoid confusion
        document.getElementById('field-student-id').disabled = true;
      }
    } else if (currentView === 'users') {
      const user = users.find(u => u.id === editId);
      if (user) {
        document.getElementById('field-name').value = `${user.nom} ${user.prenom || ''}`.trim();
        document.getElementById('field-email').value = user.email;
        document.getElementById('field-role').value = user.role;
        document.getElementById('field-status').value = user.etatAcces;
        document.getElementById('field-school-id').value = user.schoolId || '';
      }
    }
  }

  crudModal.classList.add('active');
}

function closeModal() {
  crudModal.classList.remove('active');
}

crudForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const isEdit = editingId !== null;
  const method = isEdit ? 'PUT' : 'POST';
  const urlSuffix = isEdit ? `/${editingId}` : '';

  if (currentView === 'alerts' || currentView === 'dashboard') {
    const studentId = document.getElementById('field-student-id').value;
    const emotionDetectee = document.getElementById('field-mood').value;
    const gravite = document.getElementById('field-gravite').value;
    const contexteScolaire = document.getElementById('field-contexte').value;

    const alerteData = {
      eleve: { id: parseInt(studentId) },
      emotionDetectee,
      gravite,
      contexteScolaire,
      scoreConfianceIA: gravite === 'ELEVE' ? 0.95 : 0.75,
      actionRecommandee: 'Suivi nécessaire'
    };

    await fetch(`${API_URL}/alerts${urlSuffix}`, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(alerteData)
    });
  } else if (currentView === 'users') {
    const fullName = document.getElementById('field-name').value;
    const email = document.getElementById('field-email').value;
    const role = document.getElementById('field-role').value;
    const etatAcces = document.getElementById('field-status').value;
    const schoolId = document.getElementById('field-school-id').value;

    const parts = fullName.split(' ');
    const nom = parts[0] || 'Inconnu';
    const prenom = parts.slice(1).join(' ') || '';

    const userData = { nom, prenom, email, role, etatAcces, schoolId };

    await fetch(`${API_URL}/users${urlSuffix}`, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });
  }

  await refreshData();
  renderView(currentView);
  closeModal();
  animateSuccess();
  editingId = null;
});

function editItem(id) {
  openModal(id);
}

async function deleteItem(id) {
  if (confirm('Voulez-vous vraiment supprimer cette alerte ?')) {
    await fetch(`${API_URL}/alerts/${id}`, { method: 'DELETE' });
    await refreshData();
    renderView(currentView);
  }
}

async function deleteUser(id) {
  if (confirm('Voulez-vous vraiment supprimer cet utilisateur ?')) {
    await fetch(`${API_URL}/users/${id}`, { method: 'DELETE' });
    await refreshData();
    renderView(currentView);
  }
}

function animateSuccess() {
  console.log('Action réussie !');
}

// Emotional Test System
const testQuestions = [
  {
    q: "Comment décririez-vous votre niveau d'énergie aujourd'hui ?",
    options: [
      { text: "Plein d'énergie", score: 0 },
      { text: "Stable", score: 1 },
      { text: "Un peu fatigué", score: 2 },
      { text: "Épuisant", score: 3 }
    ]
  },
  {
    q: "Avez-vous ressenti de l'inquiétude face à vos cours récemment ?",
    options: [
      { text: "Jamais", score: 0 },
      { text: "Rarement", score: 1 },
      { text: "Souvent", score: 2 },
      { text: "Constamment", score: 3 }
    ]
  },
  {
    q: "Comment s'est passée votre interaction avec vos camarades aujourd'hui ?",
    options: [
      { text: "Excellente", score: 0 },
      { text: "Normale", score: 1 },
      { text: "J'ai préféré rester seul(e)", score: 2 },
      { text: "Très difficile", score: 3 }
    ]
  }
];

let currentQuestionIndex = 0;
let testScore = 0;

function startTest() {
  currentQuestionIndex = 0;
  testScore = 0;
  renderQuestion();
}

function renderQuestion() {
  const container = document.getElementById('test-content');
  const question = testQuestions[currentQuestionIndex];
  const progress = ((currentQuestionIndex) / testQuestions.length) * 100;

  container.innerHTML = `
    <div class="question-container">
      <div class="test-progress">
        <div class="progress-fill" style="width: ${progress}%"></div>
      </div>
      <p class="question-text">${question.q}</p>
      <div class="options-list">
        ${question.options.map((opt, i) => `
          <button class="option-btn" onclick="handleAnswer(${opt.score})">
            ${opt.text}
          </button>
        `).join('')}
      </div>
    </div>
  `;
}

async function handleAnswer(score) {
  testScore += score;
  currentQuestionIndex++;

  if (currentQuestionIndex < testQuestions.length) {
    renderQuestion();
  } else {
    await finishTest();
  }
}

async function finishTest() {
  const container = document.getElementById('test-content');
  const maxScore = testQuestions.length * 3;
  const ratio = testScore / maxScore;

  let resultMsg = "";
  let emotion = "Stable";
  let gravite = "FAIBLE";

  if (ratio > 0.7) {
    resultMsg = "Nous avons remarqué que vous traversez une période difficile. Un conseiller va être informé pour vous proposer un entretien de soutien.";
    emotion = "Détresse";
    gravite = "ELEVE";
  } else if (ratio > 0.4) {
    resultMsg = "Vous semblez un peu stressé(e). N'hésitez pas à consulter nos ressources de relaxation ou à en parler à un professeur.";
    emotion = "Anxiété";
    gravite = "MOYEN";
  } else {
    resultMsg = "Tout semble aller bien ! Continuez à prendre soin de vous.";
    emotion = "Positif";
    gravite = "FAIBLE";
  }

  container.innerHTML = `
    <div class="test-intro">
      <h3 style="margin-bottom: 1rem;">Test Terminé</h3>
      <p>${resultMsg}</p>
      <button class="btn btn-ghost" onclick="resetTestView()">Retour au dashboard</button>
    </div>
  `;

  // Create alert automatically if needed
  if (gravite !== 'FAIBLE') {
    const defaultStudent = users.find(u => u.role === 'ELEVE') || { id: 2 };
    const newAlerte = {
      eleve: { id: defaultStudent.id },
      emotionDetectee: emotion,
      gravite,
      contexteScolaire: "Auto-évaluation étudiant (Test)",
      scoreConfianceIA: 1.0,
      actionRecommandee: "Soutien proactif"
    };

    await fetch(`${API_URL}/alerts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newAlerte)
    });

    await refreshData();
    renderAlertsTable();
    addDynamicInsight(`Nouvelle alerte : Auto-évaluation (${emotion}) soumise.`);
  }
}

function initStudentEvents() {
  const openBtn = document.getElementById('open-student-portal');
  const closeBtn = document.getElementById('close-student-modal');
  const modal = document.getElementById('student-modal');

  openBtn.addEventListener('click', () => {
    modal.classList.add('active');
    resetStudentTestView();
  });

  closeBtn.addEventListener('click', () => {
    modal.classList.remove('active');
  });
}

function startStudentTest() {
  currentQuestionIndex = 0;
  testScore = 0;
  renderStudentQuestion();
}

function renderStudentQuestion() {
  const container = document.getElementById('student-test-content');
  const question = testQuestions[currentQuestionIndex];
  const progress = ((currentQuestionIndex) / testQuestions.length) * 100;

  container.innerHTML = `
    <div class="question-container">
      <div class="test-progress">
        <div class="progress-fill" style="width: ${progress}%"></div>
      </div>
      <p class="question-text">${question.q}</p>
      <div class="options-list">
        ${question.options.map((opt, i) => `
          <button class="option-btn" onclick="handleStudentAnswer(${opt.score})">
            ${opt.text}
          </button>
        `).join('')}
      </div>
    </div>
  `;
}

async function handleStudentAnswer(score) {
  testScore += score;
  currentQuestionIndex++;

  if (currentQuestionIndex < testQuestions.length) {
    renderStudentQuestion();
  } else {
    await finishStudentTest();
  }
}

async function finishStudentTest() {
  const container = document.getElementById('student-test-content');
  const maxScore = testQuestions.length * 3;
  const ratio = testScore / maxScore;

  let resultMsg = "";
  let emotion = "Stable";
  let gravite = "FAIBLE";

  if (ratio > 0.7) {
    resultMsg = "Nous avons bien reçu ton message. Un conseiller va venir te voir très bientôt pour t'aider. Respire, tu n'es pas seul(e).";
    emotion = "Détresse";
    gravite = "ELEVE";
  } else if (ratio > 0.4) {
    resultMsg = "Il est normal de se sentir stressé(e) parfois. Nous avons prévenu l'équipe pour qu'on puisse en discuter ensemble.";
    emotion = "Anxiété";
    gravite = "MOYEN";
  } else {
    resultMsg = "Merci d'avoir partagé ton état. On dirait que ça va plutôt bien, mais on reste là si besoin !";
    emotion = "Positif";
    gravite = "FAIBLE";
  }

  container.innerHTML = `
    <div class="test-intro" style="animation: fadeIn 0.5s ease-out;">
      <div style="font-size: 4rem; margin-bottom: 1rem;">✨</div>
      <h3 style="margin-bottom: 1rem; color: #fff; font-size: 1.5rem;">C'est envoyé !</h3>
      <p style="font-size: 1.1rem; line-height: 1.6;">${resultMsg}</p>
      <div style="margin-top: 2rem; padding: 1rem; background: rgba(255,255,255,0.05); border-radius: 1rem;">
        <p style="font-size: 0.9rem; color: var(--accent);">✔ Ton conseiller a été notifié en priorité.</p>
      </div>
      <button class="btn btn-primary" style="margin-top: 2rem; width: 100%;" onclick="document.getElementById('student-modal').classList.remove('active')">Fermer la fenêtre</button>
    </div>
  `;

  // Use the logged student ID
  const studentId = loggedStudent ? loggedStudent.id : 2;
  const studentName = loggedStudent ? `${loggedStudent.nom} ${loggedStudent.prenom}` : 'Anonyme';

  const newAlerte = {
    eleve: { id: studentId },
    emotionDetectee: emotion,
    gravite: gravite === 'FAIBLE' ? 'MOYEN' : gravite,
    contexteScolaire: `Alerte DIRECTE de ${studentName} via le portail de soutien`,
    scoreConfianceIA: 1.0,
    actionRecommandee: "Contacter l'élève immédiatement"
  };

  await fetch(`${API_URL}/alerts`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(newAlerte)
  });

  await refreshData();
  renderView(currentView);
  addDynamicInsight(`ALERTE DIRECTE : ${studentName} a demandé de l'aide.`);
}

async function attemptStudentLogin() {
  const schoolId = document.getElementById('student-school-id').value;
  const errorDiv = document.getElementById('login-error');

  if (!schoolId) {
    errorDiv.textContent = "Merci d'entrer ton identifiant.";
    errorDiv.style.display = 'block';
    return;
  }

  try {
    const res = await fetch(`${API_URL}/login?sid=${schoolId}`, { method: 'POST' });
    if (res.ok) {
      loggedStudent = await res.json();
      document.getElementById('student-login-view').style.display = 'none';
      document.getElementById('student-intro-view').style.display = 'block';
      document.getElementById('logged-student-name').textContent = loggedStudent.prenom;
      errorDiv.style.display = 'none';
    } else {
      errorDiv.textContent = "Identifiant inconnu. Vérifie avec ton école.";
      errorDiv.style.display = 'block';
    }
  } catch (err) {
    errorDiv.textContent = "Erreur de connexion au serveur.";
    errorDiv.style.display = 'block';
  }
}

function resetStudentTestView() {
  loggedStudent = null;
  const container = document.getElementById('student-test-content');

  // Re-inject the initial login view instead of reloading the page
  container.innerHTML = `
    <!-- Login View -->
    <div id="student-login-view" class="login-step">
        <p>Entre ton identifiant scolaire pour continuer.</p>
        <div class="form-group" style="margin: 1.5rem 0;">
            <input type="text" id="student-school-id" placeholder="Ex: EDU-JEAN-103" style="text-align:center;">
            <div id="login-error" class="error-text" style="display:none; color: var(--danger); margin-top: 0.5rem; font-size: 0.8rem;"></div>
        </div>
        <button class="btn btn-primary btn-lg" style="width:100%;" onclick="attemptStudentLogin()">Se connecter</button>
        <p style="font-size: 0.75rem; margin-top: 1rem; color: var(--text-muted);">Tes réponses resteront confidentielles.</p>
    </div>

    <!-- Test Intro View (Hidden initially) -->
    <div id="student-intro-view" class="test-intro" style="display:none;">
        <p>Bonjour <span id="logged-student-name"></span> !<br>Ce petit test rapide permet de prévenir un conseiller que tu ne te sens pas bien.</p>
        <button class="btn btn-primary btn-lg" onclick="startStudentTest()">Lancer l'alerte & Faire le test</button>
    </div>
  `;
}

// Live Simulation for Dashboard
// Live Polling for Real-Time Alerts
let pollInterval = null;
let lastAlertCount = 0;

function startLiveSimulation() {
  if (pollInterval) clearInterval(pollInterval);

  // Initial check
  checkForNewAlerts();

  pollInterval = setInterval(() => {
    // Keep monitoring bars animation purely for visual effect (as it simulates real-time stream processing)
    if (currentView === 'dashboard') {
      const bars = document.querySelectorAll('.m-fill');
      bars.forEach(bar => {
        const newVal = 20 + Math.random() * 60;
        bar.style.width = newVal + '%';
      });
    }

    // Real Data Polling (every 5 seconds)
    checkForNewAlerts();
  }, 5000);
}

async function checkForNewAlerts() {
  if (!loggedAdmin) return;

  try {
    const response = await fetch(`${API_URL}/alerts`);
    if (response.ok) {
      const currentAlerts = await response.json();

      // Detect if new alerts arrived
      if (lastAlertCount > 0 && currentAlerts.length > lastAlertCount) {
        const diff = currentAlerts.length - lastAlertCount;
        const latestAlert = currentAlerts[currentAlerts.length - 1];

        showToast(`⚠️ ${diff} Nouvelle(s) Alerte(s) Détectée(s) !`);
        addDynamicInsight(`ALERTE : ${latestAlert.emotionDetectee} détecté pour ${latestAlert.eleve ? latestAlert.eleve.nom : 'Inconnu'}`);

        // If on dashboard, refresh table
        if (currentView === 'dashboard') {
          alerts = currentAlerts;
          updateStats();
          renderAlertsTable();
        }
      }

      // Update global state
      alerts = currentAlerts;
      lastAlertCount = currentAlerts.length;

      // Also check for notifications if userId is available
      if (loggedAdmin.id) {
        checkNotifications(loggedAdmin.id);
      }
    }
  } catch (e) {
    console.error("Polling error:", e);
  }
}

async function checkNotifications(userId) {
  try {
    const res = await fetch(`${API_URL}/notifications?userId=${userId}`);
    if (res.ok) {
      const notifs = await res.json();
      // Simple logic: if latest notification is very recent (last 10s), show it
      // For MVP we just log or could add a bell icon later
    }
  } catch (e) { }
}

function showToast(message) {
  const toast = document.createElement('div');
  toast.className = 'toast-notification';
  toast.innerHTML = `
        <div style="display:flex; align-items:center; gap:10px;">
            <div style="background:var(--danger); width:8px; height:8px; border-radius:50%;"></div>
            <span>${message}</span>
        </div>
    `;
  document.body.appendChild(toast);

  // Animation in css typically, here inline for simplicity
  Object.assign(toast.style, {
    position: 'fixed',
    top: '20px',
    right: '20px',
    background: 'rgba(20, 20, 30, 0.95)',
    color: '#fff',
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
    borderLeft: '4px solid var(--danger)',
    zIndex: '9999',
    animation: 'slideIn 0.3s ease-out'
  });

  setTimeout(() => {
    toast.remove();
  }, 4000);
}

function addDynamicInsight(customMsg = null) {
  const insightsList = document.getElementById('insights-list');
  if (!insightsList) return;

  const insights = [
    { text: "IA : Analyse faciale stable sur le groupe classe.", color: "var(--success)" },
    { text: "Système : 3 nouvelles auto-évaluations reçues.", color: "var(--primary)" },
  ];

  const insight = customMsg ? { text: customMsg, color: "var(--danger)" } : insights[Math.floor(Math.random() * insights.length)];

  const newItem = document.createElement('div');
  newItem.className = 'insight-item';
  newItem.style.animation = 'fadeInRight 0.5s ease-out';
  newItem.innerHTML = `
    <div class="insight-dot blink" style="background: ${insight.color}"></div>
    <div class="insight-content">
      <p>${insight.text}</p>
      <span>À l'instant</span>
    </div>
  `;

  insightsList.prepend(newItem);
  if (insightsList.children.length > 5) {
    insightsList.removeChild(insightsList.lastChild);
  }
}
