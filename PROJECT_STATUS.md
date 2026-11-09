# PROJECT_STATUS — Fase A

Tanggal: 2026-11-09. Prinsip: tidak ada klaim sukses tanpa verifikasi.

## Terverifikasi (di lingkungan pembuat, tanpa jaringan dan tanpa Android SDK)

- `extract-project.yml` dan `build-apk.yml` berupa YAML valid dan terpisah: workflow ekstraksi tidak memuat Gradle/SDK/build/upload APK;
  workflow build tidak memuat logika ekstraksi ZIP.
- Langkah-langkah `run:` milik `extract-project.yml` dijalankan sungguhan terhadap repo git lokal (21 pemeriksaan lulus, lihat di bawah):
  proyek di root ZIP, folder pembungkus `FileXStudio/FileXStudio/` dibuang, `gradlew` tetap executable, tidak ada commit kosong pada run kedua,
  `extract-project.yml` tidak tertimpa, `build-apk.yml` dilewati tanpa token dan di-commit dengan token, `remove_zip` bekerja;
  ditolak: ZIP Slip (`../`), path absolut, symlink, ZIP kosong, bukan ZIP, CRC rusak, ZIP tanpa proyek Gradle, ZIP tidak ada, `zip_path` keluar repo.
- Semua XML (manifest, resource) well-formed; ZIP bisa dibuka, lulus `unzip -t`, tanpa folder pembungkus.

- `filex-build.yml` (versi satu berkas, tidak ada di dalam ZIP): logika ekstraksinya diuji dengan 21 pemeriksaan serupa (ZIP di root, `input/`, `commit_source`, tanpa ZIP, ZIP berbahaya). Bagian build-nya sama dengan `build-apk.yml` dan belum dijalankan.

## BELUM terverifikasi

- **Kompilasi Kotlin/Gradle.** Tidak ada Android SDK dan jaringan di lingkungan pembuat. Kode ditulis dengan hati-hati dan ditinjau manual,
  tetapi build pertama sebenarnya terjadi di GitHub Actions. Kalau gagal, kirim error pertamanya.
- Kedua workflow belum pernah berjalan di runner GitHub sungguhan. Versi action (checkout v7, setup-java v5, setup-gradle v6, wrapper-validation v6,
  upload-artifact v7) diambil dari informasi publik terbaru, belum dijalankan.
- Perilaku di perangkat: SAF, MediaStore, izin, Shizuku, tema.
- Ketersediaan/otentikasi API resmi MediaFire dan TeraBox (Dropbox punya API resmi dengan OAuth 2.0).

## Batasan yang diketahui

1. **`gradlew` dan `gradle-wrapper.jar` tidak ada di ZIP.** Berkas biner itu tidak bisa dibuat tanpa jaringan, dan tidak dikarang.
   `gradle-wrapper.properties` ada (Gradle 8.11.1); `build-apk.yml` membuat wrapper resmi saat build dan memvalidasinya.
2. **GITHUB_TOKEN tidak boleh mengubah `.github/workflows/`** (aturan GitHub). Tanpa secret `EXTRACT_PUSH_TOKEN`, `build-apk.yml` harus diunggah
   manual (disediakan terpisah). Lihat `GITHUB_SETUP.md`.
3. Minifikasi release mati; release hanya dibuat bila secret penandatanganan diisi.
4. Android/data lewat Shizuku: koneksi + izin saja; operasi file butuh `UserService` (B10).

## Fitur Fase A yang ada di kode (belum diuji di perangkat)

| Area | Ada | Belum |
|---|---|---|
| Navigasi | 5 tab | — |
| Tema | mode, aksen, kepadatan, radius | simpan, impor/ekspor, warna kustom |
| LOCAL | list, buat folder, rename, hapus (tanpa mengikuti symlink) | salin, pindah |
| SAF | pemilih folder, list, buat folder, rename, hapus | salin, pindah |
| MediaStore | baca Pictures/Videos/Music | dokumen, unduhan, operasi |
| Shizuku | deteksi, koneksi, minta izin | operasi file |
| Browse | daftar folder LOCAL, naik, sembunyikan/tampilkan file tersembunyi | grid, multi-pilih, dll. |
| Dual-pane, drag & drop, bubble, auth, cloud | kontrak/model | implementasi |
| Arsip, app manager, editor teks/hex | — | semuanya |
