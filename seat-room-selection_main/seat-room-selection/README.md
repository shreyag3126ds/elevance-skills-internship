# Seat & Room Selection (Spring Boot)

## Run in IntelliJ IDEA
1. File > Open > select the `seat-room-selection` folder (the one with pom.xml) > "Open as Project".
2. Wait for Maven to import. Set Project SDK to JDK 17+ (File > Project Structure).
3. Enable Lombok: Settings > Build > Compiler > Annotation Processors > "Enable annotation processing".
   (The Lombok plugin is bundled in current IntelliJ versions.)
4. Open `SeatRoomApplication.java` and click the green Run arrow.
5. Visit http://localhost:8080  (H2 console: http://localhost:8080/h2-console, JDBC URL jdbc:h2:mem:seatroomdb)

## REST API
GET  /api/flights/{flightId}/seats
POST /api/seats/{id}/book?userId=...
POST /api/seats/{id}/release?userId=...
GET  /api/hotels/{hotelId}/rooms
POST /api/rooms/{id}/book?userId=...
POST /api/rooms/{id}/release?userId=...
GET/PUT /api/users/{userId}/preferences
