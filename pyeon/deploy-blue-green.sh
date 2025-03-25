#!/bin/bash

# Blue-Green 무중단 배포 스크립트
# 작성일: 2023-11-15
# 수정일: 2023-11-20

set -e  # 오류 발생 시 스크립트 중단

# 기본 설정
BLUE_PORT=8081
GREEN_PORT=8082
APP_NAME="app"
# Nginx 설정 파일 경로
NGINX_CONF="/app/nginx.conf"

echo "===== Blue-Green 무중단 배포 시작 ====="
echo "$(date)"

# 스크립트 실행 디렉토리로 이동
cd "$(dirname "$0")"

# 환경 변수 설정
echo "환경 변수 설정 중..."
# .env 파일이 없으면 생성
if [ ! -f .env ]; then
  echo "새 .env 파일 생성 중..."
  touch .env
fi

# PROD_FRONTEND_CALLBACK_URI 환경 변수 확인 및 설정
if grep -q "PROD_FRONTEND_CALLBACK_URI" .env; then
  # 이미 있는 경우 올바른 형식인지 확인하고 필요시 업데이트
  CURRENT_URI=$(grep "PROD_FRONTEND_CALLBACK_URI" .env | cut -d '=' -f2)
  if [[ "$CURRENT_URI" != "http"* ]]; then
    # 절대 URL이 아닌 경우 업데이트
    PROD_FRONTEND_URL=$(grep "PROD_FRONTEND_URL" .env | cut -d '=' -f2)
    if [[ -n "$PROD_FRONTEND_URL" ]]; then
      sed -i "s|PROD_FRONTEND_CALLBACK_URI=.*|PROD_FRONTEND_CALLBACK_URI=$PROD_FRONTEND_URL/auth/callback|g" .env
      echo "PROD_FRONTEND_CALLBACK_URI를 절대 URL로 업데이트했습니다: $PROD_FRONTEND_URL/auth/callback"
    else
      echo "경고: PROD_FRONTEND_URL이 .env 파일에 없어 PROD_FRONTEND_CALLBACK_URI를 업데이트할 수 없습니다."
    fi
  fi
else
  # 없는 경우 추가
  PROD_FRONTEND_URL=$(grep "PROD_FRONTEND_URL" .env | cut -d '=' -f2)
  if [[ -n "$PROD_FRONTEND_URL" ]]; then
    echo "PROD_FRONTEND_CALLBACK_URI=$PROD_FRONTEND_URL/auth/callback" >> .env
    echo "PROD_FRONTEND_CALLBACK_URI를 추가했습니다: $PROD_FRONTEND_URL/auth/callback"
  else
    echo "경고: PROD_FRONTEND_URL이 .env 파일에 없어 PROD_FRONTEND_CALLBACK_URI를 설정할 수 없습니다."
  fi
fi

# 환경 변수 확인
echo "현재 환경 변수 설정:"
grep -E "PROD_FRONTEND_URL|PROD_FRONTEND_CALLBACK_URI" .env || echo "관련 환경 변수를 찾을 수 없습니다."

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

# 모니터링 서비스 시작 또는 확인 (먼저 시작)
echo "모니터링 서비스 확인 중..."
PROMETHEUS_RUNNING=$(docker ps --filter "name=app-prometheus-1" --format "{{.Names}}" | grep -q "app-prometheus-1" && echo "yes" || echo "no")
GRAFANA_RUNNING=$(docker ps --filter "name=app-grafana-1" --format "{{.Names}}" | grep -q "app-grafana-1" && echo "yes" || echo "no")

# Grafana 관리자 비밀번호 설정
if [ -z "$GRAFANA_ADMIN_PASSWORD" ]; then
  echo "경고: GRAFANA_ADMIN_PASSWORD 환경 변수가 설정되지 않았습니다."
  echo "기본 비밀번호를 사용합니다: admin"
  export GRAFANA_ADMIN_PASSWORD="admin"
else
  echo "Grafana 관리자 비밀번호가 설정되었습니다."
fi

if [ "$PROMETHEUS_RUNNING" == "no" ] || [ "$GRAFANA_RUNNING" == "no" ]; then
  echo "모니터링 서비스 시작 중..."
  # Grafana 환경 변수 설정
  export GF_SECURITY_ADMIN_PASSWORD=$GRAFANA_ADMIN_PASSWORD
  export GF_SERVER_ROOT_URL=https://api.pyeondongbu.com/grafana
  export GF_SERVER_SERVE_FROM_SUB_PATH=true
  
  docker-compose -f docker-compose.prod.yml up -d prometheus grafana
