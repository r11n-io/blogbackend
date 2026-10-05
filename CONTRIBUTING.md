# 작업 규칙

혼자 진행하는 프로젝트지만, main을 항상 배포 가능한 상태로 유지하기 위한 최소한의 규칙.

## 브랜치 네이밍

`<타입>/<영어-kebab-case-설명>` 형식.

- `feat/` — 새 기능
- `fix/` — 버그 수정
- `test/` — 테스트 코드 전용 변경
- `chore/` — 설정, 의존성, 인프라 등
- `refactor/` — 동작 변화 없는 내부 정리

설명 부분은 영어 kebab-case로 작성.

예: `feat/secret-post`, `fix/connection-pool-leak`, `chore/add-dockerfile`

## 작업 흐름

1. 작업 단위별로 브랜치 생성
2. 로컬에서 자유롭게 커밋 (커밋 메시지는 기존처럼 한글 유지)
3. 머지 전 로컬에서 `./gradlew test`로 테스트 통과 확인
4. 문제 없으면 main으로 squash merge (merge 시 원격 브랜치는 자동 삭제됨)
5. 로컬 브랜치도 `git checkout main && git pull && git branch -d <브랜치명>`으로 정리

## 배포

main에 push되면 Cloud Build 트리거(`r11n-io-blog-backend-deploy`)가 `cloudbuild.yaml` 기준으로 자동 빌드·배포. 별도 수동 배포 단계 없음.

- 이미지: Artifact Registry(`asia-northeast3-docker.pkg.dev/r11n-io-blog/blog-backend`)에 커밋 SHA로 태깅되어 push
- 배포 대상: Cloud Run 서비스 `blog-backend-run` (리전 `asia-northeast3`)
- DB: Cloud SQL(`blog-db`), 소켓 팩토리로 연결 (IP 허용목록 불필요)
- 시크릿(`DB_PASSWORD`, `JWT_SECRET`, `SUPABASE_STORAGE_*_KEY`)은 Secret Manager에서 주입
- 빌드를 실행하는 서비스 계정(기본 Compute 계정)과 앱이 실행되는 서비스 계정(`blog-backend-run`)이 분리되어 있음 — 둘의 권한을 혼동하지 않을 것

## CI

빌드·배포 파이프라인은 있지만 테스트 게이트는 없음 — push하면 테스트 결과와 무관하게 바로 배포된다. 그래서 머지 전 로컬에서 `./gradlew test` 통과 확인이 특히 중요하다. (프론트엔드처럼 GitHub Actions lint/build/test 파이프라인으로 배포 전에 막는 건 추후 과제)

## Merge 방식

Squash merge. main 히스토리는 항상 의미 단위로 1커밋씩 남긴다.

## 예외

오타나 사소한 문구 수정처럼 리뷰가 필요 없는 초경량 변경은 브랜치 없이 main에 바로 커밋해도 된다.
