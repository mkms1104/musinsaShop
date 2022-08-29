# musinsaShop
Rest API 기반 온라인 쇼핑몰 상품 카테고리 기능 구현

## 요구사항
**카테고리 등록/수정/삭제 API**
* 카테고리를 등록/수정/삭제 할 수 있어야 한다.

**카테고리 조회 API**
* 상위 카테고리를 이용해, 해당 카테고리의 하위의 모든 카테고리를 조회
가능해야 한다.
* 상위 카테고리를 지정하지 않을 시, 전체 카테고리를 반환해야 한다.

## 개발환경
* Java 11
* Spring boot 2.7.2
* Jpa / H2
* Lombok
* Gradle

## 프로젝트 실행
* jdk 11 버전은 필수로 설치해야 합니다.
* 명령어 실행 위치는 프로젝트 루트 기준입니다.

* 빌드
```c
./gradlew clean web-api:build
```

* 실행
```c
java -jar ./web-api/build/libs/web-api-0.0.1-SNAPSHOT.jar
```
