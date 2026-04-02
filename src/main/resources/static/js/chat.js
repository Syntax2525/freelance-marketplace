let contacts = [];
let activePartnerId = null;

async function loadContacts() {
  const partners = await apiFetch('/api/messages/partners');
  const directory = await apiFetch('/api/users/directory');
  const byId = new Map();
  directory.forEach(p => byId.set(p.id, p));
  partners.forEach(p => byId.set(p.id, p));
  contacts = Array.from(byId.values()).sort((a, b) =>
    (a.fullName || '').localeCompare(b.fullName || '', undefined, { sensitivity: 'base' })
  );
}

function renderConversations() {
  const list = document.getElementById('conversationList');
  if (!list) return;
  list.innerHTML = '';
  if (!contacts.length) {
    list.innerHTML = '<div class="empty-state">No users available to message.</div>';
    return;
  }
  contacts.forEach(convo => {
    const item = document.createElement('div');
    item.className = 'chat-item';
    item.dataset.partnerId = String(convo.id);
    item.innerHTML = `<h3>${convo.fullName}</h3><p>${convo.email || ''}</p>`;
    item.addEventListener('click', () => loadConversation(convo.id));
    list.appendChild(item);
  });
}

function renderMessagePanel(messages) {
  const panel = document.getElementById('messagePanel');
  if (!panel) return;
  const me = getCurrentUser();
  const myId = me?.user?.id;
  panel.innerHTML = '';
  (messages || []).forEach(message => {
    const mine = message.senderId === myId;
    const messageItem = document.createElement('div');
    messageItem.className = 'message-item';
    messageItem.innerHTML = `<div class="message-bubble ${mine ? 'sent' : 'received'}">${message.content || ''}</div>`;
    panel.appendChild(messageItem);
  });
  panel.scrollTop = panel.scrollHeight;
}

async function loadConversation(partnerId) {
  activePartnerId = partnerId;
  document.querySelectorAll('.chat-item').forEach(item => {
    item.classList.toggle('active', item.dataset.partnerId === String(partnerId));
  });
  const partner = contacts.find(c => c.id === partnerId);
  document.getElementById('activeConversationTitle').textContent = partner
    ? partner.fullName
    : 'Conversation';
  document.getElementById('activeConversationStatus').textContent = partner
    ? `Messaging ${partner.email || ''}`
    : '';
  const panel = document.getElementById('messagePanel');
  if (panel) panel.dataset.activePartner = String(partnerId);

  try {
    const messages = await apiFetch(`/api/messages/conversation/${partnerId}?page=0&size=100`);
    renderMessagePanel(messages);
  } catch (e) {
    showToast(e.message || 'Could not load messages', 'error');
  }
}

function initChatForm() {
  const form = document.getElementById('chatForm');
  if (!form) return;
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const input = document.getElementById('chatMessageInput');
    const text = input.value.trim();
    if (!text) return;
    const panel = document.getElementById('messagePanel');
    const activePartnerIdStr = panel?.dataset.activePartner;
    if (!activePartnerIdStr) {
      showToast('Select a conversation first.', 'error');
      return;
    }
    const receiverId = Number(activePartnerIdStr);
    try {
      await apiFetch('/api/messages', {
        method: 'POST',
        body: JSON.stringify({
          receiverId,
          content: text
        })
      });
      input.value = '';
      await loadConversation(receiverId);
      showToast('Message sent.');
    } catch (e) {
      showToast(e.message || 'Send failed', 'error');
    }
  });
}

window.addEventListener('DOMContentLoaded', async () => {
  const page = location.pathname.split('/').pop();
  if (page !== 'chat.html') return;
  try {
    await loadContacts();
    renderConversations();
    initChatForm();
    if (contacts.length) {
      await loadConversation(contacts[0].id);
    }
  } catch (e) {
    showToast(e.message || 'Could not open chat', 'error');
  }
});
