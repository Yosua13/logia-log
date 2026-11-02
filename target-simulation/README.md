# Target Simulation Slot (Target-Agnostic / Kosong secara Default)

Sesuai **PRD Logia Log (Bagian 06)**, direktori ini sengaja dikosongkan karena Logia Log dirancang **Target-Agnostic (Plug-and-Play)**.

Logia Log dapat langsung dihubungkan ke aplikasi eksternal atau microservice mana pun melalui:
1. **API Testing Studio** (`/api/v1/proxy/execute`) untuk menguji REST API target secara langsung.
2. **RabbitMQ / HTTP Log Ingestion** untuk streaming log real-time ke Grafana-Style Log Explorer.
3. **Target Profiles UI** (`/api/v1/targets`) untuk mendaftarkan URL repositori GitHub dan koneksi Read-Only PostgreSQL target secara dinamis kapan saja tanpa mengubah kode Logia Log.

## Rekomendasi Sandbox Opsional untuk Pengujian Akhir
- **Spring PetClinic REST** (uji validasi `@Size`, N+1 Query, CRUD API Studio)
- **Chaos E-Commerce Sandbox** (uji `Double` vs `BigDecimal`, `@Transactional` I/O lock, race condition)
- **OWASP crAPI / WebGoat** (uji audit Security: SQL Injection, Broken JWT)
- **JVM Memory & Concurrency Lab** (uji deteksi Memory Leak pada Java 25 Virtual Threads)
- **RealWorld Conduit API** (uji chained JWT workflow & `SQLState 23505`)
