# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.cram.backend.ClassName"
```

### Database Operations
- H2 Console: http://localhost:8080/h2-console
- Connection URL: `jdbc:h2:~/cram`
- Username: `sa`, Password: (empty)

### API Documentation
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- API Docs: http://localhost:8080/v3/api-docs

## Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.4.5 with Java 17
- **Database**: H2 (in-memory/file-based)
- **Security**: Spring Security with JWT authentication
- **Messaging**: RabbitMQ for chat message processing
- **Cache**: Redis for session management
- **API Documentation**: Swagger/OpenAPI 3

### Core Architecture Pattern
This is a **domain-driven monolith** with clear separation of concerns:
- **Controller Layer**: REST endpoints for each domain
- **Service Layer**: Business logic with interface-implementation pattern
- **Repository Layer**: JPA repositories for data access
- **Entity Layer**: JPA entities with domain-specific relationships

### Key Domains
1. **Study Groups**: Core entity for organizing study sessions
2. **Group Members**: User membership and role management
3. **Chat System**: Real-time messaging with RabbitMQ
4. **Meeting Rooms**: Virtual meeting spaces with chat
5. **Alerts**: Notification system for users
6. **Authentication**: OAuth2 (Google, Naver) + JWT

### Message Flow Architecture
- **Chat Messages**: REST API → RabbitMQ → Database persistence
- **Real-time Updates**: WebSocket connections for live chat
- **Queue Processing**: `BackendMqSubscriber` handles async message processing
- **Exchanges**: `chat.direct` exchange with routing keys for different message types

### Security Architecture
- **Authentication**: JWT tokens with custom claims (userId, groupId, roomId)
- **Authorization**: Role-based access control
- **CORS**: Configured for cross-origin requests
- **OAuth2**: Google and Naver integration (currently disabled in config)

### Data Relationships
- **Users** belong to multiple **StudyGroups** through **GroupMembers**
- **StudyGroups** have **Tags** for categorization
- **Chat Messages** are linked to either **StudyGroups** or **MeetingRooms**
- **Alerts** are categorized as General or Group-specific

## Development Guidelines

### Package Structure
- Each domain follows the pattern: `controller/dto/entity/repository/service`
- Shared DTOs and entities are in the root package
- Configuration classes are in `config/` package

### Testing Approach
- JUnit 5 with Spring Boot Test
- Test classes should follow naming: `{ClassName}Test`
- Use `@SpringBootTest` for integration tests

### Database Initialization
- Schema auto-created via `hibernate.ddl-auto: update`
- Sample data loaded from `src/main/resources/data.sql`
- Always backup H2 database file before major changes

### JWT Token Types
- **Access Token**: Standard user authentication
- **Group Chat Ticket**: Chat-specific authorization with groupId
- **Meeting Room Ticket**: Room-specific authorization with roomId

### RabbitMQ Queue Configuration
- `group.chat.db.queue`: Group chat message persistence
- `meeting.room.chat.db.queue`: Meeting room chat persistence
- `meeting.room.active.queue`: Meeting room activation events
- `meeting.room.inactive.queue`: Meeting room deactivation events

### Common Patterns
- All services use interface-implementation pattern
- DTOs separate request/response models from entities
- Exception handling through custom runtime exceptions
- Lombok annotations for reducing boilerplate code