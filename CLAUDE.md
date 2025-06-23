# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a tourism management system with two main components:

- **turismobackend/**: Spring Boot REST API backend
- **turismokotlin/**: Android app using Kotlin and Jetpack Compose

## Backend (Spring Boot)

### Build and Run Commands
```bash
# Navigate to backend directory
cd turismobackend

# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run

# Package the application
./mvnw clean package
```

### Database Configuration
- Uses MySQL database named `turismo_db`
- Database is auto-created on first run
- Default connection: `localhost:3306` with user `root` and no password
- Configuration in `src/main/resources/application.properties`

### API Endpoints
- Base URL: `http://localhost:8080/api/`
- Authentication: `/api/auth/login`, `/api/auth/register`
- Municipalidades: `/api/municipalidades/*`
- Emprendedores: `/api/emprendedores/*`
- Categorías: `/api/categorias/*`

## Android App (Kotlin)

### Build and Run Commands
```bash
# Navigate to Android directory
cd turismokotlin

# Build the project
./gradlew build

# Run tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest

# Install debug APK
./gradlew installDebug
```

### Architecture
- **MVVM Pattern** with ViewModels and Repository pattern
- **Jetpack Compose** for UI
- **Room Database** for local data caching
- **Retrofit** for API communication
- **Navigation Component** for screen navigation

### Key Directories
- `data/`: API services, database, repositories, and models
- `ui/`: Compose screens, components, and ViewModels
- `utils/`: Utility classes like NetworkResult

## Core Domain Model

The system manages three main entities:

1. **Usuario (User)**: Authentication and authorization with roles
2. **Municipalidad (Municipality)**: Local government entities with departments, provinces, and districts
3. **Emprendedor (Entrepreneur)**: Business entities associated with municipalities and categories
4. **Categoria (Category)**: Business categories for entrepreneurs

### Relationships
- User → Municipality (one-to-one)
- User → Entrepreneur (one-to-one)
- Municipality → Entrepreneurs (one-to-many)
- Category → Entrepreneurs (one-to-many)

## Authentication Flow

- JWT-based authentication
- Users can have ADMIN, MUNICIPALIDAD, or EMPRENDEDOR roles
- Token expiration: 24 hours
- Backend handles role-based authorization

## Development Workflow

1. **Backend Development**: Start with `./mvnw spring-boot:run` to run the API
2. **Android Development**: Use Android Studio or `./gradlew installDebug`
3. **API Testing**: Backend includes Swagger UI at `http://localhost:8080/swagger-ui.html`

## Testing

- **Backend**: Unit tests with JUnit, integration tests with Spring Boot Test
- **Android**: Unit tests with JUnit, UI tests with Compose Testing

## Database Schema

Key tables:
- `usuarios`: User accounts with authentication
- `roles`: User roles (ADMIN, MUNICIPALIDAD, EMPRENDEDOR)
- `municipalidades`: Municipality information
- `emprendedores`: Entrepreneur/business information  
- `categorias`: Business categories

## Tourism Reservation Module

### New Entities Added
- **ServicioTuristico**: Services offered by entrepreneurs (accommodation, transport, tours, etc.)
- **PlanTuristico**: Tourism plans created by municipalities combining multiple services
- **Reserva**: User reservations for tourism plans with payment tracking
- **ServicioPlan**: Junction table linking services to plans with scheduling details
- **ReservaServicio**: Customized services for specific reservations
- **Pago**: Payment tracking for reservations

### Business Logic Flow
1. **Entrepreneurs** create services they offer (ServicioTuristico)
2. **Municipalities/Admins** create tourism plans combining services (PlanTuristico)
3. **Users** make reservations for plans (Reserva)
4. **Payment processing** through multiple payment methods (Pago)

### New API Endpoints
- `/api/servicios/*` - Tourism services management
- `/api/planes/*` - Tourism plans management
- `/api/reservas/*` - Reservations management
- `/api/pagos/*` - Payments management

### Key Features
- Service categorization and filtering
- Plan capacity and availability management
- Multi-day itinerary planning
- Flexible pricing with special rates
- Reservation lifecycle management
- Payment tracking and confirmation
- Role-based access control

## API Communication

Android app communicates with backend via:
- Base URL for debug builds: `http://10.0.2.2:8080/api/` (Android emulator localhost)
- All API calls use Retrofit with Gson converter
- JWT tokens stored in Android DataStore Preferences