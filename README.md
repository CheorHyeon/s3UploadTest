## 알아두면 좋은 것들
### S3 주요 용어
- 버킷(Bucket) : S3에서 데이터를 저장하는 기본 컨테이너
  - 고유한 글로벌 이름을 가지며 하나 이상의 객체 저장 가능
- 객체(Object) : S3에 저장되는 기본 단위
  - 파일과 메타데이터를 포함
  - 버킷 안에 저장
- 키(Key) : 객체를 고유하게 식별하는 식별자
  - 각 객체는 버킷 내에서 고유한 키를 가짐
![](https://velog.velcdn.com/images/puar12/post/e7b13f6c-447d-4b08-b54a-ca3755f5f513/image.png)
[출처 - [AWS] Amazon S3란? (특징, 용어, OneFS 비교, NAS, 사용 사례)](https://yuna-ninano.tistory.com/m/entry/AWS-Amazon-S3%EB%9E%80-%ED%8A%B9%EC%A7%95-%EC%9A%A9%EC%96%B4-OneFS-%EB%B9%84%EA%B5%90-NAS-%EC%82%AC%EC%9A%A9-%EC%82%AC%EB%A1%80)

### AWS 전체 네트워크 구성
- 여러개의 리전을 가짐
  - 리전 : 전 세계 여러 지역에 데이터 센터 명칭
  - 고유한 이름을 가지며 일반적으로 지역 코드와 번호로 구성
    - ap-northeast-2 : 아시아 태평양 지역의 서울 리전
- 각 리전은 여러개의 가용 영역을 가짐
  - 가용 영역 : 물리적으로 분리된 데이터 센터
    - 하나의 리전 내에서 독립적인 전력 공급, 냉각 및 네트워크 연결 제공
    - 장애 발생 시 다른 가용 영역에서 서비스를 지속할 수 있는 이점 제공
 

### AWS 계정, 정책 관련
- root 계정 자체
  - 계정 이메일, ID 유일한 값을 가짐
  - 계정 별칭으로 로그인 가능
- 추가 별칭으로 권한 줄 수 있음
  - IAM
  - 사용자 이름 유니크하지 않아도 됨
- AWS 정책 : 권한들의 모음
  - S3 파일 업로드 관련 권한 모음 : AmazonS3FullAccess 필요

  
### S3 파일 업로드의 큰 그림
- yml 설정 파일에 AccessToken 정보 추가
- API 호출 시 yml 정보를 같이 넘겨 권한 있는 사용자임을 인증하고 업로드
- 이후 업로드 된 경로를 응답받아 DB에 저장
  - 이미지의 경우 img 태그에 src 부분에 사용
  - 파일 다운, 삭제의 경우 s3 내 저장된 파일 경로 필요

### ContentDisposition
- HTTP 응답 헤더 중 하나로 브라우저에게 파일을 어떻게 처리할지 지시하는 역할
  - 주로 다운로드 할 파일의 이름 지정하는데 사용
    - attachment : 파일이 다운로드 형식으로 제공됨을 나타냄
      - 브라우저가 파일 직접 열지X 다운
      - inline 으로 사용하면 브라우저는 파일을 직접 열려고 시도
    - filename : 다운로드 할 파일의 이름 설정
      - 사용자가 파일 다운로드 시 이 이름으로 저장

## S3 업로드 적용 뚝딱뚝딱 과정
- 뚝딱 거림은 블로그에 상세히 적어두었습니다.
  - [블로그 글](https://velog.io/@puar12/Spring-Boot-AWS-S3%EC%97%90-%ED%8C%8C%EC%9D%BC%EC%9D%84-%EC%97%85%EB%A1%9C%EB%93%9C-%EB%8B%A4%EC%9A%B4-%EC%82%AD%EC%A0%9C)
- 각 기능별 구현 과정은 커밋별로 확인이 가능합니다.
- [1. 파일 업로드](https://github.com/CheorHyeon/s3UploadTest/commit/538da7c1efceb2960257fa1d9c7bdf8367899684)
- [2. 파일 삭제](https://github.com/CheorHyeon/s3UploadTest/commit/0b529823a8cde62ba2076f802d5fa099b2d43943)
- [3. 파일 다운 - 한글 되나, 포스트맨 테스트 시 깨지던 문제](https://github.com/CheorHyeon/s3UploadTest/commit/caef322214674b0f27b361ba55db308a40bf3672)
- [3-1. 파일 다운 - HTML 파일에서 파일 다운 테스트 하기 위함](https://github.com/CheorHyeon/s3UploadTest/commit/f45a4acc9c6322bb61813c6016f31ddd157688a2)
- [4. 코드 리팩토링 - 메서드화, 상수화](https://github.com/CheorHyeon/s3UploadTest/commit/6838c45037c1b0de240739627b6115ed84c08506)
