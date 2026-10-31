# SentinelAI v3.1 — Backend Service

Layanan inti **SentinelAI v3.1 (Target-Agnostic Enterprise Edition)** yang dibangun menggunakan **Clean / Hexagonal Architecture (Ports & Adapters)** dan **Event-Driven Architecture**.

---

## 1. Spesifikasi Stack & Prasyarat

Pastikan perangkat Anda memiliki komponen berikut sebelum menjalankan backend:

| Komponen | Versi Wajib | Keterangan |
| :--- | :--- | :--- |
| **Java JDK** | **Java `25.0.5.0.2` LTS** (`HotSpot 64-Bit Server VM`) | Diverifikasi otomatis oleh `maven-enforcer-plugin` (`[25,)`) |
| **Framework** | **Spring Boot `4.1.2`** | Sudah dibundel melalui Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| **Database** | **PostgreSQL `18.4`** (`psql 18.4`) | Mendukung native `uuidv7()`, tabel partisi, dan indeks `GIN JSONB` |
| **Message Broker** | **RabbitMQ `4.x`** | Port AMQP `5672` & Management UI `15672` |
| **Redis SaaS** | **Upstash Serverless Redis (Free Tier)** | Koneksi terenkripsi via TLS `rediss://` (tanpa container Redis lokal) |

---

## 2. Pengaturan `JAVA_HOME` ke Java 25 (Penting di Windows)

`mvnw.cmd` menggunakan variabel environment `JAVA_HOME`. Pastikan `JAVA_HOME` mengarah ke instalasi **JDK `25.0.5.0.2`** (bukan JDK versi lama):

### Set Permanen untuk User Windows (Sekali Saja di PowerShell)
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-25.0.5.0.2", "User")
```
*(Tutup dan buka kembali terminal setelah menjalankan perintah di atas).*

### Atau Set untuk Sesi Terminal Saat Ini Saja
- **PowerShell:**
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.5.0.2"
  $env:Path = "$env:JAVA_HOME\bin;$env:Path"
  .\mvnw.cmd -version
  ```
- **Command Prompt (CMD):**
  ```cmd
  set JAVA_HOME=C:\Program Files\Java\jdk-25.0.5.0.2
  set PATH=%JAVA_HOME%\bin;%PATH%
  mvnw.cmd -version
  ```

---

## 3. Persiapan Infrastruktur (PostgreSQL 18.4, RabbitMQ & Upstash Redis)

### A. Menjalankan PostgreSQL 18.4 & RabbitMQ 4
Anda dapat memilih **salah satu** dari dua opsi berikut:

#### Opsi 1 — Menggunakan Docker Compose (Direkomendasikan)
Jalankan dari **root folder monorepo** (`d:\project_yosua\logia-log`):
```powershell
cd d:\project_yosua\logia-log
docker compose up -d
docker compose ps
```
Ini akan menyalakan:
- `sentinel_postgres` (`postgres:18.4-alpine`) di `localhost:5432`
- `sentinel_rabbitmq` (`rabbitmq:4-management-alpine`) di `localhost:5672` (UI: `http://localhost:15672`)

#### Opsi 2 — Menggunakan PostgreSQL `18.4` Lokal (`psql`)
Jika Anda memakai instalasi PostgreSQL `18.4` lokal di komputer Anda, buat user dan database berikut melalui `psql`:
```sql
CREATE USER sentinel WITH PASSWORD 'sentinel_secret';
CREATE DATABASE sentinel_db OWNER sentinel;
GRANT ALL PRIVILEGES ON DATABASE sentinel_db TO sentinel;
```
*(Saat backend pertama kali dijalankan, **Flyway** akan otomatis mengeksekusi migrasi `src/main/resources/db/migration/V1__init_sentinel_schema.sql`).*

### B. Setup Upstash Serverless Redis SaaS (Gratis)
1. Buka https://console.upstash.com/ dan login (gratis tanpa kartu kredit).
2. Klik **Create Database** -> pilih Region **AWS `ap-southeast-1` (Singapore)** -> pilih plan **Free** (256 MB RAM, 500.000 command/bulan).
3. Salin URL koneksi **TLS (`rediss://`)** dari dashboard Upstash.

---

## 4. Konfigurasi File `.env` pada Backend

Backend dikonfigurasi mandiri dengan `spring.config.import` agar otomatis membaca file `backend/.env` secara terpisah (tidak digabung dengan frontend).

1. Salin template `.env.example` di dalam folder `backend` menjadi `.env`:
   ```powershell
   cd d:\project_yosua\logia-log\backend
   Copy-Item .env.example .env
   ```
