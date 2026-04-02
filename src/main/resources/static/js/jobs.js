function formatBudget(job) {
  if (job.budgetTzs != null) {
    return `TZS ${Number(job.budgetTzs).toLocaleString()}`;
  }
  return job.budgetType || '—';
}

function getQueryParam(key) {
  const params = new URLSearchParams(window.location.search);
  return params.get(key);
}

let cachedJobs = [];

async function loadJobsList() {
  const res = await apiFetch('/api/jobs?page=0&size=200');
  cachedJobs = res.content || [];
  return cachedJobs;
}

function renderJobs() {
  const jobsGrid = document.getElementById('jobsGrid');
  const searchInput = document.getElementById('jobSearchInput');
  const categoryFilter = document.getElementById('jobTypeFilter');
  if (!jobsGrid || !searchInput || !categoryFilter) return;

  const search = searchInput.value.trim().toLowerCase();
  const category = categoryFilter.value;
  const filtered = cachedJobs.filter(job => {
    const matchesSearch =
      (job.title && job.title.toLowerCase().includes(search)) ||
      (job.description && job.description.toLowerCase().includes(search));
    const cat = (job.category || '').toLowerCase();
    const matchesCategory = category === 'all' || cat === category;
    return matchesSearch && matchesCategory;
  });

  jobsGrid.innerHTML = '';
  if (!filtered.length) {
    jobsGrid.innerHTML = '<div class="empty-state">No jobs match your search. Try a different term or category.</div>';
    return;
  }

  filtered.forEach(job => {
    const card = document.createElement('article');
    card.className = 'job-card card';
    const initial = (job.title || '?').charAt(0);
    card.innerHTML = `
      <div class="job-meta-row">
        <div class="job-item-logo">${initial}</div>
        <div>
          <h3>${job.title}</h3>
          <p>${job.description || ''}</p>
        </div>
      </div>
      <div class="job-meta-row">
        <span>${job.category || 'general'}</span>
        <span>${formatBudget(job)}</span>
      </div>
      <div class="job-actions">
        <a class="btn btn-secondary" href="job-details.html?job=${job.id}">View details</a>
        <button class="btn btn-primary" data-job-id="${job.id}">Apply</button>
      </div>
    `;
    const applyButton = card.querySelector('button');
    applyButton.addEventListener('click', () => applyJob(job.id));
    jobsGrid.appendChild(card);
  });
}

async function applyJob(jobId) {
  const user = getCurrentUser();
  if (!user) {
    showToast('Please login to apply.', 'error');
    return;
  }
  if (user.user.role !== 'freelancer') {
    showToast('Only freelancers can apply to jobs.', 'error');
    return;
  }
  const coverLetter = window.prompt('Enter a short cover letter for your application:', '');
  if (coverLetter === null) return;
  const trimmed = coverLetter.trim();
  if (!trimmed) {
    showToast('Cover letter is required.', 'error');
    return;
  }
  const amountStr = window.prompt('Proposed amount in TZS (optional, leave empty):', '');
  let proposedAmountTzs = null;
  if (amountStr && amountStr.trim()) {
    proposedAmountTzs = Number(amountStr.replace(/,/g, ''));
    if (Number.isNaN(proposedAmountTzs)) {
      showToast('Invalid amount.', 'error');
      return;
    }
  }
  try {
    await apiFetch(`/api/applications/jobs/${jobId}`, {
      method: 'POST',
      body: JSON.stringify({
        coverLetter: trimmed,
        proposedAmountTzs,
        proposedTimeline: 'As discussed'
      })
    });
    showToast('Application submitted successfully!');
  } catch (e) {
    showToast(e.message || 'Could not apply', 'error');
  }
}

function initJobSearch() {
  const searchInput = document.getElementById('jobSearchInput');
  const categoryFilter = document.getElementById('jobTypeFilter');
  if (searchInput) searchInput.addEventListener('input', renderJobs);
  if (categoryFilter) categoryFilter.addEventListener('change', renderJobs);
}

