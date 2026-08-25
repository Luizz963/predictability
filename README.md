# BacResist_IA — Surveillance API

Java/Spring Boot back-end responsible for genome ingestion, multi-tenant data isolation, orchestration of the AI inference engine, and aggregated outbreak-surveillance alerting for hospital clients.

> **Scope note:** this service is part of a research and portfolio project exploring genomic surveillance of antimicrobial resistance (AMR) in hospital settings. It is not a certified medical device and is not intended to guide individual patient treatment decisions. See [Regulatory framing](#regulatory-framing) below.

## Overview

Laboratories (own or third-party sequencing providers acting on behalf of a hospital) upload bacterial genome files (`.fasta`/`.fna`) through this API. The service stores the file, sends it to the [AI inference engine](#related-components) for analysis, persists the raw per-sample result internally, and periodically aggregates results by hospital unit and time window into surveillance alerts — the only data ever exposed to the client-facing dashboard.

## Architecture / data flow

```
Laboratory (authenticated)
   │  multipart upload (.fasta/.fna) + hospitalName + hospitalUnit
   ▼
POST /api/v1/genomes/analyze  (GenomaController)
   │  202 Accepted — processing is asynchronous
   ▼
GenomeIngestionService
   │  stores raw file (StorageService → S3)
   ▼
AsyncAnalysisService
   │  calls the AI inference engine (PythonApiClientService → FastAPI /predict)
   ▼
Report  (internal entity — never exposed via a public endpoint)
   │
   ▼
SurveillanceAggregationService
   │  aggregates Report rows by laboratory + hospital unit + time window
   ▼
SurveillanceAlert  (exposed entity — aggregated only)
   │
   ▼
GET /api/v1/genomes/surveillance   →  paginated outbreak alerts
GET /api/v1/dashboard/stats        →  consolidated stats
GET /api/v1/dashboard/recent       →  recent samples (unit/gene/confidence level, no raw ML score)
```

## Tech stack

- **Spring Boot** — REST API
- **Spring Security** (`SecurityConfig`, `SecurityFilter`) — token-based authentication (`TokenService`, `AuthenticationService`)
- **Spring Data JPA** — persistence (`LaboratoryRepository`, `ReportRepository`, `SurveillanceAlertRepository`)
- **Flyway** — versioned database migrations
- **AWS S3** (`S3Config`, `StorageService`) — raw genome file storage
- **springdoc-openapi** (`OpenApiConfig`) — API documentation
- **Async processing** (`AsyncConfig`, `AsyncAnalysisService`) — non-blocking genome analysis pipeline
- **RestClient** (`RestClientConfig`, `PythonApiClientService`) — HTTP client to the Python inference engine

## Project structure

```
com.bacteria_resistant.project
├── config
│   ├── AsyncConfig
│   ├── OpenApiConfig
│   ├── RestClientConfig
│   ├── S3Config
│   ├── SecurityConfig
│   └── SecurityFilter
├── controller
│   ├── AuthController
│   ├── DashboardController
│   └── GenomaController
├── dto
│   ├── response/
│   │   ├── AiAnalysisResponse
│   │   ├── ErrorResponse
│   │   ├── LoginResponseDTO
│   │   ├── PredictionResponse
│   │   └── SurveillanceAlertDTO
│   ├── AiAnalysisRequest
│   ├── AuthenticationDTO
│   ├── ClusterAlertDTO
│   ├── DashboardStatsDTO
│   └── RecentSampleDTO
├── exception
│   ├── GlobalExceptionHandler
│   └── InvalidFileException
├── model
│   ├── enums
│   │   └── ReportStatus
│   ├── Laboratory
│   ├── Report
│   └── SurveillanceAlert
├── repository
│   ├── LaboratoryRepository
│   ├── ReportRepository
│   └── SurveillanceAlertRepository
├── service
│   ├── AsyncAnalysisService
│   ├── AuthenticationService
│   ├── DashboardService
│   ├── GenomeIngestionService
│   ├── PythonApiClientService
│   ├── StorageService
│   ├── SurveillanceAggregationService
│   └── TokenService
├── ProjectApplication
└── resources/db.migration
    ├── V1__create_table_laboratories.sql
    ├── V2__create_table_reports.sql
    ├── V3__add_hospital_and_gene_columns.sql
    └── V4__tb_surveillance_alerts.sql
```

## Endpoints

All endpoints (except authentication) require a valid token and resolve the calling `Laboratory` via `@AuthenticationPrincipal` — every query is scoped to the authenticated laboratory, enforcing tenant isolation at the repository level.

| Method | Path | Purpose | Exposure level |
|---|---|---|---|
| `POST` | `/api/v1/genomes/analyze` | Upload one or more genome files for a given hospital/unit; triggers async analysis | Write, per-sample input |
| `GET` | `/api/v1/genomes/surveillance` | Paginated outbreak alerts for a date range | Aggregated only |
| `GET` | `/api/v1/dashboard/stats` | Consolidated statistics for the authenticated laboratory | Aggregated only |
| `GET` | `/api/v1/dashboard/recent` | Recent processed samples (unit, gene, confidence level) | Per-sample — audited to exclude raw ML score / patient identity |

## Database migrations (Flyway)

- `V1` — `laboratories` table (tenant accounts)
- `V2` — `reports` table (internal, per-sample raw analysis results)
- `V3` — adds hospital/gene-related columns to `reports`
- `V4` — `tb_surveillance_alerts` table (aggregated, client-facing)

## Current aggregation logic

`SurveillanceAggregationService` currently aggregates over a fixed 7-day rolling window per laboratory + hospital unit, and triggers an alert with a simple fixed threshold: at least 3 samples in the window **and** resistance prevalence ≥ 50%.

This is a deliberately simple first rule. A more robust version — comparing current prevalence against each unit's historical baseline, or a proper statistical test (similar to SaTScan's approach) — is a planned improvement, not yet implemented. See [Roadmap](#status--roadmap).

## Regulatory framing

This service is designed to operate within the scope of **aggregated epidemiological surveillance**, not individual clinical diagnostics. Per Brazilian regulation (ANVISA RDC 657/2022), software that processes demographic/epidemiological medical data without an individual diagnostic or therapeutic purpose is excluded from the medical device software regime. Accordingly:

- `Report` (per-sample raw result) is an internal entity — no controller in this codebase should ever serialize it directly in an HTTP response.
- Only `SurveillanceAlert` (aggregated by hospital unit and time window) and reviewed per-sample summary fields (unit, gene, confidence level — never a raw probability score or patient identity) are meant to reach a hospital-facing view.
- No endpoint accepts or stores patient-identifying information — samples are tracked by laboratory, hospital, and unit only.

This is a design principle, not a legal guarantee — any commercial use should be reviewed with qualified regulatory and legal counsel.

## Related components

- **AI inference engine** — Python/FastAPI service (`bacresist-inference`) that performs known-gene detection (AMRFinderPlus) and the ML pattern signal (XGBoost), called internally via `PythonApiClientService`. (Separate repository.)

## Status / Roadmap

Active development / academic project (undergraduate thesis in progress). Not production-ready.

- [x] Multi-tenant authentication and data isolation (Spring Security + `@AuthenticationPrincipal`)
- [x] Async genome ingestion and analysis pipeline
- [x] Internal vs. aggregated data separation (`Report` vs. `SurveillanceAlert`)
- [x] Paginated surveillance alert endpoint
- [ ] Audit `RecentSampleDTO` fields to confirm no raw ML probability or patient-identifying data is exposed
- [ ] Replace fixed-threshold alert rule with historical-baseline or statistical-test-based detection
- [ ] Reconcile `Report`'s resistance flag with the AI engine's three-tier confidence level (`ALTA`/`INVESTIGAR`/`BAIXA`)
- [ ] Validation against real clinical antibiogram data with a partner institution

## License

*(Add your chosen license here — e.g. MIT, Apache 2.0.)*

## Author

Luiz — Data Science & AI student, backend developer.
