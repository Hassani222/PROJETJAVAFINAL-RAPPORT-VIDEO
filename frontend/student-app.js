// Student Portal JavaScript
const API_URL = 'http://localhost:7000/api';
let loggedStudent = null;
let currentQuestionIndex = 0;
let testScore = 0;

// Test Questions
const testQuestions = [
    {
        q: "Comment te sens-tu aujourd'hui ?",
        options: [
            { text: "😊 Très bien", score: 0 },
            { text: "🙂 Plutôt bien", score: 1 },
            { text: "😐 Pas terrible", score: 2 },
            { text: "😔 Très mal", score: 3 }
        ]
    },
    {
        q: "As-tu des difficultés à te concentrer en classe ?",
        options: [
            { text: "Non, pas du tout", score: 0 },
            { text: "Parfois", score: 1 },
            { text: "Souvent", score: 2 },
            { text: "Tout le temps", score: 3 }
        ]
    },
    {
        q: "Te sens-tu stressé(e) ou anxieux(se) ?",
        options: [
            { text: "Non", score: 0 },
            { text: "Un peu", score: 1 },
            { text: "Assez", score: 2 },
            { text: "Beaucoup", score: 3 }
        ]
    },
    {
        q: "As-tu envie de parler à quelqu'un ?",
        options: [
            { text: "Non, ça va", score: 0 },
            { text: "Peut-être", score: 1 },
            { text: "Oui, j'aimerais bien", score: 2 },
            { text: "Oui, j'en ai vraiment besoin", score: 3 }
        ]
    },
    {
        q: "Comment sont tes relations avec tes camarades ?",
        options: [
            { text: "Très bonnes", score: 0 },
            { text: "Bonnes", score: 1 },
            { text: "Difficiles", score: 2 },
            { text: "Je me sens isolé(e)", score: 3 }
        ]
    }
];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkStudentSession();
    initEventListeners();
});

function initEventListeners() {
    // Login
    document.getElementById('student-login-form').addEventListener('submit', attemptStudentLogin);

    // Logout
    document.getElementById('logout-btn').addEventListener('click', logoutStudent);

    // Test
    document.getElementById('start-test-btn').addEventListener('click', startTest);
    document.getElementById('close-test-btn').addEventListener('click', closeTest);
}

// Session Management
function checkStudentSession() {
    const session = localStorage.getItem('studentSession');
    if (session) {
        loggedStudent = JSON.parse(session);
        showDashboard();
        loadStudentData();
    } else {
        showLogin();
    }
}

function showLogin() {
    document.getElementById('student-login-container').classList.add('active');
    document.getElementById('student-dashboard').classList.remove('active');
}

function showDashboard() {
    document.getElementById('student-login-container').classList.remove('active');
    document.getElementById('student-dashboard').classList.add('active');

    if (loggedStudent) {
        document.getElementById('student-name').textContent = loggedStudent.prenom || 'Étudiant';
    }
}