function initPostJobForm() {
  const form = document.getElementById('postJobForm');
  if (!form) return;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const title = document.getElementById('jobTitle').value.trim();
    const budgetRaw = document.getElementById('jobBudget').value.trim();
    const description = document.getElementById('jobDescription').value.trim();
    const categoryEl = document.getElementById('jobCategory');
    const category = categoryEl ? categoryEl.value : 'development';
    if (!title || !budgetRaw || !description) {
      showToast('Fill in all fields to post a job.', 'error');
      return;
    }
    const user = getCurrentUser();
    if (!user || user.user.role !== 'client') {
      showToast('Only clients can post jobs.', 'error');
      return;
    }
    const budgetNum = Number(budgetRaw.replace(/[^0-9.]/g, ''));
    if (Number.isNaN(budgetNum) || budgetNum < 0) {
      showToast('Enter a valid budget number.', 'error');
      return;
    }
    try {
      await apiFetch('/api/jobs', {
        method: 'POST',
        body: JSON.stringify({
          title,
          description,
          category,
          budgetTzs: budgetNum,
          budgetType: 'FIXED',
          locationPreference: 'Remote'
        })
      });
      await loadJobsList();
      renderJobs();
      form.reset();
      showToast('Job posted successfully.');
    } catch (e) {
      showToast(e.message || 'Could not post job', 'error');
    }
  });
}

async function renderJobDetail() {
  const jobId = getQueryParam('job');
  if (!jobId) {
    showToast('Missing job id.', 'error');
    return;
  }
  try {
    const job = await apiFetch(`/api/jobs/${jobId}`);
    document.getElementById('detailTitle').textContent = job.title;
    document.getElementById('detailType').textContent = job.locationPreference || 'Remote';
    document.getElementById('detailBudget').textContent = `Budget: ${formatBudget(job)}`;
    document.getElementById('detailDescription').textContent = job.description || '';
    document.getElementById('detailClient').textContent = job.clientName || 'Client';
    const applyButton = document.getElementById('applyJobButton');
    if (applyButton) {
      applyButton.onclick = () => applyJob(job.id);
    }
    await renderSimilarJobs(job.category, job.id);
  } catch (e) {
    showToast(e.message || 'Could not load job', 'error');
  }
}

async function renderSimilarJobs(category, jobId) {
  const container = document.getElementById('similarJobs');
  if (!container) return;
  if (!cachedJobs.length) {
    await loadJobsList();
  }
  const jobs = cachedJobs.filter(item => item.category === category && item.id !== Number(jobId)).slice(0, 3);
  container.innerHTML = '';
  if (!jobs.length) {
    container.innerHTML = '<div class="empty-state">No similar jobs available at the moment.</div>';
    return;
  }
  jobs.forEach(job => {
    const card = document.createElement('article');
    card.className = 'job-card card similar-job-card';
    card.innerHTML = `
      <h3>${job.title}</h3>
      <p>${job.description || ''}</p>
      <div class="job-meta-row">
        <span>${formatBudget(job)}</span>
        <span>${job.clientName || ''}</span>
      </div>
      <div class="job-actions">
        <a class="btn btn-secondary" href="job-details.html?job=${job.id}">View</a>
      </div>
    `;
    container.appendChild(card);
  });
}

window.addEventListener('DOMContentLoaded', async () => {
  const page = location.pathname.split('/').pop();
  if (page === 'jobs.html') {
    try {
      await loadJobsList();
      renderJobs();
      initJobSearch();
      initPostJobForm();
    } catch (e) {
      showToast(e.message || 'Could not load jobs', 'error');
    }
  }
  if (page === 'job-details.html') {
    try {
      await loadJobsList();
      await renderJobDetail();
    } catch (e) {
      showToast(e.message || 'Could not load job', 'error');
    }
  }
});
