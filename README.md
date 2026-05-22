# StudyFocus - 실시간 달성률 및 공부 목표 관리 앱

StudyFocus는 사용자가 설정한 오늘 할 일과 공부 목표를 효율적으로 관리하고, 실시간으로 달성률을 시각화하여 동기부여를 돕는 안드로이드 네이티브 앱입니다.

## 주요 기능
- **실시간 달성률 대시보드**: 할 일을 완료할 때마다 상단의 원형 프로그레스 바가 즉시 업데이트됩니다.
- **공부 목표 관리**: 오늘의 공부 목표를 추가, 삭제하고 완료 상태를 관리할 수 있습니다.
- **프리미엄 UI/UX**: 다크 모드 기반의 세련된 디자인과 부드러운 애니메이션을 제공합니다.
- **지능형 알림**: 매일 저녁 9시에 오늘의 성취도를 계산하여 푸시 알림을 통해 리마인드합니다.

## 기술 스택
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room (Local Database)
- **Background Task**: WorkManager (알림 예약 및 처리)
- **Architecture**: MVVM (Model-View-ViewModel)

## 시작하기
1. 이 저장소를 클론합니다.
2. Android Studio (Ladybug 이상 권장)에서 프로젝트를 엽니다.
3. 빌드 및 실행을 통해 앱을 확인합니다.

## 개발 계획 (Roadmap)
- [x] 로컬 데이터베이스 연동 및 기본 UI 구현
- [x] 실시간 달성률 로직 구현
- [x] WorkManager 기반 로컬 알림 기능
- [ ] Supabase/Firebase 연동 (클라우드 동기화)
- [ ] 주간/월간 통계 레포트 기능
