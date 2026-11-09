# GITHUB_SETUP — dari nol sampai APK (semua lewat GitHub Web)

## Cara cepat: 1 yml + 1 ZIP (disarankan)

Berkas yang dipakai: `filex-build.yml` dan `FileXStudio-Source.zip`. Tidak perlu token.

1. Buat repository baru dan centang **Add a README file**.
2. **Add file → Create new file**, nama `.github/workflows/filex-build.yml`, paste seluruh isi `filex-build.yml`, lalu **Commit changes**.
3. **Add file → Upload files**, upload `FileXStudio-Source.zip` ke root repo, lalu commit.
4. Tab **Actions → FileX Studio - ZIP to APK → Run workflow**. Biarkan isian kosong, lalu **Run workflow**.
5. Tunggu selesai. Buka run-nya, lalu unduh **FileXStudio-Debug-APK** di bagian **Artifacts** (isinya ZIP berisi `.apk`).

Workflow ini memvalidasi ZIP (menolak path berbahaya, symlink, ZIP rusak), mengekstraknya, lalu membangun APK dalam satu run.
`build-apk.yml` di dalam ZIP diabaikan, jadi tidak ada masalah izin workflow.

Opsi saat menjalankan:

- `commit_source` = `true`: hasil ekstrak disimpan ke repo dan ZIP dihapus, supaya kodenya bisa diedit lewat GitHub Web.
  Run berikutnya membangun source yang ada di repo (tanpa ZIP). Kalau opsi ini `false`, jalankan ulang dengan ZIP yang sama setiap kali.
- `zip_path`: isi hanya kalau ZIP-mu bukan di root atau namanya lain.
- `build_release`: release hanya dibuat bila secret penandatanganan diisi (lihat `BUILD_GUIDE.md`).

Catatan: `gradlew` dan `gradle-wrapper.jar` tidak ikut di ZIP. Workflow membuat wrapper resmi untuk run itu dan memvalidasinya.

## Kalau gagal

| Gejala | Penyebab / solusi |
|---|---|
| Tombol **Run workflow** tidak ada | `filex-build.yml` belum ada di branch utama |
| `No ZIP found` | ZIP belum di root repo, atau namanya lain; isi `zip_path` |
| `Path traversal entry rejected` | ZIP berisi path berbahaya; buat ulang ZIP |
| `No Gradle settings file` | ZIP bukan proyek yang benar |
| Gagal di langkah Gradle | Buka log, cari baris `e:` (error Kotlin) atau `What went wrong`, lalu kirim ke saya |
| Push `commit_source` ditolak | **Settings → Actions → General → Workflow permissions** = *Read and write*, atau cek proteksi branch |

---

## Alternatif: dua workflow terpisah (lebih ribet)

Ini dipakai hanya bila kamu memang ingin ekstraksi dan build terpisah. Berkas: `extract-project.yml`, `build-apk.yml`, ZIP.
Batasan GitHub: `GITHUB_TOKEN` tidak boleh menulis ke `.github/workflows/`, jadi `build-apk.yml` dari ZIP tidak ikut ter-commit
kecuali kamu membuat secret `EXTRACT_PUSH_TOKEN` (token dengan scope `repo` + `workflow`). Tanpa itu, paste `build-apk.yml` sendiri.

1. Buat repo, lalu paste `extract-project.yml` ke `.github/workflows/extract-project.yml`.
2. Paste `build-apk.yml` ke `.github/workflows/build-apk.yml` (atau pasang secret `EXTRACT_PUSH_TOKEN` supaya otomatis).
3. Upload ZIP ke root repo. **Actions → Extract Project ZIP → Run workflow** dengan `zip_path` = `FileXStudio-Source.zip`.
4. Pastikan `app/`, `gradle/`, `build.gradle.kts`, `settings.gradle.kts` muncul langsung di root repo (tanpa folder pembungkus).
5. **Actions → Build APK → Run workflow**, lalu unduh artifact seperti di atas.
