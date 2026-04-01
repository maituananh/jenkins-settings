set -a 
source .env
set +a

MSYS_NO_PATHCONV=1 docker run --rm \
  -v jenkins_${JENKINS_VOLUME_BACKUPS_PATH}:/data \
  -v "$(pwd):/jenkins" \
  busybox:1.37.0 \
  sh -c "tar xzf /jenkins/jenkins-backup.tar.gz -C /data --strip-components=1"

# docker compose -f docker-compose.backups.yml build --no-cache --progress=plain 2>&1
# docker compose -f docker-compose.backups.yml up -d