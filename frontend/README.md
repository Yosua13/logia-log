# Logia Log — Frontend Workspace

Antarmuka web **Logia Log (Executive Cyber-Luxury Command Center)** yang menggabungkan **Grafana-Style Log Explorer**, **Postman-Style API Testing Studio**, dan **7-Layer Deep GitHub AI Resolution View** menggunakan arsitektur **Feature-Sliced Design (FSD)**.

---

## 1. Spesifikasi Stack & Prasyarat

| Komponen | Versi Wajib | Keterangan |
| :--- | :--- | :--- |
| **Node.js Runtime** | **Node.js `v26.13.0`** (`>=26.13.0`) | Didefinisikan pada `.nvmrc` dan `package.json` (`engines`) |
| **Package Manager** | **npm `11.x+`** | Bawaan instalasi Node.js `v26.13.0` |
| **UI Library & Bahasa** | **React `19.2`** + **TypeScript `6.x`** | Mode `strict` penuh (`tsc --noEmit`) |
| **Bundler & Styling** | **Vite `8.x`** + **Tailwind CSS `v4`** | Menggunakan `@tailwindcss/vite` + token desain 60-30-10 |

Cek versi Node.js dan npm Anda sebelum memulai:
```powershell
node -v   # Harus menampilkan v26.13.0 (atau >= 26.13.0)
npm -v
```

---

## 2. Instalasi & Konfigurasi Awal

1. Masuk ke direktori `frontend`:
   ```powershell
   cd d:\project_yosua\logia-log\frontend
   ```
2. Instal seluruh dependensi:
   ```powershell
   npm install
   ```
3. Konfigurasi file environment frontend terpisah (`frontend/.env`):
   Secara default di mode development, `vite.config.ts` sudah mem-proxy seluruh request `/api` ke backend Spring Boot di `http://localhost:8080` sehingga bebas kendala CORS. Salin template `frontend/.env.example` ke `frontend/.env` (atau `frontend/.env.local`):
   ```powershell
   cd d:\project_yosua\logia-log\frontend
   Copy-Item .env.example .env
   ```
   Isi konfigurasi pada `frontend/.env`:
   ```env
   VITE_API_BASE_URL=/api/v1
   VITE_APP_TITLE="Logia Log • Target-Agnostic Observability & API Studio"
   VITE_SSE_LOG_STREAM_PATH=/api/v1/logs/stream
   ```

---

## 3. Cara Menjalankan, Memeriksa Tipe, Lint & Build

Pastikan Anda berada di direktori `d:\project_yosua\logia-log\frontend`:

### 1. Menjalankan Development Server (HMR Aktif)
```powershell
npm run dev
```
- Aplikasi akan berjalan di **`http://localhost:5173`**.
- Pastikan backend Spring Boot (`http://localhost:8080`) juga dijalankan apabila Anda ingin menguji stream log real-time SSE dan eksekusi API Proxy.

### 2. Memeriksa Tipe TypeScript (`Typecheck`)
```powershell
npm run typecheck
```

### 3. Menjalankan Linter (`ESLint` + `typescript-eslint`)
```powershell
npm run lint
```

### 4. Membangun Bundle Production (`dist/`)
```powershell
npm run build
```
Perintah ini menjalankan kompilasi pemeriksaan tipe ketat (`tsc --noEmit`) terlebih dahulu lalu memaketkan aset teroptimasi melalui `vite build` ke folder `dist/`.

### 5. Meninjau Hasil Build Production Secara Lokal
```powershell
npm run preview
```

---

## 4. Arsitektur Feature-Sliced Design (FSD)

Struktur direktori di dalam `src/` mengikuti standar **Feature-Sliced Design** dengan aturan arah ketergantungan ketat dari atas ke bawah (`app -> pages -> widgets -> features -> entities -> shared`):

```text
src/
├── main.tsx                          # Entrypoint DOM React 19
├── app/                              # Layer 1: App Shell, Routing Tab & CSS Design Tokens (index.css)
├── pages/                            # Layer 2: Halaman penuh per modul utama
│   ├── log-explorer/                 #   • Grafana-Style Real-Time Log & Metrics View
│   ├── api-testing-studio/           #   • Postman-Style Interactive API Testing Studio
│   ├── incident-deep-audit/          #   • 7-Layer GitHub Code Analysis & AI Resolution View
│   └── target-settings/              #   • Konfigurasi Profil Target Dinamis (Target-Agnostic)
├── widgets/                          # Layer 3: Blok UI komposit mandiri
│   ├── command-header/               #   • Header status telemetri stack
│   ├── live-log-stream/              #   • Tabel stream log SSE & Occurrence Badge
│   └── api-request-builder/          #   • Builder Method, URL, Auth & X-Logia-Trace-Id
├── features/                         # Layer 4: Aksi & interaksi pengguna spesifik
│   ├── log-filter-query/             #   • Pencarian log multi-dimensi & filter severity
│   ├── auth-type-selector/           #   • Pemilih 5 tipe autentikasi API Studio
│   └── trigger-ai-audit/             #   • Pemicu analisis kode GitHub 7 dimensi
├── entities/                         # Layer 5: Model & tipe domain bisnis TypeScript
│   ├── log-entry/
│   ├── api-collection/
│   ├── diagnostic-report/
│   └── target-profile/
└── shared/                           # Layer 6: Komponen UI atomik, HTTP client & konfigurasi
    ├── api/                          #   • Wrapper fetch API (/api/v1)
    ├── config/                       #   • Konstanta metadata sistem
    └── ui/                           #   • GoldFrameCard, SharpButton, TacticalTabs
```

> **Path Alias:** Gunakan alias `@/` untuk mengimpor dari `src/` (contoh: `import { SharpButton } from '@/shared/ui/SharpButton'`). Lapisan bawah (misal `shared` atau `entities`) **dilarang** mengimpor dari lapisan di atasnya (`features`, `widgets`, `pages`, `app`).

---

## 5. Sistem Desain UI/UX (60-30-10 & Sharp-Angled Geometry)

Sesuai **PRD Logia Log (Bagian 07)**, seluruh komponen UI wajib mematuhi aturan desain berikut:
- **60% Warna Dominan**: `#0A0A0A` (*Obsidian Black*) & `#121212` (*Elevated Surface*).
- **30% Warna Sekunder Struktural**: `#D4AF37` (*Metallic Gold*), `#C5A059` (*Champagne Gold*), `#9A7B2C` (*Deep Gold Border*).
- **10% Warna Aksen Interaktif**: `#FFD700` (*Pure Luminous Gold*) & `#F59E0B` (*Amber Gold Glow*) dengan teks kontras `#0A0A0A`.
- **Geometri Siku-Lancip**: Radius maksimum `2px` (`0px` pada tabel data) ditambah aksen siku emas `10px` (`.logialog-tactical-corners`) pada panel utama.
- **Tipografi**: `Plus Jakarta Sans` untuk antarmuka umum dan `JetBrains Mono` untuk log stream, URL API, payload JSON, serta kode diff GitHub.