else
  echo "모니터링 서비스가 이미 실행 중입니다."
fi

# 현재 실행 중인 컨테이너 확인
CURRENT_CONTAINER=$(docker ps --filter "name=app-blue|app-green" --format "{{.Names}}" | head -n 1 || echo "NONE")
echo "현재 실행 중인 컨테이너: $CURRENT_CONTAINER"

# Nginx 설정에서 현재 활성화된 색상 확인
if [ -f "$NGINX_CONF" ]; then
  NGINX_ACTIVE_COLOR=$(grep -o "app-[a-z]*" "$NGINX_CONF" | head -n 1 | cut -d '-' -f 2 || echo "NONE")
  echo "Nginx 설정에서 활성화된 색상: $NGINX_ACTIVE_COLOR"
else
  NGINX_ACTIVE_COLOR="NONE"
  echo "Nginx 설정 파일을 찾을 수 없습니다: $NGINX_CONF"
fi

# 배포 대상 색상 결정
if [ "$NGINX_ACTIVE_COLOR" == "blue" ]; then
  TARGET_COLOR="green"
  IDLE_COLOR="blue"
  TARGET_PORT=$GREEN_PORT
elif [ "$NGINX_ACTIVE_COLOR" == "green" ]; then
  TARGET_COLOR="blue"
  IDLE_COLOR="green"
  TARGET_PORT=$BLUE_PORT
else
  if [[ "$CURRENT_CONTAINER" == *"blue"* ]]; then
    TARGET_COLOR="green"
    IDLE_COLOR="blue"
    TARGET_PORT=$GREEN_PORT
  else
    TARGET_COLOR="blue"
    IDLE_COLOR="green"
    TARGET_PORT=$BLUE_PORT
  fi
fi

echo "배포 대상 색상: $TARGET_COLOR (포트: $TARGET_PORT)"
echo "유휴 색상: $IDLE_COLOR"

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

# 새 컨테이너가 정상적으로 실행되는지 확인
echo "새 컨테이너 상태 확인 중... (최대 30초 대기)"
for i in {1..30}; do
  HEALTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$TARGET_PORT/actuator/health || echo "000")
  if [ "$HEALTH_STATUS" == "200" ]; then
    echo "새 컨테이너가 정상적으로 실행 중입니다. (상태 코드: $HEALTH_STATUS)"
    break
  else
    echo "새 컨테이너 상태 확인 중... 시도 $i/30 (상태 코드: $HEALTH_STATUS)"
    sleep 1
  fi
  
  if [ $i -eq 30 ]; then
    echo "경고: 새 컨테이너가 30초 내에 정상 상태가 되지 않았습니다. 배포를 계속할지 확인하세요."
    # 여기서 배포를 중단하거나 계속할지 결정할 수 있습니다
    # 현재는 경고만 표시하고 계속 진행합니다
  fi
done

# Nginx 설정 업데이트
echo "Nginx 설정 업데이트 중..."
if [ ! -f "$NGINX_CONF" ]; then
  echo "Nginx 설정 파일이 없습니다. 기본 설정 파일을 생성합니다."
  cat > "$NGINX_CONF" << EOF
# Nginx 설정 파일
# 현재 ${TARGET_COLOR} 환경으로 설정

