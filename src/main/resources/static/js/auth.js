function uiRoleToRegisterApi(role) {
  if (role === 'client') return 'CLIENT';
  return 'FREELANCER';
}

function initLoginForm() {
  const form = document.getElementById('loginForm');
  if (!form) return;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();
    if (!email || !password) {
      showToast('Please provide email and password.', 'error');
      return;
    }
    try {
      const res = await apiFetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
      });
      setSessionFromAuthResponse(res);
      const dest = landingPageForRole(getCurrentUser().user.role);
      showToast('Login successful! Redirecting...', 'success');
      setTimeout(() => {
        window.location.href = dest;
      }, 600);
    } catch (e) {
      showToast(e.message || 'Login failed', 'error');
    }
  });
}

function initRegisterForm() {
  const form = document.getElementById('registerForm');
  if (!form) return;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const name = document.getElementById('registerName').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value.trim();
    const role = document.getElementById('registerRole').value;
    if (!name || !email || !password) {
      showToast('Fill in all fields before creating your account.', 'error');
      return;
    }
    if (!role) {
      showToast('Please select whether you are registering as a freelancer or a client.', 'error');
      return;
    }
    if (password.length < 8) {
      showToast('Password must be at least 8 characters.', 'error');
      return;
    }
    try {
      await apiFetch('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          fullName: name,
          email,
          password,
          role: uiRoleToRegisterApi(role),
          bio: ''
        })
      });
      showToast('Account created successfully. Proceed to login.', 'success');
      setTimeout(() => {
        window.location.href = 'index.html';
      }, 800);
    } catch (e) {
      showToast(e.message || 'Registration failed', 'error');
    }
  });
}

window.addEventListener('DOMContentLoaded', () => {
  initLoginForm();
  initRegisterForm();
});
