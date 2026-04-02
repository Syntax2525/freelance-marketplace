function showToast(message, type = 'success') {
  const existing = document.querySelector('.toast');
  if (existing) existing.remove();
  const toast = document.createElement('div');
  toast.className = `toast ${type} show`;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => toast.classList.remove('show'), 3000);
  setTimeout(() => toast.remove(), 3600);
}

function requireAuth() {
  const user = getCurrentUser();
  if (!user) {
    const guestPages = ['index.html', 'register.html'];
    if (!guestPages.some(page => location.pathname.endsWith(page))) {
      window.location.href = 'index.html';
    }
  }
}

function redirectIfAuthenticated() {
  const user = getCurrentUser();
  if (user && (location.pathname.endsWith('index.html') || location.pathname.endsWith('register.html'))) {
    window.location.href = landingPageForRole(user.user.role);
  }
}

function initPageShell() {
  const sidebarToggle = document.getElementById('sidebarToggle');
  const sidebar = document.getElementById('sidebar');
  const logoutButton = document.getElementById('logoutButton');
  if (sidebarToggle && sidebar) {
    sidebarToggle.addEventListener('click', () => {
      sidebar.classList.toggle('open');
    });
  }
  if (logoutButton) {
    logoutButton.addEventListener('click', logout);
  }
}

function applyRoleUI() {
  const user = getCurrentUser();
  const role = user?.user?.role || null;
  document.querySelectorAll('.role-admin').forEach(el => {
    el.style.display = role === 'admin' ? 'block' : 'none';
  });
  document.querySelectorAll('.role-freelancer').forEach(el => {
    el.style.display = role === 'freelancer' ? 'block' : 'none';
  });
  document.querySelectorAll('.role-client').forEach(el => {
    el.style.display = role === 'client' ? 'block' : 'none';
  });
}

function setUserChip() {
  const userChip = document.getElementById('userChip');
  const user = getCurrentUser();
  if (userChip && user) {
    const pic = user.user.profilePictureUrl;
    if (pic) {
      userChip.innerHTML = `<img class="user-chip-avatar" src="${pic}" alt="" width="28" height="28" /> <span>${user.user.name} · ${user.user.role}</span>`;
    } else {
      userChip.textContent = `${user.user.name} · ${user.user.role}`;
    }
  }
}

function applyProfilePhoto(profilePictureUrl, displayName) {
  const img = document.getElementById('profilePhoto');
  const letter = document.getElementById('profileAvatarLetter');
  const initial = (displayName || '?').trim().charAt(0).toUpperCase() || '?';
  if (letter) letter.textContent = initial;
  if (!img || !letter) return;
  if (profilePictureUrl) {
    img.src = profilePictureUrl + (profilePictureUrl.includes('?') ? '&' : '?') + 't=' + Date.now();
    img.style.display = 'block';
    letter.style.display = 'none';
  } else {
    img.removeAttribute('src');
    img.style.display = 'none';
    letter.style.display = 'grid';
  }
}

function initProfilePictureControls() {
  const input = document.getElementById('profilePictureInput');
  const btn = document.getElementById('profilePictureUploadBtn');
  if (!input || !btn) return;
  btn.addEventListener('click', () => input.click());
  input.addEventListener('change', async () => {
    const file = input.files && input.files[0];
    if (!file) return;
    try {
      const updated = await uploadProfilePicture(file);
      setStoredAuth({
        token: getToken(),
        user: normalizeUser(updated)
      });
      applyProfilePhoto(updated.profilePictureUrl, updated.fullName);
      setUserChip();
      showToast('Profile photo updated.');
    } catch (e) {
      showToast(e.message || 'Upload failed', 'error');
    }
    input.value = '';
  });
}

function formatStatus(status) {
  if (!status) return '';
  return String(status).toLowerCase();
}

function formatDate(isoOrDate) {
  if (!isoOrDate) return '';
  const d = new Date(isoOrDate);
  if (Number.isNaN(d.getTime())) return String(isoOrDate);
  return d.toLocaleDateString();
}

async function renderApplications() {
  const appList = document.getElementById('applicationsList');
  const emptyState = document.getElementById('applicationsEmpty');
  const user = getCurrentUser();
  if (!appList || !user) return;

  try {
    const applications = await apiFetch('/api/applications/my-applications');
    appList.innerHTML = '';
    if (!applications.length) {
      emptyState.style.display = 'block';
      return;
    }
    emptyState.style.display = 'none';
    applications.forEach(item => {
      const row = document.createElement('div');
      row.className = 'table-row';
      const st = formatStatus(item.status);
      row.innerHTML = `
      <span>${item.jobTitle || 'Job'}</span>
      <span>${item.clientName || '—'}</span>
      <span>${formatDate(item.appliedAt)}</span>
      <span><span class="status-pill status-${st}">${st}</span></span>
    `;
      appList.appendChild(row);
    });
  } catch (e) {
    showToast(e.message || 'Could not load applications', 'error');
  }
}

async function renderNotifications() {
  const list = document.getElementById('notificationsList');
  if (!list) return;
  list.innerHTML = '';
  try {
    const notifications = await apiFetch('/api/notifications/my?unreadOnly=false&page=0&size=50');
    if (!notifications.length) {
      list.innerHTML = '<div class="empty-state">No notifications yet.</div>';
      return;
    }
    notifications.forEach(note => {
      const item = document.createElement('div');
      item.className = 'notification-item';
      item.innerHTML = `<h3>${note.title}</h3><p>${note.message || ''}</p>`;
      list.appendChild(item);
    });
  } catch (e) {
    showToast(e.message || 'Could not load notifications', 'error');
  }
}

