#!/usr/bin/env bash
# usage: perf/run.sh <label>   예) perf/run.sh before
# 서버가 --spring.profiles.active=local,mock 으로 떠 있어야 한다. 실행마다 perf/results/<시각>-<label>/ 에 결과를 분리 저장한다.
set -euo pipefail
LABEL=${1:?usage: perf/run.sh <label>}
HOST=${HOST:-localhost}; PORT=${PORT:-8080}; BASE=http://$HOST:$PORT
cd "$(dirname "$0")"
OUT=results/$(date +%Y%m%d-%H%M%S)-$LABEL
mkdir -p "$OUT"

curl -sf "$BASE/actuator/metrics/hikaricp.connections.max" >/dev/null \
  || { echo "Hikari 지표 접근 불가: mock 프로필로 서버를 띄웠는지 확인" >&2; exit 1; }

# 준비: 테스트 유저(이미 있으면 가입은 실패해도 무시) → 로그인 → 채팅방 생성
J='-H Content-Type:application/json'
curl -s $J -d '{"id":"perfuser","nickName":"perf","password":"perftest1234"}' "$BASE/api/v1/user/signup" >/dev/null || true
TOKEN=$(curl -s -i $J -d '{"loginId":"perfuser","password":"perftest1234"}' "$BASE/api/v1/auth/login" \
  | tr -d '\r' | awk -F': ' 'tolower($1)=="authorization"{sub(/^Bearer /,"",$2); print $2}')
[ -n "$TOKEN" ] || { echo "로그인 실패" >&2; exit 1; }
CHAT_ID=$(curl -s -X POST -H "Authorization: Bearer $TOKEN" "$BASE/api/v1/chat/create?paints=%EC%96%B4%ED%95%B4%EB%8F%84" \
  | python3 -c 'import json,sys
def find(o):
    if isinstance(o,dict):
        if "chatId" in o: return o["chatId"]
        for v in o.values():
            r=find(v)
            if r is not None: return r
print(find(json.load(sys.stdin)))')
[ "$CHAT_ID" != "None" ] || { echo "채팅방 생성 실패" >&2; exit 1; }
echo "token ok, chatId=$CHAT_ID, out=$OUT"

python3 hikari_poll.py "$OUT" "$BASE" > "$OUT/hikari-poll.log" 2>&1 &
POLL=$!
sleep 2
jmeter -n -t scenario.jmx -l "$OUT/result.jtl" -e -o "$OUT/report" -j "$OUT/jmeter.log" \
  -Jhost="$HOST" -Jport="$PORT" -Jtoken="$TOKEN" -JchatId="$CHAT_ID" \
  -JgenThreads="${GEN_THREADS:-50}" -JreadThreads="${READ_THREADS:-50}" -Jloops="${LOOPS:-10}" -JrampUp="${RAMP_UP:-5}"
kill -TERM $POLL; wait $POLL || true

echo "== Hikari ($LABEL)"; cat "$OUT/hikari-summary.json"
echo "== JMeter p95 / error ($LABEL)"
python3 - "$OUT/report/statistics.json" <<'PY'
import json, sys
for label, s in json.load(open(sys.argv[1])).items():
    print(f"{label:12s} n={s['sampleCount']:5d} avg={s['meanResTime']:.0f}ms p95={s['pct2ResTime']:.0f}ms error={s['errorPct']:.2f}%")
PY
