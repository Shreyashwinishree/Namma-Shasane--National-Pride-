# Namma Shasane

Namma Shasane is a native Android app in Kotlin for discovering, decoding, and preserving Karnataka stone inscriptions. The project now contains a complete Jetpack Compose MVP that can be opened directly in Android Studio.

## Implemented MVP

- Guest authentication entry screen.
- Interactive discovery screen with a map-style Karnataka view, search, dynasty filters, and 5 seeded inscriptions.
- Story view for each inscription with era, script, translation, condition, and preservation context.
- AI decode screen with image picker and a clean demo decoder service boundary for future ML Kit and Gemini integration.
- Damage reporting flow that queues preservation alerts locally.
- Heritage trail screen with generated routes.
- Offline status screen showing Room database records and queued reports.
- Room database, repository layer, Android ViewModel, and Compose Material 3 UI.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room Database
- Coroutines and StateFlow
- AndroidX Activity and Lifecycle

## Open And Run

1. Open this folder in Android Studio: `C:\Users\Admin\OneDrive\Desktop\prd`
2. Let Android Studio sync Gradle dependencies.
3. Select an emulator or connected Android device.
4. Run the `app` configuration.

The current app does not require Firebase, Google Maps, Gemini, or ML Kit keys to run. Those production integrations should be added behind `HeritageRepository` so the existing screens continue to work offline.

## Production Integration Notes

- Replace the demo `decodeInscriptionImage` implementation with an ML Kit OCR pass followed by a Gemini prompt that includes script, location, dynasty, and user notes.
- Add Firebase Authentication providers for email, phone, Google, and guest upgrade.
- Sync `damage_reports` to Firestore and upload field photos to Firebase Storage.
- Replace the lightweight Compose map with Google Maps SDK when an API key is available.
- Add CameraX capture for field tagging and GPS metadata.

## Developer

Shreyashwini G, USN: 1JB22CD050  
Institution: SJBIT
