
# 📅 Explore With Me

**ExploreWithMe (EWM)** — сервис, который позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них.  
Это интерактивная афиша: здесь можно не только рассказать о фотовыставке или премьере в театре, но и собрать единомышленников, чтобы вместе пойти на мероприятие.

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-lightblue.svg)

---

## 🚀 Возможности

- Публикация и модерация событий
- Фильтрация и поиск мероприятий
- Регистрация участия пользователей
- Система лайков/дизлайков для событий
- Рейтинг мероприятий и авторов
- Подсчет просмотров с помощью статистического микросервиса
- Админка для управления пользователями, категориями и подборками событий

---

## 🛠️ Технологии и стек

| Технология           | Описание                             |
|----------------------|--------------------------------------|
| Java 21              | Язык программирования                |
| Spring Boot          | Backend-фреймворк                    |
| Spring Web           | Создание REST API                    |
| Spring Data JPA      | Работа с базой данных                |
| PostgreSQL           | Реляционная БД                       |
| Hibernate            | ORM                                   |
| MapStruct            | Маппинг DTO и Entity                 |
| Lombok               | Снижение шаблонного кода             |
| Docker               | Контейнеризация                      |
| Maven                | Сборка проекта                       |
| OpenAPI 3.0.1        | Спецификация API                     |
| JUnit, Mockito       | Модульное тестирование               |
| Testcontainers       | Интеграционные тесты с изолированной БД |

---

## ⚙️ Архитектура

Проект разделён на два микросервиса:

### 📦 `main-service`
- Управляет основной логикой: события, пользователи, лайки, рейтинги, участие.
- Содержит REST API, доступное для клиентов.
- Подключён к PostgreSQL.

### 📈 `stats-service`
- Принимает и сохраняет информацию о просмотрах.
- Позволяет получить статистику по URI и времени.
- Обособленный микросервис.

Каждый сервис имеет собственную OpenAPI-спецификацию:
- `ewm-main-service-spec.json`
- `ewm-stats-service-spec.json`

---

## 🐳 Запуск с помощью Docker

### Требования
- Docker
- Docker Compose

### Команда запуска
```bash
docker-compose up
```

Контейнеры:
- `main-service`: порт `8080`
- `stats-service`: порт `9090`
- `postgres`: порт `5432`

---

## 📂 Сборка проекта вручную (без Docker)

```bash
# Сборка и установка зависимостей
mvn clean install

# Запуск main-сервиса
cd main-service
mvn spring-boot:run

# Запуск stats-сервиса
cd ../stats-service
mvn spring-boot:run
```

---

## 🧪 Тестирование

```bash
mvn test
```

Проект использует:
- `JUnit 5` для модульных тестов
- `Testcontainers` для PostgreSQL в тестах

---

## 📖 OpenAPI документация

После запуска, доступна по адресу:
- `http://localhost:8080/swagger-ui.html` — для main-сервиса
- `http://localhost:9090/swagger-ui.html` — для stats-сервиса

---

## 📊 Примеры API

- `POST /events` — создать событие
- `GET /events` — получить список событий
- `POST /events/{id}/like` — лайкнуть событие
- `GET /ratings` — получить рейтинги авторов

Полные спецификации: см. файлы `ewm-main-service-spec.json`, `ewm-stats-service-spec.json`.

---

## 👤 Автор

Проект разработан в рамках дипломного проекта на курсе Java Developer.  
Контакты и профиль: [GitHub: GordonSergey](https://github.com/GordonSergey)