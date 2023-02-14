# AutoLocationRecorderPublic
### 자동위치기록기 공유용 코드
### google-service.json, AD_ID, Firestore 관련 코드들 일부 삭제   
### [Goole Play Store Download Link](https://play.google.com/store/apps/details?id=teeu.android.autolocationrecorder&hl=ko&gl=US)   
<img src="https://user-images.githubusercontent.com/46315397/218644896-c5dfe3e5-b2ac-4c28-bf4a-ff3129efb0d4.jpg"  width="300" height="500">   

## 프로젝트 구조
### RecorderApplication 
- 앱 실행 전에 초기화 해야될 것들을 여기에서 수행
### MainActivity 
- BottomNavigation 세팅
### MyFirebaseMessagingService
- FCM 기능구현
### RecorderService
- Switch On을 했을 때, 사용자가 앱이 실행중이라는 것을 인지할 수 있게 하기 위한 포그라운드 서비스
### Repository
- static으로 사용되는 데이터들 모아둠 (Local DB생성, 전면광고 게재 등)

### database
- room을 이용해서 local db에 저장
### firebase
- Auth - Firebase Authentication에 저장되는 로그인. 사용안함. provider, email, uid, token 받아올 수 있음
- Firestore - Google 로그인시 비로그인 기록과 별개로 위치 기록 저장
### g_signin 
- Gsignin - 디바이스에 연결된 구글계정 선택해서 로그인. 로그인 상태 유지됨. displayName, email, id, photoUrl 받아올 수 있음
### model
- FirestoreData - Firestore에 저장하는 데이터
- Friend - email id와 친구여부 데이터
- Record - 위치정보를 받아온 날짜 및 시간, 위도, 경도, 상세주소를 저장하는 데이터
- Switch - Worker가 일정주기로 반복해서 위치정보를 불러오는 기능을 On/Off하는 데이터
### util
- CurrentDate - yyyy-MM-dd HH:mm:ss 포맷으로 현재 날짜와 시간을 리턴
- DeviceLocale - 앱을 실행하는 디바이스의 Locale 리턴
- DistanceCalculator - 최근위치정보와 현재위치정보의 거리를 계산해서 meter단위로 리턴
### view
- home - 비로그인/로그인 개별적으로 저장된 위치기록 디스플레이
- friend - 구글 로그인시 디스플레이. 구글 아이디를 통해서 친구신청을 하고, 상대방이 수락하면 서로의 위치기록 열람 가능
- setting - 공지사항, Switch On/Off, 로그인/로그아웃
- ad - 전면광고 게재
### worker
- 백그라운드에서 15분 주기로 현재 위치정보를 받아와서 100m 이상 위치 변화시 데이터를 저장
