#!/bin/bash

# 스크립트 실행 디렉토리로 이동
cd "$(dirname "$0")"

echo "===== Blue-Green 무중단 배포 시작 ====="
echo "$(date)"

# 환경 변수 파일 로드 (필요한 경우)
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

# 현재 실행 중인 컨테이너 확인 (blue 또는 green)
CURRENT_CONTAINER=$(docker ps --filter "name=app-" --format "{{.Names}}" | grep -E 'app-blue|app-green' || echo "NONE")

if [ "$CURRENT_CONTAINER" == "app-blue" ] || [ "$CURRENT_CONTAINER" == "NONE" ]; then
  # Blue가 실행 중이거나 컨테이너가 없으면 Green 배포
  TARGET_COLOR="green"
  IDLE_COLOR="blue"
else
  # Green이 실행 중이면 Blue 배포
  TARGET_COLOR="blue"
  IDLE_COLOR="green"
fi

echo "현재 운영 중인 컨테이너: $CURRENT_CONTAINER"
echo "배포 대상 컨테이너: app-$TARGET_COLOR"

# 최신 이미지 가져오기
echo "최신 이미지 가져오는 중..."
docker pull chisae/pyeondongbu-app:latest

# 기존 컨테이너가 없는 경우 DB와 Redis 컨테이너 먼저 시작
if [ "$CURRENT_CONTAINER" == "NONE" ]; then
  echo "DB와 Redis 컨테이너 시작 중..."
  docker-compose -f docker-compose.prod.yml up -d db redis
  
  # DB와 Redis가 준비될 때까지 대기
  echo "DB와 Redis 준비 대기 중..."
  sleep 30
fi

# 새 컨테이너 실행 (대상 컨테이너)
echo "새 컨테이너(app-$TARGET_COLOR) 시작 중..."
docker-compose -f docker-compose.prod.yml -f docker-compose.$TARGET_COLOR.yml up -d app-$TARGET_COLOR

# 새 컨테이너가 정상적으로 시작될 때까지 대기
echo "새 컨테이너 헬스체크 중..."
for i in {1..30}; do
  # 컨테이너 상태 확인
  CONTAINER_STATUS=$(docker inspect --format='{{.State.Status}}' app-$TARGET_COLOR 2>/dev/null || echo "not_found")
  
  if [ "$CONTAINER_STATUS" == "running" ]; then
    # 컨테이너가 실행 중이면 포트 접근 가능 여부 확인
    PORT_CHECK=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:808$([[ "$TARGET_COLOR" == "blue" ]] && echo "1" || echo "2") || echo "000")
    
    if [ "$PORT_CHECK" != "000" ]; then
      echo "새 컨테이너 정상 작동 확인 (상태: $CONTAINER_STATUS, 포트 응답: $PORT_CHECK)"
      # 애플리케이션 시작 시간 고려
      sleep 5
      break
    fi
  fi
  
  echo "컨테이너 상태 확인 중... (시도: $i, 상태: $CONTAINER_STATUS)"
  sleep 2
  
  if [ $i -eq 30 ]; then
    echo "새 컨테이너 시작 실패. 배포 중단."
    exit 1
  fi
done

# Nginx 설정 업데이트 (트래픽 전환)
echo "Nginx 설정 업데이트 중..."
if [ "$TARGET_COLOR" == "blue" ]; then
  sed -i 's/app-green/app-blue/g' nginx.conf
else
  sed -i 's/app-blue/app-green/g' nginx.conf
fi

# Nginx 시작 또는 재시작
if docker ps | grep -q nginx; then
  echo "Nginx 재시작 중..."
  docker-compose -f docker-compose.prod.yml restart nginx
else
  echo "Nginx 시작 중..."
  docker-compose -f docker-compose.prod.yml up -d nginx
fi

echo "트래픽이 app-$TARGET_COLOR로 전환되었습니다."

# 이전 컨테이너 중지 (잠시 대기 후)
if [ "$CURRENT_CONTAINER" != "NONE" ]; then
  echo "이전 컨테이너 종료 대기 중... (10초)"
  sleep 10
  
  echo "이전 컨테이너($CURRENT_CONTAINER) 중지 중..."
  docker stop $CURRENT_CONTAINER
fi

echo "===== Blue-Green 무중단 배포 완료 ====="
echo "$(date)"

# 불필요한 이미지 정리 (선택 사항)
echo "불필요한 이미지 정리 중..."
docker image prune -f

# 배포 결과 확인
echo "배포 결과 확인 중..."
FINAL_CHECK=$(curl -s -o /dev/null -w "%{http_code}" https://api.pyeondongbu.com/actuator/health || echo "000")
if [ "$FINAL_CHECK" == "200" ]; then
  echo "서비스가 정상적으로 배포되었습니다. (상태 코드: $FINAL_CHECK)"
else
  echo "주의: 서비스 상태 확인 실패 (상태 코드: $FINAL_CHECK)"
fi

echo "배포 스크립트 실행 완료" 