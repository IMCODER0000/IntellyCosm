#!/bin/bash

# 테스트 설정
TOTAL_REQUESTS=100
CONCURRENT_USERS=10
URL="http://localhost:8080/api/analysis/request"

echo "부하 테스트 시작"
echo "총 요청 수: $TOTAL_REQUESTS"
echo "동시 사용자 수: $CONCURRENT_USERS"

# 시작 시간 기록
START_TIME=$(date +%s%N)

# ab (Apache Bench)를 사용한 부하 테스트
ab -n $TOTAL_REQUESTS -c $CONCURRENT_USERS -T 'application/json' \
  -p request.json \
  $URL

# 종료 시간 기록
END_TIME=$(date +%s%N)

# 총 소요 시간 계산 (밀리초)
DURATION=$(( ($END_TIME - $START_TIME) / 1000000 ))

echo "테스트 완료"
echo "총 소요 시간: ${DURATION}ms"
echo "초당 처리량: $(echo "scale=2; $TOTAL_REQUESTS / ($DURATION / 1000)" | bc) requests/second"
