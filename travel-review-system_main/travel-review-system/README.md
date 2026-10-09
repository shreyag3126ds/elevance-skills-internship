# Travel Platform — Review & Rating System

Implements the internship task: a review/rating system for hotels and flights
with 1–5 star ratings, text reviews with photos, replies, flagging, and
moderator review of flagged content, plus sorting/filtering (most helpful,
newest, highest rated).

## Opening in IntelliJ

1. **File → Open** and select the `travel-review-system` folder (the one
   containing `pom.xml`). IntelliJ will detect it as a Maven project and
   download dependencies automatically (needs internet access once).
2. Make sure **Project SDK is Java 17+** (File → Project Structure → SDK).
3. Enable annotation processing for Lombok: **Settings → Build, Execution,
   Deployment → Compiler → Annotation Processors → Enable annotation
   processing**. Also install the *Lombok* IntelliJ plugin if it isn't
   bundled already.
4. Run `ReviewSystemApplication.java` (right-click → Run). It starts on
   `http://localhost:8080` using an in-memory H2 database, so there is
   nothing else to configure.
5. On startup the console prints seeded ids, e.g.
   `Seeded: user id=1, moderator id=2, hotel id=3, flight id=4` — use those
   ids in the requests below.
6. **Your default browser will open the Swagger dashboard automatically**
   a couple of seconds after the app finishes starting — no need to type
   the URL in yourself. (If it doesn't, e.g. on a restricted machine, just
   open `http://localhost:8080/swagger-ui.html` manually.)

## The dashboard (Swagger UI)

The dashboard opens automatically after Run (see step 6 above). It's a live,
clickable page listing every endpoint. For any endpoint you can click
**"Try it out"**, fill in the fields (use the seeded ids from the console
log), click **Execute**, and see the real response — no Postman or curl
needed.


## Project structure

```
src/main/java/com/travel/reviewsystem/
├── ReviewSystemApplication.java   # main class
├── DataSeeder.java                # sample data on startup
├── model/                         # JPA entities + enums
│   ├── User.java, Hotel.java, Flight.java
│   ├── Review.java                # rating, text, photos, helpful votes
│   ├── ReviewReply.java           # replies to a review
│   ├── ReviewFlag.java            # a report against a review
│   ├── TargetType.java            # HOTEL / FLIGHT
│   └── FlagStatus.java            # PENDING / DISMISSED / REMOVED
├── repository/                    # Spring Data JPA repositories
├── dto/                           # request/response objects
├── service/
│   ├── ReviewService.java         # create/list/sort reviews, replies, helpful votes
│   └── ModerationService.java     # flagging + moderator resolution
├── controller/
│   ├── ReviewController.java      # /api/reviews/**
│   └── ModerationController.java  # /api/moderation/**
└── exception/                     # 404/validation error handling
```

## API quick reference

| Action | Endpoint |
|---|---|
| Create a review | `POST /api/reviews` |
| List reviews (sorted/paged) | `GET /api/reviews?targetType=HOTEL&targetId=3&sort=MOST_HELPFUL&page=0&size=10` |
| Mark review helpful | `POST /api/reviews/{id}/helpful` |
| Reply to a review | `POST /api/reviews/{id}/replies` |
| Flag a review | `POST /api/reviews/{id}/flags` |
| View pending flags (moderator) | `GET /api/moderation/flags/pending` |
| Resolve a flag (moderator) | `POST /api/moderation/flags/{flagId}/resolve` |

`sort` accepts `NEWEST`, `HIGHEST_RATED`, `MOST_HELPFUL`.

### Example: create a review

```http
POST /api/reviews
Content-Type: application/json

{
  "targetType": "HOTEL",
  "targetId": 3,
  "authorId": 1,
  "rating": 5,
  "text": "Great pool, friendly staff, would stay again.",
  "photoUrls": ["https://cdn.example.com/photos/pool.jpg"]
}
```

### Example: flag a review

```http
POST /api/reviews/1/flags
Content-Type: application/json

{
  "reporterId": 1,
  "reason": "spam"
}
```

### Example: moderator resolves the flag

```http
POST /api/moderation/flags/1/resolve
Content-Type: application/json

{
  "moderatorId": 2,
  "remove": true,
  "note": "Confirmed spam, removed."
}
```

## Notes / next steps

- Photo **upload** itself isn't implemented here — `photoUrls` assumes photos
  are uploaded to storage (S3, Cloudinary, etc.) first and only the URLs are
  sent to this API. Wiring a real `MultipartFile` upload endpoint is a
  straightforward addition to `ReviewController` if the task requires it.
- There's no authentication layer — `authorId`/`moderatorId` are passed in
  the request body to keep the demo simple. In a real app these would come
  from a logged-in session (Spring Security) instead.
- H2 console is available at `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:mem:reviewdb`, user `sa`, empty password) to inspect data.
