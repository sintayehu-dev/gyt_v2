# 🎬 Cinema Ticket Booking System

A cloud-native microservices platform for cinema ticket booking built with Spring Boot and modern distributed architecture patterns.

## 🚀 Architecture

**Microservices:**
- **Auth Service** - JWT-based authentication & user management
- **User Service** - User profiles, ratings & booking history
- **Movie Service** - Movie catalog with metadata (genres, directors, stars) & Cloudinary integration
- **Schedule Service** - Showtimes & seat availability management
- **Ticket Service** - Booking & payment processing

**Infrastructure:**
- **API Gateway** - Single entry point with routing & security
- **Discovery Server** - Eureka service registry
- **Config Server** - Centralized configuration management

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.5.9, Spring Cloud 2025.0.1
- **Security:** JWT, Spring Security
- **Database:** PostgreSQL (per service) + Liquibase migrations
- **Messaging:** RabbitMQ for event-driven communication
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway
- **Tracing:** Zipkin for distributed tracing
- **Resilience:** Resilience4j circuit breakers
- **Communication:** OpenFeign for inter-service calls
- **Storage:** Cloudinary for media assets
- **Containerization:** Docker & Docker Compose

## 📦 Quick Start

```bash
# Build all services
./build-all.sh  # Linux/Mac
build-all.bat   # Windows

# Start infrastructure & services
docker-compose up -d

# Access points
API Gateway: http://localhost:8080
Eureka Dashboard: http://localhost:8761
RabbitMQ Management: http://localhost:15672 (admin/admin123)
Zipkin: http://localhost:9411
```

## 🔑 Key Features

- **Event-Driven Architecture** - Async communication via RabbitMQ
- **Database per Service** - Independent PostgreSQL instances
- **Distributed Tracing** - Request tracking across services
- **Centralized Config** - Environment-specific configurations
- **Health Checks** - Service health monitoring
- **Circuit Breakers** - Fault tolerance & resilience
- **JWT Authentication** - Stateless security
- **Admin Seeding** - Auto-create admin user on startup

## 📊 Service Ports

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| Auth Service | 9191 |
| User Service | 9192 |
| Movie Service | 9194 |
| Schedule Service | 9195 |
| Ticket Service | 9196 |
| Discovery Server | 8761 |
| Config Server | 8888 |

## 🗄️ Database Ports

| Database | Port |
|----------|------|
| User DB | 5432 |
| Auth DB | 5433 |
| Movie DB | 5434 |
| Schedule DB | 5435 |
| Ticket DB | 5436 |

## 🔄 Event Flow

Services communicate via RabbitMQ events:
- `UserRegisteredEvent` → User profile creation
- `UserProfileUpdatedEvent` → Profile sync across services
- `UserDeletedEvent` → Cascade deletions
- `TicketBookedEvent` → Update seat availability
- `TicketCancelledEvent` → Release seats
- `MovieRatedEvent` → Update movie ratings

## 🏗️ Project Structure

Each service follows clean architecture:
```
service/
├── config/          # Security, RabbitMQ, etc.
├── controller/      # REST endpoints
├── dto/             # Data transfer objects
├── model/           # JPA entities
├── repository/      # Data access
├── service/         # Business logic
├── security/        # JWT filters & utils
├── messaging/       # Event publishers/consumers
└── exception/       # Error handling
```

## 🔐 Environment Variables

Copy `.env.example` to `.env` and configure:
- Database credentials
- JWT secret
- Cloudinary credentials
- RabbitMQ settings

## 📝 Notes

- All services use Liquibase for database versioning
- Feign clients handle inter-service communication
- Spring Cloud Config manages environment-specific properties
- Health checks ensure proper startup order
