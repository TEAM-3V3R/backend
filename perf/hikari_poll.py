"""HikariCP 지표를 1초마다 hikari.csv 에 기록하고, 종료(SIGTERM) 시 구간 평균 점유 시간을 hikari-summary.json 에 쓴다.
usage: python3 hikari_poll.py <out_dir> [base_url]"""
import csv, json, signal, sys, time, urllib.request

out = sys.argv[1]
base = (sys.argv[2] if len(sys.argv) > 2 else "http://localhost:8080") + "/actuator/metrics/hikaricp.connections"
stop = False
signal.signal(signal.SIGTERM, lambda *_: globals().update(stop=True))


def get(name):
    with urllib.request.urlopen(f"{base}.{name}", timeout=3) as r:
        return {m["statistic"]: m["value"] for m in json.load(r)["measurements"]}


start = get("usage")
peak_pending = 0
with open(f"{out}/hikari.csv", "w", newline="") as f:
    w = csv.writer(f)
    w.writerow(["time", "active", "pending"])
    while not stop:
        active, pending = get("active")["VALUE"], get("pending")["VALUE"]
        peak_pending = max(peak_pending, pending)
        w.writerow([time.strftime("%H:%M:%S"), active, pending])
        f.flush()
        time.sleep(1)

end = get("usage")
n = end["COUNT"] - start["COUNT"]
summary = {
    "connection_acquisitions": n,
    "avg_usage_ms": round((end["TOTAL_TIME"] - start["TOTAL_TIME"]) / n * 1000, 1) if n else None,
    "max_usage_ms": round(end["MAX"] * 1000, 1),  # 풀 생성 이후 최댓값(구간 한정 아님)
    "peak_pending": peak_pending,
}
json.dump(summary, open(f"{out}/hikari-summary.json", "w"), indent=2)
print(json.dumps(summary, ensure_ascii=False))
