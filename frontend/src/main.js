import { initialData } from './mockData.js';
import { api } from './api.js';

// Application State
const state = {
  theme: localStorage.getItem('apex_theme') || 'dark',
  currentRole: 'STUDENT', // 'STUDENT' | 'INSTRUCTOR' | 'ADMIN'
  activeTab: 'dashboard',
  searchQuery: '',
  data: JSON.parse(JSON.stringify(initialData)),
  activeCourse: null,
  activeLesson: null,
  activeQuiz: null,
  quizState: {
    currentIndex: 0,
    answers: {},
    submitted: false,
    score: 0
  },
  backendStatus: {
    online: false,
    checked: false,
    latency: '—'
  }
};

// State shortcuts
state.activeCourse = state.data.courses[0];
state.activeLesson = state.activeCourse.modules[0].lessons[0];
state.activeQuiz = state.data.quizzes[0];

// Toast notification helper
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✓' : 'ℹ'}</span>
    <span>${message}</span>
  `;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Modal helper
function openModal(contentHtml) {
  const modalRoot = document.getElementById('modal-root');
  if (!modalRoot) return;
  modalRoot.innerHTML = contentHtml;
  modalRoot.classList.remove('hidden');

  const closeBtn = modalRoot.querySelector('.btn-close');
  if (closeBtn) {
    closeBtn.addEventListener('click', closeModal);
  }
  modalRoot.addEventListener('click', (e) => {
    if (e.target === modalRoot) closeModal();
  });
}

function closeModal() {
  const modalRoot = document.getElementById('modal-root');
  if (modalRoot) {
    modalRoot.classList.add('hidden');
    modalRoot.innerHTML = '';
  }
}

// Check Backend Health
async function checkBackendConnectivity() {
  const start = performance.now();
  const isOnline = await api.checkHealth();
  const latency = Math.round(performance.now() - start);
  state.backendStatus.online = isOnline;
  state.backendStatus.checked = true;
  state.backendStatus.latency = `${latency}ms`;
  renderHeaderStatus();
}

// Render Top Status Pill
function renderHeaderStatus() {
  const pill = document.getElementById('live-status-pill');
  if (!pill) return;
  if (state.backendStatus.online) {
    pill.className = 'status-pill';
    pill.innerHTML = `
      <span class="status-dot"></span>
      <span>Spring Boot Live (${state.backendStatus.latency})</span>
    `;
  } else {
    pill.className = 'status-pill offline';
    pill.innerHTML = `
      <span class="status-dot"></span>
      <span>Live Simulator (Local)</span>
    `;
  }
}

// Main View Renderer
function render() {
  const app = document.getElementById('app');
  if (!app) return;

  // Apply theme attribute
  document.documentElement.setAttribute('data-theme', state.theme);

  app.innerHTML = `
    <!-- Top Header -->
    <header class="app-header">
      <div class="header-container">
        <div class="brand-section">
          <div class="brand-logo-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
            </svg>
          </div>
          <div>
            <div class="brand-title">APEX COLLEGE LMS</div>
            <div class="brand-subtitle">Academic Enterprise OS</div>
          </div>
        </div>

        <div class="nav-search">
          <span class="search-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
            </svg>
          </span>
          <input 
            type="text" 
            id="global-search-input" 
            placeholder="Search courses, syllabi, assignments..." 
            value="${state.searchQuery}"
          />
        </div>

        <div class="header-actions">
          <!-- Role Switcher -->
          <div class="role-switcher-container" title="Switch User Persona">
            <button class="role-pill-btn ${state.currentRole === 'STUDENT' ? 'active' : ''}" data-role="STUDENT">Student</button>
            <button class="role-pill-btn ${state.currentRole === 'INSTRUCTOR' ? 'active' : ''}" data-role="INSTRUCTOR">Instructor</button>
            <button class="role-pill-btn ${state.currentRole === 'ADMIN' ? 'active' : ''}" data-role="ADMIN">Admin</button>
          </div>

          <!-- Live Status -->
          <div id="live-status-pill" class="status-pill offline">
            <span class="status-dot"></span>
            <span>Checking...</span>
          </div>

          <!-- Theme Toggle -->
          <button id="theme-toggle-btn" class="btn-icon" title="Toggle Dark/Light Mode">
            ${state.theme === 'dark' ? 
              `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>` : 
              `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>`
            }
          </button>

          <!-- User Badge -->
          <div class="user-profile-badge" id="profile-badge-btn">
            <img class="user-avatar" src="${getCurrentUser().avatar}" alt="${getCurrentUser().name}" />
            <div class="user-info-text">
              <span class="user-name-small">${getCurrentUser().name}</span>
              <span class="user-role-small">${state.currentRole}</span>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">
      <!-- Role Navigation Tabs -->
      <nav class="nav-tabs-bar" aria-label="Portal Navigation">
        ${renderNavigationTabs()}
      </nav>

      <!-- Dynamic Tab Views -->
      <div id="tab-view-container">
        ${renderCurrentView()}
      </div>
    </main>
  `;

  attachEventListeners();
  renderHeaderStatus();
}

function getCurrentUser() {
  const match = state.data.availableUsers.find(u => u.role === state.currentRole);
  return match || state.data.currentUser;
}

function renderNavigationTabs() {
  if (state.currentRole === 'STUDENT') {
    return `
      <button class="nav-tab-item ${state.activeTab === 'dashboard' ? 'active' : ''}" data-tab="dashboard">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg>
        Dashboard
      </button>
      <button class="nav-tab-item ${state.activeTab === 'classroom' ? 'active' : ''}" data-tab="classroom">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="6 3 20 12 6 21 6 3"/></svg>
        Learning Classroom
      </button>
      <button class="nav-tab-item ${state.activeTab === 'assignments' ? 'active' : ''}" data-tab="assignments">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
        Assignments & Labs
      </button>
      <button class="nav-tab-item ${state.activeTab === 'assessment' ? 'active' : ''}" data-tab="assessment">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>
        Assessment & Quizzes
      </button>
    `;
  } else if (state.currentRole === 'INSTRUCTOR') {
    return `
      <button class="nav-tab-item ${state.activeTab === 'instructor-courses' ? 'active' : ''}" data-tab="instructor-courses">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5Z"/></svg>
        Course Management
      </button>
      <button class="nav-tab-item ${state.activeTab === 'grading-queue' ? 'active' : ''}" data-tab="grading-queue">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 11 3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        Grading & Submissions Queue
      </button>
      <button class="nav-tab-item ${state.activeTab === 'virtual-classes' ? 'active' : ''}" data-tab="virtual-classes">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m15.5 8.5 5-3.5v14l-5-3.5"/><rect width="13" height="14" x="2" y="5" rx="2"/></svg>
        Live Virtual Lectures
      </button>
    `;
  } else {
    return `
      <button class="nav-tab-item ${state.activeTab === 'admin-overview' ? 'active' : ''}" data-tab="admin-overview">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="20" height="14" x="2" y="5" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
        Institution & Terms
      </button>
      <button class="nav-tab-item ${state.activeTab === 'audit-trail' ? 'active' : ''}" data-tab="audit-trail">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
        Security Audit Trail
      </button>
      <button class="nav-tab-item ${state.activeTab === 'system-diagnostics' ? 'active' : ''}" data-tab="system-diagnostics">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
        API & Gateway Diagnostics
      </button>
    `;
  }
}

function renderCurrentView() {
  switch (state.activeTab) {
    case 'dashboard':
      return renderStudentDashboard();
    case 'classroom':
      return renderLearningClassroom();
    case 'assignments':
      return renderAssignmentsView();
    case 'assessment':
      return renderAssessmentView();
    case 'instructor-courses':
      return renderInstructorCoursesView();
    case 'grading-queue':
      return renderGradingQueueView();
    case 'virtual-classes':
      return renderVirtualClassesView();
    case 'admin-overview':
      return renderAdminOverview();
    case 'audit-trail':
      return renderAuditTrailView();
    case 'system-diagnostics':
      return renderSystemDiagnosticsView();
    default:
      return renderStudentDashboard();
  }
}

/* ============================================================
   Student Dashboard View
   ============================================================ */
function renderStudentDashboard() {
  const user = getCurrentUser();
  const enrolledCourses = state.data.courses.filter(c => 
    state.searchQuery ? c.title.toLowerCase().includes(state.searchQuery.toLowerCase()) : true
  );

  return `
    <!-- Hero Banner -->
    <section class="hero-banner">
      <div>
        <div class="hero-badge-tag">
          <span>●</span> ${user.department} • Term: ${user.term}
        </div>
        <h1 class="hero-greeting">Welcome back, ${user.name}</h1>
        <p class="hero-subtext">You have 1 pending laboratory due tomorrow and 2 active modules ready for review.</p>
      </div>
      <div>
        <button class="btn-primary" id="btn-quick-continue">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
          Resume CS-482 Lesson
        </button>
      </div>
    </section>

    <!-- Metrics Grid -->
    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-icon-box indigo">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5Z"/></svg>
        </div>
        <div>
          <div class="metric-value">${state.data.courses.length}</div>
          <div class="metric-label">Enrolled Courses</div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon-box cyan">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="6"/><path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11"/></svg>
        </div>
        <div>
          <div class="metric-value">3.84</div>
          <div class="metric-label">Cumulative GPA</div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon-box amber">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        </div>
        <div>
          <div class="metric-value">2</div>
          <div class="metric-label">Upcoming Deadlines</div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon-box emerald">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        </div>
        <div>
          <div class="metric-value">96%</div>
          <div class="metric-label">Attendance Rate</div>
        </div>
      </div>
    </div>

    <!-- Active Courses Section -->
    <div class="section-header">
      <h2 class="section-title">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 17 12 22 22 17"/><polyline points="2 12 12 17 22 12"/></svg>
        Enrolled Degree Courses
      </h2>
      <div class="section-actions">
        <button class="btn-secondary" id="btn-browse-catalog">Browse Catalog</button>
      </div>
    </div>

    <div class="course-grid">
      ${enrolledCourses.map(course => `
        <article class="course-card">
          <div class="course-card-cover">
            <img src="${course.image}" alt="${course.title}" loading="lazy" />
            <span class="course-badge-code">${course.code}</span>
          </div>
          <div class="course-card-body">
            <h3 class="course-card-title">${course.title}</h3>
            <div class="course-instructor-text">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              ${course.instructor} • ${course.credits} Credits
            </div>
            <p class="course-description-text">${course.description}</p>
            
            <div class="progress-container">
              <div class="progress-header">
                <span>Progress: ${course.completedHours}/${course.totalHours} hrs</span>
                <span>${course.progress}%</span>
              </div>
              <div class="progress-track">
                <div class="progress-fill" style="width: ${course.progress}%"></div>
              </div>
            </div>

            <div class="course-card-footer">
              <span style="font-size: 0.8rem; color: var(--text-muted);">Rating: ★ ${course.rating}</span>
              <button class="btn-primary btn-launch-course" data-course-id="${course.id}">
                Enter Classroom
              </button>
            </div>
          </div>
        </article>
      `).join('')}
    </div>
  `;
}

/* ============================================================
   Learning Classroom & Interactive Player View
   ============================================================ */
function renderLearningClassroom() {
  const course = state.activeCourse;
  const lesson = state.activeLesson;

  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
          ${course.title}
        </h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary); margin-top: 2px;">
          Instructor: ${course.instructor} • Course Code: ${course.code}
        </p>
      </div>
      <div>
        <button class="btn-secondary" id="btn-back-to-dashboard">← Exit to Dashboard</button>
      </div>
    </div>

    <div class="classroom-container">
      <!-- Player & Lesson Content -->
      <div class="classroom-player-pane">
        <div class="video-frame-container">
          <video controls poster="${course.image}">
            <source src="${lesson.videoUrl || 'https://www.w3schools.com/html/mov_bbb.mp4'}" type="video/mp4">
            Your browser does not support the video tag.
          </video>
        </div>

        <div class="lesson-meta-header">
          <div>
            <div class="lesson-breadcrumbs">Now Playing • ${lesson.duration}</div>
            <h2 class="lesson-title-big">${lesson.title}</h2>
          </div>
          <button class="btn-primary" id="btn-toggle-complete-lesson">
            ${lesson.completed ? '✓ Lesson Completed' : 'Mark as Complete'}
          </button>
        </div>

        <div class="lesson-notes-content">
          <h4 style="margin-bottom: 8px; font-weight: 700; color: var(--text-primary);">Lecture Objectives & Key Takeaways</h4>
          <p>${lesson.notes || 'In this lecture session, students will examine architectural resilience paradigms, decoupling mechanisms, and contract-driven API design patterns deployed across enterprise infrastructures.'}</p>
          <div style="margin-top: 12px; display: flex; gap: 12px;">
            <button class="btn-secondary" style="font-size: 0.8rem;" id="btn-download-slides">Download Slides (.PDF)</button>
            <button class="btn-secondary" style="font-size: 0.8rem;" id="btn-open-notes">Class Discussion Forum</button>
          </div>
        </div>
      </div>

      <!-- Syllabus & Module Drawer -->
      <aside class="classroom-syllabus-pane">
        <div class="syllabus-header">
          <h3 class="syllabus-title">Course Curriculum</h3>
          <div style="font-size: 0.75rem; color: var(--accent-cyan); margin-top: 4px;">
            ${course.modules.length} Modules • Overall Progress: ${course.progress}%
          </div>
        </div>

        <div class="syllabus-scroll">
          ${course.modules.map((mod, modIdx) => `
            <div class="module-accordion">
              <div class="module-title-bar">
                <span>${mod.title}</span>
                <span style="font-size: 0.75rem; color: var(--text-muted);">${mod.lessons.length} Lessons</span>
              </div>
              <ul class="lesson-list">
                ${mod.lessons.map(les => `
                  <li class="lesson-item ${les.id === lesson.id ? 'active' : ''}" data-lesson-id="${les.id}">
                    <div style="display: flex; align-items: center; gap: 8px;">
                      <span style="color: ${les.completed ? 'var(--accent-emerald)' : 'var(--text-muted)'};">
                        ${les.completed ? '✓' : '○'}
                      </span>
                      <span>${les.title}</span>
                    </div>
                    <span style="font-size: 0.7rem; color: var(--text-muted);">${les.duration}</span>
                  </li>
                `).join('')}
              </ul>
            </div>
          `).join('')}
        </div>
      </aside>
    </div>
  `;
}

