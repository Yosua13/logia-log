# Analisis PRD Logia Log v3.1, Arsitektur Enterprise & Peta Pengerjaan GitHub Issues

Dokumen ini merangkum hasil analisis menyeluruh terhadap dokumen PRD Logia Log v3.1 Enterprise Edition ([PRD_SentinelAI_v3_Enterprise_Edition-v2.docx](file:///d:/project_yosua/logia-log/docs/PRD_SentinelAI_v3_Enterprise_Edition-v2.docx)), pembaruan tech stack ke versi terbaru pada workspace, pemilihan **Redis SaaS gratis terbaik (Upstash)**, struktur arsitektur backend & frontend, serta daftar **16 GitHub Issues** yang telah dibuat terpisah pada repositori `Yosua13/logia-log`.

> [!IMPORTANT]
> Pada environment Windows Anda, JDK `25.0.5.0.2` terpasang di `C:\Program Files\Java\jdk-25.0.5.0.2`. Script Maven Wrapper (`mvnw.cmd` dan `mvnw`) telah dikonfigurasi dengan deteksi otomatis sehingga setiap eksekusi Maven langsung menggunakan JDK 25 LTS secara native.

---

## 1. Hasil Analisis Dokumen PRD Logia Log v3.1 (Target-Agnostic Edition)

Berdasarkan ekstraksi penuh 10 bagian dokumen PRD v3.1, **Logia Log** bertransformasi dari sekadar middleware pencatat error menjadi **Unified Super-Observability & Developer Workspace** yang menggabungkan tiga kapabilitas utama secara **Target-Agnostic (Plug-and-Play)**:

| Pilar Utama | Cakupan Fungsional (Kode PRD) | Target SLA / KPI |
| :--- | :--- | :--- |
| **1. Grafana-Style Log & Metrics Explorer** | Live tail SSE/WebSocket (`FR-LOG-01`), Time-Series Histogram `15m/1h/24h/7d` (`FR-LOG-02`), Multi-dimensi & Regex Search (`FR-LOG-03`), SHA-256 Stack Trace Deduplication (`FR-LOG-04`) | Latensi Ingesti ke UI `< 500 ms (p95)` |
| **2. Postman-Style API Testing Studio** | Builder 7 HTTP Method & `{{ENV}}` (`FR-API-01`), Sync Params (`FR-API-02`), 5 Auth Types (`FR-API-03`), Header Editor (`FR-API-04`), Multi-Body + Monaco (`FR-API-05`), Response Inspector (`FR-API-06`), Auto `X-Logia-Trace-Id` Correlation (`FR-API-07`) | Overhead Proxy `< 15 ms` |
| **3. Dynamic Schema + 7-Layer GitHub AI Engine** | Regex SQL/Table Parser (`FR-DSI-01`), Optional Read-Only DDL via `information_schema` (`FR-DSI-02`), Layered PII Masking `[REDACTED]` (`FR-DSI-03`), Recurring Threshold Trigger (`FR-DSI-04`), dan **7-Layer Code Audit** (Sintaks, Query, Logic, Versi, Flow, Memory Leak, Security) | End-to-End Diagnosis `< 5 detik` |

### Karakteristik Kunci dari PRD v3.1
- **Target-Agnostic (`target-simulation/` dikosongkan)**: Tidak ada ketergantungan kode ke aplikasi simulasi tertentu. Pengguna dapat mengarahkan API Studio, stream log RabbitMQ/HTTP, repositori GitHub, dan koneksi database Read-Only ke aplikasi mana pun kapan saja lewat UI Settings (`target_profiles`).
- **Identitas Visual 60-30-10 & Sharp-Angled Tactical UI**:
  - **60% Dominan**: `#0A0A0A` (*Obsidian Black*) & `#121212` (*Elevated Surface*) — dengan opsi Light Mode `#FFFFFF` / `#F7F7F7`.
  - **30% Sekunder Struktural**: `#D4AF37` (*Metallic Gold*), `#C5A059` (*Champagne Gold*), `#9A7B2C` (*Deep Gold Border*).
  - **10% Aksen Aktif**: `#FFD700` (*Pure Luminous Gold*) & `#F59E0B` (*Amber Glow*).
  - **Geometri Presisi**: Radius tajam `2px` (`0px` pada tabel) + aksen siku `10px` emas tebal pada keempat sudut panel utama.
- **DevSecOps Wajib**: Proteksi **SSRF** pada API Proxy (blokir `169.254.169.254` & jaringan internal terlarang), enkripsi **AES-256-GCM** untuk token tersimpan, sanitasi PII berlapis sebelum dikirim ke LLM, serta mode *Advisory / Human-in-the-Loop* untuk patch kode.

---

## 2. Pemilihan Redis SaaS Terbaik & Gratis: Upstash Serverless Redis

Sesuai kebutuhan menggunakan **Managed Redis SaaS gratis** tanpa menjalankan container Redis lokal, pilihan terbaik untuk arsitektur Spring Boot 4.1.2 adalah **Upstash Serverless Redis (Free Tier)**:

| Kriteria Evaluasi | Upstash Redis (Dipilih) | Redis Cloud (Free Tier) |
| :--- | :--- | :--- |
| **Kapasitas Memori Gratis** | **256 MB** (`\$0` selamanya, tanpa kartu kredit) | Hanya **30 MB** |
| **Kuota & Throughput** | **500.000 commands / bulan** gratis | Dibatasi keras 100 ops/detik |
| **Batas Koneksi Bersamaan** | Elastis (*Serverless connection handling*) | Batas keras **30 koneksi** (rawan pool client timeout) |
| **Protokol & Keamanan** | Native **TLS `rediss://`** + REST API | Native TLS `rediss://` |
| **Region Latensi Rendah (ID)** | **AWS `ap-southeast-1` (Singapore)** | Terbatas pada region global free |

> [!TIP]
> Pada `application.yml` dan `UpstashErrorDeduplicationAdapter`, pool koneksi Lettuce disetel konservatif (`max-active: 8`, `min-idle: 0`), polling PING Actuator dinonaktifkan, dan counter deduplikasi hanya mengeksekusi `EXPIRE` saat hit pertama (`count == 1`) agar kuota 500K command/bulan sangat awet.

---

## 3. Struktur Arsitektur Backend & Frontend

```
d:\project_yosua\logia-log\
├── backend/                                      # Clean Hexagonal Architecture (Java 25 LTS, Spring Boot 4.1.2)
│   ├── src/main/java/com/logialog/
│   │   ├── domain/                               # Pure Domain Records & Services
│   │   ├── application/                          # In/Out Ports & Use Cases
│   │   ├── adapter/in/                           # REST Controllers & RabbitMQ Consumers
│   │   ├── adapter/out/                          # Persistence, Redis, SSE, GitHub, AI
│   │   └── config/                               # Security, RabbitMQ, Upstash Redis
│   └── src/main/resources/
│       ├── application.yml                       # Multi-profile & env configuration
│       └── db/migration/                         # Flyway SQL migrations (PostgreSQL 18.4)
├── frontend/                                     # Feature-Sliced Design (React 19, TypeScript, Vite)
│   ├── src/
│   │   ├── app/                                  # Providers & Styles (60-30-10 Gold Theme)
│   │   ├── pages/                                # Route Views (Log Explorer, API Studio, Settings)
│   │   ├── widgets/                              # Header, Request Builder, Live Stream
│   │   ├── features/                             # Query Filter, Environment Switcher
│   │   ├── entities/                             # Domain TypeScript Types
│   │   └── shared/                               # GoldFrameCard, SharpButton, API Client
│   └── index.html
├── docs/                                         # PRD, Architecture Blueprint & Prompts
├── target-simulation/                            # Slot opsional sandbox target-agnostik
└── docker-compose.yml                            # PostgreSQL 18.4 & RabbitMQ 4
```

---

## 4. Roadmap & Milestone Pengerjaan (GitHub Issues #1 s/d #16)

- **Fase 1: Fondasi, Infrastruktur & Log Ingestion** (Issues #1 s/d #6)
- **Fase 2: UI 60-30-10, Log Explorer & API Studio** (Issues #7 s/d #11)
- **Fase 3: Dynamic Schema & 7-Layer GitHub AI** (Issues #12 s/d #15)
- **Fase 4: DevSecOps Hardening, SLA & Final QA** (Issue #16)
