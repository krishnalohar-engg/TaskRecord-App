# TaskRecord-App
Project Overview

This application implements a practice task recording system with four 
completed steps as per requirements specification, focusing on user 
experience and technical robustness.

 Completed Features

1. Initial Setup Screen
- User-friendly introduction interface
- Bilingual instructional text implementation
- Clear call-to-action button design

 2. Environment Validation
- Visual dB meter with real-time updates
- Noise level simulation (20-60 dB range)
- Automated test results with guidance messages
- Conditional navigation based on test outcomes

3. Task Type Selection  
- Three distinct recording task options
- Visually differentiated selection cards
- Direct access to specific recording interfaces
- Additional task history access point

 4. Text Recording Module
- Live product data fetching from REST API
- Interactive recording interface with timing controls
- Recording duration enforcement (minimum 10s, maximum 20s)
- Post-recording quality verification checkpoints
- Structured local data storage implementation

 Technology Stack

- Primary Language: Kotlin
- UI Framework: Jetpack Compose (Declarative)
- Design System: Material Design 3
- State Management: Composable state hoisting
- Navigation: Compose Navigation Component
- Data Handling: Coroutine-based async operations
- External Data: Simulated REST API integration

 Architecture Pattern

The application follows a simplified MVVM-inspired architecture within 
a single Activity structure, utilizing:

- Presentation Layer: Composable functions for UI
- Business Logic: State management within ViewModel pattern
- Data Layer: Local storage simulation with model classes

 Quick Start

Prerequisites
- Android Studio (Latest Version)
- Android SDK (API 24+)
- Kotlin plugin

 Setup Instructions
1. Import project into Android Studio
2. Sync Gradle dependencies
3. Select target device (Emulator or Physical)
4. Build and run application

📊 Data Management

Recorded tasks are stored locally using a structured approach:

```kotlin
data class SavedTask(
    val taskType: String,
    val text: String,
    val audioPath: String,
    val durationSec: Int,
    val timestamp: String
)
