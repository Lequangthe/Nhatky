### Thu gọn build flavors – Chỉ giữ gmsProd
- File thay đổi: `app/build.gradle.kts`
- Chi tiết: Xoá `gmsDev`, `foss`, `lab` khỏi `productFlavors`. Cập nhật `sourceSets` chỉ dùng `src/main/` và `src/gmsProd/`. Xoá block `fossImplementation` exclude.

### Di chuyển code src/gms/ và src/dummy/ vào src/gmsProd/
- File thay đổi: nhiều file trong `app/src/gmsProd/`
- Chi tiết: Copy toàn bộ code từ `src/gms/` (Google services) và `src/dummy/` (ActivityDummy) vào `src/gmsProd/java/`. Copy `AndroidManifest.xml` từ `src/gms/` vào `src/gmsProd/`.

### Xoá thư mục flavor cũ
- File thay đổi: xoá `src/foss/`, `src/lab/`, `src/gmsDev/`, `src/gms/`, `src/dummy/`
- Chi tiết: Dọn sạch toàn bộ thư mục flavor không còn dùng.

### Fix FlavorUtils.kt – icon drawable bị thiếu
- File thay đổi: `app/src/gmsProd/java/me/blog/korn123/commons/utils/FlavorUtils.kt`
- Chi tiết: Các icon drawable (ic_raindrops, ic_bolt, ...) nằm trong `src/gmsDev/res/drawable/` đã bị xoá và không được git track. Thay bằng bản tối giản chỉ dùng icon có sẵn trong `src/main/res/drawable/`.

### Đổi tên app hiển thị thành "Nhật ký"
- File thay đổi: `app/src/main/res/values-vi/strings.xml`
- Chi tiết: Đổi `app_name` từ "Easy Diary" thành "Nhật ký" cho locale tiếng Việt.

### Xoá toàn bộ Google Auth (GMS)
- File thay đổi: xoá GoogleAuthManager, DriveServiceHelper, BackupPhotoService, FullBackupService, RecoverPhotoService, SettingsGMSBackupFragment, ActivityGms, ActivityDummy
- File thay đổi: sửa `DevActivity.kt`, `SettingsActivity.kt` (xoá GMS code)
- File thay đổi: thay `AlarmWorkExecutor.kt`, `NotificationService.kt` bằng bản tối giản
- File thay đổi: xoá `src/gmsProd/AndroidManifest.xml` (không còn service GMS)
- File thay đổi: `app/build.gradle.kts` (xoá toàn bộ GMS dependencies)
- Chi tiết: Xoá Google sign-in, Google Drive backup/restore, Google Calendar sync, In-app Review. App không còn phụ thuộc Google Play Services auth.

### Phase 1 – Chọn thư mục lưu phương tiện (Audio/Video)
- File thay đổi: tạo `app/src/main/java/me/blog/korn123/easydiary/helper/MediaStorageManager.kt`
- File thay đổi: `app/src/main/java/me/blog/korn123/easydiary/helper/Constants.kt` (thêm DIARY_AUDIO_DIRECTORY, DIARY_VIDEO_DIRECTORY)
- File thay đổi: `app/src/main/java/me/blog/korn123/commons/utils/EasyDiaryUtils.kt` (thêm initMediaDirectories)
- File thay đổi: `app/src/main/java/me/blog/korn123/easydiary/fragments/SettingsBasicFragment.kt` (thêm UI chọn thư mục)
- File thay đổi: `app/src/main/AndroidManifest.xml` (thêm RECORD_AUDIO permission)
- File thay đổi: `app/build.gradle.kts` (thêm documentfile dependency)
- File thay đổi: `app/src/main/res/values/strings.xml` + `values-vi/strings.xml` (thêm string media storage)
- Chi tiết: Dùng SAF ACTION_OPEN_DOCUMENT_TREE để người dùng chọn thư mục gốc. Tạo sub-folder Audio/Video ở đó. Lưu tree URI vào SharedPreferences, fallback về internal storage nếu chưa set.

### Nghiên cứu kiến trúc lưu trữ file của Logseq
- File thay đổi: tạo `D:\GG\B\skill-logseq-storage.md`
- Chi tiết: Phân tích toàn bộ cơ chế lưu trữ file của Logseq (protocol-based FS, Electron/Node.js backend, directory picker, graph directory structure). Dùng làm tài liệu tham khảo cho Android SAF implementation.

### Revert SAF – chuyển media về internal storage
- File thay đổi: xoá `MediaStorageManager.kt`, xoá `documentfile` dependency khỏi `build.gradle.kts`
- File thay đổi: `SettingsBasicFragment.kt` (xoá SAF card), `EasyDiaryUtils.kt` (xoá `initMediaDirectories`)
- Chi tiết: Hủy bỏ cơ chế SAF để chọn thư mục media bên ngoài. Audio/video giờ lưu trong internal storage (`/AAFactory/EasyDiary/Audio/` và `Video/`).

### Thêm ghi âm audio và quay video
- File thay đổi: `BaseDiaryEditingActivity.kt` (MediaRecorder + ACTION_VIDEO_CAPTURE launcher, MediaClickListener)
- File thay đổi: `PhotoUri.kt` (sửa `getFilePath()` cho audio/video paths)
- File thay đổi: `partial_edit_photo_container.xml` + landscape (thêm nút mic, videocam)
- File thay đổi: tạo `ic_mic.xml`, `ic_videocam.xml` (vector drawable)
- File thay đổi: `strings.xml` + `values-vi/strings.xml` (thêm `record_audio`, `record_video`)
- Chi tiết: Audio dùng `MediaRecorder` (AAC/MP4), video dùng `ACTION_VIDEO_CAPTURE`. Playback dùng system intents. Gắn theo mimeType trong `PhotoUri`.

### Thêm Backup/Restore ZIP to Downloads
- File thay đổi: `SettingsLocalBackupFragment.kt` (thêm 2 Compose cards + method `exportFullBackupToDownloads()`)
- File thay đổi: `strings.xml` (thêm string backup_to_downloads, restore_from_downloads, backup_saved_to_downloads)
- Chi tiết: Backup nén toàn bộ working directory (DB, photos, audio, video, settings) thành ZIP và copy vào thư mục Downloads qua MediaStore API. Restore dùng SAF picker (tái sử dụng importFullBackupFile).
