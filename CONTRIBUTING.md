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

main에 push되면 Railway가 감지해서 Nixpacks로 자동 빌드·배포. 별도 수동 배포 단계 없음.

> Cloud Run 전환 작업 진행 중 — 전환 완료 후 이 섹션을 Cloud Build 트리거 기준으로 갱신할 것.

## CI

아직 없음. 머지 전 로컬 테스트 통과만으로 품질 체크를 대신한다. (프론트엔드처럼 GitHub Actions lint/build/test 파이프라인 구축은 추후 과제)

## Merge 방식

Squash merge. main 히스토리는 항상 의미 단위로 1커밋씩 남긴다.

## 예외

오타나 사소한 문구 수정처럼 리뷰가 필요 없는 초경량 변경은 브랜치 없이 main에 바로 커밋해도 된다.
