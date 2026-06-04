# 분리배출 도우미

품목을 검색하면 분리배출 방법·장소·주의사항을 알려주고,  
우리 동네 배출 요일과 "오늘 버릴 수 있는 쓰레기"를 보여주는 안드로이드 앱입니다.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| **품목 검색** | 이름·키워드로 검색, 5단계 카테고리 필터, 최근 검색어 10개 저장 |
| **오늘 배출** | 현재 요일 기준으로 배출 가능한 쓰레기 종류·수거 시간 표시 |
| **배출 일정** | 주간 캘린더로 요일별 수거 일정 및 배출 완료 체크 |
| **즐겨찾기** | 자주 찾는 품목 별표 저장, 앱 재시작 후에도 유지 |
| **지역 설정** | 시/구 선택 → 공공 API로 해당 지역 수거 일정 조회 (24시간 캐싱) |
| **알림** | 배출 전날·당일 알림, 요일·시간·품목 종류 맞춤 설정 |
| **회원 관리** | 회원가입/로그인/게스트 모드, 사용자별 즐겨찾기·설정 분리 저장 |

---

## 기술 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose + Material3
- **아키텍처**: MVVM (`SearchViewModel` → `SearchUiState`)
- **네트워킹**: Retrofit 2.11 + OkHttp 4.12 + Gson
- **로컬 저장소**: SharedPreferences (사용자별 키 분리)
- **알림**: AlarmManager + BroadcastReceiver (`AlarmReceiver`, `BootReceiver`)
- **최소 SDK**: 26 (`java.time` 사용) / 타겟 SDK: 34
- **빌드**: Android Gradle Plugin 8.5.2 · Kotlin 2.0.20

---

## 화면 구성

```
LoginActivity (앱 시작)
    └── MainActivity
        └── MainScreen (하단 탭 5개)
            ├── 검색 탭       — 품목 검색 + 카테고리 필터 + 지역 선택
            ├── 오늘 탭       — 오늘 배출 가능 항목
            ├── 일정 탭       — 주간 배출 캘린더 + 완료 체크
            ├── 즐겨찾기 탭   — 별표 저장 품목 목록
            └── 설정 탭       — 지역·알림·통계·로그아웃
```

> 품목 상세, 월별 통계, 알림 설정은 전체화면 오버레이로 표시됩니다.

---

## 패키지 구조

```
com.example.recyclehelper
├── MainActivity.kt / MainScreen.kt
├── ui/
│   ├── auth/          LoginActivity, RegisterActivity
│   ├── search/        SearchScreen, SearchViewModel
│   ├── today/         TodayScreen
│   ├── calendar/      CalendarScreen
│   ├── favorites/     FavoritesScreen
│   ├── settings/      SettingsScreen
│   ├── detail/        DetailScreen (오버레이)
│   ├── stats/         StatsScreen (오버레이)
│   ├── notification/  NotificationSettingsScreen (오버레이)
│   ├── components/    RecycleItemCard, RegionPickerDialog
│   └── theme/         Color, Theme
├── data/
│   ├── auth/          UserSessionManager, UserDatabase
│   ├── model/         RecycleItem, ZoneInfo, WasteCategory, DisposalRecord …
│   ├── local/         WasteItemStore (assets/waste_items.json)
│   ├── mock/          MockData (폴백용)
│   ├── repository/    WasteRepository (API + 파싱)
│   ├── remote/        RetrofitClient, WasteApi, WasteApiResponse
│   └── prefs/         PrefsManager (사용자별 SharedPrefs)
└── notification/
    ├── NotificationHelper.kt
    ├── AlarmReceiver.kt
    └── BootReceiver.kt
```

---

## 실행 방법

1. `local.properties`에 공공 API 키 추가
   ```properties
   WASTE_API_KEY=YOUR_API_KEY_HERE
   ```
2. Android Studio에서 프로젝트 열기 → Gradle Sync 완료
3. ▶ Run (에뮬레이터 또는 실기기, API 26 이상)
4. 검색창에 `페트병`, `치킨박스`, `건전지`, `스티로폼` 등 입력

---

## 주요 구현 포인트

- **다단계 검색**: 정확 일치 → 부분 일치 → 키워드 → 서브카테고리 → 폐기물 그룹 순으로 매칭
- **API 캐싱**: 지역별 수거 정보를 24시간 SharedPreferences에 캐싱, 만료 시 백그라운드 갱신
- **사용자 격리**: 즐겨찾기·검색 기록·배출 기록을 `userId` 접두어로 분리 저장
- **다중 페이지 API**: 첫 페이지로 totalCount 확인 후 나머지 페이지 병렬 요청
- **재부팅 복구**: `BootReceiver`로 기기 재시작 후 알림 일정 재등록

---

## 권한

| 권한 | 용도 |
|------|------|
| `INTERNET` | 공공 API 호출 |
| `POST_NOTIFICATIONS` | Android 13+ 알림 표시 |
| `RECEIVE_BOOT_COMPLETED` | 재부팅 후 알림 재등록 |
