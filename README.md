# 화록

> 디지털 제작자를 위한 작업 히스토리 추적 웹 서비스  
> 생성형 AI 이미지 창작 과정을 기록하고, 창작자의 기여도·창의성을 정량 지표로 제시합니다.

| 항목 | 내용 |
|---|---|
| 기간 | 2025.01 ~ 2025.08 (리팩터링 2026.10) |
| 구분 | 졸업 프로젝트 · ICT 한이음 |
| 팀 구성 | 4인 (백엔드 1인 전담) |
| 성과 | ICT 한이음 중간평가 우수작 선정 · 학술대회 논문 발표 |

---

## 📌 개요

**화록**은 생성형 AI 이미지 창작에서 결과 이미지만 남기는 대신  
창작자의 **프롬프트 작성·수정과 후처리 작업 이력**을 기록하고,  
이를 분석해 **기여도와 창의성을 정량 지표로 제시**하는 서비스입니다.

---

## 🎯 프로젝트 목표

- AI 생성 이미지에 대한 **창작자의 개입 요소 추적**
- 프롬프트·이미지·후처리(Unreal) 작업의 **히스토리 기록**
- 창작자의 **기여도 및 창의성 정량 평가** (유창성·지속성·창의성 지표)

---

## 🛠 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Java 21, Spring Boot 3.3.2, Spring Security, Spring Data JPA, WebSocket |
| DB · Cache | MySQL (AWS RDS), Redis, Ehcache(JCache) |
| 외부 연동 | OpenAI 이미지 생성 API(`gpt-image-1`), Flask AI 서버(SAM 이미지 분할·카테고리 분류·분석 리포트), AWS S3 |
| Infra | AWS EC2, Docker, GitHub Actions, Nginx |
| 인증 | JWT (Access/Refresh), Redis |
| 측정 · 모니터링 | JMeter, Actuator(HikariCP 지표), Logback + MDC |
| 문서 | Swagger(springdoc) |

---

## 🏗️ 아키텍처
<img width="682" height="293" alt="image" src="https://github.com/user-attachments/assets/6484a27f-029f-4428-ad8f-d1f955bb893b" />

---

## 🎥 시연 영상

