function setActiveNavLink() {
  const path = location.pathname.split('/').pop();
  document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
    const href = link.getAttribute('href');
    link.classList.toggle('active', href === path);
  });
}

function enforceRoleLinks() {
  const user = getCurrentUser();
  if (!user) return;
  document.querySelectorAll('.role-admin').forEach(el => {
    el.style.display = user.user.role === 'admin' ? 'block' : 'none';
  });
  document.querySelectorAll('.role-client').forEach(el => {
    el.style.display = user.user.role === 'client' ? 'block' : 'none';
  });
  document.querySelectorAll('.role-freelancer').forEach(el => {
    el.style.display = user.user.role === 'freelancer' ? 'block' : 'none';
  });
}

window.addEventListener('DOMContentLoaded', () => {
  setActiveNavLink();
  enforceRoleLinks();
});
