const $ = (id) => document.getElementById(id);
$('userId').value = localStorage.getItem('userId') || 'demo-user';
const userId = () => $('userId').value.trim() || 'demo-user';
$('userId').addEventListener('change', () => { localStorage.setItem('userId', userId()); load(); });

const label = (t) => t.replace('_', ' ').toLowerCase();
const money = (v) => '$' + Number(v).toFixed(2);

function typeBars(container, byType, total) {
  container.innerHTML = '';
  const entries = Object.entries(byType);
  if (!entries.length) { container.textContent = 'None available'; return; }
  entries.forEach(([type, n]) => {
    const row = document.createElement('div');
    row.className = 'bar-row';
    const name = document.createElement('span'); name.className = 'name'; name.textContent = label(type);
    const bar = document.createElement('div'); bar.className = 'bar';
    const fill = document.createElement('i'); fill.style.width = Math.max(4, (n / total) * 100) + '%';
    bar.appendChild(fill);
    const num = document.createElement('span'); num.className = 'n'; num.textContent = n;
    row.append(name, bar, num);
    container.appendChild(row);
  });
}

function renderBookings(list) {
  const box = $('bookings');
  box.innerHTML = '';
  if (!list.length) { box.className = 'muted'; box.textContent = 'No bookings yet. Choose a seat or a room above.'; return; }
  box.className = '';
  list.forEach((b) => {
    const row = document.createElement('div');
    row.className = 'booking';
    const left = document.createElement('div');
    const pill = document.createElement('span'); pill.className = 'pill ' + b.kind; pill.textContent = b.kind === 'SEAT' ? 'Flight' : 'Hotel';
    left.append(pill, document.createTextNode(`${b.label} · ${label(b.detail)}`));
    const right = document.createElement('b'); right.textContent = money(b.price);
    row.append(left, right);
    box.appendChild(row);
  });
}

async function load() {
  try {
    const res = await fetch('/api/dashboard?userId=' + encodeURIComponent(userId()));
    if (!res.ok) throw new Error(res.statusText);
    const d = await res.json();
    $('seatsAvail').textContent = d.flight.available;
    $('seatsSub').textContent = `of ${d.flight.total} seats · ${d.flight.booked} taken`;
    $('roomsAvail').textContent = d.hotel.available;
    $('roomsSub').textContent = `of ${d.hotel.total} rooms · ${d.hotel.booked} booked`;
    $('myCount').textContent = d.myBookings.length;
    $('myTotal').textContent = d.myBookings.length ? `Total ${money(d.myTotal)}` : 'Nothing booked yet';
    renderBookings(d.myBookings);
    typeBars($('seatTypes'), d.flight.availableByType, d.flight.total);
    typeBars($('roomTypes'), d.hotel.availableByType, d.hotel.total);
  } catch (e) { /* server restarting */ }
}

load();
setInterval(load, 3000);
