# 분리배출 도우미 (Recycling Assistant)

품목을 검색하면 분리배출 방법·장소·주의사항을 알려주고,
우리 동네 배출 요일과 "오늘 버릴 수 있는 쓰레기"를 보여주는 안드로이드 앱입니다.

> 현재는 **API 없이 Mock 데이터**로 동작하는 기본 틀입니다.
> 추후 기후에너지환경부 / 행정안전부 API 를 연동하면 됩니다.

## 기술 스택
- Kotlin + Jetpack Compose (Material3)
- MVVM 구조 (화면별 ViewModel)
- minSdk 26 (java.time 사용), targetSdk 34

## 화면 구성
- **검색**: 품목 검색 → 분리배출 정보 카드, 즐겨찾기(별) 토글
- **오늘 배출**: 오늘 요일에 배출 가능한 카테고리
- **배출 일정**: 요일별 배출 카테고리·시간대
- **설정**: 우리 동네, 배출일 알림 토글

## 패키지 구조
```
com.example.recyclehelper
├── MainActivity.kt          앱 진입점
├── MainScreen.kt            하단 탭 네비게이션
├── data
│   ├── model                RecycleItem, RegionSchedule, Category
│   └── mock                 MockData (임시 데이터)
└── ui
    ├── theme                Color, Theme
    ├── components           RecycleItemCard, CategoryChip
    ├── search               SearchScreen, SearchViewModel
    ├── today                TodayScreen
    ├── schedule             ScheduleScreen
    └── settings             SettingsScreen
```

## 실행 방법
1. 안드로이드 스튜디오에서 이 폴더를 **Open** (Gradle 프로젝트로 자동 인식)
2. Gradle Sync 완료 후 ▶ Run
3. 검색창에 `페트병`, `치킨박스`, `건전지`, `스티로폼` 등 입력

## 다음 단계 (확장 아이디어)
- [ ] Retrofit 으로 실제 API 연동 (MockData → Repository 교체)
- [ ] Room DB 로 즐겨찾기/캐시 영구 저장
- [ ] 위치 권한 + 행정동 코드 매핑 (지역 자동 감지)
- [ ] WorkManager 로 배출 전날 알림
- [ ] 홈 화면 위젯 ("오늘 배출 가능")
- [ ] ML Kit 카메라 분류 ("이거 어떻게 버려요?")