/* ============================================================
   Assignments & Submissions View
   ============================================================ */
function renderAssignmentsView() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
          Academic Assignments & Lab Deliverables
        </h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary); margin-top: 2px;">
          Submit your project source code, lab writeups, and track evaluator grading feedback.
        </p>
      </div>
    </div>

    <div class="data-table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Course</th>
            <th>Assignment Title</th>
            <th>Due Date</th>
            <th>Points</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          ${state.data.assignments.map(asg => `
            <tr>
              <td><strong>${asg.courseName}</strong></td>
              <td>${asg.title}</td>
              <td><span style="color: var(--accent-amber); font-weight: 600;">${asg.dueDate}</span></td>
              <td>${asg.points} pts</td>
              <td>
                <span class="badge-tag ${asg.status === 'SUBMITTED' ? 'badge-submitted' : asg.status === 'GRADED' ? 'badge-graded' : 'badge-pending'}">
                  ${asg.status}
                </span>
              </td>
              <td>
                <button class="btn-primary btn-submit-asg" data-asg-id="${asg.id}" style="padding: 6px 14px; font-size: 0.8rem;">
                  ${asg.status === 'SUBMITTED' ? 'View Submission' : 'Submit Lab'}
                </button>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

/* ============================================================
   Assessment & Quiz Runner View
   ============================================================ */
function renderAssessmentView() {
  const quiz = state.activeQuiz;
  const qState = state.quizState;
  const currentQ = quiz.questions[qState.currentIndex];

  if (qState.submitted) {
    const percentage = Math.round((qState.score / quiz.questions.length) * 100);
    return `
      <div class="quiz-runner-card" style="text-align: center;">
        <div style="width: 72px; height: 72px; border-radius: 50%; background: rgba(16, 185, 129, 0.15); color: var(--accent-emerald); display: flex; align-items: center; justify-content: center; margin: 0 auto 18px; font-size: 2rem;">
          ✓
        </div>
        <h2 style="font-size: 1.6rem; font-weight: 800; margin-bottom: 8px;">Assessment Completed!</h2>
        <p style="color: var(--text-secondary); margin-bottom: 24px;">${quiz.title}</p>
        
        <div style="font-size: 3rem; font-weight: 800; color: var(--accent-emerald); font-family: var(--font-display); margin-bottom: 8px;">
          ${percentage}%
        </div>
        <p style="font-size: 1rem; color: var(--text-secondary); margin-bottom: 28px;">
          You answered ${qState.score} out of ${quiz.questions.length} questions correctly.
        </p>

        <div style="display: flex; gap: 12px; justify-content: center;">
          <button class="btn-primary" id="btn-retake-quiz">Retake Assessment</button>
          <button class="btn-secondary" id="btn-quiz-back-dash">Back to Dashboard</button>
        </div>
      </div>
    `;
  }

  return `
    <div class="quiz-runner-card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid var(--border-subtle); padding-bottom: 14px;">
        <div>
          <span style="font-size: 0.75rem; color: var(--accent-cyan); font-weight: 700; text-transform: uppercase;">
            ${quiz.courseName}
          </span>
          <h3 style="font-size: 1.15rem; font-weight: 700;">${quiz.title}</h3>
        </div>
        <div style="background: var(--bg-tertiary); padding: 6px 14px; border-radius: var(--radius-full); font-size: 0.8rem; font-weight: 700; color: var(--accent-amber);">
          Question ${qState.currentIndex + 1} of ${quiz.questions.length}
        </div>
      </div>

      <h2 class="quiz-question-title">${currentQ.question}</h2>

      <div class="quiz-options-list">
        ${currentQ.options.map((optionText, optIdx) => {
          const isSelected = qState.answers[qState.currentIndex] === optIdx;
          return `
            <button class="quiz-option-btn ${isSelected ? 'selected' : ''}" data-opt-index="${optIdx}">
              <span style="width: 24px; height: 24px; border-radius: 50%; border: 1px solid var(--border-subtle); display: inline-flex; align-items: center; justify-content: center; font-size: 0.75rem; font-weight: 700;">
                ${String.fromCharCode(65 + optIdx)}
              </span>
              <span>${optionText}</span>
            </button>
          `;
        }).join('')}
      </div>

      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 28px; border-top: 1px solid var(--border-subtle); padding-top: 20px;">
        <button class="btn-secondary" id="btn-prev-q" ${qState.currentIndex === 0 ? 'disabled' : ''}>
          Previous
        </button>
        ${qState.currentIndex === quiz.questions.length - 1 ? `
          <button class="btn-primary" id="btn-submit-quiz">Submit Assessment</button>
        ` : `
          <button class="btn-primary" id="btn-next-q">Next Question →</button>
        `}
      </div>
    </div>
  `;
}

/* ============================================================
   Instructor Portal Views
   ============================================================ */
function renderInstructorCoursesView() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">Instructor Course Management</h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary);">
          Create syllabi, manage student rosters, and publish real-time curriculum updates.
        </p>
      </div>
      <button class="btn-primary" id="btn-create-course-modal">+ Create New Course</button>
    </div>

    <div class="course-grid">
      ${state.data.courses.map(course => `
        <article class="course-card">
          <div class="course-card-cover">
            <img src="${course.image}" alt="${course.title}" />
            <span class="course-badge-code">${course.code}</span>
          </div>
          <div class="course-card-body">
            <h3 class="course-card-title">${course.title}</h3>
            <div class="course-instructor-text">
              Enrolled: <strong>${course.enrolledCount} Students</strong> • Rating: ★ ${course.rating}
            </div>
            <p class="course-description-text">${course.description}</p>
            <div class="course-card-footer" style="border-top: 1px solid var(--border-subtle); padding-top: 14px;">
              <button class="btn-secondary btn-manage-modules" data-course-id="${course.id}">Edit Modules (${course.modules.length})</button>
              <button class="btn-primary btn-view-roster" data-course-id="${course.id}">Student Roster</button>
            </div>
          </div>
        </article>
      `).join('')}
    </div>
  `;
}

function renderGradingQueueView() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">Submission & Grading Queue</h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary);">
          Review student lab submissions, score against rubrics, and publish feedback to gradebooks.
        </p>
      </div>
    </div>

    <div class="data-table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Student</th>
            <th>Course</th>
            <th>Assignment</th>
            <th>Submitted Time</th>
            <th>Score</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><strong>Aiden Vance</strong> (usr-01)</td>
            <td>CS-482 Cloud Architecture</td>
            <td>Resilient Microservices Topology & Gateway Lab</td>
            <td>10 mins ago</td>
            <td><span class="badge-tag badge-pending">Needs Grading</span></td>
            <td><button class="btn-primary btn-grade-submission" data-student="Aiden Vance" style="padding: 6px 14px; font-size: 0.8rem;">Evaluate Lab</button></td>
          </tr>
          <tr>
            <td><strong>Sarah Lin</strong> (usr-88)</td>
            <td>CS-482 Cloud Architecture</td>
            <td>Resilient Microservices Topology & Gateway Lab</td>
            <td>1 hour ago</td>
            <td><span class="badge-tag badge-graded">95/100</span></td>
            <td><button class="btn-secondary" style="padding: 6px 14px; font-size: 0.8rem;">Edit Grade</button></td>
          </tr>
        </tbody>
      </table>
    </div>
  `;
}

function renderVirtualClassesView() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">Live Virtual Lectures & Office Hours</h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary);">
          Conduct synchronized live video sessions with automatic student attendance logging.
        </p>
      </div>
      <button class="btn-primary" id="btn-schedule-lecture">+ Schedule Virtual Session</button>
    </div>

    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-icon-box indigo">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polygon points="10 8 16 12 10 16 10 8"/></svg>
        </div>
        <div>
          <div class="metric-value">Today, 2:00 PM</div>
          <div class="metric-label">CS-482 Live Lecture: Envoy & Spiffe Mesh</div>
          <div style="margin-top: 10px;">
            <button class="btn-primary" style="padding: 6px 12px; font-size: 0.75rem;" onclick="alert('Starting virtual conference room with WebRTC / Zoom bridge...')">Launch Virtual Room</button>
          </div>
        </div>
      </div>
    </div>
  `;
}

