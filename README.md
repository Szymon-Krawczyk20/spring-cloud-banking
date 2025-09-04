# Spring Cloud Banking — Internet Banking Microservices

A production-ready internet banking backend built with **Spring Boot 3.2.4**, **Spring Cloud 2023.0.0**, and **Java 21** using microservices architecture.

## Architecture

```
                        ┌─────────────────┐
                        │   API Gateway   │ :8082
                        │   + Keycloak    │
                        └────────┬────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
   ┌──────┴──────┐        ┌──────┴──────┐       ┌──────┴────────────┐
   │    User     │        │    Fund     │       │    Utility        │
   │   Service  │        │  Transfer   │       │    Payment        │
   │   :8083    │        │   Service   │       │    Service        │
   │   MySQL    │        │   :8084     │       │    :8085          │
   └────────────┘        │   MySQL     │       │    MySQL          │
                         └──────┬──────┘       └────────┬──────────┘
                                │                       │
                         ┌──────┴───────────────────────┘
                         │
                  ┌──────┴──────┐
                  │    Core     │
                  │   Banking   │
                  │   Service   │
                  │   :8092     │
                  │   MySQL     │
                  └────────────┘
```

## Services

| Service | Port | Description |
|---|---|---|
| Config Server | 8090 | Centralized configuration management |
| Service Registry | 8081 | Netflix Eureka service discovery |
| API Gateway | 8082 | Routing + Keycloak OAuth2 security |
| User Service | 8083 | User registration and management |
| Fund Transfer Service | 8084 | Fund transfers between accounts |
| Utility Payment Service | 8085 | Utility bill payments |
| Core Banking Service | 8092 | Accounts, transactions, core operations |

## Tech Stack

- **Java 21** + **Spring Boot 3.2.4**
- **Spring Cloud 2023.0.0** — Config, Gateway, Eureka, OpenFeign
- **Keycloak** — OAuth2 authentication and authorization
- **MySQL** — Relational database for all services
- **RabbitMQ** — Asynchronous messaging
- **Zipkin** — Distributed tracing
- **Flyway** — Database migrations
- **Docker Compose** — Full local infrastructure setup

## Prerequisites

- Java 21+
- Gradle
- Docker & Docker Compose

## Getting Started

### 1. Start Infrastructure

```shell
cd docker-compose
docker-compose -f docker-compose-support-apps.yml up -d
```

### 2. Clone the Repository

```shell
git clone https://github.com/Edward-harris-java/spring-cloud-banking.git
cd spring-cloud-banking
```

### 3. Start Services (in order)

```shell
# 1. Config Server
cd internet-banking-config-server && ./gradlew bootRun

# 2. Service Registry
cd internet-banking-service-registry && ./gradlew bootRun

# 3. API Gateway
cd internet-banking-api-gateway && ./gradlew bootRun

# 4. Business Services (any order)
cd core-banking-service && ./gradlew bootRun
cd internet-banking-user-service && ./gradlew bootRun
cd internet-banking-fund-transfer-service && ./gradlew bootRun
cd internet-banking-utility-payment-service && ./gradlew bootRun
```

## Docker Containers

| Container | Port |
|---|---|
| Zipkin | 9411 |
| Keycloak | 8080 |
| MySQL | 3306 |
| Config Server | 8090 |
| Service Registry | 8081 |
| API Gateway | 8082 |
| User Service | 8083 |
| Fund Transfer Service | 8084 |
| Utility Payment Service | 8085 |
| Core Banking Service | 8092 |

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/users` | Register user |
| GET | `/api/v1/users/{id}` | Get user details |
| POST | `/api/v1/fund-transfers` | Initiate fund transfer |
| GET | `/api/v1/fund-transfers/{id}` | Get transfer details |
| POST | `/api/v1/utility-payments` | Process utility payment |
| GET | `/api/v1/accounts/{number}` | Get bank account |

## Monitoring

| Tool | URL |
|---|---|
| Eureka Dashboard | http://localhost:8081 |
| Zipkin Tracing | http://localhost:9411 |
| Keycloak Admin | http://localhost:8080 |

## License

This project is licensed under the MIT License.