async function renderProfile() {
  const user = getCurrentUser();
  if (!user) return;
  try {
    const me = await apiFetch('/api/users/me');
    const nu = normalizeUser(me);
    const profileName = document.getElementById('profileName');
    const profileRole = document.getElementById('profileRole');
    const profileEmail = document.getElementById('profileEmail');
    const profileNameInput = document.getElementById('profileNameInput');
    const profileEmailInput = document.getElementById('profileEmailInput');
    const profileAbout = document.getElementById('profileAbout');
    if (profileName) profileName.textContent = nu.name;
    if (profileRole) profileRole.textContent = nu.role;
    if (profileEmail) profileEmail.textContent = nu.email;
    if (profileNameInput) profileNameInput.value = nu.name;
    if (profileEmailInput) profileEmailInput.value = nu.email;
    if (profileAbout) profileAbout.value = me.bio || '';
    applyProfilePhoto(me.profilePictureUrl, nu.name);
  } catch (e) {
    showToast(e.message || 'Could not load profile', 'error');
  }
}

function initProfileForm() {
  const form = document.getElementById('profileForm');
  if (!form) return;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const name = document.getElementById('profileNameInput').value.trim();
    const email = document.getElementById('profileEmailInput').value.trim();
    const bio = document.getElementById('profileAbout').value.trim();
    if (!name || !email) {
      showToast('Name and email are required.', 'error');
      return;
    }
    try {
      const updated = await apiFetch('/api/users/me', {
        method: 'PUT',
        body: JSON.stringify({
          fullName: name,
          email,
          bio
        })
      });
      setStoredAuth({
        token: getToken(),
        user: normalizeUser(updated)
      });
      renderProfile();
      setUserChip();
      showToast('Profile updated successfully.');
    } catch (e) {
      showToast(e.message || 'Update failed', 'error');
    }
  });
}

async function renderAdminPanel() {
  const usersList = document.getElementById('adminUsersList');
  const jobsList = document.getElementById('adminJobsList');
  const adminUsersCount = document.getElementById('adminUsersCount');
  const adminJobsCount = document.getElementById('adminJobsCount');
  const adminReportsCount = document.getElementById('adminReportsCount');
  if (!usersList || !jobsList) return;

  try {
    const data = await apiFetch('/api/admin/overview');
    usersList.innerHTML = '';
    jobsList.innerHTML = '';
    (data.users || []).forEach(u => {
      const row = document.createElement('div');
      row.className = 'table-row';
      const roles = u.roles ? Array.from(u.roles).join(', ') : '';
      row.innerHTML = `<span>${u.fullName}</span><span>${u.email}</span><span>${roles}</span><span>Active</span>`;
      usersList.appendChild(row);
    });
    (data.jobs || []).forEach(job => {
      const row = document.createElement('div');
      row.className = 'table-row';
      row.innerHTML = `<span>${job.title}</span><span>${job.clientName || ''}</span><span>${job.category || ''}</span><span>Open</span>`;
      jobsList.appendChild(row);
    });
    if (adminUsersCount) adminUsersCount.textContent = `${data.userCount ?? 0}`;
    if (adminJobsCount) adminJobsCount.textContent = `${data.jobCount ?? 0}`;
    if (adminReportsCount) adminReportsCount.textContent = '—';
  } catch (e) {
    showToast(e.message || 'Could not load admin data', 'error');
  }
}

async function initPage() {
  const page = location.pathname.split('/').pop();
  if (page === 'index.html' || page === 'register.html') {
    redirectIfAuthenticated();
  } else {
    requireAuth();
  }
  initPageShell();
  applyRoleUI();
  setUserChip();

  if (page === 'dashboard.html') {
    try {
      const jobsRes = await apiFetch('/api/jobs?page=0&size=500');
      const jobCount = jobsRes.totalElements ?? (jobsRes.content ? jobsRes.content.length : 0);
      let appCount = 0;
      let notifCount = 0;
      const role = getCurrentUser()?.user?.role;
      if (role === 'freelancer') {
        const apps = await apiFetch('/api/applications/my-applications');
        appCount = apps.length;
      }
      try {
        const notifs = await apiFetch('/api/notifications/my?unreadOnly=false&page=0&size=50');
        notifCount = notifs.length;
      } catch {
        notifCount = 0;
      }
      let msgCount = 0;
      try {
        const partners = await apiFetch('/api/messages/partners');
        msgCount = partners.length;
      } catch {
        msgCount = 0;
      }
      const statJobs = document.getElementById('statJobs');
      const statMessages = document.getElementById('statMessages');
      const statApplications = document.getElementById('statApplications');
      const statNotifications = document.getElementById('statNotifications');
      if (statJobs) statJobs.textContent = String(jobCount);
      if (statMessages) statMessages.textContent = String(msgCount);
      if (statApplications) statApplications.textContent = role === 'freelancer' ? String(appCount) : '—';
      if (statNotifications) statNotifications.textContent = String(notifCount);
    } catch (e) {
      showToast(e.message || 'Could not load dashboard stats', 'error');
    }
  }
  if (page === 'applications.html') await renderApplications();
  if (page === 'notifications.html') await renderNotifications();
  if (page === 'profile.html') {
    await renderProfile();
    initProfileForm();
    initProfilePictureControls();
  }
  if (page === 'admin.html') {
    const user = getCurrentUser();
    if (user?.user?.role !== 'admin') {
      window.location.href = 'dashboard.html';
      return;
    }
    await renderAdminPanel();
  }
}

window.addEventListener('DOMContentLoaded', initPage);
