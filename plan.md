# Nhatky — Hiện trạng & Kế hoạch

## Hiện trạng App

- **Nền tảng:** Android, Kotlin + Compose + View hybrid
- **Kiến trúc:** View-based cũ (Activity/Fragment/XML) + Compose mới (15 activity đã migrate)
- **Core features:** Diary, Note, Task, D-Day, Lock (pin/fingerprint)
- **Đã xoá:** 15+ tính năng không cần thiết, 60+ file chết, calendar feature, helper/ package
- **Package structure hiện tại:**
  - `core/{config,application,export,navigation,manager}/`
  - `ui/features/{diary,main,note,task,settings,auth,media}/`
  - `extensions/` — 28 file (đã tách xong Context.kt + Activity.kt)
  - `commons/utils/` — 16 file (đã tách xong EasyDiaryUtils)
- **Build:** Thành công (compileDebugKotlin), còn ~20 deprecated warnings

## Đã hoàn thành: Tách Context.kt (1321 dòng → 15 file)
| File | Dòng | Nội dung |
|------|:----:|----------|
| `Theme.kt` | ~280 | isNightMode, updateTextColors, updateAppViews, pauseLock |
| `Dialog.kt` | ~140 | showAlertDialog (4 overloads), updateAlertDialog |
| `Config.kt` | ~60 | config, preferencesContains, preferenceToJsonString |
| `Markdown.kt` | ~150 | applyMarkDownPolicy, applyBoldToDate, parsedMarkdownString |
| `Files.kt" | ~80 | getUriForFile, shareFile, exportDatabaseFile |
| `Location.kt` | ~70 | isLocationEnabled, getLastKnownLocation, fullAddress |
| `Time.kt` | ~50 | formatTime, getFormattedTime, formatTo12HourFormat |
| `Permission.kt` | ~40 | checkPermission, hasPermission, getPermissionString |
| `Launcher.kt` | ~30 | themeItems, toggleLauncher, checkAppIconColor |
| `Dimension.kt` | ~25 | dpToPixel, dpToPixelFloatValue, spToPixelFloatValue |
| `Misc.kt` | ~25 | version checks, isScreenOn, isColorLight, findActivity |
| `Toast.kt` | ~15 | makeToast, makeSnackBar |
| `Network.kt` | ~10 | isConnectedOrConnecting |
| `Alarm.kt` | ~15 | pendingIntentFlag, pendingIntentFlagMutable |
| `Menu.kt` | ~10 | applyFontToMenuItem |

## Đã hoàn thành: Tách Activity.kt (722 dòng → 6 file)
| File | Dòng | Nội dung |
|------|:----:|----------|
| `Display.kt` | ~75 | isLandScape, actionBarHeight, statusBarHeight, getDefaultDisplay |
| `SystemBars.kt" | ~65 | hideSystemBars, applyFullScreenStatusBarTheme |
| `Navigation.kt` | ~65 | startActivityWithTransition, refreshApp, triggerRestart, openGoogleMap |
| `Security.kt` | ~105 | confirmPermission, resumeLock, acquireGPSPermissions |
| `Export.kt` | ~110 | exportHtmlBook, photoToBase64, createHtmlString, uriToFile |
| `ActivityUtils.kt` | ~70 | makeSnackBar, checkWhatsNew, holdCurrentOrientation |