2. Sesuaikan nilai pada file `backend/.env`:
   ```env
   POSTGRES_HOST=localhost
   POSTGRES_PORT=5432
   POSTGRES_DB=sentinel_db
   POSTGRES_USER=sentinel
   POSTGRES_PASSWORD=sentinel_secret

   RABBITMQ_HOST=localhost
   RABBITMQ_AMQP_PORT=5672
   RABBITMQ_USER=sentinel
   RABBITMQ_PASSWORD=sentinel_secret
   SENTINEL_RABBIT_CONSUMER_ENABLED=false

   UPSTASH_REDIS_URL=rediss://default:<PASSWORD_UPSTASH_ANDA>@<ENDPOINT_ANDA>.upstash.io:6379
   REDIS_SSL_ENABLED=true
   SENTINEL_REDIS_DEDUP_ENABLED=false
   ```
   > **Catatan Mode Offline/Dev Cepat:** Jika RabbitMQ atau Upstash Redis belum ingin diaktifkan saat mencoba awal, biarkan `SENTINEL_RABBIT_CONSUMER_ENABLED=false` dan `SENTINEL_REDIS_DEDUP_ENABLED=false` (sistem otomatis memakai fallback counter in-memory). Docker Compose di root juga otomatis membaca kredensial database & broker dari file `backend/.env` ini.

---

## 5. Cara Menjalankan, Menguji & Build Backend

Masuk ke direktori `backend`:
```powershell
cd d:\project_yosua\logia-log\backend
```

### 1. Menjalankan Server Development (Hot-Reload DevTools)
```powershell
.\mvnw.cmd spring-boot:run
```
*(Di Linux/macOS gunakan `./mvnw spring-boot:run`).*
Server akan berjalan di **`http://localhost:8080`** dengan **Java 25 Virtual Threads** aktif.

### 2. Menjalankan Unit & Integration Test
Pengujian memakai profil `test` (`src/test/resources/application-test.yml` dengan H2 mode PostgreSQL), sehingga dapat langsung dijalankan kapan saja tanpa membutuhkan koneksi database/broker aktif:
```powershell
.\mvnw.cmd test
```

### 3. Build Artefak Production JAR & Menjalankannya
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\sentinelai-backend-0.1.0-SNAPSHOT.jar
```

---

## 6. Verifikasi Endpoint Saat Backend Menyala

Setelah backend aktif di `http://localhost:8080`, uji dengan perintah berikut:

1. **Cek Informasi Runtime (Java 25, Spring Boot 4.1.2 & Virtual Threads):**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8080/api/v1/system/info"
   ```
2. **Cek Health Actuator:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"
   ```
3. **Uji Kirim Log Error & Deduplikasi SHA-256:**
   ```powershell
   $body = @{
     serviceName = "order-service"
     level       = "ERROR"
     traceId     = "sntl-test-trace-001"
     rawMessage  = "PSQLException: value too long for type character varying(50)"
     stackTrace  = "org.postgresql.util.PSQLException at com.app.OrderRepo.save(OrderRepo.java:42)"
   } | ConvertTo-Json

   Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/logs/ingest" -ContentType "application/json" -Body $body
   ```
4. **Uji Live Stream SSE (`GET /api/v1/logs/stream`):**
   ```powershell
   curl.exe -N http://localhost:8080/api/v1/logs/stream
   ```

---

## 7. Struktur Arsitektur Clean Hexagonal (`com.sentinelai`)

```text
src/main/java/com/sentinelai/
├── SentinelAiApplication.java        # Main entrypoint Spring Boot 4.1.2
├── config/                           # Konfigurasi Security, RabbitMQ DLQ, Upstash Redis & Properties
├── domain/                           # Inti bisnis murni (LogEntry, Fingerprinter SHA-256, PII Sanitizer)
├── application/
│   ├── port/in/                      # Kontrak Inbound Use Cases
│   ├── port/out/                     # Kontrak Outbound Infrastructure Ports
│   └── usecase/                      # Orkestrasi alur bisnis aplikasi
└── adapter/
    ├── in/
    │   ├── web/                      # REST Controllers & Endpoint SSE
    │   └── messaging/                # RabbitMQ Consumer (@RabbitListener di atas Virtual Threads)
    └── out/
        ├── persistence/              # PostgreSQL 18.4 JPA/JDBC Repositories
        ├── redis/                    # Upstash Redis SaaS Deduplication & Cooldown Adapter
        ├── sse/                      # SseEmitter Broadcaster
        ├── targetdb/                 # Dynamic Read-Only DDL Inspector (information_schema)
        ├── apiproxy/                 # HTTP Request Executor untuk modul API Studio
        ├── github/                   # Konektor GitHub REST/GraphQL v4
        └── ai/                       # Konektor 7-Layer AI Diagnostic Engine
```

---

## 8. Troubleshooting Umum

- **Error `SentinelAI v3.1 membutuhkan Java 25 LTS` saat menjalankan Maven:**
  Artinya `JAVA_HOME` di terminal Anda masih menunjuk ke JDK di bawah versi 25 (misalnya JDK 11/17/21). Ikuti langkah pada **Bagian 2** di atas.
- **Gagal konek ke PostgreSQL (`Connection refused` di port `5432`):**
  Pastikan Docker container `sentinel_postgres` atau service PostgreSQL `18.4` lokal sudah menyala, dan username/password di `.env` sesuai.
- **Koneksi Upstash Redis Timeout / SSL Handshake Error:**
  Pastikan URL menggunakan skema **`rediss://`** (dengan dua huruf `s` untuk TLS) dan `REDIS_SSL_ENABLED=true`.

