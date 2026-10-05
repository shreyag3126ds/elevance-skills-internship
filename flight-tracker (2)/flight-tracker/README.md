# Live Flight Status – Internship Task

Spring Boot backend implementing the "Live Flight Status" feature: a mock API
simulates real-time flight updates ("Delayed by 1h", "On Time", "Boarding"),
pushes notifications on important changes, includes delay reasons + revised
schedules, and supports tracking multiple flights at once with dynamic ETAs.

## Project structure

```
flight-tracker/
├── pom.xml
└── src/main/
    ├── java/com/internship/flighttracker/
    │   ├── FlightTrackerApplication.java      # main entry point (@EnableScheduling)
    │   ├── model/
    │   │   ├── Flight.java                    # domain object for a tracked flight
    │   │   ├── FlightStatus.java              # ON_TIME, BOARDING, DELAYED, DEPARTED, LANDED, CANCELLED
    │   │   ├── StatusUpdate.java              # notification payload pushed to clients
    │   │   └── TrackFlightRequest.java        # DTO for POST /track
    │   ├── service/
    │   │   ├── MockFlightApiService.java      # simulates the external flight-data API
    │   │   ├── FlightTrackingService.java     # in-memory store, supports many flights at once
    │   │   └── NotificationService.java       # SSE-based push notifications
    │   ├── scheduler/
    │   │   └── FlightUpdateScheduler.java     # polls mock API every 10s, broadcasts changes
    │   ├── controller/
    │   │   └── FlightController.java          # REST API
    │   └── config/
    │       ├── CorsConfig.java                # allows the dashboard to call the API
    │       └── DashboardLauncher.java         # auto-opens the dashboard on startup
    └── resources/
        ├── application.properties
        └── static/
            └── index.html                     # the dashboard UI (served at http://localhost:8080/)
```

## How to open in IntelliJ

1. `File > Open`, select the `flight-tracker` folder (the one with `pom.xml`).
2. IntelliJ will detect it as a Maven project and auto-import dependencies
   (make sure "Auto-import" or the Maven refresh icon is used if prompted).
3. Requires JDK 17+ — set it under `File > Project Structure > Project SDK`.
4. Run `FlightTrackerApplication.java` (green run arrow next to `main`).
5. Server starts on `http://localhost:8080` and **the dashboard opens automatically**
   in your default browser (via `DashboardLauncher`). If it doesn't pop up (e.g. on a
   headless/remote box), just open `http://localhost:8080` yourself.

## Using the dashboard

The page at `http://localhost:8080` lets you:
- Track a new flight with a form (flight number, origin, destination, times)
- See all tracked flights as live-updating cards (status, ETA, delay reason)
- Get a toast notification the instant a flight's status changes, via the
  `/api/flights/stream` SSE endpoint — no page refresh needed
- Stop tracking a flight with the × on its card

## Trying it via curl instead

**Track a flight:**
```bash
curl -X POST http://localhost:8080/api/flights/track \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "AI202",
    "origin": "DEL",
    "destination": "BOM",
    "scheduledDeparture": "2026-10-22T09:00:00",
    "scheduledArrival": "2026-10-22T11:15:00"
  }'
```

**List all tracked flights (with live status):**
```bash
curl http://localhost:8080/api/flights
```

**Get one flight:**
```bash
curl http://localhost:8080/api/flights/AI202
```

**Listen for live push notifications (SSE):**
```bash
curl http://localhost:8080/api/flights/stream
```
Leave this running — within ~10-20 seconds you'll start seeing
`flight-status-update` events as the scheduler simulates status changes,
each with a message, delay reason (if any), and revised ETA.

**Stop tracking:**
```bash
curl -X DELETE http://localhost:8080/api/flights/track/AI202
```

## Notes / how it maps to the task requirements

- **Mock API** → `MockFlightApiService`: randomly but plausibly transitions a
  flight through statuses (On Time → Boarding → Departed → Landed, with a
  chance of Delayed in between).
- **Push notifications for important updates** → `NotificationService`
  broadcasts over SSE whenever the scheduler detects a change; swap this for
  Firebase Cloud Messaging / APNs calls to reach an actual mobile app.
- **Delay reason + revised schedule** → included directly on `StatusUpdate`
  and `Flight` (`delayReason`, `estimatedArrival`).
- **Track multiple flights simultaneously** → `FlightTrackingService` keeps
  every tracked flight in a concurrent map, keyed by flight number.
- **Dynamic ETA updates for dashboard/app** → `GET /api/flights` always
  reflects the latest `estimatedArrival` per flight; the SSE stream pushes
  changes the instant they happen.

## Suggested next steps

- Swap the in-memory `ConcurrentHashMap` for a database (e.g. PostgreSQL +
  Spring Data JPA) so tracked flights persist across restarts.
- Add a `userId` to `Flight`/`TrackFlightRequest` so tracking is per-user.
- Replace SSE with actual FCM/APNs calls in `NotificationService` for real
  mobile push notifications.
- Add unit tests for `MockFlightApiService` status transitions and for the
  scheduler's broadcast logic.
