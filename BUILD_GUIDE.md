# BUILD_GUIDE

## Alur build (`build-apk.yml`)

1. Checkout, lalu cek bahwa proyek sudah diekstrak (`settings.gradle.kts` + `app/`).
2. Baca versi Gradle dari `gradle/wrapper/gradle-wrapper.properties` (tidak pernah diganti sembarangan).
3. JDK 17 (Temurin) — syarat AGP 8.7.x.
4. Android SDK bawaan runner: terima lisensi, pastikan `platforms;android-35` dan `build-tools;35.0.0`.
5. `gradle/actions/setup-gradle` (cache dependensi). Bila `gradlew`/`gradle-wrapper.jar` tidak ada, wrapper dibuat
   untuk run itu saja memakai versi dari properti; lalu `wrapper-validation` memeriksa jar-nya.
6. `./gradlew assembleDebug --stacktrace --console=plain`
7. Release hanya bila keempat secret penandatanganan ada: `./gradlew assembleRelease --stacktrace --console=plain`
8. Unggah artifact `FileXStudio-Debug-APK` (dan `FileXStudio-Release-APK` bila ada). Bila gagal, laporan build diunggah sebagai `FileXStudio-Build-Reports`.

Pemicu: manual (`workflow_dispatch`) dan push yang menyentuh `app/**`, `gradle/**`, berkas Gradle root, atau workflow ini.

## Release APK bertanda tangan (opsional)

Tidak ada kredensial bawaan. Tanpa secret di bawah, hanya debug APK yang dibuat (dicatat di ringkasan run).

Buat keystore (mis. di Termux dengan `openjdk`, atau mesin mana pun yang punya `keytool`):

```
keytool -genkeypair -v -keystore release.keystore -alias filex -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 release.keystore
```

Simpan hasil `base64` dan kata sandinya di **Settings → Secrets and variables → Actions**:

| Secret | Isi |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | keluaran `base64 -w0 release.keystore` |
| `ANDROID_KEYSTORE_PASSWORD` | kata sandi keystore |
| `ANDROID_KEY_ALIAS` | mis. `filex` |
| `ANDROID_KEY_PASSWORD` | kata sandi key |

Jangan pernah meng-commit file keystore (`.gitignore` sudah memblokir `*.keystore` dan `*.jks`). Simpan cadangannya sendiri:
kalau hilang, update aplikasi bertanda tangan yang sama tidak bisa dibuat lagi.

Minifikasi R8 sengaja **mati** di Fase A; dinyalakan setelah diuji (task C13/C16).

## Troubleshooting

- **`Android project not found`** — jalankan *Extract Project ZIP* dulu.
- **Lisensi SDK / komponen tidak ditemukan** — lihat langkah "Prepare Android SDK" di log; runner harus punya `cmdline-tools`.
- **`Wrapper validation failed`** — `gradle-wrapper.jar` di repo bukan jar resmi. Hapus `gradle/wrapper/gradle-wrapper.jar` dan `gradlew*`
  dari repo; workflow membuat yang resmi.
- **Error `e: ...kt`** — error kompilasi Kotlin. Ambil baris `e:` pertama dari log lengkap beserta ~10 baris sekitarnya.
- **`Could not resolve ...`** — dependensi tidak terunduh; jalankan ulang, dan cek `gradle/libs.versions.toml`.
- **Kehabisan memori** — turunkan `org.gradle.jvmargs` di `gradle.properties` (mis. `-Xmx2g`).
- **Artifact kosong / `No files were found`** — build gagal sebelum menghasilkan APK; baca langkah Gradle yang merah.

## Versi yang dipakai

AGP 8.7.3 + Gradle 8.11.1 + Kotlin 2.0.21 + Compose BOM 2024.12.01 dipilih sebagai set yang saling kompatibel.
Versi yang lebih baru ada, tetapi belum diuji bersama; naikkan bertahap setelah build hijau.
