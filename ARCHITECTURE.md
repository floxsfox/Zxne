# Arsitektur FileX Studio

Pola: MVVM + Repository, satu modul `:app`, DI manual lewat `AppContainer` (tanpa framework DI).
Paket dasar: `com.filex.studio`.

## Lapisan

```
ui/            Compose: navigation, components, screens (+ ViewModel)
theme/         Design tokens, FileXTheme, ThemeConfig + ThemeRepository
data/
  repository/  FileRepository -> memilih backend, menolak operasi yang tidak didukung
  storage/     StorageBackend + Local, SAF, MediaStore, Shizuku; StorageManager (routing)
  shizuku/     ShizukuController (koneksi + izin via API resmi Shizuku)
  permission/  PermissionManager (semua-file, media, overlay)
  cloud/       CloudProvider + Dropbox/MediaFire/TeraBox (belum diimplementasi)
feature/       Kontrak untuk dualpane, dragdrop, floating, auth (belum ada implementasi)
core/          FxResult / FxError / validasi nama
```

## Aturan inti

1. **Tidak ada sukses palsu.** Semua operasi mengembalikan `FxResult`. Jika OS menolak, hasilnya `Failure`.
   Operasi yang belum diimplementasi sebuah backend dilaporkan `Unsupported`, bukan diam-diam berhasil.
2. **`FileOp` per backend.** `StorageBackend.supportedOps` menyatakan apa yang benar-benar ada;
   `DefaultFileRepository` memeriksanya sebelum memanggil backend.
3. **Alamat netral backend.** `FxLocation(backend, value)`: path (LOCAL/SHIZUKU), Uri dokumen (SAF), id kategori/Uri (MEDIA_STORE).

## Routing backend (`StorageManager.chooseBackendForPath`)

| Kondisi | Backend |
|---|---|
| `Android/data` atau `Android/obb`, API < 30 | LOCAL |
| `Android/data` atau `Android/obb`, API >= 30, Shizuku diizinkan | SHIZUKU |
| `Android/data` atau `Android/obb`, API >= 30, tanpa Shizuku | SAF (bisa dibatasi Android) |
| Path lain, akses semua-file diberikan | LOCAL |
| Path lain, tanpa akses semua-file | SAF |

## Shizuku

`ShizukuController` memakai listener resmi (`addBinderReceivedListenerSticky`, `addBinderDeadListener`,
`addRequestPermissionResultListener`), `ShizukuProvider` di manifest, dan `<queries>` untuk mendeteksi aplikasi manajernya.
Operasi file lewat Shizuku butuh `UserService` (AIDL) dan **belum** dibuat (task B10).

## Rencana modul fase B (kontrak sudah ada)

- Dual-pane: `PaneState` / `DualPaneState` (task B13).
- Drag & drop: `DragPayload`, `DragDropCoordinator`, `ExternalShareBridge` — URI konten + izin sementara,
  fallback Sharesheet, tanpa klaim drag lintas aplikasi yang tidak pasti (B20, B23).
- Jendela mengambang: `FloatingWindowController` — izin overlay, layanan latar (B21, B22).
- Autentikasi: `CredentialVault`, `AuthManager` — hash berkunci Android Keystore, BiometricPrompt (B24-B26).
- Cloud: `CloudProvider` — hanya API resmi, tanpa kredensial bawaan (B28-B30).
