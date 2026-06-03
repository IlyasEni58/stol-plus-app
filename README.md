# Автор 
### Еникеев Ильяс
Telegram: @IlyasEn1
# Stol Plus App

Backend для мебельного магазина/сервиса. Управление пользователями, каталогом товаров, корзиной, избранным и заказами.

## Технологии

- Java 17
- Spring Boot 3
- Spring Security (JWT-аутентификация)
- Spring Data JPA
- PostgreSQL 15
- Flyway (миграции)
- Docker / Docker Compose
- Maven
- GitHub Actions (CI/CD)
- Swagger / OpenAPI 3

---

##  Как запустить проект

### Через Docker (рекомендуется)

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/IlyasEni58/stol-plus-app.git
   cd stol-plus-app
   
2. Запустите контейнеры
   ```bash
   docker-compose up -d

3. Приложение будет доступно по адресу: http://localhost:8090
4. Документация API (Swagger): http://localhost:8090/swagger-ui/index.html

##  Локальный запуск (без Docker)
1. Установите PostgreSQL 15

2. Создайте базу данных 
   ```sql
   CREATE DATABASE stol_db;
3. В src/main/resources/application.yml укажите свои логин/пароль
4. Запустите 
    ```bash
   ./mvnw spring-boot:run
(или через зелёную стрелку в IDEA)

##  Тестовый пользователь

После запуска миграции Flyway создадут администратора:

Логин: admin@example.com

Пароль: admin

##  Примеры API-запросов
Регистрация нового пользователя
   ```bash
   curl -X POST http://localhost:8090/api/auth/register \
   -H "Content-Type: application/json" \
   -d '{"email":"user@example.com","password":"123456","name":"Иван"}'
 ```
Авторизация (получение JWT-токена)
 ```bash
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin"}'
 ``` 
Запрос с токеном (например, избранное)
 ```bash
 curl -H "Authorization: Bearer <ваш_токен>" http://localhost:8090/api/favorites
 ```
##  Структура проекта
controller — REST API

service — бизнес-логика

repository — работа с БД (Spring Data JPA)

dto — объекты для ввода/вывода

mapper — конвертация между Entity и DTO (MapStruct)

model — сущности JPA

config — настройки безопасности, CORS, OpenAPI

exception — глобальная обработка ошибок
