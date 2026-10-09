# Booking Cancellation & Refund System

Spring Boot backend + dashboard implementing the internship task:
- Cancel a booking directly from the dashboard (web UI at http://localhost:8080)
- Refund auto-calculated from a predefined policy (see `RefundPolicyService`)
- Partial refunds supported
- User must pick a reason from a predefined dropdown (`CancellationReason` enum)
- Refund status tracker: PENDING -> PROCESSED -> COMPLETED, with an expected completion date
- The dashboard auto-opens in your browser when the app starts (see `BookingRefundApplication.java`)

## Open in IntelliJ IDEA
1. File > Open -> select the `booking-refund-system` folder -> open as a Maven project.
2. Let Maven finish downloading dependencies.
3. Run `BookingRefundApplication.java` (green run icon).
4. Your browser should open automatically to http://localhost:8080 showing the dashboard.
   If it doesn't open automatically, just visit that URL yourself.
5. H2 console (to inspect the DB) at http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:bookingdb, user sa, no password.

Requires Java 17+ and Maven (bundled with IntelliJ).

## Refund policy (edit in RefundPolicyService)
| Time before reservation when cancelled | Refund |
|---|---|
| More than 7 days | 100% |
| 24 hours - 7 days | 75% |
| Less than 24 hours | 50% |
| After reservation time | 0% |

## Project structure
```
src/main/java/com/internship/bookingrefund/
├── BookingRefundApplication.java   entry point + auto-opens dashboard
├── model/        Booking, Refund, BookingStatus, RefundStatus, CancellationReason
├── repository/   Spring Data JPA repositories
├── dto/          Request/response objects for the API
├── service/      RefundPolicyService (the rules), BookingService (cancel flow), RefundService (status tracker)
├── controller/    REST endpoints
└── exception/    Clean JSON error responses

src/main/resources/
├── static/index.html   the dashboard UI
├── application.properties
└── data.sql             sample bookings
```
