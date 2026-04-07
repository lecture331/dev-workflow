# Contributing Guide

## 브랜치 네이밍 규칙

| 유형 | 형식 | 예시 |
|------|------|------|
| 기능 개발 | `feature/BOOT-XXX-짧은-설명` | `feature/BOOT-123-add-product-api` |
| 버그 수정 | `fix/BOOT-XXX-짧은-설명` | `fix/BOOT-456-fix-stock-calculation` |
| 리팩토링 | `refactor/BOOT-XXX-짧은-설명` | `refactor/BOOT-789-extract-service` |
| 문서 | `docs/BOOT-XXX-짧은-설명` | `docs/BOOT-101-update-readme` |

## 커밋 메시지 규칙

[Conventional Commits](https://www.conventionalcommits.org/) 형식을 따르되, Jira 티켓 번호를 포함합니다.

```
<type>: BOOT-<번호> <설명>
```

### 타입 목록

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 (기능 변경 없음) |
| `test` | 테스트 추가/수정 |
| `docs` | 문서 수정 |
| `chore` | 빌드, 설정 변경 등 |

### 예시

```
feat: BOOT-123 장바구니 추가 API
fix: BOOT-456 재고 부족 시 예외 처리 누락 수정
test: BOOT-789 ProductService 단위 테스트 추가
refactor: BOOT-101 DTO 변환 로직 분리
```

## PR 생성부터 머지까지 10단계 워크플로우

1. **Jira 티켓 확인**: 작업할 Jira 티켓을 확인하고 상태를 "In Progress"로 변경
2. **브랜치 생성**: `main`에서 네이밍 규칙에 맞는 브랜치 생성
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/BOOT-123-add-product-api
   ```
3. **코드 작성**: 기능 구현 + 테스트 코드 작성
4. **로컬 검증**: 빌드 및 테스트 통과 확인
   ```bash
   ./gradlew clean build jacocoTestReport
   ```
5. **커밋 & 푸시**: Conventional Commits 형식으로 커밋 후 원격에 푸시
   ```bash
   git add .
   git commit -m "feat: BOOT-123 상품 조회 API 구현"
   git push origin feature/BOOT-123-add-product-api
   ```
6. **PR 생성**: GitHub에서 PR 템플릿에 맞춰 PR 생성
7. **CI 확인**: `ci.yml`과 `sonar.yml` 워크플로우가 통과하는지 확인
8. **코드 리뷰 반영**: CodeRabbit 자동 리뷰 + 팀원 리뷰 코멘트 확인 및 반영
9. **Quality Gate 통과 확인**: SonarCloud Quality Gate 결과 확인
10. **Squash Merge**: 모든 체크가 통과하면 Squash and Merge 진행

## 로컬에서 SonarCloud 분석 실행하기

### 사전 준비

1. [SonarCloud](https://sonarcloud.io)에서 토큰 발급
2. 환경 변수 설정:
   ```bash
   export SONAR_TOKEN=your_token_here
   export SONAR_PROJECT_KEY=your_project_key
   export SONAR_ORGANIZATION=your_organization
   ```

### 실행

```bash
./gradlew clean build jacocoTestReport sonar \
  -Dsonar.projectKey=$SONAR_PROJECT_KEY \
  -Dsonar.organization=$SONAR_ORGANIZATION \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=$SONAR_TOKEN
```

### 결과 확인

SonarCloud 대시보드에서 분석 결과를 확인합니다:
`https://sonarcloud.io/project/overview?id=<YOUR_PROJECT_KEY>`
