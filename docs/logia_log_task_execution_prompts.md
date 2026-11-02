# 16 Master Execution Prompts — Logia Log v3.1 (GitHub Issues #1 s/d #16)

Panduan ini berisi **16 Prompt Siap Pakai** (*ready-to-use execution prompts*) yang dirancang khusus untuk mengeksekusi setiap GitHub Issue pada repositori `Yosua13/logia-log`.

Setiap prompt telah dilengkapi dengan:
- **Konteks & Sasaran Task** (mengacu pada PRD Logia Log v3.1 Enterprise Edition)
- **Spesifikasi & Batasan Teknis Eksak** (Java 25.0.5.0.2 LTS, Spring Boot 4.1.2, PostgreSQL 18.4, Node.js 26.13.0, RabbitMQ 4, Upstash Redis SaaS TLS rediss://)
- **Daftar File Target yang Dikerjakan**
- **Langkah-langkah Implementasi Detail**
- **Perintah Pengujian & Verifikasi Acceptance Criteria**

---

## Daftar Isi Prompt

- [Prompt 01: [INFRA] Setup Monorepo, Docker Compose (PostgreSQL 18.4 & RabbitMQ 4) & Environment Blueprint (Issue #1)](#prompt-01-infra-setup-monorepo-docker-compose-postgresql-18-4-rabbitmq-4-environment-blueprint)
- [Prompt 02: [BACKEND] Upgrade Core Stack ke Java 25.0.5.0.2 LTS & Spring Boot 4.1.2 dengan Clean Hexagonal Architecture (Issue #2)](#prompt-02-backend-upgrade-core-stack-ke-java-25-0-5-0-2-lts-spring-boot-4-1-2-dengan-clean-hexagonal-architecture)
- [Prompt 03: [BACKEND/INFRA] Integrasi Upstash Redis SaaS (TLS rediss://) untuk Cache, Deduplikasi & Rate Limiting (Issue #3)](#prompt-03-backend-infra-integrasi-upstash-redis-saas-tls-rediss-untuk-cache-deduplikasi-rate-limiting)
- [Prompt 04: [DATABASE] Skema Inti PostgreSQL 18.4 & Flyway Migration (Partitioned Logs, GIN JSONB & UUIDv7) (Issue #4)](#prompt-04-database-skema-inti-postgresql-18-4-flyway-migration-partitioned-logs-gin-jsonb-uuidv7)
- [Prompt 05: [BACKEND] Pipeline RabbitMQ Log Ingestion (DLQ, Virtual Threads) & SSE Real-Time Broadcaster (FR-LOG-01, FR-LOG-04) (Issue #5)](#prompt-05-backend-pipeline-rabbitmq-log-ingestion-dlq-virtual-threads-sse-real-time-broadcaster-fr-log-01-fr-log-04)
- [Prompt 06: [FRONTEND] Upgrade Runtime Node.js v26.13.0, Migrasi TypeScript & Arsitektur Feature-Sliced Design (FSD) (Issue #6)](#prompt-06-frontend-upgrade-runtime-node-js-v26-13-0-migrasi-typescript-arsitektur-feature-sliced-design-fsd)
- [Prompt 07: [FRONTEND] Design System 60-30-10 Obsidian Black & Royal Gold dengan Sharp-Angled Tactical UI (Issue #7)](#prompt-07-frontend-design-system-60-30-10-obsidian-black-royal-gold-dengan-sharp-angled-tactical-ui)
- [Prompt 08: [BACKEND] REST API Log Search Multi-Dimensi, Regex Filtering & Time-Series Histogram (FR-LOG-02, FR-LOG-03) (Issue #8)](#prompt-08-backend-rest-api-log-search-multi-dimensi-regex-filtering-time-series-histogram-fr-log-02-fr-log-03)
- [Prompt 09: [FRONTEND] Modul Grafana-Style Real-Time Log Explorer & Metrics Dashboard (FR-LOG-01..04) (Issue #9)](#prompt-09-frontend-modul-grafana-style-real-time-log-explorer-metrics-dashboard-fr-log-01-04)
- [Prompt 10: [BACKEND] Engine Proxy Eksekusi API, Manajemen Koleksi & Auto Trace-ID Correlation (FR-API-01..07) (Issue #10)](#prompt-10-backend-engine-proxy-eksekusi-api-manajemen-koleksi-auto-trace-id-correlation-fr-api-01-07)
- [Prompt 11: [FRONTEND] Modul Postman-Style Integrated API Testing & Debugging Studio (FR-API-01..07) (Issue #11)](#prompt-11-frontend-modul-postman-style-integrated-api-testing-debugging-studio-fr-api-01-07)
- [Prompt 12: [BACKEND/FRONTEND] Manajemen Target Profile Dinamis (Target-Agnostic) & Read-Only DB Schema Inspector (FR-DSI-01, FR-DSI-02) (Issue #12)](#prompt-12-backend-frontend-manajemen-target-profile-dinamis-target-agnostic-read-only-db-schema-inspector-fr-dsi-01-fr-dsi-02)
- [Prompt 13: [BACKEND] Pipeline PII/Sensitive Data Masking & Recurring Error Threshold Trigger via Upstash Redis (FR-DSI-03, FR-DSI-04) (Issue #13)](#prompt-13-backend-pipeline-pii-sensitive-data-masking-recurring-error-threshold-trigger-via-upstash-redis-fr-dsi-03-fr-dsi-04)
- [Prompt 14: [BACKEND] Konektor GitHub API & 7-Layer AI Deep Code Resolution Engine (Modul 4) (Issue #14)](#prompt-14-backend-konektor-github-api-7-layer-ai-deep-code-resolution-engine-modul-4)
- [Prompt 15: [FRONTEND] Halaman Incident Deep Audit, 7-Layer AI Resolution View & Monaco Patch Diff Viewer (Issue #15)](#prompt-15-frontend-halaman-incident-deep-audit-7-layer-ai-resolution-view-monaco-patch-diff-viewer)
- [Prompt 16: [SECURITY/QA] Pengamanan SSRF Guard, Enkripsi Kredensial AES-256-GCM, Performance SLA & E2E Testing (Issue #16)](#prompt-16-security-qa-pengamanan-ssrf-guard-enkripsi-kredensial-aes-256-gcm-performance-sla-e2e-testing)

---

### Prompt 01: [INFRA] Setup Monorepo, Docker Compose (PostgreSQL 18.4 & RabbitMQ 4) & Environment Blueprint

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #1 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Menyiapkan fondasi **Monorepo Target-Agnostic** beserta infrastruktur pendukung berdasarkan **PRD Logia Log v3.1 (Bagian 03, 04.3 & 10.2 - Fase 1)**.

## Spesifikasi Teknis
- **Database Utama**: `PostgreSQL 18.4` (`postgres:18.4-alpine` / lokal `psql 18.4`)
- **Message Broker**: `RabbitMQ 4.x` (`rabbitmq:4-management-alpine` pada port `5672` & `15672`)
- **Managed Redis SaaS**: **Upstash Serverless Redis (Free Tier)** melalui protokol TLS `rediss://` (tanpa container Redis lokal agar ringan dan siap cloud)
- **Target Slot**: Direktori `target-simulation/` dikosongkan secara default (*Plug-and-Play / Target-Agnostic*)

## Scope Pengerjaan & Checklist
- [ ] Perbarui `docker-compose.yml` menggunakan `postgres:18.4-alpine` dan `rabbitmq:4-management-alpine` lengkap dengan `healthcheck` dan named volumes.
- [ ] Buat `.env.example` terstandarisasi untuk konfigurasi PostgreSQL 18.4, RabbitMQ, Upstash Redis (`UPSTASH_REDIS_URL=rediss://...`), enkripsi `AES-256-GCM`, GitHub API, dan AI Provider.
- [ ] Tambahkan `.nvmrc` (`26.13.0`) dan perbarui `.gitignore` untuk artefak Java 25, Maven, Vite/TypeScript, dan file `.env`.
- [ ] Siapkan slot direktori `target-simulation/` (kosong/opsional) beserta dokumentasi arsitektur pada `README.md` root.

## Kriteria Penerimaan (Acceptance Criteria)
1. `docker compose config` valid tanpa peringatan versi usang.
2. Container PostgreSQL 18.4 dan RabbitMQ 4 menyala sehat (`healthy`) saat dijalankan.
3. Tidak ada kredensial rahasia yang ter-commit ke repositori (`.env` diabaikan oleh Git).

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 02: [BACKEND] Upgrade Core Stack ke Java 25.0.5.0.2 LTS & Spring Boot 4.1.2 dengan Clean Hexagonal Architecture

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #2 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Melakukan migrasi penuh backend dari Java versi lama ke **Java 25.0.5.0.2 LTS** (`HotSpot 64-Bit Server VM build 25.0.5.0.2+1-LTS-2`) dan **Spring Boot 4.1.2**, serta menyusun fondasi **Clean / Hexagonal Architecture (Ports & Adapters)** sesuai **PRD Logia Log v3.1 (Bagian 03 & 04.1)**.

## Spesifikasi Teknis
- **JVM Runtime**: `Java 25.0.5.0.2 LTS` (`<release>25</release>`, `maven-enforcer-plugin` `[25,)`)
- **Framework**: `Spring Boot 4.1.2` (Spring Framework 7 / Jakarta EE 11)
- **Concurrency**: Mengaktifkan **Project Loom Virtual Threads** (`spring.threads.virtual.enabled=true`)
- **Struktur Paket (`com.logialog`)**:
  - `domain/` (`model`, `service` murni tanpa ketergantungan framework eksternal)
  - `application/` (`port/in`, `port/out`, `usecase`)
  - `adapter/in/` (`web`, `messaging`)
  - `adapter/out/` (`persistence`, `redis`, `sse`, `targetdb`, `apiproxy`, `github`, `ai`)
  - `config/`

## Scope Pengerjaan & Checklist
- [ ] Upgrade `backend/pom.xml` ke `spring-boot-starter-parent` versi `4.1.2` dan `<java.version>25</java.version>`.
- [ ] Tambahkan `maven-enforcer-plugin` untuk memastikan build hanya berjalan pada JDK `>= 25`.
- [ ] Hapus class `@SpringBootApplication` duplikat bawaan template lama (`DemoApplication` & `BackendApplication`) dan ganti dengan `com.logialog.LogiaLogApplication`.
- [ ] Konfigurasi `application.yml` dengan Virtual Threads aktif, CORS terukur untuk frontend, Spring Security 7 stateless, dan Spring Boot Actuator (`/actuator/health`, `/api/v1/system/info`).
- [ ] Susun kerangka paket Clean Hexagonal lengkap beserta unit/integration test awal (`@SpringBootTest`).

## Kriteria Penerimaan (Acceptance Criteria)
1. `./mvnw clean test` dan `./mvnw package` berjalan sukses di atas JDK `25.0.5.0.2` dan Spring Boot `4.1.2`.
2. Endpoint `GET /api/v1/system/info` mengembalikan versi Java `25.x`, Spring Boot `4.1.2`, dan status `virtualThreadsActive: true`.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 03: [BACKEND/INFRA] Integrasi Upstash Redis SaaS (TLS rediss://) untuk Cache, Deduplikasi & Rate Limiting

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #3 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Mengintegrasikan **Managed Redis SaaS gratis terbaik ΓÇö Upstash Serverless Redis (Free Tier)** ke backend Spring Boot 4.1.2 untuk caching laporan diagnostik AI, deduplikasi fingerprint error berulang, dan rate-limiting pemicu AI sesuai **PRD Logia Log v3.1 (Bagian 03, 05.3 & 09)**.

## Mengapa Upstash Serverless Redis (Free Tier)?
- **256 MB Storage & 500.000 Commands/Bulan Gratis Selamanya** (tanpa kartu kredit), jauh lebih lega dibanding Redis Cloud Free Tier (30 MB & batas keras 30 koneksi).
- Mendukung protokol standar **TLS `rediss://`** sehingga kompatibel langsung dengan `spring-boot-starter-data-redis` (Lettuce) di region terdekat (**AWS `ap-southeast-1` Singapore**).

## Scope Pengerjaan & Checklist
- [ ] Tambahkan dependensi `spring-boot-starter-data-redis`, `spring-boot-starter-cache`, dan `commons-pool2` pada `backend/pom.xml`.
- [ ] Konfigurasi koneksi `spring.data.redis.url=${UPSTASH_REDIS_URL}` dengan SSL (`rediss://`) dan ukuran pool Lettuce konservatif (`max-active: 8`, `min-idle: 0`) serta menonaktifkan polling background `PING` Actuator agar hemat kuota 500K command/bulan.
- [ ] Implementasikan `UpstashRedisConfig` dengan TTL spesifik untuk cache `aiDiagnosticReports` (15 menit) dan `errorFingerprints` (5 menit).
- [ ] Implementasikan `UpstashErrorDeduplicationAdapter` (`INCR` atomik + `EXPIRE` pada hit pertama) beserta fallback in-memory untuk mode pengujian offline/lokal.
- [ ] Tambahkan mekanisme cooldown kunci Redis (`SET NX EX`) agar satu fingerprint error unik hanya memicu panggilan LLM/GitHub maksimal 1x per jendela waktu (mitigasi risiko *Log Flood* pada PRD Bagian 09).

## Kriteria Penerimaan (Acceptance Criteria)
1. Koneksi ke endpoint `rediss://...upstash.io:6379` berhasil membaca/menulis counter deduplikasi dan cache.
2. Penggunaan command Redis efisien (maksimal 1-2 command per event error) dan memiliki graceful fallback apabila jaringan eksternal terputus.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 04: [DATABASE] Skema Inti PostgreSQL 18.4 & Flyway Migration (Partitioned Logs, GIN JSONB & UUIDv7)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #4 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Merancang dan mengimplementasikan skema database inti pada **PostgreSQL 18.4** beserta migrasi otomatis **Flyway** dan repositori JPA/JDBC sesuai **PRD Logia Log v3.1 (Bagian 08.1)**.

## Spesifikasi Tabel Inti (PostgreSQL 18.4)
Memanfaatkan fungsi native `uuidv7()` bawaan PostgreSQL 18, *Range Partitioning*, dan `GIN Index` pada `JSONB`:
1. `target_profiles`: Slot profil target dinamis/opsional (`service_name`, `base_api_url`, `github_repo_url`, `github_branch`, `readonly_db_jdbc_url`, kredensial terenkripsi).
2. `app_logs`: Tabel terpartisi berdasarkan rentang waktu `created_at` (`PARTITION BY RANGE`) + `GIN (metadata jsonb_path_ops)` untuk pencarian cepat setara Grafana.
3. `error_aggregates`: Pelacak frekuensi error unik (`fingerprint_hash CHAR(64) PK`, `occurrence_count`, `first_seen`, `last_seen`, `status`).
4. `api_test_collections` & `api_test_requests`: Penyimpanan koleksi pengujian API setara Postman (`query_params`, `auth_type`, `auth_config_enc`, `headers`, `body_payload`).
5. `api_test_histories`: Riwayat eksekusi pengujian API yang terhubung langsung ke `trace_id` pada `app_logs`.
6. `github_code_audits`: Laporan analisis AI 7 dimensi (`analysis_7_layers JSONB`, `db_ddl_snapshot`, `suggested_patch_diff`).

## Scope Pengerjaan & Checklist
- [ ] Konfigurasi `spring-boot-starter-flyway` dan `flyway-database-postgresql` pada Spring Boot 4.1.2.
- [ ] Buat script migrasi `V1__init_logialog_schema.sql` dan prosedur manajemen partisi otomatis harian/mingguan untuk `app_logs`.
- [ ] Implementasikan JPA Entities / JDBC Repositories di `com.logialog.adapter.out.persistence` beserta Outbound Ports di layer `application`.
- [ ] Uji eksekusi migrasi dan query JSONB di atas PostgreSQL `18.4`.

## Kriteria Penerimaan (Acceptance Criteria)
1. Saat aplikasi menyala terhadap database PostgreSQL 18.4 kosong, seluruh 7 tabel, partisi `app_logs`, dan indeks GIN terbentuk otomatis melalui Flyway tanpa error.
2. Validasi skema Hibernate (`ddl-auto: validate`) lolos 100%.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 05: [BACKEND] Pipeline RabbitMQ Log Ingestion (DLQ, Virtual Threads) & SSE Real-Time Broadcaster (FR-LOG-01, FR-LOG-04)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #5 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Membangun pipeline penerimaan log berkecepatan tinggi melalui **RabbitMQ 4.x** & REST Ingestion API, kalkulasi fingerprint SHA-256, serta streaming real-time **Server-Sent Events (SSE)** ke dashboard sesuai **PRD Logia Log v3.1 (FR-LOG-01 & FR-LOG-04)**.

## Referensi Kebutuhan Fungsional & SLA
- **FR-LOG-01 (Live Tail & Streaming)**: Aliran log dari RabbitMQ atau HTTP Ingestion API disiarkan seketika melalui SSE (`GET /api/v1/logs/stream`) dengan latensi `< 500 ms (p95)`.
- **FR-LOG-04 (Log Deduplication & Counter)**: Normalisasi stack trace ke SHA-256 fingerprint (mengabaikan timestamp, UUID, memory address), pembaruan `occurrence_count` di `error_aggregates` + Upstash Redis, dan penandaan `Recurring Anomaly`.

## Scope Pengerjaan & Checklist
- [ ] Konfigurasi topologi RabbitMQ: `TopicExchange` (`logialog.logs.exchange`), `Queue` utama (`logialog.logs.ingest.queue`), `DeadLetterExchange` (`logialog.logs.dlx`), `DeadLetterQueue` (`logialog.logs.dlq`), dan `x-message-ttl`.
- [ ] Implementasikan `@RabbitListener` yang berjalan di atas **Java 25 Virtual Threads** dengan JSON message converter dan penanganan poison message ke DLQ.
- [ ] Implementasikan endpoint HTTP alternatif `POST /api/v1/logs/ingest` (mendukung korelasi header `X-Logia-Trace-Id`).
- [ ] Sempurnakan `StackTraceFingerprinter` untuk mengekstrak `exception_class`, `target_file`, dan `target_line` dari stack trace Java/Node/Python/Go.
- [ ] Implementasikan `SseLogBroadcasterAdapter` (`GET /api/v1/logs/stream`) lengkap dengan heartbeat keep-alive, penanganan klien putus, dan filter opsional pada stream.

## Kriteria Penerimaan (Acceptance Criteria)
1. Pesan log yang dipublikasikan ke RabbitMQ maupun `POST /api/v1/logs/ingest` tersimpan di `app_logs`, ter-agregasi di `error_aggregates`, dan muncul di stream SSE dalam `< 500 ms`.
2. Pesan rusak (malformed JSON) otomatis dialihkan ke `logialog.logs.dlq` tanpa menghentikan consumer.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 06: [FRONTEND] Upgrade Runtime Node.js v26.13.0, Migrasi TypeScript & Arsitektur Feature-Sliced Design (FSD)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #6 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Melakukan upgrade runtime frontend ke **Node.js v26.13.0**, migrasi kode dari JavaScript biasa (`.jsx`) ke **TypeScript ketat (`.ts` / `.tsx`)**, dan menata ulang arsitektur direktori menggunakan **Feature-Sliced Design (FSD)** sesuai **PRD Logia Log v3.1 (Bagian 03, 04.2 & 04.3)**.

## Spesifikasi Arsitektur FSD (`frontend/src/`)
- `app/`: Konfigurasi global, routing, providers, koneksi global SSE, dan tema.
- `pages/`: `LogExplorerPage`, `ApiTestingStudioPage`, `IncidentDeepAuditPage`, `TargetConnectionSettingsPage`.
- `widgets/`: `LiveLogStreamPanel`, `TimeSeriesHistogram`, `ApiRequestBuilder`, `ApiResponseViewer`, `GithubCodeDiffViewer`.
- `features/`: `AuthTypeSelector`, `HeaderKeyValueEditor`, `TriggerAiDeepAuditButton`, `LogFilterQueryBar`.
- `entities/`: Model TypeScript domain (`LogEntry`, `ApiCollection`, `TargetProfile`, `GithubDiagnosticReport`) & store state.
- `shared/`: Komponen UI atomik (`ui/`), HTTP API client (`api/`), utilitas (`lib/`), dan konstanta (`config/`).

## Scope Pengerjaan & Checklist
- [ ] Set spesifikasi `"engines": { "node": ">=26.13.0" }` pada `frontend/package.json` serta konfigurasi `tsconfig.json` / `tsconfig.app.json` dan `vite.config.ts` (dengan path alias `@/`).
- [ ] Konfigurasi ESLint untuk TypeScript + React 19 (`typescript-eslint`) dan Tailwind CSS v4 (`@tailwindcss/vite`).
- [ ] Migrasi `main.jsx` & `App.jsx` ke arsitektur FSD berbasis TypeScript (`src/app/entrypoint`, `src/pages`, `src/widgets`, `src/features`, `src/entities`, `src/shared`).
- [ ] Siapkan state management (`Zustand`) dan data fetching (`TanStack Query`) serta integrasi dasar ke backend `/api/v1`.

## Kriteria Penerimaan (Acceptance Criteria)
1. `npm run lint` dan `npm run build` (`tsc -b && vite build`) berjalan bersih tanpa error di atas **Node.js v26.13.0**.
2. Seluruh struktur lapisan FSD terbentuk rapi tanpa pelanggaran arah impor antar-layer (`shared -> entities -> features -> widgets -> pages -> app`).

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 07: [FRONTEND] Design System 60-30-10 Obsidian Black & Royal Gold dengan Sharp-Angled Tactical UI

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #7 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Mengimplementasikan identitas visual **Executive Cyber-Luxury Command Center** dengan aturan komposisi warna presisi **60-30-10 (Obsidian Black & Royal Gold)** serta geometri kontainer **Sharp-Angled Tactical UI** sesuai **PRD Logia Log v3.1 (Bagian 07.1 & 07.2)**.

## Spesifikasi Sistem Desain
- **60% Warna Dominan (Canvas & Surface)**:
  - Dark Mode (Default): `#0A0A0A` (*Obsidian Black*), `#121212` (*Elevated Surface*)
  - Light Mode (Opsional): `#FFFFFF` (*Pure White*), `#F7F7F7` (*Surface Canvas*)
- **30% Warna Sekunder Struktural (Royal Gold)**:
  - `#D4AF37` (*Metallic Gold*), `#C5A059` (*Muted Champagne Gold*), `#9A7B2C` (*Deep Gold Border*)
- **10% Warna Aksen Aktif (Luminous Highlight)**:
  - `#FFD700` (*Pure Luminous Gold*), `#F59E0B` (*Amber Gold Glow*), teks kontras `#0A0A0A`
- **Geometri Siku-Lancip**:
  - `border-radius: 2px` (atau `0px` pada tabel utama) ΓÇö tanpa sudut membulat besar
  - **Tactical Corner Accent**: Bingkai `1px solid rgba(212, 175, 55, 0.35)` dengan aksen siku tebal `2px solid #FFD700` sepanjang `10px` pada keempat sudut kontainer utama
- **Tipografi**: `Plus Jakarta Sans` / `Inter` (UI) + `JetBrains Mono` (Log stream, JSON, Code Diff)

## Scope Pengerjaan & Checklist
- [ ] Definisikan CSS Design Tokens & tema Tailwind v4 untuk palet 60-30-10 beserta toggle Dark Mode (default) / Light Mode.
- [ ] Bangun komponen atomik di `src/shared/ui/`:
  - `GoldFrameCard` (dengan 4 sudut *Tactical Corner Accent* `10px`)
  - `SharpButton` (`primary` `#FFD700`, `secondary` `#D4AF37` outline, `ghost`)
  - `TacticalTabs`, `SharpInput`, `StatusBadge`, dan `SeverityPill`
- [ ] Bangun layout navigasi utama (`CommandCenterShell` sidebar + top telemetry status bar) untuk berpindah antar 4 halaman utama Logia Log.

## Kriteria Penerimaan (Acceptance Criteria)
1. Komposisi warna di layar konsisten mengikuti rasio 60-30-10 dan kontras keterbacaan memenuhi standar aksesibilitas.
2. Seluruh kartu, tombol, input, dan tab menerapkan sudut tajam (`0px`ΓÇô`2px`) dan aksen siku emas pada panel utama.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 08: [BACKEND] REST API Log Search Multi-Dimensi, Regex Filtering & Time-Series Histogram (FR-LOG-02, FR-LOG-03)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #8 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Mengimplementasikan endpoint REST API pencarian log multi-dimensi dengan dukungan Regex serta agregasi metrik distribusi volume log berbasis waktu sesuai **PRD Logia Log v3.1 (FR-LOG-02, FR-LOG-03 & Bagian 08.2)**.

## Kontrak Endpoint
- `GET /api/v1/logs/search`: Pencarian log historis dengan filter `serviceName`, `levels` (`INFO`, `WARN`, `ERROR`, `FATAL`), `traceId`, `statusCode` (di `metadata` JSONB), rentang waktu (`from`, `to`), `regex` / keyword query, serta cursor/keyset pagination.
- `GET /api/v1/logs/histogram`: Agregasi jumlah log per bucket waktu berdasarkan tingkat keparahan untuk preset rentang `15m`, `1h`, `24h`, dan `7d`.

## Scope Pengerjaan & Checklist
- [ ] Implementasikan `LogSearchUseCase` dan query PostgreSQL 18.4 teroptimasi memanfaatkan partisi `created_at` dan `GIN Index` pada `metadata`.
- [ ] Tambahkan validasi pola Regex dengan proteksi timeout/panjang ekspresi agar aman dari *ReDoS (Regular Expression Denial of Service)*.
- [ ] Implementasikan query agregasi bucket waktu (`date_bin` di PostgreSQL 18.4) untuk `GET /api/v1/logs/histogram`.
- [ ] Tambahkan cache singkat (misal TTL 10-30 detik di Upstash Redis untuk rentang `24h`/`7d`) guna mempercepat respons dashboard metrik.
- [ ] Buat pengujian integrasi untuk pencarian kombinasi filter dan akurasi bucket histogram.

## Kriteria Penerimaan (Acceptance Criteria)
1. `GET /api/v1/logs/search` mendukung filter kombinasi `service`, `level`, `trace_id`, `metadata` JSONB, dan `regex` dengan waktu respons cepat.
2. `GET /api/v1/logs/histogram` mengembalikan deret waktu lengkap per level (`INFO`, `WARN`, `ERROR`, `FATAL`) untuk rentang `15m`, `1h`, `24h`, dan `7d`.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 09: [FRONTEND] Modul Grafana-Style Real-Time Log Explorer & Metrics Dashboard (FR-LOG-01..04)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #9 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Membangun antarmuka **Grafana-Style Real-Time Log Observability & Metrics Dashboard** (`LogExplorerPage`) pada frontend menggunakan arsitektur FSD dan desain 60-30-10 Obsidian Black & Royal Gold sesuai **PRD Logia Log v3.1 (Bagian 05.1: FR-LOG-01..04)**.

## Komponen FSD Terkait
- `pages/log-explorer/ui/LogExplorerPage.tsx`
- `widgets/live-log-stream/ui/LiveLogStreamPanel.tsx`
- `widgets/time-series-histogram/ui/TimeSeriesHistogram.tsx`
- `features/log-filter-query/ui/LogFilterQueryBar.tsx`

## Scope Pengerjaan & Checklist
- [ ] **FR-LOG-01 (`LiveLogStreamPanel`)**: Integrasi koneksi `EventSource` ke `GET /api/v1/logs/stream` dengan kontrol **Pause**, **Resume**, **Clear**, dan **Auto-Scroll**, serta virtualisasi baris agar tetap mulus pada ribuan log/detik.
- [ ] **FR-LOG-02 (`TimeSeriesHistogram`)**: Grafik batang interaktif (setara Grafana Loki) yang memvisualisasikan distribusi volume log berdasarkan level (`INFO`, `WARN`, `ERROR`, `FATAL`) pada rentang `15m`, `1h`, `24h`, dan `7d` (mendukung klik/drag rentang waktu untuk memfilter log).
- [ ] **FR-LOG-03 (`LogFilterQueryBar`)**: Bar pencarian multi-dimensi berdasarkan nama service, level log, `Trace ID`, HTTP status code, dan pola **Regex**.
- [ ] **FR-LOG-04 (Deduplication Badges)**: Tampilkan **Occurrence Badge** (`xN`) dan label peringatan **Recurring Anomaly** pada baris error berulang beserta tombol pintas menuju halaman *Incident Deep Audit*.

## Kriteria Penerimaan (Acceptance Criteria)
1. Stream log masuk tampil seketika di UI tanpa lag, dapat di-*pause* dan dilanjutkan kembali tanpa kehilangan konteks.
2. Filter `Trace ID` (termasuk navigasi langsung dari modul API Testing Studio) menyaring log terkait secara akurat.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 10: [BACKEND] Engine Proxy Eksekusi API, Manajemen Koleksi & Auto Trace-ID Correlation (FR-API-01..07)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #10 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Membangun backend engine eksekusi HTTP Proxy setara Postman di atas **Java 25 Virtual Threads**, manajemen koleksi pengujian API, dan injeksi otomatis `X-Logia-Trace-Id` sesuai **PRD Logia Log v3.1 (Bagian 05.2: FR-API-01..07 & Bagian 08.2)**.

## Kontrak Endpoint & SLA
- `POST /api/v1/proxy/execute`: Mengeksekusi HTTP request ke endpoint target mana pun dengan **overhead latensi `< 15 ms`** (SLA Bagian 10.1).
- `CRUD /api/v1/collections/**`: Mengelola koleksi, folder, variabel environment (`{{BASE_URL}}`), dan template request API di PostgreSQL 18.4 (`api_test_collections` & `api_test_requests`).

## Scope Pengerjaan & Checklist
- [ ] Implementasikan `ApiExecutionProxyUseCase` & Outbound Adapter berbasis Java 25 `HttpClient` / Spring `RestClient` untuk mendukung method `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, dan `OPTIONS`.
- [ ] Dukungan penuh 5 tipe autentikasi (**FR-API-03**):
  1. `NO_AUTH`
  2. `BEARER_TOKEN` (`Authorization: Bearer <token>`)
  3. `BASIC_AUTH` (Base64 `username:password`)
  4. `API_KEY` (sisip ke Header atau Query Params)
  5. `OAUTH2` (pengambilan token via *Client Credentials* / *Authorization Code*)
- [ ] Dukungan multi-format payload (**FR-API-05**): `none`, `form-data` (multipart), `x-www-form-urlencoded`, `raw` (`JSON`/`XML`/`Text`/`HTML`), dan `binary`.
- [ ] **FR-API-07**: Suntikkan header unik `X-Logia-Trace-Id` secara otomatis pada setiap eksekusi dan rekam metrik (`status_code`, `duration_ms`, `response_size_bytes`, `response_headers`, `response_body`) ke tabel `api_test_histories`.
- [ ] Implementasikan endpoint CRUD koleksi & riwayat eksekusi pengujian API.

## Kriteria Penerimaan (Acceptance Criteria)
1. Proxy berhasil mengeksekusi berbagai kombinasi Method, 5 tipe Auth, Custom Headers, dan Body dengan overhead internal `< 15 ms`.
2. Setiap respons dari `/api/v1/proxy/execute` menyertakan `traceId` yang dapat dikorelasikan langsung ke tabel `app_logs`.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 11: [FRONTEND] Modul Postman-Style Integrated API Testing & Debugging Studio (FR-API-01..07)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #11 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Membangun ruang kerja pengujian API interaktif setara Postman (`ApiTestingStudioPage`) langsung di dalam Logia Log dengan korelasi otomatis ke log server & diagnosis AI sesuai **PRD Logia Log v3.1 (Bagian 05.2: FR-API-01..07)**.

## Komponen FSD Terkait
- `pages/api-testing-studio/ui/ApiTestingStudioPage.tsx`
- `widgets/api-request-builder/ui/ApiRequestBuilder.tsx`
- `widgets/api-response-viewer/ui/ApiResponseViewer.tsx`
- `features/auth-type-selector/ui/AuthTypeSelector.tsx`
- `features/header-kv-editor/ui/HeaderKeyValueEditor.tsx`

## Scope Pengerjaan & Checklist
- [ ] **FR-API-01**: Selector HTTP Method (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS`) + input URL dengan interpolasi variabel environment dinamis `{{BASE_URL}}/api/v1/...`.
- [ ] **FR-API-02**: Tabel interaktif Query Parameters & Path Variables (checkbox aktif/nonaktif, deskripsi, dan sinkronisasi dua arah otomatis dengan string URL).
- [ ] **FR-API-03**: Panel `AuthTypeSelector` untuk 5 tipe autentikasi (*No Auth*, *Bearer Token JWT*, *Basic Auth*, *API Key*, *OAuth 2.0*).
- [ ] **FR-API-04**: Tabel `HeaderKeyValueEditor` dengan auto-complete HTTP header standar dan mode *Bulk Edit*.
- [ ] **FR-API-05**: Editor Request Body multi-format termasuk integrasi **Monaco Editor** untuk payload `raw` (`JSON`, `XML`, `Text`, `HTML`) lengkap dengan tombol *Beautify / Format*.
- [ ] **FR-API-06**: Panel `ApiResponseViewer` menampilkan Status Code Badge (`200 OK`, `400`, `500`), Waktu Eksekusi (`ms`), Ukuran Payload (`KB`), tab *Pretty / Raw / Preview*, dan *Response Headers*.
- [ ] **FR-API-07**: Sidebar manajemen Koleksi & History + tombol aksi kontekstual **`Lihat Log & Diagnosis AI untuk Request Ini`** ketika respons error terdeteksi pada `X-Logia-Trace-Id` terkait.

## Kriteria Penerimaan (Acceptance Criteria)
1. Pengguna dapat membuat koleksi, menyusun request lengkap, menekan **SEND REQUEST**, dan menginspeksi hasil respons tanpa meninggalkan Logia Log.
2. Saat terjadi kegagalan HTTP `4xx`/`5xx` dari layanan yang terhubung, tombol korelasi langsung membuka log dan diagnosis berdasarkan `X-Logia-Trace-Id`.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 12: [BACKEND/FRONTEND] Manajemen Target Profile Dinamis (Target-Agnostic) & Read-Only DB Schema Inspector (FR-DSI-01, FR-DSI-02)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #12 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Mengimplementasikan pengelolaan **Target Profile Dinamis (Target-Agnostic / Plug-and-Play)** beserta ekstraktor metadata DDL database target secara *Strictly Read-Only* sesuai **PRD Logia Log v3.1 (Bagian 05.3: FR-DSI-01, FR-DSI-02, Bagian 06.1 & 08.2)**.

## Kontrak Endpoint & Komponen
- Backend: `CRUD /api/v1/targets/**`, `POST /api/v1/targets/{id}/test-connection`, `DynamicSchemaFetchUseCase`, `TargetDbReadOnlyInspectorAdapter`
- Frontend: `TargetConnectionSettingsPage` (`pages/target-settings`)

## Scope Pengerjaan & Checklist
- [ ] Pastikan sistem berjalan mandiri secara default walaupun tabel `target_profiles` masih kosong (*Target-Agnostic*).
- [ ] Implementasikan `CRUD /api/v1/targets/**` untuk mendaftarkan/mengubah `service_name`, `base_api_url`, `github_repo_url`, `github_branch`, dan `readonly_db_jdbc_url` (opsional) kapan saja tanpa restart aplikasi.
- [ ] **FR-DSI-01 (Regex SQL & Table Extraction)**: Bangun parser di layer `domain` untuk mengekstrak nama tabel, nama kolom, constraint, dan kode error database (misal PostgreSQL `SQLState 22001`, `23505`, `42P01`, `23503`) dari stack trace error.
- [ ] **FR-DSI-02 (Optional Read-Only Metadata Querying)**: Implementasikan konektor JDBC dinamis dengan proteksi `connection.setReadOnly(true)` dan `SET SESSION CHARACTERISTICS AS TRANSACTION READ ONLY` untuk membaca struktur kolom, tipe data, indeks, dan constraint melalui `information_schema` / `pg_catalog`.
- [ ] Bangun antarmuka `TargetConnectionSettingsPage` untuk menambah, menguji koneksi, mengaktifkan/menonaktifkan, atau mengosongkan profil target.

## Kriteria Penerimaan (Acceptance Criteria)
1. Logia Log tetap beroperasi penuh meskipun belum ada Target Profile yang didaftarkan.
2. Ketika koneksi database target dikonfigurasi, sistem berhasil mengekstrak snapshot DDL tabel terkait error SQL secara otomatis dan menolak segala operasi non-SELECT.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 13: [BACKEND] Pipeline PII/Sensitive Data Masking & Recurring Error Threshold Trigger via Upstash Redis (FR-DSI-03, FR-DSI-04)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #13 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Mengimplementasikan pipeline penyensoran data sensitif (**PII Masking**) berlapis serta orkestrasi pemicu otomatis **Deep GitHub Code Analysis** saat error berulang melewati ambang batas sesuai **PRD Logia Log v3.1 (Bagian 05.3: FR-DSI-03, FR-DSI-04 & Bagian 09)**.

## Scope Pengerjaan & Checklist
- [ ] **FR-DSI-03 (Layered PII & Sensitive Data Masking)**: Perluas `SensitiveDataSanitizer` di layer `domain` untuk menyensor:
  - Nomor Induk Kependudukan (NIK 16 digit) & nomor kartu pembayaran (Luhn check)
  - Token JWT (`eyJ...`), API Key, Bearer Token, Basic Auth header
  - Field `password`, `secret`, `pin`, `cvv`, `otp`, serta nominal transaksi rahasia sesuai aturan regex menjadi `[REDACTED]` sebelum prompt dikirim ke LLM eksternal.
- [ ] **FR-DSI-04 (Recurring Error Threshold Trigger)**: Implementasikan `RecurringErrorDetectionUseCase` berbasis sliding/tumbling window di **Upstash Redis SaaS** (default: `>= 3` kemunculan dalam `5 menit` untuk `fingerprint_hash` yang sama, dapat dikonfigurasi).
- [ ] Integrasikan event internal (`RecurringAnomalyDetectedEvent` di atas Virtual Threads) untuk mengaktifkan antrean analisis kode GitHub secara asinkron tanpa memblokir pipeline ingesti log.
- [ ] Terapkan rate-limiter/cooldown per `fingerprint_hash` di Upstash Redis (default `15 menit`) agar kuota LLM & GitHub API terlindungi saat terjadi *Infinite Loop Log Flood*.

## Kriteria Penerimaan (Acceptance Criteria)
1. Unit test komprehensif membuktikan seluruh variasi PII, password, kartu, dan JWT berubah menjadi `[REDACTED]` pada payload prompt.
2. Error yang muncul `>= 3` kali dalam 5 menit memicu workflow analisis tepat **1 kali** dalam jendela cooldown 15 menit.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 14: [BACKEND] Konektor GitHub API & 7-Layer AI Deep Code Resolution Engine (Modul 4)

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #14 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Membangun fitur unggulan Logia Log: **GitHub Repository Source Fetcher** dan **7-Layer AI Resolution Engine** yang memadukan stack trace tersanitasi, DDL database target, kode sumber asli GitHub, serta file konfigurasi build untuk menghasilkan solusi perbaikan tingkat produksi dalam `< 5 detik` sesuai **PRD Logia Log v3.1 (Bagian 05.4, 08.2 & 10.1)**.

## Kontrak Endpoint
- `POST /api/v1/diagnostics/{hash}/analyze-github`: Memicu penarikan kode GitHub + DDL DB dan menjalankan analisis AI 7 dimensi.
- `GET /api/v1/diagnostics/{hash}/report`: Mengambil laporan hasil analisis 7 dimensi beserta rekomendasi patch diff dari PostgreSQL 18.4 (`github_code_audits`) / cache Upstash Redis.

## Cakupan 7 Dimensi Analisis Wajib
1. **Perbaikan Sintaks (*Syntax & Annotations*)**: Kelengkapan Bean Validation (`@NotBlank`, `@Size`), kecocokan DTO vs Entity, fitur bahasa Java 25.
2. **Perbaikan Query (*Query & DB Tuning*)**: Audit JPQL/Native SQL, kecocokan panjang kolom DDL PostgreSQL 18.4, deteksi *N+1 Query*, dan rekomendasi `CREATE INDEX`.
3. **Perbaikan Logic (*Business Logic & Edge Cases*)**: Null-safety, *boundary conditions*, serta akurasi desimal (`BigDecimal` vs `Double`).
4. **Analisis Versi (*Version Compatibility*)**: Pemeriksaan `pom.xml` / `build.gradle` / `package.json` terhadap Java 25.0.5.0.2 LTS, Spring Boot 4.1.2 / Jakarta EE 11, PostgreSQL 18.4, dan API deprecated.
5. **Perbaikan Flow (*Execution & Transaction Flow*)**: Evaluasi batas `@Transactional` (mencegah HTTP I/O eksternal mengunci koneksi HikariCP).
6. **Deteksi Memory Leak (*Resource & Memory Safety*)**: Deteksi stream tanpa `try-with-resources`, koleksi `static` tanpa batas, kebocoran `ThreadLocal` pada Virtual Threads, dan pool koneksi.
7. **Analisis Security (*Security Vulnerability Audit*)**: Audit celah SQL Injection, *Mass Assignment*, validasi JWT/Spring Security, dan kebocoran stack trace ke publik.

## Scope Pengerjaan & Checklist
- [ ] Implementasikan `GithubSourceAdapter` (GitHub REST / GraphQL API v4) menggunakan **Java 25 Structured Concurrency / Virtual Threads** untuk menarik paralel: (a) file kode sumber pada baris stack trace terkait beserta konteks sekitarnya, dan (b) file manifes build (`pom.xml` / `package.json`).
- [ ] Rakit `SevenLayerPromptBuilder` di layer `domain` dengan skema JSON output terstruktur untuk ke-7 dimensi analisis + unified `git diff` patch.
- [ ] Implementasikan `AiDiagnosticAdapter` (mendukung Gemini 2.5 Pro / LLM kompatibel) dan simpan laporan ke tabel `github_code_audits` serta cache Upstash Redis.

## Kriteria Penerimaan (Acceptance Criteria)
1. Total waktu diagnosis end-to-end (fetch DDL + fetch GitHub paralel + inferensi AI terstruktur) memenuhi target SLA `< 5 detik` pada kondisi jaringan normal.
2. Output laporan JSON memuat ke-7 dimensi secara lengkap beserta `suggested_patch_diff` yang valid dan berstatus *Advisory / Human-in-the-Loop*.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 15: [FRONTEND] Halaman Incident Deep Audit, 7-Layer AI Resolution View & Monaco Patch Diff Viewer

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #15 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Membangun halaman **Incident Deep Audit & 7-Layer AI Resolution View** (`IncidentDeepAuditPage`) beserta penampil kode **Monaco Side-by-Side Diff** dengan prinsip *Human-in-the-Loop* sesuai **PRD Logia Log v3.1 (Bagian 04.2, 05.4, 07 & 09)**.

## Komponen FSD Terkait
- `pages/incident-deep-audit/ui/IncidentDeepAuditPage.tsx`
- `widgets/github-code-diff/ui/GithubCodeDiffViewer.tsx`
- `features/trigger-ai-audit/ui/TriggerAiDeepAuditButton.tsx`
- `entities/diagnostic-report/model/types.ts`

## Scope Pengerjaan & Checklist
- [ ] Bangun `TriggerAiDeepAuditButton` (`ANALYZE GITHUB CODE`) dengan indikator progres tahapan real-time (*Extracting SQL/DDL -> Fetching GitHub Branch/Commit -> Running 7-Layer AI Engine*).
- [ ] Bangun tampilan terstruktur untuk **7 Dimensi Analisis AI**:
  1. *Syntax & Annotations*
  2. *Query & DB Tuning*
  3. *Business Logic & Edge Cases*
  4. *Version Compatibility (Java 25 / Spring Boot 4.1.2 / Node 26.13)*
  5. *Execution & Transaction Flow*
  6. *Memory Leak & Resource Safety*
  7. *Security Vulnerability Audit*
- [ ] Tampilkan panel **Dynamic DB Schema Snapshot** (DDL tabel terkait dari PostgreSQL 18.4 `information_schema` jika profil target terhubung) dan metadata commit GitHub (`repo_url`, `branch`, `commit_sha`).
- [ ] Integrasikan `GithubCodeDiffViewer` berbasis **Monaco DiffEditor** untuk menampilkan perbandingan *Original Code* vs *Suggested Fix* lengkap dengan tombol *Copy Patch Diff* / *Export PR Draft* (wajib melalui persetujuan manusia / *Advisory Mode*).

## Kriteria Penerimaan (Acceptance Criteria)
1. Pengguna dapat menavigasi dari baris error berulang di *Log Explorer* maupun *API Testing Studio* langsung ke laporan 7 dimensi terkait.
2. Monaco DiffEditor merender kode dengan font `JetBrains Mono`, tema Obsidian Black & Royal Gold, dan aksi penyalinan patch berjalan mulus.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

### Prompt 16: [SECURITY/QA] Pengamanan SSRF Guard, Enkripsi Kredensial AES-256-GCM, Performance SLA & E2E Testing

```markdown
Anda adalah Senior/Principal Software Engineer yang bertugas mengimplementasikan GitHub Issue #16 pada proyek Logia Log.

### Konteks & Sasaran
## Ringkasan Task
Melaksanakan pengerasan keamanan menyeluruh (**DevSecOps**), enkripsi kredensial sensitif, perlindungan **SSRF**, validasi target **SLA performa**, serta pengujian integrasi end-to-end sesuai **PRD Logia Log v3.1 (Bagian 06.2, 09, 10.1 & 10.2 - Fase 4)**.

## Mitigasi Risiko & Target SLA
- **Proteksi SSRF (`ApiExecutionProxyUseCase`)**: Mencegah penyalahgunaan API Tester untuk menembak IP internal terlarang, loopback tidak sah, link-local, dan cloud metadata (`169.254.169.254`), disertai domain whitelist opsional dan batas timeout ketat.
- **Enkripsi Kolom Sensitif (`AES-256-GCM`)**: Mengenkripsi `auth_config_enc`, `github_token_encrypted`, dan `readonly_db_pass_enc` sebelum disimpan ke PostgreSQL 18.4 dengan IV acak per record.
- **Target KPI / SLA**:
  - Latensi Ingesti ke SSE `< 500 ms (p95)`
  - Overhead Eksekusi API Proxy `< 15 ms`
  - Waktu Diagnosis End-to-End 7-Layer GitHub AI `< 5 detik`

## Scope Pengerjaan & Checklist
- [ ] Implementasikan `SsrfProtectionValidator` (resolusi DNS + blokir blok CIDR privat/link-local `169.254.0.0/16`, `127.0.0.0/8` kecuali diizinkan eksplisit pada mode dev lokal) pada modul API Proxy.
- [ ] Implementasikan `AesGcmAttributeEncryptor` (`AES/GCM/NoPadding`, 256-bit key, 96-bit IV, 128-bit auth tag) untuk seluruh kredensial tersimpan di database.
- [ ] Buat suite pengujian integrasi (Testcontainers PostgreSQL 18.4 + RabbitMQ + Mock GitHub/LLM) dan benchmark verifikasi ke-3 metrik SLA.
- [ ] Siapkan panduan pengujian demonstrasi terhadap salah satu aplikasi sandbox rekomendasi PRD Bagian 06.2 (misal *Spring PetClinic REST* atau *Chaos E-Commerce Sandbox*) serta dokumentasi rilis v3.1.

## Kriteria Penerimaan (Acceptance Criteria)
1. Percobaan request API Proxy ke `http://169.254.169.254/latest/meta-data/` langsung ditolak (`403 Forbidden - SSRF Blocked`).
2. Tidak ada token GitHub, password DB target, maupun token koleksi API yang tersimpan dalam bentuk plaintext di tabel PostgreSQL 18.4.
3. Seluruh pengujian unit, integrasi, dan benchmark SLA lulus hijau.

### Instruksi Eksekusi Kode
1. Analisis arsitektur dan file terkait sebelum mengubah kode.
2. Pastikan mengikuti Clean Hexagonal Architecture (backend) atau Feature-Sliced Design (frontend).
3. Backend harus tetap kompatibel dengan Java 25.0.5.0.2 LTS & Spring Boot 4.1.2.
4. Frontend harus tetap kompatibel dengan Node.js 26.13.0 & TypeScript strict mode.
5. Jaga kerahasiaan credential: jangan men-commit file .env atau secret ke git.
6. Jalankan pengujian unit/integrasi untuk memastikan acceptance criteria terpenuhi 100%.
```

---

