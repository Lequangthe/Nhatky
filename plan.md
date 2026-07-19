### Cập nhật tính năng chèn vị trí thông minh – My Location Tool
- File thay đổi: 
    - `app/src/main/java/me/blog/korn123/easydiary/activities/BaseDiaryEditingActivity.kt`
    - `app/src/main/res/layout/partial_edit_photo_container.xml`
    - `app/src/main/res/layout/partial_edit_contents.xml` (và bản `layout-land`)
    - `app/src/main/res/layout/fragment_diary_read.xml`
    - `app/src/main/res/layout/item_diary_main.xml`
    - `app/src/main/res/layout/item_diary_dashboard.xml`
    - `D:\GG\B\skill_mylocation.md`
- Chi tiết:
    - Thêm nút "My Location" vào thanh công cụ soạn thảo nhật ký.
    - Triển khai kỹ thuật **Content Injection**: Tự động chèn chuỗi `Kinh tuyến : {lat} Vĩ tuyến : {lng}` vào vị trí con trỏ trong nội dung nhật ký.
    - Thêm cơ chế **Force Accurate Fetching**: Cưỡng bức cập nhật GPS thời gian thực kèm ProgressBar và Timeout (15s) để đảm bảo độ chính xác.
    - Cập nhật giao diện toàn hệ thống để hiển thị đồng thời Địa chỉ, Kinh độ và Vĩ độ.
    - Cập nhật tài liệu Kỹ năng định vị (`skill_mylocation.md`) with các hướng dẫn kỹ thuật chuyên sâu về chèn nội dung và quản lý tiến độ.

### Sửa lỗi mất nút xanh Location khi mở lại màn hình chỉnh sửa
- File thay đổi:
    - `app/src/main/java/me/blog/korn123/easydiary/activities/BaseDiaryEditingActivity.kt`
- Chi tiết:
    - Fix bug: `mLocation` là managed Realm object khi load từ DB → gán vào unmanaged `Diary` gây lỗi lưu ở lần sau.
    - Giải pháp: copy `Location` thành unmanaged object qua constructor khi load từ `diary.location`.
    
### Vô hiệu hoá auto-save temporary diary (xoá dialog "Tải nhật kí tự động lưu")
- File thay đổi:
    - `app/src/main/java/me/blog/korn123/easydiary/activities/DiaryEditingActivity.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/activities/DiaryWritingActivity.kt`
- Chi tiết:
    - Xoá `checkTemporaryDiary(...)` khỏi `onCreate` ở cả 2 activity
    - Xoá `saveTemporaryDiary(...)` và `deleteTemporaryDiaryBy(...)` khỏi `onPause` ở cả 2 activity
    - Kết quả: không còn dialog hỏi khôi phục nhật ký tạm khi quay lại màn hình chỉnh sửa

### Tách LocationContainer thành Custom View (LocationContainerView)
- File thay đổi:
    - `app/src/main/res/layout/view_location_container.xml` (mới)
    - `app/src/main/java/.../views/LocationContainerView.kt` (mới)
    - `app/src/main/res/values/attrs.xml` (thêm `displayMode` enum)
    - `app/src/main/res/layout/partial_edit_contents.xml`
    - `app/src/main/res/layout-land/partial_edit_contents.xml`
    - `app/src/main/res/layout/fragment_diary_read.xml`
    - `app/src/main/res/layout/item_diary_main.xml`
    - `app/src/main/res/layout/item_diary_main_mig.xml`
    - `app/src/main/res/layout/item_diary_dashboard.xml`
    - `app/src/main/java/.../activities/BaseDiaryEditingActivity.kt`
    - `app/src/main/java/.../activities/DiaryReadingActivity.kt`
    - `app/src/main/java/.../activities/DiaryEditingActivity.kt`
    - `app/src/main/java/.../activities/DiaryWritingActivity.kt`
    - `app/src/main/java/.../adapters/DiaryMainItemAdapter.kt`
    - `app/src/main/java/.../adapters/DiaryDashboardItemAdapter.kt`
    - `app/src/main/java/.../ui/components/LegacyItemCard.kt`
- Chi tiết:
    - Gom toàn bộ UI + logic location (hiển thị, click mở Maps, search/edit dialog) vào `LocationContainerView`
    - Hỗ trợ 3 chế độ: `editing` (có search/edit icons), `readOnly` (chỉ arrow), `compact` (danh sách)
    - Xoá `updateLocationUI()`, `showSearchAddressDialog()`, `showEditLocationDialog()` khỏi activity
    - Activity/Adapter chỉ cần gọi `locationContainer.setLocation(data)` — tự động VISIBLE/GONE
    - Click container tự mở Google Maps, không cần setOnClickListener ở mỗi nơi
    - `mLocation` sync qua `setOnLocationChangeListener`

