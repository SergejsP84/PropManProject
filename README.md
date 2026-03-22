# Destiny PropMan — Property Rental Management Backend

A full-featured REST API backend for a property rental management platform, conceptually similar to Airbnb. The system supports three distinct user roles — **Admin**, **Manager**, and **Tenant** — each with their own set of capabilities and secured endpoints.

Built with **Java 21** and **Spring Boot 3.2**.

---

## Features

### Tenant
- Register and confirm account via email token
- Search available properties by date range, location, type, amenities, price range, size, and rating
- Make, view, and cancel bookings
- Submit and track claims against managers
- Leave reviews for properties previously stayed in
- Request early lease termination
- View payment history and request refunds
- Maintain a favourites list
- Send and receive messages with managers

### Manager
- Register and confirm account via email token
- Add, configure, and manage properties
- Set per-property pricing (daily / weekly / monthly) with seasonal discount/surcharge rules
- Add bills to properties and track their payment status
- View and respond to bookings and early termination requests
- Submit and track claims against tenants
- Send and receive messages with tenants
- View financial summaries per property

### Admin
- Full CRUD over all entities (managers, tenants, properties, currencies, amenities, etc.)
- Activate/deactivate manager and tenant accounts
- Resolve open claims
- Settle pending payouts and refunds
- Configure platform-wide numerical parameters (fees, timeouts, etc.)
- Add and manage supported currencies with exchange rates

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| Persistence | Spring Data JPA + MySQL |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Mapping | MapStruct |
| Boilerplate reduction | Lombok |
| Reports | Apache POI (Excel) |
| Email | JavaMail |
| Containerisation | Docker / Docker Compose |

---

## Architecture

```
src/main/java/lv/emendatus/Destiny_PropMan/
├── controllers/          REST controllers (one per domain area)
├── service/
│   ├── interfaces/       Service contracts
│   └── implementation/   JPA-backed service implementations
├── repository/interfaces/ Spring Data JPA repositories
├── domain/
│   ├── entity/           JPA entities
│   ├── dto/              Request / response DTOs
│   └── enums_for_entities/
├── mapper/               MapStruct mappers (entity ↔ DTO)
├── config/               Security, CORS, Jackson, Swagger configuration
├── annotation/           Custom Swagger annotation interfaces per endpoint
├── scheduled_tasks/      Booking lifecycle, payment processing, token cleanup
├── util/                 JWT, financial report generation, serialisers
└── exceptions/           Custom exception classes
```

---

## Selected Technical Highlights

### JWT Authentication with per-user secret keys
Each user session is signed with an individual `SecretKey` stored server-side, not a global secret. This means a user's tokens can be invalidated independently without affecting anyone else.
- [`JwtUtil.java`](src/main/java/lv/emendatus/Destiny_PropMan/util/JwtUtil.java)
- [`SecretKeyMapConverter.java`](src/main/java/lv/emendatus/Destiny_PropMan/util/SecretKeyMapConverter.java)
- [`JpaNumericDataMappingService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaNumericDataMappingService.java)

### Role-based access control (Admin / Manager / Tenant)
Spring Security is configured with three distinct authority levels. Endpoints are secured at the path level in `SecurityConfig` and additionally with `@PreAuthorize` at the method level where finer-grained ownership checks are needed.
- [`SecurityConfig.java`](src/main/java/lv/emendatus/Destiny_PropMan/config/SecurityConfig.java)
- [`UserDetailsInnerService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/UserDetailsInnerService.java)

### Booking availability with overlap detection
Available properties for a requested date range are found by first querying for any bookings whose dates overlap with the request (using a `NOT (end < start OR start > end)` predicate pushed to the database), then excluding properties with such bookings.
- [`BookingRepository.java`](src/main/java/lv/emendatus/Destiny_PropMan/repository/interfaces/BookingRepository.java) — `findOverlappingBookings`
- [`JpaPropertyService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaPropertyService.java) — `getAvailableProperties`

### Amenity filtering with a single aggregation query
Finding properties that have *all* of a requested set of amenities is handled with a `GROUP BY / HAVING COUNT(DISTINCT ...)` JPQL query, returning matching property IDs in one database round-trip.
- [`PropertyAmenityRepository.java`](src/main/java/lv/emendatus/Destiny_PropMan/repository/interfaces/PropertyAmenityRepository.java) — `findPropertyIdsWithAllAmenities`

### Multi-criteria property search
A dedicated `SearchCriteria` object carries all optional search parameters. The search service builds an in-memory intersection from the DB-filtered results for each active criterion, allowing any combination of filters to be applied.
- [`PropertySearchService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/PropertySearchService.java)
- [`SearchCriteria.java`](src/main/java/lv/emendatus/Destiny_PropMan/domain/dto/search/SearchCriteria.java)

### Booking price calculation with seasonal discounts and surcharges
The total price is calculated by decomposing the booking length into full months, full weeks, and remaining days (each at their respective rate), then applying per-day discount or surcharge percentages to produce a final rounded price.
- [`JpaBookingService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaBookingService.java) — `calculateTotalPrice`
- [`JpaPropertyDiscountService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaPropertyDiscountService.java)

### Automated booking lifecycle management
A scheduled task runs on a configurable cadence and handles the full lifecycle of bookings without manual intervention: transitioning statuses (PENDING → CURRENT → OVER), releasing properties, archiving leasing history, generating payments and payouts, and sending email notifications to both tenants and managers.
- [`ScheduledTasks.java`](src/main/java/lv/emendatus/Destiny_PropMan/scheduled_tasks/ScheduledTasks.java)

### Monthly financial report generation (Excel)
On a scheduled monthly basis, the platform generates an `.xlsx` financial report for the previous month, covering all tenant payments, refunds, and manager payouts, using Apache POI.
- [`FinancialReportGenerator.java`](src/main/java/lv/emendatus/Destiny_PropMan/util/FinancialReportGenerator.java)
- [`FinancialReportScheduler.java`](src/main/java/lv/emendatus/Destiny_PropMan/util/FinancialReportScheduler.java)

### Multi-currency support
All monetary values are stored in a configurable base currency. The platform supports additional currencies with exchange rates that can be managed by admins. Price range search criteria are automatically converted to the base currency before querying.
- [`JpaCurrencyService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaCurrencyService.java)

### Email confirmation flow
Both manager and tenant registration uses a double opt-in flow: a confirmation token is generated on sign-up, emailed to the user, and the account is only activated once the token is submitted back to the confirmation endpoint.
- [`JpaManagerRegistrationService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaManagerRegistrationService.java)
- [`JpaTenantRegistrationService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaTenantRegistrationService.java)
- [`JpaEmailService.java`](src/main/java/lv/emendatus/Destiny_PropMan/service/implementation/JpaEmailService.java)

---

## Running the Project

### With Docker Compose
```bash
docker compose up --build
```
The API will be available at `http://localhost:8080`.

### With Maven (requires a running MySQL instance)
Configure your database connection in `src/main/resources/application.properties`, then:
```bash
./mvnw spring-boot:run
```

---

## API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```
All endpoints are documented with request/response schemas and grouped by controller area. Each endpoint also carries a custom `@Operation` annotation describing its purpose and access requirements.

---

## Project Structure Notes

- `completedPayouts.txt` / `completedRefunds.txt` / `completedTenantPayments.txt` — running logs written by the scheduled tasks to record processed financial transactions.
- `Extrastore/` — directory used by the application for storing uploaded property photos.
