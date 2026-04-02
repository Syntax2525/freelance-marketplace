const AUTH_KEY = 'freelance_auth';

function getStoredAuth() {
  const raw = localStorage.getItem(AUTH_KEY);
  return raw ? JSON.parse(raw) : null;
}

function setStoredAuth(payload) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(payload));
}

function clearAuth() {
  localStorage.removeItem(AUTH_KEY);
}

function getToken() {
  const a = getStoredAuth();
  return a?.token || null;
}

/**
 * Maps backend roles (ROLE_*) to UI role strings used by the SPA.
 */
function mapRolesToUi(roles) {
  if (!roles || !roles.length) return 'freelancer';
  const joined = roles.join(' ');
  if (joined.includes('ADMIN')) return 'admin';
  if (joined.includes('CLIENT')) return 'client';
  return 'freelancer';
}

/**
 * Normalizes API user payload for the rest of the app (name, role, id).
 */
function normalizeUser(apiUser) {
  if (!apiUser) return null;
  let rolesArr = [];
  if (Array.isArray(apiUser.roles)) {
    rolesArr = apiUser.roles;
  } else if (apiUser.roles && typeof apiUser.roles === 'object') {
    rolesArr = Object.values(apiUser.roles);
  }
  return {
    id: apiUser.id,
    name: apiUser.fullName || apiUser.name || '',
    email: apiUser.email,
    role: mapRolesToUi(rolesArr),
    bio: apiUser.bio || '',
    profilePictureUrl: apiUser.profilePictureUrl || null
  };
}

/**
 * Upload a profile image (multipart). Returns updated UserResponseDto JSON.
 */
async function uploadProfilePicture(file) {
  const token = getToken();
  if (!token) {
    throw new Error('Not signed in');
  }
  const formData = new FormData();
  formData.append('file', file);
  const res = await fetch('/api/users/me/profile-picture', {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
    credentials: 'same-origin'
  });
  if (res.status === 401) {
    clearAuth();
    window.location.href = 'index.html';
    throw new Error('Session expired');
  }
  if (!res.ok) {
    const errText = await res.text();
    let msg = res.statusText;
    try {
      const j = JSON.parse(errText);
      msg = j.message || j.detail || j.error || msg;
    } catch {
      msg = errText || msg;
    }
    throw new Error(msg);
  }
  return res.json();
}

async function apiFetch(path, options = {}) {
  const headers = { ...options.headers };
  const isJsonBody =
    options.body &&
    typeof options.body === 'string' &&
    !headers['Content-Type'] &&
    !headers['content-type'];
  if (isJsonBody) {
    headers['Content-Type'] = 'application/json';
  }
  const token = getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  const res = await fetch(path, { ...options, headers, credentials: 'same-origin' });
  if (res.status === 401) {
    clearAuth();
    if (!path.includes('/api/auth/')) {
      window.location.href = 'index.html';
    }
    const errText = await res.text();
    let msg = 'Unauthorized';
    try {
      const j = JSON.parse(errText);
      msg = j.message || j.detail || j.error || msg;
    } catch {
      msg = errText || msg;
    }
    throw new Error(msg);
  }
  if (!res.ok) {
    const errText = await res.text();
    let msg = res.statusText;
    try {
      const j = JSON.parse(errText);
      msg = j.message || j.detail || j.error || j.title || (Array.isArray(j.errors) ? j.errors.map(e => e.defaultMessage).join(', ') : msg);
    } catch {
      msg = errText || msg;
    }
    throw new Error(msg);
  }
  if (res.status === 204) return null;
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

function getCurrentUser() {
  const auth = getStoredAuth();
  if (!auth || !auth.user) return null;
  return auth;
}

function setSessionFromAuthResponse(authResponse) {
  const u = normalizeUser(authResponse.user);
  setStoredAuth({
    token: authResponse.token,
    user: u
  });
}

function logout() {
  clearAuth();
  window.location.href = 'index.html';
}

/**
 * First page after login / when visiting index while already signed in, by registered role.
 */
function landingPageForRole(role) {
  if (role === 'admin') return 'admin.html';
  if (role === 'client') return 'dashboard.html';
  return 'jobs.html';
}