## Đã hoàn thành: Tách EasyDiaryUtils (869 dòng → 8 file)
| File | Dòng | Nội dung |
|------|:----:|----------|
| `EasyImageUtils.kt` | ~210 | createAttachedPhotoView, downSamplingImage, createThumbnailGlideOptions |
| `EasyFileUtils.kt` | ~60 | initWorkingDirectory, getApplicationDataDirectory, readFileWithSAF |
| `EasyViewUtils.kt` | ~120 | boldString, highlightString, disableTouchEvent, applyMarkDownEllipsize |
| `EasyConversionUtils.kt" | ~40 | jsonStringToHashMap, hashMapToJsonString, fromHtml |
| `EasyDateUtils.kt` | ~60 | datePickerToTimeMillis, convDateToTimeMillis, getCalendarInstance |
| `EasyStringUtils.kt` | ~30 | summaryDiaryLabel, searchWordIndexes, easyDiaryMimeType |
| `EasyNumberUtils.kt` | ~15 | isNumberString, isStockNumber, findNumber |
| `EasyDialogUtils.kt` | ~55 | createSecondsPickerBuilder, openCustomOptionMenu |
| `EasyDiaryUtils.kt` | ~30 | Delegate object (giữ backward compatibility) |

## Đã hoàn thành: Dọn dẹp deprecated warnings (~20 lỗi)
- **Activity/Display:** Thay `defaultDisplay`, `getSize()`, `getMetrics()` bằng `WindowMetrics` (API 30+).
- **Navigation:** Thay `overridePendingTransition()` bằng `overrideActivityTransition()` (API 34+).
- **Location:** Cập nhật `Geocoder.getFromLocationName()` sử dụng callback (API 33+).
- **Misc:** Thay `PowerManager.isScreenOn` bằng `isInteractive` (API 20+).
- **Utils:** Loại bỏ `IOUtils`, `FileUtils` (Apache Commons), thay bằng Kotlin standard functions (`use`, `copyTo`).
- **Models:** Thay `values()` bằng `entries` cho Enum (Kotlin 1.9+).
- **BaseActivity:** Xử lý `setTaskDescription` theo API level mới.

## Đã hoàn thành: Tái thiết lập Tab Nhiệm vụ (Google Style)
- **ViewModel:** Tách biệt nhiệm vụ Đang làm và Đã hoàn thành, tự động thêm Header phân cách.
- **UI Model:** Thêm `DiaryMainItem.Header` để hỗ trợ hiển thị tiêu đề nhóm trong danh sách.
- **Component:** Redesign `TaskItemCard` theo phong cách Google Tasks (Checkbox tròn, Typography sạch sẽ, hiển thị subtasks và priority label).
- **Layout:** Cập nhật `DiaryMainActivity` để tối ưu padding cho danh sách nhiệm vụ và xử lý Header linh hoạt trên nhiều kích thước màn hình.
- **Đa ngôn ngữ:** Thêm string resource `completed_tasks` cho tiếng Anh và tiếng Việt.

## Đã hoàn thành: Sửa lỗi UI và Logic cho Tab Nhiệm vụ
- **UI:** Cập nhật `TaskItemCard` hiển thị tối đa 3 nội dung check-list của nhiệm vụ ngay tại màn hình chính để người dùng dễ theo dõi mà không cần mở chi tiết.
- **Logic:** Cải thiện `TaskEditDialog` sử dụng `FocusRequester`. Khi người dùng nhấn Enter để xuống dòng trong danh sách check-list, ứng dụng sẽ tự động tạo dòng mới và di chuyển con trỏ (focus) vào dòng vừa tạo.

## Đã hoàn thành: Hoàn thiện tính năng Media & Thumbnails
- **UI:** Nâng cấp `DiaryItemCard` và `DiaryTimelineItem` hiển thị các badge biểu tượng (Ảnh, Video, Link, Vị trí) để người dùng nhận diện nhanh nội dung media.
- **Thumbnails:** Tích hợp `GlideImage` (landscapist) để hiển thị danh sách ảnh/video thu nhỏ mượt mà. Video được đánh dấu bằng icon Play đè lên trên.
- **Soạn thảo:** Cập nhật `DiaryWritingActivity` cho phép chọn cả Ảnh và Video từ thư viện. Thêm khu vực xem trước (Media Preview) cho phép xem và xoá các tệp đính kèm hoặc vị trí trước khi lưu.
- **Kiến trúc:** Chuyển đổi các thành phần UI media từ `AndroidView` (View cũ) sang Pure Compose để tối ưu hiệu năng.

## Đã hoàn thành: Hợp nhất giao diện Đọc & Sửa (Seamless Experience)
- **Hợp nhất:** Thay thế `DiaryReadingActivity` và `DiaryWritingActivity` bằng một màn hình duy nhất `DiaryDetailActivity`.
- **Trải nghiệm:** Khi mở nhật ký cũ, ứng dụng hiển thị ở chế độ **Đọc** (Read mode) với giao diện Typography sạch sẽ, ảnh thu nhỏ lớn hơn. Người dùng có thể chuyển sang chế độ **Sửa** (Edit mode) ngay lập tức bằng nút FAB hoặc nút trên TopBar.
- **Auto-save:** Hỗ trợ lưu thay đổi và quay lại chế độ đọc một cách mượt mà không cần chuyển đổi màn hình vật lý.
- **Dọn dẹp:** Xoá bỏ các file cũ (`DiaryReadingActivity`, `DiaryWritingActivity`, `DiaryReadViewModel`) để tinh gọn dự án.

## Đã hoàn thành: Nâng cấp trải nghiệm thêm Media
- **Luồng chọn thông minh:** Thay thế các nút riêng biệt bằng hai nút chính: **Ảnh (Photo)** và **Video**.
- **Popup lựa chọn:** Khi nhấn vào nút Ảnh hoặc Video, một thông báo lựa chọn (AlertDialog) sẽ hiện ra để ní chọn giữa: **Chọn từ thư viện** hoặc **Chụp/Quay mới**. Việc này giúp giao diện gọn gàng hơn nhiều.
- **Tối ưu hóa:** Tách biệt `photoPickerLauncher` và `videoPickerLauncher` để đảm bảo hệ thống filter đúng định dạng tệp, tránh lỗi khi người dùng chọn nhầm tệp.

## Đã hoàn thành: Rà soát & Sửa lỗi Media (Internal Storage)
- **Sửa lỗi hiển thị ảnh:** Giải quyết triệt để lỗi `SecurityException` khiến ảnh không hiển thị (broken icon) trong trang chi tiết. Ứng dụng hiện tại đã tự động **sao chép** mọi tệp media (Ảnh, Video, Audio) vào bộ nhớ trong của ứng dụng ngay khi được chọn, đảm bảo ảnh luôn hiển thị ổn định ngay cả khi khởi động lại máy.
- **Tính năng Ghi âm (Audio):** Bổ sung nút Audio với hai lựa chọn: **Ghi âm mới** (mở trình ghi âm của máy) hoặc **Chọn từ thư viện**. Hiển thị icon nốt nhạc tinh tế cho các tệp âm thanh đính kèm.
- **Speech-to-Text:** Hoàn thiện tính năng nhập liệu bằng giọng nói (Mic icon). Khi nhấn vào, người dùng có thể đọc để máy tự động chuyển thành văn bản và chèn vào nội dung nhật ký.
- **Đồng bộ giao diện:** Thumbnail đã hiển thị đầy đủ và mượt mà trong trang chi tiết (cả chế độ Đọc và Sửa), đồng nhất với trang danh sách ngoài.

## Đã hoàn thành: Tối ưu hiển thị Thumbnail & Vị trí
- **Loại bỏ Badge:** Xoá bỏ các icon siêu nhỏ không cần thiết vì Thumbnail đã cung cấp thông tin trực quan tốt hơn.
- **Thumbnail Vị trí (Location):** Chuyển đổi hiển thị vị trí sang dạng Thumbnail cùng kích thước (60dp hoặc 120dp) với Ảnh/Video. Vị trí giờ đây nằm chung hàng `LazyRow` với media khác, tạo sự đồng bộ tuyệt đối.
- **Tắt Auto-location:** Vô hiệu hoá tính năng tự động lấy vị trí khi mở màn hình. Người dùng sẽ chủ động nhấn nút "Location" để thêm vị trí khi cần thiết.

## Đã hoàn thành: Nâng cấp tương tác Media & Liên kết
- **Hợp nhất Link vào Media Row:** Các liên kết (Web links) giờ đây được tự động nhận diện từ nội dung và hiển thị dưới dạng Thumbnail đồng bộ với Ảnh/Video trong cả danh sách ngoài và trang chi tiết.
- **Trình xem Media (Viewer):** 
    - **Ảnh:** Tích hợp trình xem ảnh toàn màn hình với tính năng Zoom (Sử dụng `PhotoViewPagerActivity`).
    - **Video:** Tự động mở trình phát video **ngay trong app** (Sử dụng `MediaViewerActivity` với VideoView & MediaController).
    - **Audio:** Mở trình phát nhạc hệ thống cho các tệp âm thanh.
    - **Link:** Sửa lỗi không mở được link. Tự động kiểm tra và thêm tiền tố `https://` nếu thiếu, đảm bảo mở trình duyệt mượt mà.
    - **Vị trí:** Mở ứng dụng Google Maps chính xác tại tọa độ đã lưu khi nhấn vào Thumbnail vị trí.
- **Tương tác thông minh:** Các tính năng "Xem/Mở" này chỉ kích hoạt ở chế độ **Đọc** để tránh xung đột khi người dùng đang muốn xóa/sửa ở chế độ **Sửa**.
- **Tinh giản TopBar:** Loại bỏ nút chuyển đổi chế độ xem dư thừa, giúp thanh công cụ gọn gàng và tập trung vào các tính năng chính (Cây, Lịch trình, Cài đặt).

## Còn lại
- Kiểm tra lại toàn bộ app sau khi dọn dẹp.
