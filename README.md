# College Timetable (Android, Kotlin + Jetpack Compose)

## Run it
1. Android Studio (Koala or newer) → File → Open → select the `CollegeTimetable` folder.
2. Let Gradle sync finish (needs internet the first time).
3. Press Run on an emulator or a phone (Android 8.0+).

## Features
- Tabs for Mon–Sun; opens on today (marked with •)
- Add / edit / delete classes: subject, room, teacher, day, start/end time, colour
- Blocks overlapping classes on the same day
- "ONGOING" tag on the class running right now
- Saved on the device automatically

## Files
- `MainActivity.kt` – all screens (tabs, class cards, add/edit dialog)
- `TimetableViewModel.kt` – state + overlap check
- `TimetableRepository.kt` – saves data as JSON in SharedPreferences
- `Model.kt`, `Theme.kt` – data class, colours, theme
