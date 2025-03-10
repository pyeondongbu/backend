#!/bin/bash

# 스크립트 실행 디렉토리로 이동
cd "$(dirname "$0")"

echo "===== 배포 시작 ====="
echo "$(date)"

# 최신 이미지 가져오기
echo "최신 이미지 가져오는 중..."
docker pull chisae/pyeondongbu-app:latest

# 기존 컨테이너 중지 및 제거
echo "기존 컨테이너 중지 중..."
docker-compose -f docker-compose.prod.yml down

# 새 컨테이너 시작
echo "새 컨테이너 시작 중..."
docker-compose -f docker-compose.prod.yml up -d

# 컨테이너 상태 확인
echo "컨테이너 상태 확인 중..."
docker ps

echo "===== 배포 완료 ====="
echo "$(date)"

# 불필요한 이미지 정리 (선택 사항)
echo "불필요한 이미지 정리 중..."
docker image prune -f

echo "배포 스크립트 실행 완료" 