#!/bin/bash

# 스크립트 실행 디렉토리로 이동
cd "$(dirname "$0")"

echo "===== Blue-Green 무중단 배포 시작 ====="
echo "$(date)"

# 환경 변수 파일 로드 (필요한 경우)
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

# 네트워크 존재 여부 확인 및 생성
NETWORK_EXISTS=$(docker network ls | grep app_app-network || echo "")
if [ -z "$NETWORK_EXISTS" ]; then
  echo "app_app-network 네트워크가 존재하지 않습니다. 생성합니다."
  docker network create app_app-network
else
  echo "app_app-network 네트워크가 이미 존재합니다."
fi

# 기존 단일 배포 컨테이너 확인 및 중지
OLD_APP_CONTAINER=$(docker ps --filter "name=app-app-1" --format "{{.Names}}" | grep -q "app-app-1" && echo "yes" || echo "no")
if [ "$OLD_APP_CONTAINER" == "yes" ]; then
  echo "기존 단일 배포 컨테이너(app-app-1)가 실행 중입니다. 중지합니다."
  docker stop app-app-1
  docker rm app-app-1
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

# 배포 전 상태 저장 (롤백을 위해)
if [ "$CURRENT_CONTAINER" != "NONE" ]; then
  echo "롤백을 위해 현재 상태 저장"
  CURRENT_NGINX_CONF=$(cat /app/nginx.conf)
fi

# 최신 이미지 가져오기
echo "최신 이미지 가져오는 중..."
docker pull chisae/pyeondongbu-app:latest

# 기존 컨테이너가 없는 경우 DB와 Redis 컨테이너 먼저 시작
if [ "$CURRENT_CONTAINER" == "NONE" ]; then
  echo "DB와 Redis 컨테이너 시작 중..."
  # 이미 실행 중인 DB와 Redis 컨테이너가 있는지 확인
  DB_RUNNING=$(docker ps --filter "name=app-db-1" --format "{{.Names}}" | grep -q "app-db-1" && echo "yes" || echo "no")
  REDIS_RUNNING=$(docker ps --filter "name=app-redis-1" --format "{{.Names}}" | grep -q "app-redis-1" && echo "yes" || echo "no")
  
  if [ "$DB_RUNNING" == "no" ] || [ "$REDIS_RUNNING" == "no" ]; then
    docker-compose -f docker-compose.prod.yml up -d db redis
  else
    echo "DB와 Redis 컨테이너가 이미 실행 중입니다."
  fi
  
  # DB와 Redis가 준비될 때까지 대기
  echo "DB와 Redis 준비 대기 중..."
  sleep 30
fi

# 새 컨테이너 실행 (대상 컨테이너)
echo "새 컨테이너(app-$TARGET_COLOR) 시작 중..."
docker-compose -f docker-compose.prod.yml -f docker-compose.$TARGET_COLOR.yml up -d app-$TARGET_COLOR

# 새 컨테이너가 정상적으로 시작될 때까지 대기
echo "새 컨테이너 헬스체크 중..."
HEALTH_CHECK_PASSED=false
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
      HEALTH_CHECK_PASSED=true
      break
    fi
  fi
  
  echo "컨테이너 상태 확인 중... (시도: $i, 상태: $CONTAINER_STATUS)"
  sleep 2
  
  if [ $i -eq 30 ]; then
    echo "새 컨테이너 시작 실패. 배포 중단 및 롤백 시작."
    # 롤백 로직 - 새 컨테이너 중지
    docker stop app-$TARGET_COLOR
    docker rm app-$TARGET_COLOR
    
    if [ "$CURRENT_CONTAINER" != "NONE" ]; then
      echo "이전 상태로 롤백합니다."
      # 이전 컨테이너가 있었다면 계속 사용
      echo "롤백 완료. 이전 컨테이너($CURRENT_CONTAINER)를 계속 사용합니다."
    fi
    
    echo "===== 배포 실패, 롤백 완료 ====="
    exit 1
  fi
done

# 헬스체크 실패 시 롤백
if [ "$HEALTH_CHECK_PASSED" != "true" ]; then
  echo "헬스체크 실패. 롤백을 시작합니다."
  docker stop app-$TARGET_COLOR
  docker rm app-$TARGET_COLOR
  
  if [ "$CURRENT_CONTAINER" != "NONE" ]; then
    echo "이전 상태로 롤백합니다."
    # 이전 컨테이너가 있었다면 계속 사용
    echo "롤백 완료. 이전 컨테이너($CURRENT_CONTAINER)를 계속 사용합니다."
  fi
  
  echo "===== 배포 실패, 롤백 완료 ====="
  exit 1
fi

# Nginx 설정 업데이트 (트래픽 전환)
echo "Nginx 설정 업데이트 중..."
if [ "$TARGET_COLOR" == "blue" ]; then
  sed -i 's/app-green/app-blue/g' /app/nginx.conf
else
  sed -i 's/app-blue/app-green/g' /app/nginx.conf
fi

# Nginx 시작 또는 재시작
if docker ps | grep -q app-nginx-1; then
  echo "Nginx 재시작 중..."
  docker restart app-nginx-1
else
  echo "Nginx 시작 중..."
  docker-compose -f docker-compose.prod.yml up -d nginx
fi

# 모니터링 서비스 시작 또는 재시작
echo "모니터링 서비스 확인 중..."
PROMETHEUS_RUNNING=$(docker ps --filter "name=app-prometheus-1" --format "{{.Names}}" | grep -q "app-prometheus-1" && echo "yes" || echo "no")
GRAFANA_RUNNING=$(docker ps --filter "name=app-grafana-1" --format "{{.Names}}" | grep -q "app-grafana-1" && echo "yes" || echo "no")

if [ "$PROMETHEUS_RUNNING" == "no" ] || [ "$GRAFANA_RUNNING" == "no" ]; then
  echo "모니터링 서비스 시작 중..."
  docker-compose -f docker-compose.prod.yml up -d prometheus grafana
else
  echo "모니터링 서비스가 이미 실행 중입니다."
fi

# Nginx 재시작 후 상태 확인
NGINX_STATUS=$(docker inspect --format='{{.State.Status}}' app-nginx-1 2>/dev/null || echo "not_found")
if [ "$NGINX_STATUS" != "running" ]; then
  echo "Nginx 재시작 실패. 롤백을 시작합니다."
  
  # Nginx 설정 롤백
  if [ "$CURRENT_CONTAINER" != "NONE" ]; then
    echo "$CURRENT_NGINX_CONF" > /app/nginx.conf
    docker-compose -f docker-compose.prod.yml up -d nginx
  fi
  
  # 새 컨테이너 중지
  docker stop app-$TARGET_COLOR
  docker rm app-$TARGET_COLOR
  
  echo "롤백 완료. 이전 컨테이너($CURRENT_CONTAINER)를 계속 사용합니다."
  echo "===== 배포 실패, 롤백 완료 ====="
  exit 1
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
  echo "하지만 컨테이너는 정상 실행 중입니다. 모니터링이 필요합니다."
fi

echo "배포 스크립트 실행 완료" 