// Authentication
async function attemptStudentLogin(e) {
    e.preventDefault();

    const email = document.getElementById('student-email').value;
    const password = document.getElementById('student-password').value;
    const errorDiv = document.getElementById('login-error');

    console.log('Attempting login with:', email);

    try {
        const response = await fetch(`${API_URL}/admin/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        console.log('Response status:', response.status);

        if (response.ok) {
            const user = await response.json();
            console.log('User data:', user);

            // Check if user is a student (accept both ELEVE and Eleve)
            const userRole = user.role ? user.role.toUpperCase() : '';
            console.log('User role:', userRole);

            if (userRole !== 'ELEVE') {
                errorDiv.textContent = `Accès réservé aux étudiants. Votre rôle: ${user.role || 'Non défini'}`;
                errorDiv.classList.add('show');
                return;
            }

            loggedStudent = user;
            localStorage.setItem('studentSession', JSON.stringify(user));
            errorDiv.classList.remove('show');
            showDashboard();
            loadStudentData();
        } else {
            const errorText = await response.text();
            console.error('Login failed:', errorText);
            errorDiv.textContent = errorText || "Email ou mot de passe incorrect.";
            errorDiv.classList.add('show');
        }
    } catch (err) {
        console.error("Login Error:", err);
        errorDiv.textContent = "Erreur de connexion au serveur. Vérifiez que le serveur est démarré.";
        errorDiv.classList.add('show');
    }
}

function logoutStudent() {
    localStorage.removeItem('studentSession');
    loggedStudent = null;
    showLogin();
}

// Load Student Data
async function loadStudentData() {
    if (!loggedStudent) {
        console.error('No logged student');
        return;
    }

    console.log('Loading data for student:', loggedStudent.id);

    try {
        // Load alerts history for this specific student
        const alertsResponse = await fetch(`${API_URL}/alerts/eleve/${loggedStudent.id}`);
        if (alertsResponse.ok) {
            const myAlerts = await alertsResponse.json();
            console.log('My alerts:', myAlerts);
            renderAlertsHistory(myAlerts);
        } else {
            console.error('Failed to load alerts:', alertsResponse.status);
        }

        // Load interventions specifically for this student
        const interventionsResponse = await fetch(`${API_URL}/interventions/eleve/${loggedStudent.id}`);
        if (interventionsResponse.ok) {
            const myInterventions = await interventionsResponse.json();
            console.log('My interventions:', myInterventions);
            renderInterventions(myInterventions);
        } else {
            console.error('Failed to load interventions:', interventionsResponse.status);
        }
    } catch (err) {
        console.error("Error loading data:", err);
    }
}

function renderInterventions(interventions) {
    const container = document.getElementById('interventions-list');

    if (interventions.length === 0) {
        container.innerHTML = '<p class="empty-state">Aucune intervention pour le moment</p>';
        return;
    }

    container.innerHTML = interventions.map(inter => `
        <div class="intervention-item">
            <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 1rem;">
                <div>
                    <h4 style="margin-bottom: 0.5rem;">📋 Plan d'intervention</h4>
                    <span class="status-pill ${inter.statut === 'En cours' ? 'warning' : 'success'}">${inter.statut || 'En cours'}</span>
                </div>
                <small style="color: var(--text-muted);">${formatDate(inter.dateDebut)}</small>
            </div>
            <p style="line-height: 1.6; color: var(--text-muted);">${inter.description || 'Pas de description'}</p>
        </div>
    `).join('');
}

function renderAlertsHistory(alerts) {
    const container = document.getElementById('alerts-history');

    if (alerts.length === 0) {
        container.innerHTML = '<p class="empty-state">Aucun historique</p>';
        return;
    }

    container.innerHTML = alerts.slice(0, 5).map(alert => `
        <div class="history-item">
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                <strong>${alert.emotionDetectee}</strong>
                <span class="status-pill ${getGravityClass(alert.gravite)}">${alert.gravite}</span>
            </div>
            <p style="font-size: 0.9rem; color: var(--text-muted);">${alert.contexteScolaire || 'Auto-évaluation'}</p>
            <small style="color: var(--text-muted);">${formatDate(alert.dateDetection)}</small>
        </div>
    `).join('');
}

function getGravityClass(gravity) {
    switch (gravity) {
        case 'ELEVE': return 'danger';
        case 'MOYEN': return 'warning';
        default: return 'success';
    }
}

function formatDate(dateArr) {
    if (!dateArr) return 'N/A';
    if (Array.isArray(dateArr)) {
        const [year, month, day, hour, minute] = dateArr;
        return `${day}/${month.toString().padStart(2, '0')}/${year} ${hour}:${minute.toString().padStart(2, '0')}`;
    }
    return dateArr;
}

// Test Flow
function startTest() {
    currentQuestionIndex = 0;
    testScore = 0;
    document.getElementById('test-modal').classList.add('active');
    renderQuestion();
}

function closeTest() {
    document.getElementById('test-modal').classList.remove('active');
}

function renderQuestion() {
    const container = document.getElementById('test-content');
    const question = testQuestions[currentQuestionIndex];
    const progress = ((currentQuestionIndex) / testQuestions.length) * 100;

    container.innerHTML = `
        <div class="question-container">
            <div class="progress-bar">
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

    let emotion, gravite, message;

    if (ratio > 0.6) {
        emotion = "Détresse";
        gravite = "ELEVE";
        message = "Nous avons remarqué que tu traverses une période difficile. Un conseiller va être informé pour te proposer un entretien de soutien. Tu n'es pas seul(e) ! 💙";
    } else if (ratio > 0.4) {
        emotion = "Anxiété";
        gravite = "MOYEN";
        message = "Tu sembles un peu stressé(e). N'hésite pas à en parler à un professeur ou à consulter nos ressources de relaxation. On est là pour toi ! 🌟";
    } else {
        emotion = "Positif";
        gravite = "FAIBLE";
        message = "Tout semble aller bien ! Continue à prendre soin de toi. N'hésite pas à revenir si tu en ressens le besoin. 😊";
    }

    container.innerHTML = `
        <div class="test-result">
            <h3>Merci pour ta confiance</h3>
            <p>${message}</p>
            <button class="btn btn-primary" onclick="closeTest(); loadStudentData();">Retour au tableau de bord</button>
        </div>
    `;

    // Create alert if needed
    if (gravite !== 'FAIBLE') {
        await createAlert(emotion, gravite);
    }
}

async function createAlert(emotion, gravite) {
    if (!loggedStudent) return;

    try {
        const alertData = {
            eleve: { id: loggedStudent.id },
            emotionDetectee: emotion,
            gravite: gravite,
            contexteScolaire: "Auto-évaluation étudiant (Portail)",
            scoreConfianceIA: 1.0,
            actionRecommandee: "Soutien proactif"
        };

        await fetch(`${API_URL}/alerts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(alertData)
        });

        console.log("Alert created successfully");
    } catch (err) {
        console.error("Error creating alert:", err);
    }
}
