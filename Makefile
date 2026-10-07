.PHONY: up down fresh logs ps

up:
	docker compose up --build

down:
	docker compose down

fresh:
	docker compose down -v

docker compose up --build

logs:
	docker compose logs -f

ps:
	docker compose ps
