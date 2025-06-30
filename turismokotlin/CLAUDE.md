# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android tourism application ("Turismo Kotlin") built for the Capachica municipality. It's a comprehensive tourism management system that allows users to explore tourist services, make reservations, and manage tourism-related businesses.

## Technology Stack

- **Language**: Kotlin
- **Framework**: Android with Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt (Dagger)
- **Navigation**: Navigation Compose
- **HTTP Client**: Retrofit with Gson converter
- **Local Database**: Room
- **Image Loading**: Coil
- **Coroutines**: Kotlinx Coroutines
- **Data Storage**: DataStore Preferences
- **Min SDK**: 26, Target SDK: 34, Compile SDK: 35

## Build Commands

Since the project uses Gradle wrapper, use these commands:

```bash
# Make gradlew executable (if needed)
chmod +x ./gradlew

# Build the project
./gradlew build

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

**Note**: Ensure JAVA_HOME is set or Java is available in PATH before running Gradle commands.

## Application Architecture

### Data Layer
- **API Service** (`data/api/`): Retrofit interfaces for REST API communication
- **Local Database** (`data/local/`): Room database with DAOs and entities
- **Repositories** (`data/repository/`): Abstraction layer between ViewModels and data sources
- **Models** (`data/model/Models.kt`): Data classes with Parcelable support

### UI Layer
- **Screens** (`ui/screens/`): Organized by feature modules:
  - `auth/`: Login and registration
  - `home/`: Main dashboard
  - `municipalidad/`: Municipality management
  - `emprendedor/`: Entrepreneur management
  - `categorias/`: Category management
  - `planes/`: Tourism plans
  - `reservas/`: Reservation management
  - `servicios/`: Tourism services
  - `admin/`: Administrative panels
  - `pagos/`: Payment management
- **Components** (`ui/components/`): Reusable UI components
- **ViewModels** (`ui/viewmodel/`): State management and business logic
- **Theme** (`ui/theme/`): Material Design theme configuration

### Navigation
Navigation is handled through Compose Navigation with a centralized Routes object in `MainActivity.kt`. The app supports role-based navigation with different flows for users, entrepreneurs, and administrators.

### User Roles
- **Usuario**: Regular users who can browse and make reservations
- **Emprendedor**: Business owners who can manage their services and plans
- **Administrador**: Administrators with full system access

## Key Features

1. **Authentication**: JWT-based authentication with role management
2. **Municipality Management**: CRUD operations for municipalities
3. **Entrepreneur Management**: Business registration and management
4. **Tourism Services**: Service catalog and management
5. **Tourism Plans**: Package deals and itineraries
6. **Reservation System**: Booking and reservation management
7. **Payment Integration**: Payment processing system
8. **Admin Panel**: Administrative dashboard and user management

## API Integration

The app connects to a backend API at `http://10.0.2.2:8080/api/` (configured for Android emulator). The API specification is documented in `apis.json` (OpenAPI 3.1.0 format).

## Development Notes

- The app uses dependency injection with Hilt - look for `@HiltAndroidApp`, `@AndroidEntryPoint`, and `@Inject` annotations
- ViewModels are created through a custom `ViewModelFactory` that handles repository dependencies
- All data classes implement `Parcelable` for efficient data passing between screens
- Navigation uses a centralized route management system to avoid typos
- The app implements offline-first architecture with Room for local caching
- Session management is handled through `SessionManager` with DataStore Preferences
- **Role-based access control**: Admin features restricted to `ROLE_ADMIN` users
- **Location integration**: Entrepreneurs can set GPS coordinates and addresses
- **Cart system**: Complete shopping cart for tourism services with real-time updates
- **Database version**: Currently at version 3 (includes location fields for entrepreneurs)
- **Maps integration ready**: LocationPicker component prepared for Google Maps SDK