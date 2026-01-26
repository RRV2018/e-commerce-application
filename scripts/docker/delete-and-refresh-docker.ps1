docker compose down -v
docker volume prune -f
docker network prune -f


docker compose up -d