server {
    listen 80;
    server_name api.pyeondongbu.com;

    # 애플리케이션 API
    location / {
        proxy_pass http://app-${TARGET_COLOR}:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # Prometheus
    location /prometheus/ {
        auth_basic "Prometheus";
        auth_basic_user_file /etc/nginx/.htpasswd.prometheus;
        proxy_pass http://app-prometheus-1:9090/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
    }

    # Grafana
    location /grafana/ {
        proxy_pass http://app-grafana-1:3000/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
    }
}
EOF
  echo "기본 Nginx 설정 파일이 생성되었습니다."
else
  # 기존 설정 파일이 있는 경우 업데이트
  sed -i "s/proxy_pass http:\/\/app-[a-z]*:8080/proxy_pass http:\/\/app-$TARGET_COLOR:8080/g" "$NGINX_CONF"
  sed -i "s/현재 [a-z]* 환경으로 설정/현재 $TARGET_COLOR 환경으로 설정/g" "$NGINX_CONF"
  echo "Nginx 설정이 app-$TARGET_COLOR로 업데이트되었습니다."
fi

# 변경 후 Nginx 설정 확인
echo "업데이트된 Nginx 설정 확인:"
grep -o "proxy_pass http://app-[a-z]*:8080" "$NGINX_CONF" || echo "설정을 확인할 수 없습니다."

# Nginx 재시작
echo "Nginx 재시작 중..."
if docker ps | grep -q app-nginx; then
  docker restart app-nginx-1
  echo "Nginx 컨테이너가 재시작되었습니다."
else
  echo "Nginx 컨테이너 시작 중..."
  docker-compose -f docker-compose.prod.yml up -d nginx
  echo "Nginx 컨테이너가 시작되었습니다."
fi

# 이전 컨테이너 중지 (잠시 대기 후)
echo "이전 컨테이너 종료 대기 중... (10초)"
sleep 10

# 이전 컨테이너(IDLE_COLOR) 중지
IDLE_CONTAINER=$(docker ps --filter "name=app-$IDLE_COLOR" --format "{{.Names}}" || echo "NONE")
if [ "$IDLE_CONTAINER" != "NONE" ]; then
  echo "이전 컨테이너($IDLE_CONTAINER) 중지 중..."
  docker stop $IDLE_CONTAINER
else
  echo "중지할 이전 컨테이너가 없습니다."
fi

# 배포 완료 메시지
echo ""
echo "===== Blue-Green 무중단 배포 완료 ====="
echo "배포 시간: $(date)"
echo "활성화된 컨테이너: app-$TARGET_COLOR (포트: $TARGET_PORT)"
echo "Nginx 설정이 app-$TARGET_COLOR로 업데이트되었습니다."
echo "모니터링 시스템 접속 URL:"
echo "- Prometheus: https://api.pyeondongbu.com/prometheus/"
echo "- Grafana: https://api.pyeondongbu.com/grafana/"
echo ""
echo "배포 스크립트 실행 완료"

# 불필요한 이미지 정리 (선택 사항)
echo "불필요한 이미지 정리 중..."
docker image prune -f

# 배포 결과 확인
echo "배포 결과 확인 중..."
FINAL_CHECK=$(curl -s -o /dev/null -w "%{http_code}" https://api.pyeondongbu.com/actuator/health || echo "000")
PROMETHEUS_STATUS=$(docker ps --filter "name=app-prometheus-1" --format "{{.Status}}" | grep -E 'Up|running' || echo "none")
GRAFANA_STATUS=$(docker ps --filter "name=app-grafana-1" --format "{{.Status}}" | grep -E 'Up|running' || echo "none")
TARGET_CONTAINER_STATUS=$(docker ps --filter "name=app-$TARGET_COLOR" --format "{{.Status}}" | grep -E 'Up|running' || echo "none")

# Nginx 설정 다시 확인
FINAL_NGINX_CONFIG=$(grep -o "proxy_pass http://app-[a-z]*:8080" /app/nginx.conf | head -1 || echo "NONE")

echo "애플리케이션 상태 코드: $FINAL_CHECK"
echo "Prometheus 상태: $PROMETHEUS_STATUS"
echo "Grafana 상태: $GRAFANA_STATUS"
echo "대상 컨테이너($TARGET_COLOR) 상태: $TARGET_CONTAINER_STATUS"
echo "최종 Nginx 설정: $FINAL_NGINX_CONFIG"

# 배포 결과 검증
if [[ "$FINAL_NGINX_CONFIG" == *"app-$TARGET_COLOR"* ]] && [ "$TARGET_CONTAINER_STATUS" != "none" ]; then
  echo "배포 성공: Nginx 설정과 실행 중인 컨테이너가 일치합니다."
  
  if [ "$FINAL_CHECK" == "200" ]; then
    echo "애플리케이션 서비스가 정상적으로 응답합니다."
  else
    echo "주의: 애플리케이션 상태 확인 실패 (상태 코드: $FINAL_CHECK)"
    echo "애플리케이션 로그를 확인하세요: docker logs app-$TARGET_COLOR"
  fi
else
  echo "주의: Nginx 설정과 실행 중인 컨테이너가 일치하지 않습니다."
  echo "Nginx 설정: $FINAL_NGINX_CONFIG"
  echo "실행 중인 컨테이너: app-$TARGET_COLOR ($TARGET_CONTAINER_STATUS)"
fi

if [ "$PROMETHEUS_STATUS" != "none" ] && [ "$GRAFANA_STATUS" != "none" ]; then
  echo "모니터링 시스템이 정상적으로 실행 중입니다."
else
  echo "주의: 모니터링 시스템이 완전히 실행되지 않았습니다."
fi 