/* ============================================================
   Administrator Portal Views
   ============================================================ */
function renderAdminOverview() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">Institutional Hierarchy & Academic Terms</h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary);">
          Manage institutional faculties, academic terms, and departmental governance.
        </p>
      </div>
    </div>

    <h2 style="font-size: 1.15rem; margin-bottom: 14px;">Active Academic Terms</h2>
    <div class="metrics-grid" style="margin-bottom: 32px;">
      ${state.data.academicTerms.map(term => `
        <div class="metric-card">
          <div class="metric-icon-box ${term.status === 'CURRENT' ? 'emerald' : 'indigo'}">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect width="18" height="18" x="3" y="4" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <div>
            <div class="metric-value" style="font-size: 1.25rem;">${term.name}</div>
            <div class="metric-label">${term.startDate} to ${term.endDate}</div>
            <div style="font-size: 0.75rem; color: var(--accent-cyan); font-weight: 700; margin-top: 4px;">Status: ${term.status} (${term.coursesActive} Courses)</div>
          </div>
        </div>
      `).join('')}
    </div>

    <h2 style="font-size: 1.15rem; margin-bottom: 14px;">Academic Departments</h2>
    <div class="data-table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Department Code</th>
            <th>Department Name</th>
            <th>Department Chair</th>
            <th>Faculty Count</th>
            <th>Enrolled Students</th>
          </tr>
        </thead>
        <tbody>
          ${state.data.departments.map(dep => `
            <tr>
              <td><span class="badge-tag badge-submitted">${dep.code}</span></td>
              <td><strong>${dep.name}</strong></td>
              <td>${dep.head}</td>
              <td>${dep.facultyCount} Professors</td>
              <td>${dep.studentCount} Students</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

function renderAuditTrailView() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">Security & System Audit Log</h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary);">
          Immutable security audit trail recording all authentication events, role changes, and API transactions.
        </p>
      </div>
      <button class="btn-secondary" id="btn-refresh-audit">Refresh Audit Logs</button>
    </div>

    <div class="data-table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Actor Email</th>
            <th>Action Code</th>
            <th>Resource Target</th>
            <th>Client IP</th>
            <th>Timestamp</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          ${state.data.auditEvents.map(evt => `
            <tr>
              <td><code>${evt.actor}</code></td>
              <td><strong>${evt.action}</strong></td>
              <td>${evt.resource}</td>
              <td>${evt.ip}</td>
              <td>${evt.time}</td>
              <td><span class="badge-tag badge-active">${evt.status}</span></td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

function renderSystemDiagnosticsView() {
  return `
    <div class="section-header">
      <div>
        <h1 class="section-title">Backend API Diagnostics & System Telemetry</h1>
        <p style="font-size: 0.85rem; color: var(--text-secondary);">
          Live connection metrics to Spring Boot 3.4.0 REST microservices and database gateway.
        </p>
      </div>
      <button class="btn-primary" id="btn-run-diagnostics">Re-Test All Endpoints</button>
    </div>

    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-icon-box ${state.backendStatus.online ? 'emerald' : 'amber'}">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
        </div>
        <div>
          <div class="metric-value">${state.backendStatus.online ? 'Online' : 'Simulator Mode'}</div>
          <div class="metric-label">Backend Status (Port 8080)</div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon-box cyan">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        </div>
        <div>
          <div class="metric-value">${state.backendStatus.latency}</div>
          <div class="metric-label">HTTP Ping Latency</div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon-box indigo">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/><path d="M3 12c0 1.66 4 3 9 3s9-1.34 9-3"/></svg>
        </div>
        <div>
          <div class="metric-value">MySQL 8.0</div>
          <div class="metric-label">Target Database (lms_db)</div>
        </div>
      </div>
    </div>

    <div class="data-table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Endpoint URI</th>
            <th>HTTP Method</th>
            <th>Function</th>
            <th>Auth Requirement</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><code>/api/v1/auth/login</code></td>
            <td>POST</td>
            <td>Session Authenticator & Cookie Issuer</td>
            <td>PermitAll</td>
          </tr>
          <tr>
            <td><code>/api/v1/courses</code></td>
            <td>GET</td>
            <td>Course Catalog Service</td>
            <td>Authenticated</td>
          </tr>
          <tr>
            <td><code>/api/v1/institution/terms</code></td>
            <td>GET</td>
            <td>Academic Term Governance</td>
            <td>Authenticated</td>
          </tr>
          <tr>
            <td><code>/actuator/health</code></td>
            <td>GET</td>
            <td>Liveness & Readiness Probe</td>
            <td>PermitAll</td>
          </tr>
        </tbody>
      </table>
    </div>
  `;
}

/* ============================================================
   Event Listeners & Interactive Handlers
   ============================================================ */
function attachEventListeners() {
  // Global search input
  const searchInput = document.getElementById('global-search-input');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      state.searchQuery = e.target.value;
      const viewContainer = document.getElementById('tab-view-container');
      if (viewContainer) {
        viewContainer.innerHTML = renderCurrentView();
        attachViewSpecificListeners();
      }
    });
  }

  // Role Switcher buttons
  document.querySelectorAll('.role-pill-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const newRole = e.currentTarget.getAttribute('data-role');
      if (newRole !== state.currentRole) {
        state.currentRole = newRole;
        if (newRole === 'STUDENT') state.activeTab = 'dashboard';
        else if (newRole === 'INSTRUCTOR') state.activeTab = 'instructor-courses';
        else state.activeTab = 'admin-overview';
        showToast(`Switched active view to ${newRole} Persona`, 'info');
        render();
      }
    });
  });

  // Theme toggle button
  const themeToggle = document.getElementById('theme-toggle-btn');
  if (themeToggle) {
    themeToggle.addEventListener('click', () => {
      state.theme = state.theme === 'dark' ? 'light' : 'dark';
      localStorage.setItem('apex_theme', state.theme);
      render();
    });
  }

  // Navigation tab buttons
  document.querySelectorAll('.nav-tab-item').forEach(btn => {
    btn.addEventListener('click', (e) => {
      state.activeTab = e.currentTarget.getAttribute('data-tab');
      render();
    });
  });

  attachViewSpecificListeners();
}

