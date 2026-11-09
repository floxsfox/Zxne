# Daftar Tugas

Legenda: **Selesai** = ditulis dan diverifikasi sejauh yang tertulis. **Ditulis** = kode/berkas ada tetapi
belum dikompilasi/diuji. **Kerangka** = hanya kontrak/model. **Belum** = belum dikerjakan.

## Fase A — Arsitektur

| ID | Tugas | Status | Catatan |
|---|---|---|---|
| A01 | Inisialisasi proyek | Ditulis | Belum pernah dikompilasi |
| A02 | Konfigurasi Gradle | Ditulis | `gradlew` + `gradle-wrapper.jar` tidak ada di ZIP (lihat PROJECT_STATUS) |
| A03 | Konfigurasi Compose | Ditulis | |
| A04 | Arsitektur paket | Selesai | |
| A05 | Navigasi | Ditulis | 5 tab |
| A06 | Design system | Ditulis | Token warna/spasi/bentuk |
| A07 | Abstraksi storage | Ditulis | LOCAL: list, buat folder, rename, hapus |
| A08 | Integrasi SAF | Ditulis | list, buat folder, rename, hapus + pemilih folder |
| A09 | Integrasi MediaStore | Ditulis | Hanya baca: Pictures/Videos/Music |
| A10 | Integrasi Shizuku | Ditulis | Hanya koneksi + izin; operasi file belum |
| A11 | Manajemen izin | Ditulis | Semua-file, media, overlay (status) |
| A12 | File repository | Ditulis | list/buat folder/rename/hapus |
| A13 | Arsitektur dual-pane | Kerangka | Model saja |
| A14 | Arsitektur drag & drop | Kerangka | Kontrak saja |
| A15 | Arsitektur jendela mengambang | Kerangka | Kontrak saja |
| A16 | Arsitektur autentikasi | Kerangka | Kontrak saja |
| A17 | Arsitektur cloud | Kerangka | Antarmuka + 3 provider "belum diimplementasi" |
| A18 | Arsitektur tema | Ditulis | Disimpan di memori, belum persisten |
| A19 | Struktur proyek GitHub | Selesai | |
| A20 | Pengemasan ZIP | Selesai | Integritas dan struktur diperiksa |
| A21 | Workflow ekstraksi | Selesai | Logika diuji lokal; belum dijalankan di GitHub |
| A22 | Workflow build APK | Ditulis | Sintaks YAML valid; belum dijalankan di GitHub |

## Fase B — Fitur

| ID | Tugas | Status |
|---|---|---|
| B01 | Dashboard Home | Belum (Home sekarang hanya panel status) |
| B02-B04 | Explorer, navigasi folder, grid/list | Sebagian kecil: daftar folder + naik/turun (belum grid, belum tab penuh) |
| B05-B12 | Sortir/filter, cari, operasi file, multi-pilih, inspektur, preview, recent, favorit | Belum (comparator sortir ada di kode) |
| B13 | Dual-pane | Belum |
| B14-B15 | Manajer arsip | Belum |
| B16-B17 | Manajer aplikasi, inspektur APK | Belum |
| B18-B19 | Editor teks, hex | Belum |
| B20-B23 | Drag & drop, bubble, jendela mengambang, berbagi lintas aplikasi | Belum |
| B24-B26 | File tersembunyi, autentikasi, biometrik | Belum |
| B27 | Tema kustom | Sebagian: mode, aksen, kepadatan, radius (belum simpan/impor/ekspor) |
| B28-B30 | Dropbox, MediaFire, TeraBox | Belum |
| B31-B32 | Analisis storage, pengaturan | Belum |
| B33 | Validasi pengemasan ZIP | Belum (dilakukan ulang di akhir) |

## Fase C — Penyelesaian

Seluruh C01-C22 belum dikerjakan.
