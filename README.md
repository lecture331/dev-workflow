# Product CRUD - 개발 워크플로우 검증 프로젝트

## 프로젝트 목적

검증 대상 워크플로우:
> **Jira 티켓 → GitHub 브랜치 → 코드 작성 → PR 생성 → CI/CD(CodeRabbit + SonarCloud) → 코드 리뷰 → Squash Merge**

실제 비즈니스 로직보다는 CI/CD 파이프라인과 품질 게이트가 의도대로 동작하는지 확인하는 것이 핵심입니다.

## 기술 스택

- Java 21, Spring Boot 4.0.3, Gradle (Groovy DSL)
- Spring Web, Spring Data JPA, H2 (in-memory)
- Lombok, Jakarta Validation
- JUnit 5, Mockito, AssertJ
- JaCoCo (커버리지), SonarCloud (정적 분석)

## 로컬 실행 방법

```bash
# 프로젝트 클론
git clone <repository-url>
cd product-crud

# 빌드 및 실행
./gradlew bootRun

# 애플리케이션 접속
# API: http://localhost:8080/products
# H2 콘솔: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb)
```

## API 명세

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/products` | 전체 상품 목록 조회 |
| GET | `/products/{id}` | 상품 단건 조회 |
| POST | `/products` | 상품 생성 |
| PUT | `/products/{id}` | 상품 수정 |
| DELETE | `/products/{id}` | 상품 삭제 |

### 요청 바디 예시 (POST, PUT)

```json
{
  "name": "맥북 프로",
  "price": 2500000,
  "stock": 10
}
```

## 테스트 실행 방법

```bash
# 전체 테스트 실행
./gradlew test

# 테스트 + JaCoCo 커버리지 리포트 생성
./gradlew clean build jacocoTestReport

# 커버리지 리포트 확인
open build/reports/jacoco/test/html/index.html
```

## 의도적으로 커버리지가 빠진 메서드

> **`ProductService.decreaseStock(Long id, int quantity)`**
>
> 위치: `src/main/java/com/example/productcrud/service/ProductService.java`

이 메서드는 **의도적으로 테스트를 작성하지 않았습니다**.
SonarCloud가 "uncovered lines"로 감지하는지 확인하기 위한 용도입니다.

## CI/CD 파이프라인

### 1. `ci.yml` - 빌드 및 테스트

- **트리거**: `push to main`, `pull_request to main`
- **동작**: 코드 체크아웃 → JDK 21 셋업 → `./gradlew clean build jacocoTestReport`
- **산출물**: JaCoCo HTML 리포트를 GitHub Artifact로 업로드

### 2. `sonar.yml` - SonarCloud 정적 분석

- **트리거**: `push to main`, `pull_request to main`
- **동작**: 전체 히스토리 체크아웃(`fetch-depth: 0`) → 빌드 → JaCoCo → SonarCloud 분석
- **필요 시크릿**: `SONAR_TOKEN`, `SONAR_PROJECT_KEY`, `SONAR_ORGANIZATION`

## SonarCloud 연동 방법

1. [SonarCloud](https://sonarcloud.io)에 GitHub 계정으로 로그인
2. 우측 상단 프로필 아이콘 클릭 → My Account
  - Security 탭 클릭               
  - "Generate Tokens" 섹션에서:                            
    - Token name: 아무 이름              
    - Generate 클릭                                                                                                                            - 표시되는 토큰 복사
3. "+" → "Analyze new project" → 해당 리포지토리 선택
4. Project Key와 Organization 확인
5. GitHub 리포지토리 → Settings → Secrets and variables → Actions에 시크릿 3개 등록:
   - `SONAR_TOKEN`: SonarCloud에서 발급받은 토큰
   - `SONAR_PROJECT_KEY`: SonarCloud 프로젝트 키
   - `SONAR_ORGANIZATION`: SonarCloud 조직명

## CodeRabbit 연동 방법

1. [CodeRabbit](https://coderabbit.ai) 접속 → GitHub 앱 설치
2. 해당 리포지토리에 대한 접근 권한 부여
3. PR 생성 시 자동으로 코드 리뷰 코멘트가 달립니다

## Quality Gate 권장 설정

SonarCloud에서 아래 조건으로 Quality Gate를 설정하세요:

| 항목 | 조건 |
|------|------|
| 새 코드 라인 커버리지 | >= 80% |
| 새 코드 중복도 | <= 3% |
| 새 코드 버그 | = 0 |
| 새 코드 취약점 | = 0 |
| 새 코드 코드 스멜 | <= 5 |

### 사전 설정

- [ ] GitHub 리포지토리 생성 및 코드 push
- [ ] Branch Protection Rule 설정 (아래 "수동 설정 단계" 참조)
- [ ] SonarCloud 연동 및 시크릿 등록
- [ ] CodeRabbit 앱 설치

### 검증 항목

1. **[ ] main 직접 push 차단**: Branch Protection Rule로 main에 직접 push가 막히는가
2. **[ ] CI 워크플로우 실행**: PR 생성 시 `ci.yml`과 `sonar.yml`이 모두 도는가
3. **[ ] 테스트 실패 PR 차단**: 일부러 테스트를 깨뜨린 PR이 머지 차단되는가
   - 예: 기존 테스트의 assertion 값을 잘못된 값으로 변경
4. **[ ] Quality Gate 실패 PR 차단**: SonarCloud 룰 위반 코드를 넣은 PR이 Quality Gate에서 막히는가
   - 예: 사용하지 않는 변수 선언, 빈 catch 블록 추가
5. **[ ] CodeRabbit 자동 리뷰**: PR에 CodeRabbit이 코멘트를 다는가
6. **[ ] Squash Merge 동작**: 모든 체크 통과 후 Squash merge가 정상 동작하는가
7. **[ ] 브랜치 자동 삭제**: 머지 후 소스 브랜치가 자동 삭제되는가

## GitHub Push 후 수동 설정 단계

코드를 GitHub에 push한 뒤 아래 설정을 수동으로 진행하세요.

### 1. Branch Protection Rule 설정

**Settings → Branches → Add branch protection rule**

- Branch name pattern: `main`
- [x] Require a pull request before merging
  - [x] Require approvals (1명 이상)
- [x] Require status checks to pass before merging
  - 필수 체크: `build` (ci.yml), `sonar` (sonar.yml)
- [x] Require conversation resolution before merging
- [x] Do not allow bypassing the above settings
- [x] Automatically delete head branches (Settings → General에서 설정)

### 2. SonarCloud 시크릿 등록

**Settings → Secrets and variables → Actions → New repository secret**

| Name | Value |
|------|-------|
| `SONAR_TOKEN` | SonarCloud에서 발급받은 토큰 |
| `SONAR_PROJECT_KEY` | SonarCloud 프로젝트 키 |
| `SONAR_ORGANIZATION` | SonarCloud 조직명 |

### 4. CodeRabbit 앱 설치

- [GitHub Marketplace](https://github.com/marketplace)에서 CodeRabbit 설치
- 해당 리포지토리에 접근 권한 부여

### 5. Squash Merge 설정

**Settings → General → Pull Requests**

- [x] Allow squash merging
- [ ] Allow merge commits (선택 해제 권장)
- [ ] Allow rebase merging (선택 해제 권장)
- [x] Automatically delete head branches