function attachViewSpecificListeners() {
  // Launch Course button
  document.querySelectorAll('.btn-launch-course').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const courseId = e.currentTarget.getAttribute('data-course-id');
      const found = state.data.courses.find(c => c.id === courseId);
      if (found) {
        state.activeCourse = found;
        state.activeLesson = found.modules[0].lessons[0];
        state.activeTab = 'classroom';
        render();
      }
    });
  });

  // Quick continue
  const btnQuick = document.getElementById('btn-quick-continue');
  if (btnQuick) {
    btnQuick.addEventListener('click', () => {
      state.activeTab = 'classroom';
      render();
    });
  }

  // Exit classroom
  const btnBack = document.getElementById('btn-back-to-dashboard');
  if (btnBack) {
    btnBack.addEventListener('click', () => {
      state.activeTab = 'dashboard';
      render();
    });
  }

  // Select lesson in syllabus
  document.querySelectorAll('.lesson-item').forEach(item => {
    item.addEventListener('click', (e) => {
      const lessonId = e.currentTarget.getAttribute('data-lesson-id');
      for (const mod of state.activeCourse.modules) {
        const found = mod.lessons.find(l => l.id === lessonId);
        if (found) {
          state.activeLesson = found;
          render();
          break;
        }
      }
    });
  });

  // Mark lesson completed
  const btnToggleComplete = document.getElementById('btn-toggle-complete-lesson');
  if (btnToggleComplete) {
    btnToggleComplete.addEventListener('click', () => {
      state.activeLesson.completed = !state.activeLesson.completed;
      // recalculate course progress
      let totalLessons = 0;
      let completedLessons = 0;
      state.activeCourse.modules.forEach(m => {
        m.lessons.forEach(l => {
          totalLessons++;
          if (l.completed) completedLessons++;
        });
      });
      state.activeCourse.progress = Math.round((completedLessons / totalLessons) * 100);
      showToast(state.activeLesson.completed ? 'Lesson marked as completed! Course progress updated.' : 'Lesson marked as incomplete.');
      render();
    });
  }

  // Submit assignment modal
  document.querySelectorAll('.btn-submit-asg').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const asgId = e.currentTarget.getAttribute('data-asg-id');
      const asg = state.data.assignments.find(a => a.id === asgId);
      if (!asg) return;

      openModal(`
        <div class="modal-dialog">
          <div class="modal-header">
            <h3 class="modal-title">Submit Lab Deliverable</h3>
            <button class="btn-close">✕</button>
          </div>
          <p style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 16px;">
            ${asg.courseName} • <strong>${asg.title}</strong>
          </p>
          <div class="form-group">
            <label class="form-label">Submission Notes / Git Repository URL</label>
            <input type="text" class="form-input" id="sub-git-url" placeholder="https://github.com/apex-student/resilient-gateway" />
          </div>
          <div class="form-group">
            <label class="form-label">Attached Report / Artifacts</label>
            <div style="border: 2px dashed var(--border-subtle); border-radius: var(--radius-md); padding: 24px; text-align: center; background: var(--bg-tertiary);">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin: 0 auto 8px; color: var(--accent-indigo);"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
              <div style="font-size: 0.85rem; font-weight: 600;">Drag and drop PDF report or archive</div>
              <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 4px;">Max file size: 50MB (Validated with Apache Tika)</div>
            </div>
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button class="btn-secondary" onclick="document.getElementById('modal-root').classList.add('hidden')">Cancel</button>
            <button class="btn-primary" id="btn-confirm-submit-asg">Confirm & Submit</button>
          </div>
        </div>
      `);

      document.getElementById('btn-confirm-submit-asg')?.addEventListener('click', () => {
        asg.status = 'SUBMITTED';
        closeModal();
        showToast('Assignment successfully submitted for review!', 'success');
        render();
      });
    });
  });

  // Quiz Options
  document.querySelectorAll('.quiz-option-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const optIdx = parseInt(e.currentTarget.getAttribute('data-opt-index'), 10);
      state.quizState.answers[state.quizState.currentIndex] = optIdx;
      render();
    });
  });

  // Quiz Navigation
  document.getElementById('btn-next-q')?.addEventListener('click', () => {
    if (state.quizState.currentIndex < state.activeQuiz.questions.length - 1) {
      state.quizState.currentIndex++;
      render();
    }
  });

  document.getElementById('btn-prev-q')?.addEventListener('click', () => {
    if (state.quizState.currentIndex > 0) {
      state.quizState.currentIndex--;
      render();
    }
  });

  document.getElementById('btn-submit-quiz')?.addEventListener('click', () => {
    let score = 0;
    state.activeQuiz.questions.forEach((q, idx) => {
      if (state.quizState.answers[idx] === q.correctIndex) {
        score++;
      }
    });
    state.quizState.score = score;
    state.quizState.submitted = true;
    showToast('Quiz submitted! Calculating assessment score...', 'success');
    render();
  });

  document.getElementById('btn-retake-quiz')?.addEventListener('click', () => {
    state.quizState = { currentIndex: 0, answers: {}, submitted: false, score: 0 };
    render();
  });

  document.getElementById('btn-quiz-back-dash')?.addEventListener('click', () => {
    state.activeTab = 'dashboard';
    render();
  });

  // Re-run diagnostics
  document.getElementById('btn-run-diagnostics')?.addEventListener('click', async () => {
    showToast('Testing live backend endpoints...', 'info');
    await checkBackendConnectivity();
    render();
  });

  // Create course modal
  document.getElementById('btn-create-course-modal')?.addEventListener('click', () => {
    openModal(`
      <div class="modal-dialog">
        <div class="modal-header">
          <h3 class="modal-title">Create New Academic Course</h3>
          <button class="btn-close">✕</button>
        </div>
        <div class="form-group">
          <label class="form-label">Course Title</label>
          <input type="text" class="form-input" id="new-course-title" placeholder="e.g. Distributed Consensus & Raft Protocols" />
        </div>
        <div class="form-group">
          <label class="form-label">Course Code & Department</label>
          <input type="text" class="form-input" id="new-course-code" placeholder="CS-504 • Computer Science" />
        </div>
        <div class="form-group">
          <label class="form-label">Course Description</label>
          <textarea class="form-textarea" rows="3" placeholder="Syllabus overview..."></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button class="btn-secondary" onclick="document.getElementById('modal-root').classList.add('hidden')">Cancel</button>
          <button class="btn-primary" id="btn-save-new-course">Publish Course</button>
        </div>
      </div>
    `);

    document.getElementById('btn-save-new-course')?.addEventListener('click', () => {
      const title = document.getElementById('new-course-title')?.value || 'New Academic Course';
      const code = document.getElementById('new-course-code')?.value || 'CS-999';
      state.data.courses.push({
        id: 'crs-' + Date.now(),
        code,
        title,
        instructor: getCurrentUser().name,
        department: 'Computer Science',
        image: '/courses/cloud-arch.jpg',
        progress: 0,
        status: 'IN_PROGRESS',
        description: 'New course created via Instructor Curriculum Management interface.',
        credits: 4,
        totalHours: 40,
        completedHours: 0,
        enrolledCount: 1,
        rating: 5.0,
        modules: []
      });
      closeModal();
      showToast('Course successfully published to catalog!', 'success');
      render();
    });
  });

  // Grade evaluation modal
  document.querySelectorAll('.btn-grade-submission').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const studentName = e.currentTarget.getAttribute('data-student');
      openModal(`
        <div class="modal-dialog">
          <div class="modal-header">
            <h3 class="modal-title">Evaluate Lab: ${studentName}</h3>
            <button class="btn-close">✕</button>
          </div>
          <div class="form-group">
            <label class="form-label">Numerical Score (0 - 100)</label>
            <input type="number" class="form-input" id="grade-score" value="95" min="0" max="100" />
          </div>
          <div class="form-group">
            <label class="form-label">Instructor Qualitative Feedback</label>
            <textarea class="form-textarea" rows="3">Excellent implementation of Resilience4j retry and fallback logic. Tests passed with 100% coverage.</textarea>
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button class="btn-secondary" onclick="document.getElementById('modal-root').classList.add('hidden')">Cancel</button>
            <button class="btn-primary" id="btn-confirm-grade">Publish Grade to Student</button>
          </div>
        </div>
      `);

      document.getElementById('btn-confirm-grade')?.addEventListener('click', () => {
        closeModal();
        showToast('Grade recorded and student notified via email service!', 'success');
        render();
      });
    });
  });
}

// Initial Boot
render();
checkBackendConnectivity();
// Periodically check backend
setInterval(checkBackendConnectivity, 15000);