[![화록 시연 영상](https://img.youtube.com/vi/HIYSgbBQLHU/0.jpg)](https://www.youtube.com/watch?v=HIYSgbBQLHU)

---

## 📂 패키지 구조
```text
src/main/java/_v3r/project
├── category        # 프롬프트 카테고리 분류 결과 관리 (Flask 연동)
├── common          # 공통 응답, 예외 처리, 인증(JWT), 캐시, S3, WebSocket, 로깅 필터, 설정
├── flask/service   # Flask 서버 연동 (카테고리 분류, 이미지 분할, 분석 리포트)
├── history         # 창작 히스토리 및 Unreal 작업 이력 관리
├── imageflow       # 결과 이미지 분할(segment)·다운로드 흐름
├── prompt          # 채팅방·프롬프트 관리 및 이미지 생성(OpenAI)
├── report          # 창의성·기여도 분석 리포트
├── user            # 사용자 관리
└── ProjectApplication.java

perf/               # 부하 테스트 (JMeter 시나리오, HikariCP 지표 수집, 실행별 측정 결과)
```

---

### 외부 API 호출로 인한 DB 커넥션 고갈

#### 1. 문제
운영 중 API 응답 지연이 발생했습니다. MySQL `show processlist`로 확인하니 RDS 커넥션 12개 중 쿼리를 실행 중인 것은 2개뿐이고, 나머지는 `Sleep` 상태로 오래 유지되고 있었습니다.

#### 2. 1차 대응과 한계 (2025.08, 운영 당시)
- 당시에는 유휴 커넥션이 오래 남는 문제로 보고 HikariCP 설정(pool size, idle-timeout, max-lifetime)을 조정했습니다.
- 결과: Sleep 세션 유지 시간 500 ~ 1000s → 90 ~ 180s, JMeter 부하 테스트(100 VUser × Loop 10) 에러율 **6.45% → 0%**
- 한계: 에러는 사라졌지만 평균 응답은 1초 이상으로 남았고, **커넥션이 왜 오래 점유되는지는 설명하지 못했습니다.**

#### 3. 재현 환경 구성 (2026.10, 프로젝트 종료 후 리팩터링 · 이슈 #160)
원인을 하나씩 검증하기 위해 같은 구조를 로컬에서 재현했습니다. 아래 수치는 모두 이 환경에서 측정한 값이며, 운영 환경 수치가 아닙니다.

| 항목 | 조건 |
|---|---|
| 실행 | 로컬 PC, 애플리케이션 단일 인스턴스 (`local,mock` 프로필) |
| 커넥션 풀 | HikariCP 최대 10개 |
| 부하 | JMeter 동시 실행: 이미지 생성 API(`POST /api/v1/prompt/generate-image`) 50 VUser + 채팅 목록 조회 API(`GET /api/v1/chat/find-all-chat`) 50 VUser, 각 Loop 10 (API별 500건), Ramp-up 5초 |
| 테스트 데이터 | 테스트 사용자 1명·채팅방 1개를 모든 요청이 공유 |
| 외부 호출 | 실제 네트워크 없이 지연 후 고정 응답 반환 (`MockExternalApiConfig`, `MockS3Service`) — OpenAI 2.5초(실측 2~3초 기준), S3 0.3초(실측하지 않은 임의값) |
| 지표 | HikariCP `active`·`pending`을 1초 간격으로 샘플링한 최댓값, JMeter p95·에러율 |
| 기록 | 실행마다 `perf/results/<시각>-<label>/`에 분리 저장 |

채팅 목록 조회 API는 외부 호출이 없는 API입니다. 같은 사용자의 요청이라 첫 요청 이후에는 캐시에 적중하고, DB 커넥션은 JWT 인증 필터의 사용자 조회에만 사용합니다. 그래서 이 API의 지연은 **외부 호출과 무관한 짧은 DB 접근도 커넥션을 받지 못해 밀리는지**를 보여줍니다.

**재현 결과**: 커넥션 10개가 모두 사용 중인 상태에서 대기 요청(pending)이 최대 81개까지 쌓였고, 채팅 목록 조회 p95는 단독 실행 시 11ms에서 동시 실행 시 11,124ms로 늘었습니다. 에러는 0%로, 요청이 실패하지 않고 커넥션을 기다리며 지연되었습니다.

#### 4. 원인 분석과 해결

**원인 ① 외부 호출이 트랜잭션 안에서 실행됨**
- 확인: `PromptService.generateImage`(OpenAI 호출 + S3 업로드)와 `ImageService.segmentResultImage`(Flask SAM 호출 + S3 업로드)가 `@Transactional` 안에서 외부 응답을 기다림
- 조치: `TransactionTemplate`으로 `DB 조회·검증(트랜잭션) → 외부 호출(트랜잭션 밖) → DB 저장(트랜잭션)`으로 분리하고, 저장이 실패하면 이번 요청이 올린 S3 파일을 삭제하는 보상 처리 추가
- 결과: **개선되지 않음** (pending 82, 조회 p95 11,338ms · 3회 중앙값)

**원인 ② 트랜잭션이 끝나도 커넥션이 반환되지 않음**
- 확인: OSIV(`open-in-view`, 기본 true)로 요청이 끝날 때까지 Hibernate 세션이 열려 있고, Hibernate 기본 커넥션 처리 방식(`DELAYED_ACQUISITION_AND_HOLD`)은 세션이 닫힐 때까지 JDBC 커넥션을 보유합니다. 그래서 트랜잭션을 나눠도 외부 호출을 기다리는 동안 커넥션이 풀로 돌아가지 않았습니다.
- 검증: OSIV를 끈 상태에서 트랜잭션 분리를 적용하자 pending이 최대 0~2개로 줄었습니다 (조회 p95 23ms · 3회 중앙값).
- 조치: Hibernate Connection Handling Mode를 `DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION`으로 변경해 트랜잭션 종료 시 커넥션을 반환 (`JpaConnectionConfig`)
- 결과: **pending 0, 조회 p95 32ms** (3회 중앙값)

**교차 검증**: 트랜잭션 분리 없이 설정만 바꾸거나 OSIV만 끈 경우에는 개선되지 않았습니다. 두 원인이 겹쳐 있어 **둘 다 해결해야** 커넥션 점유가 사라졌습니다.

### 5. 결과

| 구성 | 측정 | 대기 커넥션 최대 (pending) | 채팅 목록 조회 p95 | 이미지 생성 p95 | 에러율 |
|---|---|---|---|---|---|
| 개선 전 | 1회 | 81 | 11,124ms | 17,147ms | 0% |
| 트랜잭션 분리만 | 3회 중앙값 | 82 | 11,338ms | 17,299ms | 0% |
| 커넥션 반환 설정만 | 1회 | 85 | 12,573ms | 19,914ms | 0% |
| OSIV off만 | 1회 | 81 | 11,374ms | 19,759ms | 0% |
| 트랜잭션 분리 + OSIV off | 3회 중앙값 | 0 | 23ms | 2,909ms | 0% |
| **트랜잭션 분리 + 커넥션 반환 설정 (최종 적용)** | **3회 중앙값** | **0** | **32ms** | **2,877ms** | 0% |
| 참고: 조회 API 단독 실행 | 1회 | 0 | 11ms | - | 0% |

- 최종 적용 후 조회 p95는 3회 측정에서 22~82ms 범위
- 최종 적용 후 이미지 생성 p95(2,877ms)는 Mock 지연 합(2.8초)에 근접 → 남은 시간은 외부 호출 자체의 대기 시간

**측정 재현**
```bash
./gradlew bootRun --args='--spring.profiles.active=local,mock'
perf/run.sh <label>
```

### 6. 남은 한계
- 수치는 로컬 단일 인스턴스와 Mock 지연 기준입니다. 운영 환경에서의 개선 폭은 측정하지 않았습니다.
- 부하 시나리오는 이미지 생성 경로(OpenAI + S3)만 포함합니다. 이미지 분할(Flask SAM) 경로는 같은 방식으로 분리했지만 부하 측정은 하지 않았습니다.
- 카테고리 분류(`CategoryService.receiveCategory`)와 분석 리포트(`ReportService.receiveReport`)의 Flask 호출은 아직 트랜잭션 안에 있습니다.
- 검증과 저장 사이에 채팅방 상태가 바뀌는 경합은 저장 시점에 재검증하지 않습니다.
- S3 보상 삭제가 실패하면 고아 파일이 남을 수 있습니다.
- Flask·OpenAI 호출(`RestTemplate`)에 타임아웃·재시도·폴백을 두지 않았습니다.

---

## 🤖 AI 서버 연동 구조

- 이미지 생성: Spring 서버가 OpenAI 이미지 생성 API를 호출하고 결과를 S3에 저장
- 카테고리 분류·이미지 분할(SAM)·분석 리포트: Spring 서버 → Flask 서버 REST 호출(`RestTemplate`, 동기 방식)

Flask 호출 실패 시 요청은 오류로 응답됩니다.

---

## 🔌 WebSocket

`/ws/json` 엔드포인트로 Unreal Engine 클라이언트와 WebSocket 세션을 맺습니다.

- 연결 시 세션 ID를 클라이언트에 전달
- 이미지 분할 완료 시 결과 JSON의 presigned URL을 push
- 세션은 가장 최근에 연결된 1개만 보관하는 단일 클라이언트 구조

Unreal에서 발생한 작업 이력은 WebSocket이 아니라 REST API(`POST /api/v1/unreal-history/{chatId}`)로 수신합니다.

---

## 🔐 인증 시스템

Spring Security 기반 JWT 인증 구조를 구현했습니다.

- Access Token → `Authorization` 헤더로 전달
- Refresh Token → HttpOnly·Secure 쿠키로 전달
- Refresh Token은 Redis에 저장하고, 재발급 시 저장된 값과 일치할 때만 새 토큰을 발급 (Refresh Token Rotation)
- 로그아웃 시 Redis의 Refresh Token을 삭제 (발급된 Access Token은 만료 전까지 유효)
- 비밀번호는 BCrypt로 해시 저장

JWT 검증 필터는 토큰 검증 후 요청마다 DB에서 사용자를 조회합니다.

---

## 🗂 캐시

반복 조회되는 채팅·옵션 목록 API에 Ehcache(JCache) 기반 `@Cacheable`을 적용했습니다.

- 대상: 전체 채팅방 목록(`findAllChatsCache`), 채팅방 상세(`findChatCache`), 옵션 목록(`optionLists`)
- TTL 기반 만료: 채팅 관련 10분, 옵션 목록 12시간

캐시 무효화(`@CacheEvict`)는 두지 않고 TTL 만료에 의존합니다. 그래서 새로 만든 채팅방이나 추가된 프롬프트가 목록·상세 조회에 최대 10분 늦게 반영될 수 있습니다.

---

## 📊 로그 및 모니터링

Logback(`logback-spring.xml`)과 MDC 필터로 요청 단위 로그를 추적합니다.

- 요청마다 `traceId`(UUID)를 발급해 MDC에 저장하고, 요청 처리 중 로그에 `[traceId]`를 출력 (`LoggingMdcFilter`)
- JSON 요청 본문을 로그로 기록하며 `password` 필드는 `***`로 마스킹
- 프로필별 출력: `prod`는 파일(`hwarok.log`), `org.springframework` WARN 이상은 별도 파일(`log/hotfixLog.log`), `local`은 콘솔
- 전역 예외 핸들러의 예외 로그, 캐시 이벤트 로깅(`CacheEventLogger`)
- 부하 측정 시 Actuator의 HikariCP 지표(`hikaricp.connections.*`)를 1초 간격으로 수집 (`perf/hikari_poll.py`)
