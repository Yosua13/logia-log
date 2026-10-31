# SentinelAI v3.1 (Target-Agnostic Enterprise Edition)

**Unified Real-Time Observability (Setara Grafana) • Interactive API Testing Studio (Setara Postman) • Dynamic DB Schema Injection • 7-Layer Deep GitHub AI Diagnostic Platform**

---

## 1. Spesifikasi Tech Stack Standar 2026

| Layer | Teknologi & Versi Eksak | Arsitektur & Peran |
| :--- | :--- | :--- |
| **Java Runtime (JVM)** | **Java `25.0.5.0.2` LTS** (`2026-10-21`, HotSpot 64-Bit Server VM `build 25.0.5.0.2+1-LTS-2`) | Project Loom **Virtual Threads** (`spring.threads.virtual.enabled=true`) untuk konkurensi non-blocking SSE, RabbitMQ & HTTP Proxy |
| **Backend Framework** | **Spring Boot `4.1.2`** (Spring Framework 7 / Jakarta EE 11) | **Clean Hexagonal Architecture (Ports & Adapters)** + Event-Driven Architecture |
| **Database Utama** | **PostgreSQL `18.4`** (`psql (PostgreSQL) 18.4`) | Native `uuidv7()`, Range-Partitioned `app_logs`, dan `GIN Index` pada `JSONB` metadata (Flyway V1) |
| **Message Broker** | **RabbitMQ `4.x`** (`rabbitmq:4-management-alpine`) | Penahan lonjakan trafik log (*spike absorber*) lengkap dengan Dead-Letter Exchange (`DLX`) & `DLQ` |
| **Managed Redis SaaS** | **Upstash Serverless Redis (Free Tier)** | Koneksi TLS `rediss://` (256 MB RAM, 500K commands/bulan gratis, region AWS `ap-southeast-1` Singapore) untuk deduplikasi error, rate-limit AI, dan cache |
| **Frontend Runtime** | **Node.js `v26.13.0`** (React 19 + TypeScript + Vite 8 + Tailwind CSS v4) | **Feature-Sliced Design (FSD)** dengan sistem desain **60-30-10 Obsidian Black & Royal Gold** (*Sharp-Angled Tactical UI*) |

---

## 2. Struktur Monorepo

```text
logia-log/
├── backend/                                      # Java 25.0.5.0.2 LTS + Spring Boot 4.1.2 (Hexagonal)
│   ├── src/main/java/com/sentinelai/
│   │   ├── adapter/
│   │   │   ├── in/web/                           # REST Controllers & SSE Streamer (/api/v1/**)
│   │   │   ├── in/messaging/                     # RabbitMQ Consumer (Virtual Threads)
│   │   │   └── out/
│   │   │       ├── persistence/                  # PostgreSQL 18.4 JPA/JDBC Adapters
│   │   │       ├── redis/                        # Upstash Serverless Redis Deduplication Adapter
│   │   │       ├── sse/                          # Real-Time Server-Sent Events Broadcaster
│   │   │       ├── targetdb/                     # Dynamic Read-Only DDL Inspector
│   │   │       ├── apiproxy/                     # Postman-Style HTTP Proxy Engine
│   │   │       ├── github/                       # GitHub REST/GraphQL Source Fetcher
│   │   │       └── ai/                           # 7-Layer AI Diagnostic Client
│   │   ├── application/
│   │   │   ├── port/in/                          # Inbound Use Case Interfaces
│   │   │   ├── port/out/                         # Outbound Infrastructure Ports
│   │   │   └── usecase/                          # Application Orchestration Services
│   │   ├── domain/
│   │   │   ├── model/                            # Pure Domain Records & Enums
│   │   │   └── service/                          # SHA-256 Fingerprinter & PII Sanitizer
│   │   └── config/                               # Security, RabbitMQ DLQ, Upstash Redis & Props
│   ├── .env.example                                  # Template environment terpisah untuk backend
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/V1__init_sentinel_schema.sql
├── frontend/                                     # Node.js v26.13.0 + React 19 + TypeScript (FSD)
│   ├── .env.example                                  # Template environment terpisah untuk frontend
│   └── src/
│       ├── app/                                  # App Shell & 60-30-10 CSS Design Tokens
│       ├── pages/                                # LogExplorer, ApiStudio, IncidentAudit, TargetSettings
│       ├── widgets/                              # CommandHeader, LiveLogStreamPanel, ApiRequestBuilder
│       ├── features/                             # LogFilterQueryBar, AuthTypeSelector, TriggerAiAudit
│       ├── entities/                             # Domain TypeScript Interfaces
│       └── shared/                               # GoldFrameCard, SharpButton, TacticalTabs, API Client
├── target-simulation/                            # [KOSONG / AGNOSTIK] Slot opsional sandbox masa depan
├── docs/                                         # Dokumen PRD SentinelAI v3.1 Enterprise Edition
└── docker-compose.yml                            # PostgreSQL 18.4 & RabbitMQ 4.x (membaca backend/.env)
```

---

## 3. Panduan Menjalankan Proyek

Dokumentasi lengkap cara menjalankan, menguji, dan mem-build masing-masing bagian tersedia di dalam folder terkait:
- **Panduan Lengkap Backend**: [`backend/README.md`](./backend/README.md)
- **Panduan Lengkap Frontend**: [`frontend/README.md`](./frontend/README.md)

### Ringkasan Cepat (Quick Start)

#### A. Konfigurasi `JAVA_HOME` (Java 25.0.5.0.2 LTS)
Pastikan `JAVA_HOME` mengarah ke instalasi JDK 25 (`maven-enforcer-plugin` mewajibkan Java `>= 25`):
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.5.0.2"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

#### B. Setup Environment File Terpisah (Backend & Frontend)
Salin masing-masing template environment ke dalam foldernya:
```powershell
# Setup Environment Backend
cd d:\project_yosua\logia-log\backend
Copy-Item .env.example .env

# Setup Environment Frontend
cd ..\frontend
Copy-Item .env.example .env
```

#### C. Setup Upstash Redis SaaS (Gratis)
1. Buka [https://console.upstash.com/](https://console.upstash.com/) dan masuk menggunakan akun GitHub/Google (tanpa kartu kredit).
2. Klik **Create Database** -> pilih **Regional: AWS `ap-southeast-1` (Singapore)** -> pilih plan **Free** (256 MB, 500K commands/bulan).
3. Salin URL koneksi **TLS (`rediss://`)** lalu masukkan ke file `backend/.env`:
   ```env
   UPSTASH_REDIS_URL=rediss://default:<PASSWORD>@<ENDPOINT>.upstash.io:6379
   SENTINEL_REDIS_DEDUP_ENABLED=true
   ```

#### D. Menjalankan Infrastruktur, Backend & Frontend
```powershell
# 1. Jalankan PostgreSQL 18.4 & RabbitMQ 4 (otomatis membaca backend/.env)
cd d:\project_yosua\logia-log
docker compose up -d

# 2. Jalankan Backend (Spring Boot 4.1.2 + Java 25)
cd backend
.\mvnw.cmd spring-boot:run

# 3. Jalankan Frontend (Node.js v26.13.0 + Vite 8)
cd ..\frontend
npm run dev
```