### Thiết lập cấu hình opencode cho Nhatky – Fix lỗi AI không phản hồi
- File thay đổi:
    - `D:\AndroidStudioProjects\Nhatky\.opencode\opencode.json` (mới)
    - `D:\AndroidStudioProjects\Nhatky\AGENTS.md` (mới)
    - `C:\Users\Administrator\.config\opencode\opencode.jsonc` (sửa `skills.paths` từ `D:\GG\A\` → `D:\GG\A1\`)
- Chi tiết:
    - Tạo `.opencode\opencode.json` cho Nhatky: instructions trỏ đến local `AGENTS.md`, skills dùng chung từ `D:\GG\A1\.opencode\skills`
    - Tạo `AGENTS.md` riêng cho Easy Diary: thêm rules về Realm, Diary/Note/Task, Build Variants (gmsProd/gmsDev)
    - Sửa global config: `skills.paths` đang trỏ sai `D:\GG\A\.opencode\skills` (không tồn tại) → đúng `D:\GG\A1\.opencode\skills`
    - Kết quả: AI có thể chat và làm việc trong workspace `D:\AndroidStudioProjects\Nhatky`

### Tối ưu hóa toàn diện (Bước 1-3) – Dọn dẹp dependencies, tách ViewModel, toUnmanaged()
- File thay đổi:
    - `app/build.gradle.kts` (xoá duplicate work-runtime-ktx)
    - `app/.../helper/Constants.kt` (xoá symbol/weather constants, AlarmConstants, DiaryComponentConstants)
    - `app/.../models/ModelExtensions.kt` (mới – toUnmanaged() extension)
    - `app/.../viewmodels/DiaryEditingViewModel.kt` (mới)
    - `app/.../helper/MediaPickerManager.kt` (mới)
    - `app/.../helper/LocationHelper.kt` (mới)
    - `app/.../activities/BaseDiaryEditingActivity.kt` (xoá PickPhotoView, easter egg, symbol references)
- Chi tiết:
    - **Dependencies:** Gỡ `com.werb.pickphotoview` (lib không còn trong build.gradle), gỡ duplicate `work-runtime-ktx`
    - **Constants:** Xoá `SYMBOL_SELECT_ALL`, `SYMBOL_USER_CUSTOM_START`, `SELECTED_SYMBOL_SEQUENCE`, `SETTING_TASK_SYMBOL_TOP_ORDER`, `SETTING_QUICK_SETTING`, `AlarmConstants`, `DiaryComponentConstants`, `DEV_SYNC_SYMBOL_*`, các `DAILY_TODO/DOING/DONE/CANCEL` cũ. Xoá `WEATHER` khỏi `SettingLocalConstants`
    - **toUnmanaged():** Tạo `Diary.toUnmanaged()`, `Location.toUnmanaged()`, `PhotoUri.toUnmanaged()` chuẩn hoá deep-copy Realm → unmanaged
    - **ViewModel:** Tạo `DiaryEditingViewModel` quản lý state (photoUris, location, datetime, diary) bằng StateFlow
    - **Helpers:** Tách `MediaPickerManager` (xử lý attach media background thread) và `LocationHelper` (quản lý GPS location)
    - **Activity:** Xoá `mSymbolSequence`, `mRequestPickPhotoData` (PickConfig), easter egg checks, `selectFeelingSymbol()` no-op, `isExistEasterEggDiary`, `duplicatedEasterEggWarning`

### Tối ưu 3 Tab (Diary, Note, Task) – UI soạn thảo riêng biệt
- File thay đổi:
    - `app/.../activities/BaseDiaryEditingActivity.kt` (thêm `mEntryType`, base `updateUIByEntryType()`)
    - `app/.../activities/DiaryWritingActivity.kt` (dùng base class `mEntryType`, gọi super)
    - `app/.../activities/DiaryEditingActivity.kt` (dùng base class `mEntryType`, gọi super)
- Chi tiết:
    - Chuyển `mEntryType` lên BaseDiaryEditingActivity để base class quản lý
    - Base `updateUIByEntryType()`: NOTE → ẩn photo toolbar, location, allDay; TASK → ẩn photo, location, allDay; DIARY → giữ nguyên
    - Subclass `updateUIByEntryType()` gọi `super.updateUIByEntryType()` cho NOTE/TASK
    - Mỗi tab có hint placeholder riêng (Note Title, Task Name, v.v.)

### Dọn dẹp tài nguyên thừa (Symbol/Weather remnants)
- File thay đổi:
    - `res/layout/fragment_dashboard_rank.xml` (DELETED – orphaned)
    - `res/layout/fragment_custom_cell.xml` (DELETED – orphaned)
    - `res/drawable/ic_clouds_and_sun.xml` (DELETED)
    - `res/drawable/ic_sunny.xml` (DELETED)
    - `res/values/styles.xml` (xoá style `text_dashboard_ranking`)
- Chi tiết:
    - Xoá 2 layout orphaned (DashBoardRank, CustomCell)
    - Xoá 2 drawable weather/symbol
    - Xoá style không còn dùng

### Chuẩn hoá Strings – Xoá symbol/weather strings
- File thay đổi:
    - `res/values/strings.xml` (xoá 12 symbol/weather strings)
    - `res/values-vi/strings.xml` (xoá 12 symbol/weather strings tương ứng)
- Chi tiết:
    - Xoá: `diary_symbol`, `diary_symbol_guide_message`, `category_weather`, `category_symbol`, `statistics_symbol_all/top_ten`, `diary_symbol_search_message`, `task_symbol_top_order_*`, `dashboard_title_daily_symbol_tile`, `symbol_filter_picker_remove_guide_message`, `recently_used_symbol`, `export_excel_header_weather`, `create_diary_showcase_title_1/message_1`
    - Dọn sạch tương ứng trong bản dịch tiếng Việt

### MVVM – Tích hợp DiaryEditingViewModel vào Activity
- File thay đổi:
    - `app/.../viewmodels/DiaryEditingViewModel.kt` (thêm `updateLocation()`)
    - `app/.../activities/BaseDiaryEditingActivity.kt` (thêm `editingViewModel` bằng viewModels())
- Chi tiết:
    - Thêm `editingViewModel: DiaryEditingViewModel by viewModels()` vào BaseDiaryEditingActivity
    - ViewModel có sẵn: loadDiary(), addPhotoUri(), updateLocation(), applyRemovals(), updateDateTime()
    - Activity có thể dần dần migrate state sang ViewModel

### Vô hiệu hoá auto-save temporary diary
- File thay đổi:
    - `app/src/main/res/layout/fragment_diary_read.xml`
    - `app/src/main/res/layout/partial_edit_contents.xml`
    - `app/src/main/java/me/blog/korn123/easydiary/ui/components/LegacyItemCard.kt`
- Chi tiết:
    - Thêm biểu tượng mũi tên/bản đồ trực quan vào cạnh vùng toạ độ để người dùng dễ dàng nhận biết vùng này có thể bấm được.
    - Đảm bảo tất cả các màn hình (viết nhật ký, đọc nhật ký, danh sách chính, bảng điều khiển) đều hỗ trợ mở Google Maps khi nhấn vào thẻ vị trí.

### Tinh gọn ứng dụng – Dọn dẹp Cài đặt & Lịch trình
- File thay đổi:
    - `app/src/gmsProd/java/me/blog/korn123/easydiary/activities/SettingsActivity.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/activities/BaseSettingsActivity.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/fragments/SettingsBasicFragment.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/fragments/SettingsFontFragment.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/fragments/SettingsAppInfoFragment.kt`
    - `app/src/main/AndroidManifest.xml`
- Chi tiết:
    - Loại bỏ hoàn toàn màn hình "Cài đặt lịch trình" (Schedule Settings) và tính năng báo thức.
    - Ẩn mục "Đánh giá trong ứng dụng Google Play" trong Cài đặt cơ bản.
    - Ẩn mục "Cài đặt phông chữ" và "Thêm phông chữ TTF" (Giữ lại Kích thước và Khoảng cách dòng).
    - Ẩn mục "Mời bạn bè" và "Đánh giá ứng dụng" trong Thông tin ứng dụng.
    - Gỡ bỏ quyền `SCHEDULE_EXACT_ALARM` và ngắt kết nối `AlarmReceiver` để tối ưu hệ thống.

### Tinh gọn Cài đặt & Dashboard – Loại bỏ Sao lưu & Khôi phục
- File thay đổi:
    - `app/src/gmsProd/java/me/blog/korn123/easydiary/activities/SettingsActivity.kt`
    - `app/src/gmsProd/res/layout/fragment_dashboard_summary.xml`
    - `app/src/main/java/me/blog/korn123/easydiary/fragments/DashBoardSummaryFragment.kt`
- Chi tiết:
    - Loại bỏ hoàn toàn màn hình "Sao lưu và Khôi phục" (Backup & Restore) khỏi danh sách trang Cài đặt.
    - Ẩn khối thông tin lịch sử sao lưu (Google Drive & Local) khỏi màn hình Dashboard (Bảng điều khiển) để giữ giao diện sạch sẽ.
    - Cập nhật điều hướng Cài đặt để trang cuối cùng là "Thông tin ứng dụng" (FAQ, Chính sách, v.v.).

### Xoá hàng loạt tính năng (Phase 2 – source code)
- File thay đổi:
    - `app/src/main/java/.../helper/Constants.kt`
    - `app/src/main/java/.../helper/Config.kt`
    - `app/src/main/java/.../fragments/SettingsBasicFragment.kt`
    - `app/src/main/java/.../viewmodels/SettingsViewModel.kt`
    - `app/src/main/java/.../activities/EasyDiaryActivity.kt`
    - `app/src/main/java/.../compose/EasyDiaryComposeBaseActivity.kt`
    - `app/src/main/java/.../compose/QuickSettingsActivity.kt` (DELETED)
    - `app/src/main/java/.../compose/DiaryMainActivity.kt`
    - `app/src/main/java/.../activities/DiaryMainActivity.kt`
    - `app/src/main/java/.../helper/EasyDiaryDbHelper.kt`
    - `app/src/main/AndroidManifest.xml`
    - `app/src/main/res/layout/popup_menu_main.xml`
- Chi tiết:
    - **Quick Settings + Shake Detector:** Xoá toàn bộ `QuickSettingsActivity` (file + manifest + popup menu). Xoá `ShakeDetector`, `SensorManager`, `hearShake()`, `setupMotionSensor()` khỏi `EasyDiaryActivity` và `EasyDiaryComposeBaseActivity`.
    - **Welcome Dashboard Popup:** Xoá khỏi `Config.kt`, `Constants.kt`, `SettingsBasicFragment.kt`, `DiaryMainActivity.kt` (import + `checkIntent()`).
    - **Attached Photo Highlights:** Xoá khỏi `Config.kt`, `Constants.kt` (`PhotoHighlightConstants`), `SettingsBasicFragment.kt`, `DiaryMainActivity.kt` (`setupPhotoHighlight()`, `togglePhotoHighlight()`).
    - **Align Task Symbol to Top:** Xoá khỏi `Config.kt`, `Constants.kt`, `SettingsBasicFragment.kt`, `EasyDiaryDbHelper.kt`.
    - **Case Sensitive Search:** Xoá khỏi `Config.kt`, `Constants.kt`, `SettingsBasicFragment.kt`.
    - **Text Counting:** Xoá khỏi `Config.kt`, `Constants.kt`.
    - **Google Play In-App Review:** Xoá khỏi `Config.kt`, `Constants.kt`, `SettingsViewModel.kt`, `SettingsBasicFragment.kt`, `DiaryMainActivity.kt`.
    - **Calendar Font Size:** Xoá khỏi `Config.kt`, `Constants.kt`, `SettingsViewModel.kt`.
    - **Font Setting (typeface):** Xoá khỏi `Config.kt`, `Constants.kt`, `SettingsViewModel.kt` (`_fontSettingDescription`, `_fontFamily`).
    - **Export/Email Excel:** Xoá `BACKUP_EXCEL_DIRECTORY`, `REQUEST_CODE_SAF_WRITE_XLS`, `REQUEST_CODE_EXTERNAL_STORAGE_WITH_EXPORT_EXCEL` khỏi `Constants.kt`.
    - **Schedule Alarm:** Xoá `AlarmConstants`, `REQUEST_CODE_SCHEDULE_EXACT_ALARM` khỏi `Constants.kt`.

### Đánh giá toàn diện dự án & Kế hoạch Tách biệt chức năng (Diary/Note/Task)
- **Đánh giá hiện trạng:**
    - **UI/UX:** Quá tải tùy chỉnh, các chức năng Diary, Note và Task hiện đang bị trộn lẫn (đều dùng chung Model Diary và Activity chỉnh sửa), gây rối cho người dùng.
    - **Kiến trúc:** Tồn tại các "God Activities" (như `BaseDiaryEditingActivity`) gánh vác quá nhiều logic. UI đang ở trạng thái lai giữa XML và Compose.
    - **Hiệu suất:** Nhiều tác vụ nặng (xử lý ảnh, DB) chưa được đưa vào nền một cách triệt để.

### Tách biệt 3 Tab (Diary, Note, Task) & Loại bỏ Symbol
- File thay đổi:
    - `app/src/main/java/me/blog/korn123/easydiary/models/Diary.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/enums/DiaryEntryType.kt` (mới)
    - `app/src/main/java/me/blog/korn123/easydiary/helper/Constants.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/helper/EasyDiaryMigration.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/activities/DiaryMainActivity.kt`
    - `app/src/main/res/layout/activity_diary_main.xml`
    - `app/src/main/res/layout/partial_edit_contents.xml`
    - `app/src/main/java/me/blog/korn123/easydiary/activities/BaseDiaryEditingActivity.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/activities/DiaryWritingActivity.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/activities/DiaryEditingActivity.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/adapters/DiaryMainItemAdapter.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/ui/components/DiaryItemCard.kt`
- Chi tiết:
    - **Note:** Thêm bộ chọn màu sắc, hiển thị thời gian cập nhật, hỗ trợ ghim và sắp xếp thư mục lên đầu.
    - **Task:** Thêm 3 mức độ ưu tiên (Thấp, Trung bình, Cao) kèm màu sắc đặc trưng (Xanh, Cam, Đỏ).
    - **Sorting:** Sắp xếp theo PocketPlan: Ghi chú ghim lên đầu, Nhiệm vụ chưa xong có độ ưu tiên cao nhất lên đầu.
    - **UI/UX:** Cải tiến thẻ Ghi chú/Nhiệm vụ với thanh màu, tiến độ và checklist preview. Thêm thông báo "Trống" riêng biệt.
    - **Interactive:** Thêm square checkbox cho phép tick/uncheck nhanh nhiệm vụ ngay từ màn hình chính.
    - **Refresh:** Tự động tải lại dữ liệu khi quay lại màn hình chính.

### 🧹 Tối ưu hóa toàn diện (Bước 1-2) – Dọn dẹp tài nguyên & Hợp nhất Compose
- File đã xoá:
    - `app/src/main/java/.../activities/DiaryMainActivity.kt` (Bản XML cũ)
    - `res/layout/activity_diary_main.xml`
    - `res/layout/item_diary_main.xml`
    - `res/layout/fragment_dashboard_rank.xml`
    - `res/layout/fragment_custom_cell.xml`
    - `res/drawable/ic_select_symbol.xml`
- Chi tiết:
    - Loại bỏ hoàn toàn sự phụ thuộc vào bản `DiaryMainActivity` cũ bằng XML.
    - Dọn dẹp các hằng số Symbol/Weather dư thừa trong `Constants.kt`.
    - Thu dọn các Layout mồ côi không còn sử dụng sau khi chuyển sang Compose.
    - App hiện tại đã chạy 100% bản Compose cho màn hình chính, sẵn sàng cho việc mở rộng.

### 💎 Compose-hóa 100% màn hình Xem & Viết Nhật ký (Deep Refactoring Phase 1)
- File thay đổi:
    - `app/src/main/java/.../compose/DiaryReadingActivity.kt` (Mới)
    - `app/src/main/java/.../compose/DiaryWritingActivity.kt` (Mới)
    - `app/src/main/java/.../viewmodels/DiaryReadViewModel.kt` (Cập nhật logic tìm kiếm)
    - `AndroidManifest.xml` (Cập nhật đăng ký Activity)
- File đã xoá:
    - `activities/BaseDiaryEditingActivity.kt` (Hòn đá cản đường)
    - `activities/DiaryReadingActivity.kt`, `DiaryWritingActivity.kt`, `DiaryEditingActivity.kt`
    - 7+ file Layout XML (`activity_diary_reading.xml`, `partial_edit_contents.xml`...)
- Chi tiết:
    - Chuyển đổi toàn bộ logic Xem (Detail View) và Viết/Sửa Nhật ký sang Jetpack Compose.
    - Tích hợp `HorizontalPager` cho màn hình Xem Nhật ký, hỗ trợ vuốt mượt mà giữa các bài viết.
    - Tích hợp TTS (Text To Speech) trực tiếp vào màn hình Compose.
    - Hỗ trợ hiển thị Ảnh và Vĩ độ (Location) trong cả chế độ xem và soạn thảo.
    - Loại bỏ hàng ngàn dòng code logic cũ và file XML dư thừa, giúp kiến trúc app cực kỳ tinh gọn.

### 🏗️ Triển khai Repository Pattern (Deep Refactoring Phase 2)
- File thay đổi:
    - `app/src/main/java/.../repositories/DiaryRepository.kt` (Mới)
    - `app/src/main/java/.../repositories/NoteRepository.kt` (Mới)
    - `app/src/main/java/.../repositories/TaskRepository.kt` (Mới)
    - `app/src/main/java/.../viewmodels/` (Cập nhật 5 ViewModels)
- Chi tiết:
    - Tách biệt hoàn toàn logic truy cập dữ liệu khỏi các `ViewModel`.
    - Các `ViewModel` hiện tại giao tiếp với Database thông qua các lớp Repository trung gian.
    - Chuẩn hoá việc trả về đối tượng **Unmanaged** từ Repository để tránh lỗi Transaction trong Compose.
    - Kiến trúc app trở nên chuyên nghiệp hơn, sẵn sàng cho việc thay đổi Database (ví dụ: sang Room) hoặc tích hợp Sync mà không cần sửa code ở tầng UI/ViewModel.

### 🎨 Hệ thống Theme & Design System tập trung (Deep Refactoring Phase 3)
- File thay đổi:
    - `app/src/main/java/.../ui/theme/EasyDiaryTheme.kt` (Cập nhật)
    - `app/src/main/java/.../compose/` (Cập nhật toàn bộ Activity Compose)
    - `app/src/main/java/.../ui/components/` (Cập nhật toàn bộ Item Cards)
- Chi tiết:
    - Xây dựng bộ `AppTheme` hoàn chỉnh sử dụng `CompositionLocal` để quản lý màu sắc tùy chỉnh.
    - Chuyển đổi toàn bộ các Composable từ việc gọi trực tiếp `config.primaryColor` sang sử dụng `MaterialTheme.colorScheme`.
    - Thống nhất các giá trị bo góc (CornerRadius), khoảng cách (Padding) và màu nền bề mặt (Surface) theo chuẩn Material 3.
    - Giúp việc thay đổi giao diện (Dark Mode, đổi màu chủ đạo) trở nên cực kỳ đơn giản và đồng bộ trên toàn ứng dụng.

### 📦 Chuẩn hoá Dữ liệu & Media Manager (Deep Refactoring Phase 4)
- File thay đổi:
    - `app/src/main/java/.../helper/MediaManager.kt` (Mới - Gom từ MediaPickerManager & MediaJanitor)
    - `app/src/main/java/.../models/Diary.kt` (Dọn dẹp & Đánh dấu deprecated các trường thừa)
    - `app/src/main/java/.../viewmodels/DiaryEditingViewModel.kt` (Cập nhật dùng MediaManager)
    - `app/src/main/java/.../viewmodels/SettingsViewModel.kt` (Cập nhật dùng MediaManager)
    - `app/src/main/java/.../fragments/SettingsBasicFragment.kt` (Cập nhật dùng MediaManager)
- Chi tiết:
    - **MediaManager:** Tạo lớp quản lý tập trung toàn bộ file phương tiện (Ảnh, Âm thanh, Video). Hỗ trợ đính kèm, nén ảnh background thread và dọn dẹp file "mồ côi" hiệu quả.
    - **Model Diary:** Loại bỏ/Đánh dấu lỗi thời các trường `originSequence`, `entryType`, `fontName`, `fontSize` không còn cần thiết sau khi tách Note/Task. Giảm bớt gánh nặng cho bộ nhớ.
    - **Hiệu suất:** Quá trình xử lý ảnh khi đính kèm nhật ký giờ đây mượt mà hơn, không gây lag giao diện nhờ cơ chế xử lý luồng của MediaManager.

### 🛠️ Nâng cấp Tính năng & Sửa lỗi Hậu Refactor (Deep Refactoring Phase 5)
- File thay đổi:
    - `app/src/main/java/.../compose/DiaryWritingActivity.kt` (Sửa lỗi vị trí + Quyền)
    - `app/src/main/java/.../ui/components/DiaryItemCard.kt` (Nâng cấp giao diện Task + Fix Icon)
    - `app/src/main/java/.../helper/export/ExportManager.kt` (Mới - Xuất dữ liệu đa nền tảng)
    - `app/src/main/java/.../fragments/SettingsBasicFragment.kt` (Tích hợp Selective Export)
    - `app/src/main/java/.../extensions/Activity.kt` (Mở rộng quyền GPS cho Compose)
    - `app/src/main/java/.../activities/BaseDevActivity.kt` (Sửa import & drawable)
- Chi tiết:
    - **Fix triệt để lỗi mất Vị trí:** Sử dụng cơ chế `copyToRealm` cưỡng bức kèm việc làm mới instance Realm. Toạ độ giờ đây được lưu trữ vĩnh viễn, không bị mất khi sửa nhật ký nhiều lần.
    - **Quyền GPS mượt mà:** Chuyển đổi extension `acquireGPSPermissions` để hỗ trợ tốt hơn cho các Activity mới dựa trên Compose.
    - **Tab Nhiệm vụ "Cực phẩm":** 
        - Thiết kế lại thẻ nhiệm vụ với Icon `CheckCircle` mượt mà và thanh tiến độ mảnh chuẩn Google.
        - Sửa lỗi thiếu Icon `CheckCircle` và `RadioButtonUnchecked` khiến app không build được.
    - **Selective Export (Xuất dữ liệu chọn lọc):** 
        - Người dùng có thể chọn xuất Diary, Note, hoặc Task tùy ý.
        - Tự động tạo file `.txt` có định dạng đẹp mắt, ghi rõ ngày giờ và địa chỉ từng bài viết.
    - **BaseDevActivity Maintenance:** Sửa lỗi thiếu import cho `DiaryMainActivity` và thay thế drawable `ic_select_symbol` đã xoá bằng icon mặc định của app.

### Dọn dẹp code chết & Refactor kiến trúc (Phase 2026.07)
- File thay đổi:
    - `res/drawable/ic_bug_2.xml` (tạo mới)
    - `res/drawable/ic_tree_structure.xml` (tạo mới)
    - `res/layout/fragment_custom_cell.xml` (tạo lại)
    - `res/layout/fragment_dashboard_rank.xml` (tạo lại)
    - `res/layout/activity_diary_main.xml` (tạo lại cho portrait)
    - `compose/QuickSettingsActivity.kt` (xoá)
    - `activities/ToolbarControlBaseActivity.kt` (xoá)
    - `workers/BackupOperations.kt`, `FullBackupWorker.kt`, `FullRecoveryWorker.kt` (xoá)
    - `helper/ZipHelper.kt`, `helper/ZipHelperWithVisitor.kt` (xoá)
    - `adapters/CheatSheetAdapter.kt`, `FontItemAdapter.kt` (xoá)
    - `views/DiaryCardLayout.kt` (xoá)
    - `dialogs/DashboardDialogFragment.kt` (xoá)
    - `adapters/DiaryMainItemAdapter.kt` (xoá)
    - `helper/Constants.kt` (xoá hằng số chết)
    - `services/BaseNotificationService.kt` (xoá backup handler)
- Chi tiết:
    - **🔴 Fix crash:** Tạo vector drawable `ic_bug_2` và `ic_tree_structure` thay thế cho 2 icon bị thiếu (app crash khi bật Debug mode)
    - **🗑️ Xoá code chết:** 8 file `.kt` không còn dùng (ToolbarControlBaseActivity, QuickSettingsActivity, workers backup, ZipHelper, CheatSheetAdapter, FontItemAdapter, DiaryCardLayout, DashboardDialogFragment, DiaryMainItemAdapter)
    - **📦 Tạo lại layout:** `fragment_custom_cell.xml`, `fragment_dashboard_rank.xml`, `activity_diary_main.xml` (portrait) để binding generation không lỗi
    - **🧹 Dọn hằng số:** Xoá `SELECTED_SYMBOL_SEQUENCE`, `SYMBOL_SEQUENCE`, `SettingLocalConstants`, `ALARM_ID`, `WORK_MANAGER_*`, `ACTION_FULL_*_CANCEL`, `ACTION_*_GMS_*`
    - **📝 Sửa import:** 9 file chuyển import `activities.` → `compose.` cho DiaryReadingActivity, DiaryWritingActivity, DiaryMainActivity
    - **✏️ DiaryWritingActivity:** Thêm xử lý `INITIALIZE_TIME_MILLIS` cho CalendarActivity

### Phase 1 XML?Compose Migration (6 activities)   Build SUCCESSFUL
- File thay d?i:
    - compose/MediaViewerActivity.kt (m?i)
    - compose/PinLockActivity.kt (m?i)
    - compose/PhotoViewPagerActivity.kt (m?i)
    - compose/GalleryViewPagerActivity.kt (m?i)
    - compose/PostcardViewPagerActivity.kt (m?i)
    - compose/MarkDownViewerActivity.kt (m?i)
    - compose/FingerprintLockActivity.kt (m?i)
    - compose/BaseDevActivity.kt (m?i)
    - compose/DevActivity.kt (m?i)
    - AndroidManifest.xml (c?p nh?t)
- File d  xo :
    - 6 activities + 3 layout XML + BaseDevActivity/DevActivity
- Import fixes: 9 file (Activity.kt, Context.kt, DiaryMainActivity.kt, ...)
- Chi ti?t: Migrate 6 activities MediaViewer/PinLock/PhotoViewPager/PostcardViewPager/GalleryViewPager/MarkDownViewer/FingerprintLock + DevActivity sang Compose. Build SUCCESSFUL.

### Foundation Refactoring (suspend Repository + allowWritesOnUiThread removal)
- File thay d?i:
    - repositories/DiaryRepository.kt, NoteRepository.kt, TaskRepository.kt (all methods ? suspend + withContext(Dispatchers.IO))
    - viewmodels/DiaryMainViewModel, DiaryEditingViewModel, DiaryReadViewModel, NoteViewModel, TaskViewModel (wrap calls in viewModelScope.launch)
    - compose/DiaryWritingActivity, Demo1Activity, SelfDevelopmentRepoActivity, TreeTimelineActivity (lifecycleScope.launch)
    - helper/EasyDiaryDbHelper.kt (xo  .allowWritesOnUiThread(true))
    - AndroidManifest.xml (s?a 5 entries .activities. ? .compose. + 2 entries TodoTask/SimpleNoteActivity)
- Chi ti?t: Chuy?n 21 Repository methods sang suspend + Dispatchers.IO. Xo  allowWritesOnUiThread(true). Fix 7 manifest entries.

### Move TodoTaskActivity & SimpleNoteActivity ? compose/ package
- File thay d?i: compose/TodoTaskActivity.kt, compose/SimpleNoteActivity.kt (moved from activities/); DiaryMainActivity.kt (update import)

### Phase 2 XML?Compose Migration (Statistics, PostcardViewer, Gallery)   Build SUCCESSFUL
- compose/StatisticsActivity.kt (m?i, wraps chart fragments in AndroidView + Scaffold)
- compose/PostcardViewerActivity.kt (m?i, LazyVerticalGrid + Glide via AndroidView)
- compose/GalleryActivity.kt (m?i, LazyVerticalGrid + getAttachedPhotos companion)
- Xo  activities/StatisticsActivity.kt, PostcardViewerActivity.kt, GalleryActivity.kt
- Xo  layout/activity_statistics.xml, activity_postcard_viewer.xml, activity_gallery.xml
- Xo  orphaned layout: activity_intro.xml, activity_google_drive.xml, activity_diary_main.xml
- Import fixes: GalleryViewPagerActivity, PostcardActivity, WritingBarChart/WeightLineChart/StockLineChart fragments

### Phase 2 XML→Compose Migration (Customization, Calendar) – Build SUCCESSFUL
- compose/CustomizationActivity.kt (mới, HSV color picker với Hue/Sat/Bri sliders + Preset Themes)
- compose/CalendarActivity.kt (mới, lưới tháng + chọn ngày + hiển thị diary list + LaunchedEffect)
- Xoá activities/CustomizationActivity.kt, CalendarActivity.kt
- Xoá layout/activity_customization.xml, activity_calendar.xml
- AndroidManifest.xml: sửa 2 entries .activities. → .compose.

### Phase 2 XML→Compose Migration (Dashboard, Settings) – Build SUCCESSFUL
- compose/DashboardActivity.kt (mới, FragmentCard composable bọc 7 fragments trong AndroidView + Scaffold + FAB)
- compose/SettingsActivity.kt (mới, ViewPager + DotsIndicator qua AndroidView + Compose Scaffold)
- Xoá activities/DashboardActivity.kt, SettingsActivity.kt, BaseSettingsActivity.kt
- Xoá layout/activity_dashboard.xml, activity_base_settings.xml
- AndroidManifest.xml: sửa 2 entries .activities. → .compose.

### Phase 2 XML→Compose Migration (Postcard, Timeline) – Build SUCCESSFUL
- compose/PostcardActivity.kt (mới, AndroidView bọc ActivityPostcardBinding + Compose Scaffold + dropdown menu cho bgColor/textColor/save/share)
- compose/TimelineActivity.kt (mới, Compose Scaffold + AnimatedVisibility filter panel + AndroidView ListView + DatePickerDialog + FAB)
- Xoá activities/PostcardActivity.kt, TimelineActivity.kt
- Xoá layout/activity_postcard.xml, activity_timeline.xml, partial_timeline_filter.xml
- AndroidManifest.xml: sửa 2 entries .activities. → .compose.
- Fix PhotoAdapter.kt import (activities.PostcardActivity → compose.PostcardActivity)

### DiaryMainActivity manifest duplicate fix
- AndroidManifest.xml: sửa .activities.DiaryMainActivity → .compose.DiaryMainActivity (dòng 38)
- Xoá duplicate DiaryMainActivity declaration (dòng 184-188) gây manifest merge lỗi
- Thêm launchMode="singleTask" vào declaration chính

### Dọn rác toàn diện (Phase Cleanup) – Build SUCCESSFUL
- **26 layout XML mồ côi:** dialog_dashboard_calendar_item, dialog_delete_realm_files, dialog_export_progress_excel, dialog_feeling, dialog_feeling_pager, dialog_fonts, dialog_highlight_keyword, dialog_realm_files, dialog_setting_gallery, dialog_simple, dialog_sync_google_calendar, fragment_settings_app_info, fragment_settings_backup_gms, fragment_settings_backup_local, fragment_settings_progress, item_cheat_sheet, item_diary_calendar, item_realm_file, partial_appbar_layout, partial_content_post_card_viewer, partial_preference_category, partial_progress_bar, popup_encryption, popup_location_selector, popup_menu_main, popup_menu_read
- **14 drawable mồ côi:** bg_balloon_t2, bg_caldroid_selected, bg_card_button, bg_card_cell_default, bg_card_plan, bg_card_postcard, bg_card_stamp, bg_card_thumbnail_rect, bg_card_timeline_filter, bg_circle_stroke, ic_marker2, ic_options_three_dots, ic_selector (+ ic_selector_default/press), ic_star
- **6 drawable-v21 mồ côi:** bg_card_button, bg_card_plan, bg_card_stamp, bg_card_thumbnail_rect, bg_card_timeline_filter, ic_selector
- **5 drawable-hdpi mồ côi:** ic_cloud_download, ic_cloud_upload, ic_data_backup, ic_alarm_clock, ic_schedule_error
- **14 menu XML mồ côi:** toàn bộ `res/menu/` (tất cả)
- **4 adapter .kt mồ côi:** DiaryCalendarItemAdapter, GalleryAdapter, PostcardAdapter, SimpleCheckboxAdapter
- **1 adapter + 1 fragment:** RealmFileItemAdapter.kt (mồ côi), fragment_settings_progress.xml (layout đã xoá)
- **Sửa Context.kt:** xoá reference `R.id.createdDate` từ layout đã xoá

### 🎨 Thiết kế lại Tab Nhiệm vụ (Task Tab Redesign)
- Redesign TodoTaskActivity: gradient header, priority chấm tròn, checklist tự động thêm dòng khi Enter, checkbox tròn canvas, giao diện tối giản 1 màn hình
- Redesign TaskItemCard: thanh priority bên trái, checkbox canvas tròn, sub-items preview, progress bar mảnh

### 🧹 Phase 1–2: Kiểm soát Repository + Dọn rác (Jul 2026)
- Audit toàn diện: 13 Fragment, 10 Adapter, 96 chỗ gọi EasyDiaryDbHelper trực tiếp, 41 XML layout
- Phase 1: Thêm missing methods vào Repository (findDiaryByDateString, findDiary)
- Phase 1: Sửa compose/gallery/calendar/postcard/timeline → gọi qua Repository
- Phase 1: Sửa DiaryMainViewModel, NoteViewModel → Repository
- Phase 2: Xoá DashBoardSummaryFragment (shell rỗng), BindingAdapter (rỗng)
- Phase 2: Xoá 6 @Deprecated function, pushMarkDown/syncMarkDown body rỗng
- Phase 2: Xoá comment khối lớn trong DiaryFragment, StockLineChartFragment, Context.kt


### 🎨 Thiết kế lại Tab Nhiệm vụ (Task Tab Redesign) – "Lột mặt nạ"
- File thay đổi:
    - `app/src/main/java/.../ui/components/DiaryItemCard.kt` (Redesign TaskItemCard)
- Chi tiết:
    - **Giao diện hiện đại:** Chuyển sang phong cách Material 3 với các góc bo tròn lớn (16.dp), đổ bóng nhẹ và viền mảnh.
    - **Hệ thống Priority trực quan:** Thay đổi màu sắc và thêm nhãn "High", "Medium", "Low" rõ ràng thay vì chỉ là một vạch màu.
    - **Checkbox phong cách mới:** Sử dụng `CheckCircle` và `RadioButtonUnchecked` thay thế cho các ô vuông checkbox cũ kỹ.
    - **Thanh tiến độ tinh tế:** Thanh progress mỏng hơn, có phần trăm hiển thị trực quan và thay đổi màu sắc theo tiến độ/độ ưu tiên.
    - **Xem trước sub-tasks:** Hiển thị các nhiệm vụ con trong một khối màu nền nhạt, giúp người dùng nắm bắt nội dung mà không cần mở chi tiết.
    - **Tối ưu hóa không gian:** Tự động ẩn các thành phần phụ khi nhiệm vụ đã hoàn thành để tập trung vào các nhiệm vụ đang thực hiện.

### 🏗️ Di chuyển sang Room Database (Phase 1)
- File thay đổi:
    - `app/build.gradle.kts` (thêm dependencies Room)
    - `app/src/main/java/.../data/entities/` (tạo 8 entities mới: Diary, Photo, Note, Task, v.v.)
    - `app/src/main/java/.../data/dao/` (tạo 5 DAOs: Diary, Note, Task, ActionLog, DDay)
    - `app/src/main/java/.../data/AppDatabase.kt` (thiết lập database)
    - `app/src/main/java/.../data/MigrationManager.kt` (logic chuyển đổi dữ liệu từ Realm sang Room)
    - `app/src/main/java/.../repositories/` (cập nhật Diary/Note/Task Repository dùng Room)
    - `app/src/main/java/.../helper/EasyDiaryApplication.kt` (kích hoạt migration khi khởi chạy)
- Chi tiết:
    - Triển khai toàn bộ cấu trúc Room Database song song với Realm.
    - Viết logic Migration tự động: khi người dùng mở app lần đầu sau cập nhật, toàn bộ dữ liệu (Nhật ký, Ghi chú, Nhiệm vụ, Ảnh, D-Day, Log) sẽ được copy từ Realm sang Room một cách an toàn.
    - Chuyển đổi toàn bộ Repositories sang sử dụng Room DAO thay cho `EasyDiaryDbHelper`.
    - Hỗ trợ mapping dữ liệu từ Room Entity sang Domain Model (Realm Objects cũ nhưng đã unmanaged) để tránh phá vỡ giao diện Compose hiện tại.
    - Kết quả: App hiện đã chạy trên nền tảng Room Database, sẵn sàng cho các nâng cấp hiệu năng và Android SDK mới nhất.

### 🧹 Loại bỏ Fragment & Compose thuần Dashboard (Progress)
- File thay đổi:
    - `app/src/main/java/.../compose/DashboardActivity.kt`
    - `app/src/main/java/.../viewmodels/DashboardViewModel.kt` (mới)
    - `app/src/main/java/.../ui/components/DashboardComponents.kt` (mới)
- Chi tiết:
    - Khởi tạo `DashboardViewModel` để quản lý dữ liệu cho màn hình Dashboard.
    - Tạo các Composable thay thế Fragment: `DDayItem`, `DDayAddCard`.
    - Bắt đầu tích hợp các thành phần Compose thuần vào `DashboardActivity`.

### 🛠️ Sửa lỗi Data Binding & Dọn dẹp Symbol leftover
- File thay đổi:
    - `app/src/main/res/layout-land/activity_diary_main.xml`
    - `app/src/main/java/me/blog/korn123/easydiary/viewmodels/DiaryMainViewModel.kt`
    - `app/src/main/java/me/blog/korn123/easydiary/helper/Constants.kt`
- Chi tiết:
    - Fix lỗi build: `Cannot find a setter for app:symbolSequence`.
    - Loại bỏ nút chọn Symbol (`feelingSymbolButton`) khỏi bản landscape của màn hình chính.
    - Xoá triệt để trạng thái `symbol` và hàm `updateSymbolSequence` trong `DiaryMainViewModel`.
    - Dọn dẹp các hằng số liên quan đến Symbol (`SYMBOL_SELECT_ALL`, `DEV_SYNC_SYMBOL_*`) trong `Constants.kt`.


Để app chạy mượt như "Vanilla Ice Cream", tôi đề xuất chúng ta thực hiện các bước sau:
Giai đoạn 1: Đoạn tuyệt với Realm (Cleanup)
•
Xoá hoàn toàn EasyDiaryDbHelper và các dependency liên quan đến Realm sau khi chắc chắn Migration Room đã ổn định.
•
Chuyển đổi các Model từ "Realm-style" sang "Kotlin Data Class" thuần túy để nhẹ app.
### Giai đoạn 2: "Compose-hóa" nốt các mảnh vỡ – HOÀN THÀNH
- **Settings:** Viết lại màn hình Cài đặt (`SettingsActivity`) bằng Compose thuần túy, tích hợp 3 tab (Cơ bản, Phông chữ, Khóa) vào một màn hình cuộn duy nhất.
- **Dọn dẹp:** Xoá bỏ 3 Fragment cài đặt cũ (`SettingsBasicFragment`, `SettingsFontFragment`, `SettingsLockFragment`) và các layout XML tương ứng.
- **Dashboard:** Loại bỏ `DashBoardSummaryFragment` (shell rỗng) và dọn dẹp các tham chiếu cũ.

### Giai đoạn 3: Tách biệt Logic & Tái cấu trúc thương hiệu (Refactor) – HOÀN THÀNH
- **Đổi tên Package:** Đã chuyển hoàn toàn sang `com.quangthe.nhatky`.
    - Cập nhật `namespace`, `applicationId` và cấu trúc thư mục vật lý.
    - Sửa package/import cho 600+ file (mã nguồn, layout, proguard, test).
    - Cập nhật FileProvider authority và Custom View tags trong XML.
- **Dọn dẹp legacy (Realm):** Xoá sạch comment, import và đổi tên các chuỗi liên quan đến Realm sang Database.
- **Build status:** SUCCESSFUL.

### Giai đoạn 4: Đánh bóng (Polishing) – ĐANG THỰC HIỆN
- **Dọn rác tài nguyên:** Xoá các `strings.xml` và `drawables` không còn dùng (Symbol/Weather).
- **Chuẩn hoá UI:** Rà soát lại Material Theme 3 trên các màn hình vừa migrate.
•
Tách DiaryRepository thành 3 Repository thực thụ: DiaryRepository, NoteRepository, TaskRepository với các DB Table riêng biệt.
•
Chuẩn hoá hệ thống Navigation: Hiện tại app đang dùng Intent để chuyển Activity, nếu chuyển sang Compose Navigation sẽ mượt hơn và quản lý state tốt hơn.
Giai đoạn 4: Đánh bóng (Polishing)
•
Dọn sạch các strings.xml và drawables không còn dùng (vẫn còn khá nhiều rác sau khi xoá tính năng Symbol/Weather).
Ní thấy kế hoạch này thế nào? Nếu ổn, tôi sẽ bắt đầu xử lý phần "Dọn dẹp mã nguồn Realm" trước để giảm tải cho project.

### Giai đoạn 1: Đoạn tuyệt với Realm (Cleanup) – HOÀN THÀNH (Build SUCCESSFUL)
- **File thay đổi:**
    - Xoá `app/src/main/java/.../models/ModelExtensions.kt` (cả 2 bản).
    - Cập nhật toàn bộ domain models sang `data class`: `Diary`, `PhotoUri`, `Location`, `SimpleNote`, `NoteFolder`, `TodoTask`, `TodoItem`, `ActionLog`.
    - Cập nhật `DiaryMapper.kt`, `TaskMapper.kt` (xoá `RealmList`).
    - Fix 20+ file (Activity, Fragment, Adapter, Helper) để dùng Repository thay cho `EasyDiaryDbHelper`.
    - Dọn dẹp `Constants.kt` (xoá `RealmConstants`, đổi tên hằng số DATABASE).
    - Cập nhật `Context.kt` và `Activity.kt` extensions (xoá logic Realm legacy).
- **Chi tiết:**
    - Dự án đã hoàn toàn không còn phụ thuộc vào Realm trong mã nguồn (`src/main`).
    - Toàn bộ logic truy cập dữ liệu đã được chuyển sang Room thông qua Repositories.
    - Các Model đã trở thành Plain Kotlin Data Classes, giúp giảm nhẹ ứng dụng và dễ dàng bảo trì.
    - **Build status:** SUCCESSFUL.
    - Sẵn sàng chuyển sang Giai đoạn 2: Compose-hóa các thành phần còn lại
  giai đoạn 3:
      Kế hoạch Giai đoạn 3 (Đổi tên Package): Tôi sẽ thực hiện theo trình tự an toàn nhất để tránh làm hỏng dự án:
1.
Sửa namespace và applicationId trong build.gradle.kts.
2.
Dùng lệnh shell để đổi tên hàng loạt khai báo package và import trong toàn bộ code.
3.
Cập nhật AndroidManifest.xml và các file layout còn sót lại.
4.
Di chuyển các file vật lý vào đúng cấu trúc thư mục com/quangthe/nhatky.

### Dọn dẹp toàn diện + Thêm nút Settings + Git sạch – 19/07/2026
- File thay đổi:
    - `compose/DiaryMainActivity.kt` (thêm icon Settings ở top bar)
    - `extensions/Context.kt` (xoá DialogMessageBinding, contentsLength, symbolTextArrow)
    - `extensions/Activity.kt` (xoá ActivityDiaryMainBinding, dashboard_container, app_bar)
    - `activities/EasyDiaryActivity.kt` (xoá R.id.compose_view)
    - `commons/utils/EasyDiaryUtils.kt` (xoá import DiaryFragment chết)
    - `AndroidManifest.xml` (xoá CAMERA permission tools:node="remove")
    - `app/build.gradle.kts` (xoá legacy-support-v4, nineoldandroids)
    - `.gitignore` (thêm .kotlin/, *.aab, *.apk, app/foss/release/)
- File xoá:
    - 8 file .kt chết: DDayFragment, DashBoardRankFragment, DiaryFragment, DDayAdapter, DiaryDashboardItemAdapter, TimelineItemAdapter, SafeFlexboxLayoutManager, LabelLayout, ChartBase
    - 12 layout XML mồ côi: dialog_message, dialog_dday, fragment_dashboard_rank, fragment_dday, fragment_diary, item_dday, item_dday_add, item_diary_dashboard, item_diary_dashboard_calendar, item_timeline, partial_notification, partial_notification_contents
    - 11 layout-land mồ côi: popup_encryption, partial_edit_photo_container, partial_edit_contents, partial_bottom_toolbar, dialog_feeling_pager, dialog_feeling, activity_pin_lock, activity_diary_main, activity_dashboard, activity_calendar, activity_base_diary_editing
    - Thư mục dự án: screenshots/, metadata/, .github/, .opencode/, AGENTS.md, google1f12e741993edc25.html, me/blog/korn123/easydiary/ (rỗng)
- Git: commit sạch (540 files, -34k/+10k lines), push force lên repo mới

### Fix font quá to: widget 40sp→20sp, alarm 44sp→20sp – 19/07/2026
- File thay đổi:
    - `res/layout/widget_item_diary_main.xml:33` (40sp → 20sp)
    - `ui/components/SettingCard.kt:1154` (44sp → 20sp)
- Chi tiết: User phản hồi chữ quá to ở widget ngày tháng và giờ báo thức. Hạ từ 40sp/44sp xuống 20sp chuẩn.