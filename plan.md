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
| `Files.kt` | ~80 | getUriForFile, shareFile, exportDatabaseFile |
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
| `SystemBars.kt` | ~65 | hideSystemBars, applyFullScreenStatusBarTheme |
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
| `EasyConversionUtils.kt` | ~40 | jsonStringToHashMap, hashMapToJsonString, fromHtml |
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

## Còn lại
- Kiểm tra lại toàn bộ app sau khi dọn dẹp.
