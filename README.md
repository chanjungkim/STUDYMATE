# STUDYMATE

내 주변 스터디 모임정보, 스터디 장소정보를 제공해주는 어플입니다.

## 사용 기술
* 언어 : PHP, JAVA
* 운영체제 : Linux(Ubuntu)
* 데이터베이스 : MySQL
* 웹 서버 : Apache
* API/라이브러리 : google map api, cropicker, glide, volley, FCM 등

## 주요 기능
1. 로그인 & 회원가입
2. 스터디 모임 등록, 수정, 삭제
3. 지역별, 카테고리별 운영 중인 스터디 검색
4. 스터디 상세정보 확인
5. 스터디 모임 참여, 탈퇴
6. 스터디 원들과 채팅
7. 스터디 원과 1:1 영상통화
8. 현재 위치에서 가까운 스터디 룸 정보 제공
9. 프로필 이미지 수정 시, 얼굴탐지기능

### 1. 로그인 & 회원가입
<p><img src="https://user-images.githubusercontent.com/49344118/57312943-84601d00-7129-11e9-815d-6317eec78f22.gif" height="400">
<img src="https://user-images.githubusercontent.com/49344118/57313180-ff293800-7129-11e9-86f4-ba7898d04c6e.gif" height="400"></p>

- 사용자는 로그인을 하지 않아도 주변 모임 정보목록을 볼 수 있습니다.
- 하지만 자신이 가입한 모임을 보려면, 로그인해야 합니다.
- 회원가입 시 이미 가입된 이메일, 닉네임으로는 가입할 수 없습니다.
<br>

### 2. 스터디모임 등록, 수정, 삭제
<p><img src="https://user-images.githubusercontent.com/49344118/57313924-8925d080-712b-11e9-9eec-db3405e71b86.gif" height="400">
<img src="https://user-images.githubusercontent.com/49344118/57313923-8925d080-712b-11e9-81c2-63eba53e743b.gif" height="400">
<img src="https://user-images.githubusercontent.com/49344118/57313926-89be6700-712b-11e9-81cd-6e55d5099e43.gif" height="400"></p>

#### 등록
- 스터디 모임 대표 이미지, 모임 제목, 카테고리 등을 입력하여 스터디 모임을 등록합니다.
- 대표 이미지는 앨범에서 가져온 이미지를 크롭해서 등록할 수 있습니다.
- 스터디 모임이 등록되면, 스터디 모임 목록 상단에 등록된 모임이 보입니다.
#### 수정, 삭제
- 모임 상세페이지에 설정을 클릭하면, 해당 모임 정보를 수정 또는 삭제할 수 있습니다.
- 스터디 모임 장만 모임을 삭제할 수 있습니다.
<br>

### 3. 지역별, 카테고리별 운영 중인 스터디 검색
<img src="https://user-images.githubusercontent.com/49344118/57317821-f9d0eb00-7133-11e9-8a97-39db57f0df90.gif" height="400">

- 앱 바에 있는 스피너로 지역, 카테고리를 선택하면 그에 맞는 지역, 카테고리별로 스터디 모임이 검색됩니다.
<br>

### 4. 스터디 상세정보 확인
<img src="https://user-images.githubusercontent.com/49344118/57317822-f9d0eb00-7133-11e9-91ff-e67b4828ec66.gif" height="400">

- 스터디 모임 목록에서 하나를 클릭하면, 해당 스터디 모임의 상세 정보를 볼 수 있습니다.
<br>

### 5. 스터디 모임 참여, 탈퇴
<p><img src="https://user-images.githubusercontent.com/49344118/57317743-d0b05a80-7133-11e9-9937-a27ffa739b42.gif" height="400">
<img src="https://user-images.githubusercontent.com/49344118/57317744-d0b05a80-7133-11e9-9005-6e4c0cbed7f1.gif" height="400"></p>

- 참여는 스터디 모임 상세 정보 화면에서 해당 스터디 모임에 참여할 수 있습니다.
- 탈퇴는 상세 정보 화면의 설정화면에 들어가면 탈퇴를 할 수 있습니다. 
- 참여하게 되면 스터디 모임 상세 정보 화면 하단에 참여 스터디 원 목록에 추가되고 탈퇴 시 삭제됩니다.
<br>

### 6. 스터디 원들과 채팅
<img src="https://user-images.githubusercontent.com/49344118/57321226-dad65700-713b-11e9-99fe-20344623f195.gif" height="400">

- 스터디 모임에 참여를 하는 경우, 스터디 원들끼리 채팅을 할 수 있습니다.
- 스터디 원이 채팅방에 있지 않다면 채팅 알림을 보냅니다.
- 채팅 알림을 클릭하는 경우, 해당 채팅방으로 들어가 스터디 원들과 채팅을 할 수 있습니다.
<br>

### 7. 스터디 원과 1:1 영상통화
<img src="https://user-images.githubusercontent.com/49344118/57321225-dad65700-713b-11e9-9d9d-86ef8c143c25.gif" height="400">

- 스터디 모임 상세 정보 화면 하단에 참여 스터디 원 목록에서 한 스터디 원을 클릭하면, 영상통화를 할 수 있는 버튼이 나옵니다.
- 영상통화 버튼을 클릭하게 되면, 해당 스터디 원과 1:1 영상통화를 할 수 있습니다.
<br>

### 8. 현재 위치에서 가까운 스터디 룸 정보 제공
<img src="https://user-images.githubusercontent.com/49344118/57321458-559f7200-713c-11e9-9094-06c25460babe.gif" height="400">

- 현재 위치를 찾고 현재 위치 주변에 있는 스터디 룸 정보를 지도상에서 보여줍니다.
- '목록으로 보기' 버튼을 클릭하면 목록으로도 볼 수 있습니다.
<br>

### 9. 프로필 이미지 수정 시, 얼굴탐지기능
<p><img src="https://user-images.githubusercontent.com/49344118/57318706-e58ded80-7135-11e9-9355-07e647680b1f.gif" height="400">
<img src="https://user-images.githubusercontent.com/49344118/57322534-bc258f80-713e-11e9-8065-81009cb3b459.gif" height="400">
<img src="https://user-images.githubusercontent.com/49344118/57318709-e6268400-7135-11e9-86ee-a121b0537c40.gif" height="400"></p>

- 개인정보수정화면으로 들어가면, 프로필 이미지를 수정할 수 있습니다.
- 프로필 이미지 수정 시 얼굴이 탐지되면, '캡처' 버튼이 활성화되고 사진을 찍을 수 있습니다.

