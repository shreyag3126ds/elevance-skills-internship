const FLIGHT_ID = 'FL100';
const HOTEL_ID = 'H1';

let seats = [], rooms = [];
let selectedSeatId = null, selectedRoomId = null;
let prefs = {};
let gallery = { images: [], index: 0 };

const $ = (id) => document.getElementById(id);
$('userId').value = localStorage.getItem('userId') || 'demo-user';
const userId = () => $('userId').value.trim() || 'demo-user';
$('userId').addEventListener('change', () => localStorage.setItem('userId', userId()));

async function api(url, options) {
  const res = await fetch(url, options);
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(err.message || 'Request failed');
  }
  return res.json();
}

function toast(msg, isError = false) {
  const t = $('toast');
  t.textContent = msg;
  t.className = isError ? 'error' : '';
  t.hidden = false;
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => (t.hidden = true), 3000);
}

const seatCss = (t) => ({ STANDARD: 'standard', EXTRA_LEGROOM: 'extra', PREMIUM: 'premium' }[t]);
const label = (t) => t.replace('_', ' ').toLowerCase();

/* ---------------- data loading (polled = real-time availability) ---------------- */
async function loadSeats() {
  seats = await api(`/api/flights/${FLIGHT_ID}/seats`);
  const sel = seats.find((s) => s.id === selectedSeatId);
  if (sel && sel.status === 'BOOKED' && sel.bookedBy !== userId()) {
    selectedSeatId = null;
    toast('Your selected seat was just taken', true);
  }
  renderSeats();
}

async function loadRooms() {
  rooms = await api(`/api/hotels/${HOTEL_ID}/rooms`);
  const sel = rooms.find((r) => r.id === selectedRoomId);
  if (sel && sel.status === 'BOOKED' && sel.bookedBy !== userId()) {
    selectedRoomId = null;
    toast('Your selected room was just booked', true);
  }
  renderRooms();
}

async function loadPrefs() {
  prefs = await api(`/api/users/${encodeURIComponent(userId())}/preferences`);
  $('seatPrefText').textContent = prefs.preferredSeatType
    ? `Saved preference: ${label(prefs.preferredSeatType)} seats (outlined in purple)` : '';
  $('roomPrefText').textContent = prefs.preferredRoomType
    ? `Saved preference: ${label(prefs.preferredRoomType)} rooms (outlined in purple)` : '';
  renderSeats();
  renderRooms();
}

async function refresh() {
  try { await Promise.all([loadSeats(), loadRooms()]); } catch (e) { /* server may be restarting */ }
}

/* ---------------- seats ---------------- */
function renderSeats() {
  const map = $('seatMap');
  map.innerHTML = '';
  const rows = {};
  seats.forEach((s) => (rows[s.rowNumber] = rows[s.rowNumber] || []).push(s));

  Object.keys(rows).forEach((rowNo) => {
    const row = document.createElement('div');
    row.className = 'seat-row';
    const num = document.createElement('span');
    num.className = 'row-num';
    num.textContent = rowNo;
    row.appendChild(num);

    rows[rowNo].forEach((s) => {
      if (s.letter === 'D') { const a = document.createElement('span'); a.className = 'aisle'; row.appendChild(a); }
      const b = document.createElement('button');
      b.className = 'seat ' + seatCss(s.seatType);
      b.textContent = s.letter;
      b.title = `${s.rowNumber}${s.letter} · ${label(s.seatType)} · $${s.price}`;
      if (s.status === 'BOOKED') {
        b.classList.add('booked');
        if (s.bookedBy === userId()) b.classList.add('mine');
        b.disabled = s.bookedBy !== userId();
      }
      if (s.id === selectedSeatId) b.classList.add('selected');
      if (prefs.preferredSeatType === s.seatType && s.status === 'AVAILABLE') b.classList.add('pref');
      b.onclick = () => { selectedSeatId = s.id; renderSeats(); };
      row.appendChild(b);
    });
    map.appendChild(row);
  });
  renderSeatSummary();
}

function renderSeatSummary() {
  const s = seats.find((x) => x.id === selectedSeatId);
  const mine = s && s.status === 'BOOKED' && s.bookedBy === userId();
  $('seatSummary').innerHTML = s
    ? `<b>${s.rowNumber}${s.letter}</b> · ${label(s.seatType)}<br>Price: <b>$${s.price}</b>${mine ? '<br><span class="tag">Booked by you</span>' : ''}`
    : 'Select a seat on the map.';
  $('bookSeat').disabled = !s || s.status === 'BOOKED';
  $('releaseSeat').disabled = !mine;
  $('savePrefSeat').disabled = !s;
}

