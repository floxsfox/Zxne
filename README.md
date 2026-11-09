# FileX Studio

File manager Android lanjutan (Kotlin, Jetpack Compose, Material 3) dengan arsitektur MVVM + Repository.
Dibangun dan dikelola sepenuhnya lewat GitHub Web: tanpa Android Studio, tanpa PC.

> **Status: Fase A (arsitektur) selesai ditulis.** Aplikasi baru berupa kerangka yang bisa dijelajahi
> (5 tab, tema, backend storage, status Shizuku/izin). Sebagian besar fitur masih belum ada.
> Lihat `PROJECT_STATUS.md` untuk daftar jujur apa yang jalan, apa yang belum, dan apa yang belum diverifikasi.

## Dokumen

| File | Isi |
|---|---|
| `GITHUB_SETUP.md` | Langkah dari nol: buat repo, upload ZIP, ekstrak, build, unduh APK |
| `BUILD_GUIDE.md` | Detail build, tanda tangan release, troubleshooting |
| `ARCHITECTURE.md` | Struktur paket, backend storage, alur data |
| `PROJECT_STATUS.md` | Status terverifikasi vs belum |
| `TASKS.md` | Daftar tugas A/B/C |
| `CHANGELOG.md` | Riwayat perubahan |

## Dua workflow terpisah

- `.github/workflows/extract-project.yml` — hanya mengekstrak ZIP ke root repo. Tidak menjalankan Gradle.
- `.github/workflows/build-apk.yml` — hanya membangun APK. Tidak mengekstrak ZIP.

## Persyaratan build (dijalankan GitHub, bukan di perangkatmu)

JDK 17, Gradle 8.11.1 (dari `gradle-wrapper.properties`), AGP 8.7.3, Kotlin 2.0.21, compileSdk 35, minSdk 26.
