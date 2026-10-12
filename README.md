# 화록

> AI 기반 디지털 창작자 히스토리 및 기여도 분석 플랫폼  

---

## 📌 개요

**화록**은 생성형 AI를 활용한 이미지 창작 과정에서  
창작자의 **프롬프트 작성, 수정, 후처리 개입 과정**을 기록·분석하여  
**기여도와 창의성을 정량 지표로 제시**하는 플랫폼입니다.

AI가 생성한 결과물만이 아니라 **창작 과정 전체를 데이터로 기록**하여  
창작 과정을 설명할 수 있는 근거 데이터를 제공합니다.

---

## 🎯 프로젝트 목표

- AI 생성 이미지에 대한 **창작자의 개입 요소 추적**
- 프롬프트·이미지·후처리 작업의 **히스토리 기록**
- 창작자의 **기여도 및 창의성 정량 평가** (유창성·지속성·창의성 지표)

---

## 🛠 기술 스택

- Java 21, Spring Boot 3.3.2, Spring Security, Spring Data JPA
- MySQL, Redis, Ehcache(JCache)
- AWS S3, OpenAI 이미지 생성 API(`gpt-image-1`)
- Flask AI 서버 (이미지 분할 SAM, 카테고리 분류, 분석 리포트)
- JWT, WebSocket, Swagger(springdoc), Actuator

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
├── common          # 공통 응답, 예외 처리, 인증(JWT), 캐시, S3, WebSocket, 유틸
├── flask/service   # Flask 서버 연동 (카테고리 분류, 이미지 분할, 분석 리포트)
├── history         # 창작 히스토리 및 Unreal 작업 이력 관리
├── imageflow       # 결과 이미지 분할(segment)·다운로드 흐름
├── prompt          # 채팅방·프롬프트 관리 및 이미지 생성(OpenAI)
├── report          # 창의성·기여도 분석 리포트
├── user            # 사용자 관리
└── ProjectApplication.java
```
---

## 🔌 WebSocket

`/ws/json` 엔드포인트로 Unreal Engine 클라이언트와 WebSocket 세션을 맺습니다.

- 연결 시 세션 ID 전달
- 이미지 분할 완료 시 결과 JSON의 presigned URL을 연결된 클라이언트에 push

Unreal에서 발생한 작업 이력은 WebSocket이 아니라 REST API(`POST /api/v1/unreal-history/{chatId}`)로 수신합니다.

---
## 🤖 AI 서버 연동 구조

- 이미지 생성: Spring 서버가 OpenAI 이미지 생성 API를 호출하고 결과를 S3에 저장
- 카테고리 분류·이미지 분할(SAM)·분석 리포트: Spring 서버 → Flask 서버 REST 호출(`RestTemplate`, 동기 방식)

Flask 호출에는 타임아웃·재시도·폴백을 두지 않았고, 호출 실패 시 요청이 오류로 응답됩니다.

---
## 🔐 인증 시스템

Spring Security 기반 JWT 인증 구조를 구현했습니다.

- Access Token → `Authorization` 헤더로 전달
- Refresh Token → HttpOnly, Secure Cookie로 전달
- Refresh Token은 Redis에 저장하고, 재발급 시 저장된 값과 일치할 때만 새 토큰을 발급(Refresh Token 교체)
- 로그아웃 시 Redis의 Refresh Token을 삭제 (발급된 Access Token은 만료 전까지 유효)

JWT 검증 필터는 Redis를 조회하지 않고, 토큰 검증 후 요청마다 DB에서 사용자를 조회합니다.

---

## ⚙️ 성능 최적화

### 1. DB 커넥션 점유 문제 해결

**문제**: 서비스가 불안정하고 응답이 느려짐. RDS 커넥션 12개 중 쿼리를 실행 중인 것은 2개뿐이고 나머지는 `Sleep` 상태로 오래 유지됨.

**1차 대응 (2025.08)**
- `show processlist`로 Sleep 세션의 장기 유지를 확인
- HikariCP 커넥션 풀 설정(pool size, idle-timeout, max-lifetime) 조정
  - Sleep 세션 유지 시간 500~1000s → 90~180s, JMeter(100 VUser × Loop 10) Error Rate 6.45% → 0%
  - 에러는 사라졌지만 평균 응답이 1초 이상으로 남아 근본 원인이 해결되지 않음

**근본 원인 분석 및 해결 (2026.10, 이슈 #160)**

원인
- OpenAI·Flask 호출과 S3 업로드가 `@Transactional` 안에 있어, 외부 응답을 기다리는 동안 DB 커넥션을 점유
- 트랜잭션을 분리해도 Hibernate의 기본 커넥션 보유 방식(`DELAYED_ACQUISITION_AND_HOLD`)과 `open-in-view`(기본 true) 때문에 세션이 열려 있는 동안 커넥션이 반환되지 않음

해결
- 외부 호출을 트랜잭션 밖으로 분리: `DB 조회·검증 → 외부 호출(트랜잭션 밖) → DB 저장`
  - `PromptService.generateImage`, `ImageService.segmentResultImage`
- 트랜잭션 종료 시 커넥션을 반환하도록 Hibernate 설정 변경 (`JpaConnectionConfig`)
- DB 저장 실패 시 이번 요청이 올린 S3 파일을 삭제하는 보상 처리 (삭제 실패 시 고아 파일이 남을 수 있음)

**측정 결과** (로컬 Docker, 풀 크기 10, OpenAI 2.5초·S3 0.3초 지연 Mock, JMeter 100 VUser × Loop 10, 실행별 결과 분리 저장)

| 항목 | 개선 전 | 개선 후 (3회 중앙값) |
|---|---|---|
| 커넥션 풀 최대 대기(pending) | 81 | 0 |
| 외부 호출과 무관한 조회 API p95 | 11,124ms | 32ms (단독 실행 시 11ms) |
| 외부 호출 API p95 | 17,147ms | 2,877ms |
| 에러율 | 0% | 0% |

- 트랜잭션 분리만 적용한 경우(pending 82, 조회 p95 11,338ms), 커넥션 반환 설정만 적용한 경우(1회 측정, pending 85, 조회 p95 12,573ms)에는 개선이 없었고 **두 변경을 함께 적용해야** 개선됨

**측정 재현**
```bash
./gradlew bootRun --args='--spring.profiles.active=local,mock' 
perf/run.sh <label>                                      
```

---

### 2. 캐시 기반 조회 성능 개선

채팅 조회와 옵션 목록 조회는 생성 빈도보다 조회 빈도가 높은 특성을 가집니다.

- Ehcache(JCache) 기반 `@Cacheable` 적용: 전체 채팅방 목록(`findAllChatsCache`), 채팅방 상세(`findChatCache`), 옵션 목록(`optionLists`)
- TTL 기반 만료: 채팅 관련 10분, 옵션 목록 12시간

캐시 무효화(`@CacheEvict`)는 두지 않고 TTL 만료에 의존합니다.

---

## 📊 로그 및 모니터링

Logback(`logback-spring.xml`)과 MDC 필터로 요청 단위 로그를 추적합니다.

- 요청마다 `traceId`(UUID)를 발급해 MDC에 저장하고, 모든 로그 라인에 `[traceId]`를 출력 (`LoggingMdcFilter`)
- JSON 요청 본문을 로그로 기록하며 `password` 필드는 `***`로 마스킹
- 프로필별 출력: `prod`는 파일(`hwarok.log`), `org.springframework` WARN 이상은 별도 파일(`log/hotfixLog.log`), `local`은 콘솔
- 전역 예외 핸들러의 예외 로그, 캐시 이벤트 로깅(`CacheEventLogger`)
- 로컬 부하 측정 시 Actuator의 HikariCP 지표(`hikaricp.connections.*`) 수집


---

## 🏆 성과

- ICT 한이음 프로젝트 **중간평가 우수작 선정**

### 📄 논문

- **DPDT기반 생성형 AI 콘텐츠 창의성 추적 시스템**
  - 2025년 한국방송·미디어공학회 하계학술대회
  - 최원영, 강민지, 방예원, 이다희, 임양미 (공동 저자)
  - 화록 프로젝트 관련 논문

---