/* ---------------- rooms ---------------- */
function renderRooms() {
  const grid = $('roomGrid');
  grid.innerHTML = '';
  const filter = $('roomFilter').value;

  rooms.filter((r) => !filter || r.roomType === filter).forEach((r) => {
    const card = document.createElement('div');
    card.className = 'room';
    const booked = r.status === 'BOOKED';
    const mine = booked && r.bookedBy === userId();
    if (booked && !mine) card.classList.add('booked-room');
    if (mine) card.classList.add('mine-room');
    if (r.id === selectedRoomId) card.classList.add('selected-room');
    if (prefs.preferredRoomType === r.roomType && !booked) card.classList.add('pref-room');

    const img = document.createElement('img');
    img.src = r.images[0] || '';
    img.alt = 'Room ' + r.roomNumber;
    const info = document.createElement('div');
    info.className = 'info';
    info.innerHTML = `<b>Room ${r.roomNumber}</b> <span class="tag">${label(r.roomType)}</span><br>
      $${r.pricePerNight} / night ${booked ? (mine ? '· <b>yours</b>' : '· <i>booked</i>') : ''}`;

    const actions = document.createElement('div');
    actions.className = 'actions';
    const preview = document.createElement('button');
    preview.textContent = 'Preview';
    preview.onclick = (e) => { e.stopPropagation(); openGallery(r); };
    actions.appendChild(preview);
    info.appendChild(actions);

    card.append(img, info);
    if (!booked || mine) card.onclick = () => { selectedRoomId = r.id; renderRooms(); };
    grid.appendChild(card);
  });
  renderRoomSummary();
}

function renderRoomSummary() {
  const r = rooms.find((x) => x.id === selectedRoomId);
  const mine = r && r.status === 'BOOKED' && r.bookedBy === userId();
  $('roomSummary').innerHTML = r
    ? `<b>Room ${r.roomNumber}</b> · ${label(r.roomType)}<br>Price: <b>$${r.pricePerNight}</b> / night${mine ? '<br><span class="tag">Booked by you</span>' : ''}`
    : 'Select a room.';
  $('bookRoom').disabled = !r || r.status === 'BOOKED';
  $('releaseRoom').disabled = !mine;
  $('savePrefRoom').disabled = !r;
}

/* ---------------- gallery / 3D preview ---------------- */
function openGallery(room) {
  gallery = { images: room.images, index: 0, room };
  const tour = $('tourLink');
  tour.hidden = !room.previewUrl;
  if (room.previewUrl) tour.href = room.previewUrl;
  showImage();
  $('modal').hidden = false;
}
function showImage() {
  $('modalImg').src = gallery.images[gallery.index] || '';
  $('modalCaption').textContent = `Room ${gallery.room.roomNumber} · ${gallery.index + 1}/${gallery.images.length}`;
}
$('prevImg').onclick = () => { gallery.index = (gallery.index - 1 + gallery.images.length) % gallery.images.length; showImage(); };
$('nextImg').onclick = () => { gallery.index = (gallery.index + 1) % gallery.images.length; showImage(); };
$('closeModal').onclick = () => ($('modal').hidden = true);

/* ---------------- actions ---------------- */
async function act(url, okMsg, reload) {
  try {
    await api(url, { method: 'POST' });
    toast(okMsg);
  } catch (e) {
    toast(e.message, true);
  }
  await reload();
}
const uid = () => encodeURIComponent(userId());

$('bookSeat').onclick = () => act(`/api/seats/${selectedSeatId}/book?userId=${uid()}`, 'Seat booked!', loadSeats);
$('releaseSeat').onclick = () => act(`/api/seats/${selectedSeatId}/release?userId=${uid()}`, 'Seat released', loadSeats);
$('bookRoom').onclick = () => act(`/api/rooms/${selectedRoomId}/book?userId=${uid()}`, 'Room booked!', loadRooms);
$('releaseRoom').onclick = () => act(`/api/rooms/${selectedRoomId}/release?userId=${uid()}`, 'Room released', loadRooms);

async function savePref(body) {
  try {
    await api(`/api/users/${uid()}/preferences`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body),
    });
    toast('Preference saved for future bookings');
    await loadPrefs();
  } catch (e) { toast(e.message, true); }
}
$('savePrefSeat').onclick = () => savePref({ preferredSeatType: seats.find((s) => s.id === selectedSeatId).seatType });
$('savePrefRoom').onclick = () => savePref({ preferredRoomType: rooms.find((r) => r.id === selectedRoomId).roomType });

$('roomFilter').onchange = renderRooms;
$('userId').onchange = () => { loadPrefs(); refresh(); };

document.querySelectorAll('.tab').forEach((tab) => {
  tab.onclick = () => {
    document.querySelectorAll('.tab').forEach((t) => t.classList.remove('active'));
    tab.classList.add('active');
    $('flight').hidden = tab.dataset.tab !== 'flight';
    $('hotel').hidden = tab.dataset.tab !== 'hotel';
  };
});

/* ---------------- start ---------------- */
refresh();
loadPrefs();
setInterval(refresh, 3000);

/* open the tab requested by the dashboard (?tab=hotel) */
if (new URLSearchParams(location.search).get('tab') === 'hotel') {
  document.querySelector('.tab[data-tab="hotel"]').click();
}
