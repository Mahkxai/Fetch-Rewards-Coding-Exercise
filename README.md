# Fetch Rewards Coding Exercise - Software Engineering - Mobile
An Android application developed in Kotlin that fetches and displays items from the provided [API](https://fetch-hiring.s3.amazonaws.com/hiring.json). The application showcases efficient data-fetching using Retrofit, sorting, and grouping methodologies using modern Android development tools and practices.

## Getting Started
1. **Setup**: Clone this repository and open it using the latest version of Android Studio.
2. **Run**: Build and run the application on an emulator or a physical device supporting the current release of the Android OS.

## Requirements Addressed
- [x] Retrieve data from the provided endpoint.
- [x] Group items by "listId".
- [x] Sort items first by "listId" and then by "name".
- [x] Filter out items with blank or null "name".

## Additional Features:
- **Shimmer Loading Effect**: Integrated Facebook's shimmer effect for a polished loading experience when fetching items.
- **Sticky Headers**: Added sticky headers for group titles (listId) for an intuitive navigation.

## Build
- **Built With**: Android Studio Dolphin | 2021.3.1 Patch 1

## Compatibility
- **Minimum SDK Version**: Android 8 (API 26)
- **Target SDK Version**: Android Tiramisu (API 33)
