# Clipboard Rule Manager 📋

An Android application that monitors your clipboard and automatically cleans URLs by applying custom regex rules. Perfect for removing tracking parameters from social media links (like Instagram's `?igsh=...` parameter).

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-purple)
![Android](https://img.shields.io/badge/android-28+-green)

## Features

✨ **Core Features:**
- 🔍 **Background Monitoring**: Runs as an AccessibilityService to detect clipboard changes in real-time
- 🎯 **Rule-Based Cleaning**: Define regex patterns to automatically clean URLs
- 📝 **Rule Management**: Add, edit, enable/disable, and delete rules through an intuitive UI
- 📊 **Change History**: Track all clipboard modifications with timestamps
- 🔐 **Anti-Loop Prevention**: Prevents infinite loops when the app writes back to clipboard
- 💾 **Persistent Storage**: Rules stored in Room Database, settings in DataStore
- 🎨 **Material Design 3 UI**: Built with Jetpack Compose

## Setup Instructions

### Prerequisites
- Android SDK 28 (Android 9) or higher
- Android Studio (latest version recommended)
- Kotlin 1.9.0+

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/clipboard-rules.git
   cd clipboard-rules
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `clipboard-rules` folder

3. **Build the project**:
   ```bash
   ./gradlew build
   ```

4. **Run on device or emulator**:
   - Connect your Android device or start an emulator
   - Click `Run` > `Run 'app'` in Android Studio
   - Or use: `./gradlew installDebug`

### First-Time Setup

After installing the app:

1. **Enable the Accessibility Service**:
   - Open the app
   - Click "Enable Service in Accessibility Settings"
   - Go to Settings > Accessibility > Accessibility Services
   - Find "Clipboard Rule Manager"
   - Enable it by toggling the switch
   - Grant the requested permissions

2. **Add Your First Rule**:
   - Go to the "Rules" tab
   - Click "Add New Rule"
   - Fill in the following:
     - **Rule Name**: A descriptive name (e.g., "Remove Instagram Tracking")
     - **Match Contains**: A string that must be in the URL (e.g., `instagram.com`)
     - **Regex Pattern**: The pattern to match and remove (e.g., `\?igsh=.*$`)
     - **Replacement**: Leave empty to remove, or enter text to replace
     - **Enabled**: Toggle to activate the rule

## Configuration Examples

### Example 1: Instagram URL Cleaner

Remove `?igsh=` tracking parameters from Instagram URLs:

```
Rule Name: Instagram URL Cleaner
Match Contains: instagram.com
Regex Pattern: \?igsh=.*$
Replacement: (leave empty)
Enabled: ✓
```

**Before**: `https://www.instagram.com/reel/DUOGuoeCnkN/?igsh=azF2ZGhkbzNueTUx`
**After**: `https://www.instagram.com/reel/DUOGuoeCnkN/`

### Example 2: YouTube Video ID Only

Extract just the video ID from YouTube URLs:

```
Rule Name: YouTube Video ID
Match Contains: youtube.com
Regex Pattern: ^.*v=([a-zA-Z0-9_-]+).*$
Replacement: https://youtu.be/$1
Enabled: ✓
```

### Example 3: Remove URL Parameters

Remove all query parameters from any URL:

```
Rule Name: Remove Query Parameters
Match Contains: (leave empty to match all)
Regex Pattern: \?.*$
Replacement: (leave empty)
Enabled: ✓
```

### Example 4: Clean Amazon Links

Remove affiliate tags from Amazon URLs:

```
Rule Name: Amazon Affiliate Cleaner
Match Contains: amazon.com
Regex Pattern: tag=[^&]*&?
Replacement: (leave empty)
Enabled: ✓
```

## Project Structure

```
clipboard-rules/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/clipboard/rulemanager/
│   │   │   │   ├── data/
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── ClipboardRuleDao.kt
│   │   │   │   │   │   └── ClipboardHistoryDao.kt
│   │   │   │   │   ├── database/
│   │   │   │   │   │   └── AppDatabase.kt
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── ClipboardRule.kt
│   │   │   │   │   │   └── ClipboardHistory.kt
│   │   │   │   │   ├── preferences/
│   │   │   │   │   │   └── ServicePreferences.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── ClipboardRepository.kt
│   │   │   │   ├── domain/
│   │   │   │   │   └── RuleEngine.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── ui/
│   │   │   │   │   │   ├── theme/
│   │   │   │   │   │   │   └── Theme.kt
│   │   │   │   │   │   └── screens/
│   │   │   │   │   │       ├── MainScreen.kt
│   │   │   │   │   │       ├── RulesScreen.kt
│   │   │   │   │   │       ├── HistoryScreen.kt
│   │   │   │   │   │       └── AddEditRuleDialog.kt
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       ├── RulesViewModel.kt
│   │   │   │   │       └── HistoryViewModel.kt
│   │   │   │   └── service/
│   │   │   │       └── ClipboardAccessibilityService.kt
│   │   │   ├── res/
│   │   │   │   ├── xml/
│   │   │   │   │   └── accessibility_service_config.xml
│   │   │   │   └── values/
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## How It Works

### Architecture

The app uses **Clean Architecture** with clear separation of concerns:

1. **Data Layer** (`data/`):
   - DAOs for database access (Room)
   - Models for entities
   - Repository for abstraction
   - Preferences for app settings

2. **Domain Layer** (`domain/`):
   - `RuleEngine`: Core business logic for applying rules

3. **Presentation Layer** (`presentation/`):
   - ViewModels: State management
   - Composables: UI components
   - Screens: Full-screen layouts

4. **Service Layer** (`service/`):
   - `ClipboardAccessibilityService`: Background monitoring

### How Rules Are Applied

1. **Clipboard Detection**: AccessibilityService detects `TYPE_CLIPBOARD_CHANGED` events
2. **Text Retrieval**: Gets current clipboard text
3. **Rule Matching**: Iterates through enabled rules:
   - Checks if text contains the `matchContains` string
   - Applies regex pattern matching
   - Replaces matched text with replacement value
4. **Writing Back**: Updates clipboard with cleaned text
5. **History Recording**: Logs the change to the database
6. **Anti-Loop Prevention**: Avoids reprocessing the same text

### Key Classes

#### `ClipboardAccessibilityService`
Runs in the background and monitors clipboard changes.

#### `RuleEngine`
Applies regex rules to text with anti-loop prevention.

#### `ClipboardRepository`
Manages database operations and clipboard access.

## Regex Patterns Guide

### Common Patterns

| Task | Pattern | Example |
|------|---------|---------|
| Remove query string | `\?.*$` | `url?param=value` → `url` |
| Remove fragment | `#.*$` | `url#section` → `url` |
| Extract domain | `^https?://([^/]+)` | `https://example.com/path` → `example.com` |
| Remove trailing slash | `/$` | `url/` → `url` |
| Remove www prefix | `^www\.` | `www.example.com` → `example.com` |
| Remove specific param | `&?param=[^&]*` | `url?a=1&param=2&b=3` → `url?a=1&b=3` |

### Regex Syntax Reference

- `^` - Start of string
- `$` - End of string
- `.` - Any character
- `*` - Zero or more
- `+` - One or more
- `?` - Zero or one
- `\?` - Literal question mark
- `[a-z]` - Character range
- `(...)` - Capture group
- `\1` - Backreference to group 1

## Troubleshooting

### Service not working after enabling?

1. **Ensure accessibility service is properly enabled**:
   - Settings > Accessibility > Accessibility Services
   - Toggle off and on again

2. **Check app permissions**:
   - No additional runtime permissions are required
   - AccessibilityService permission is declared in manifest

3. **Restart the device** (sometimes helps with accessibility service caching)

### Regex not working?

1. **Test your regex** at [regex101.com](https://regex101.com) with Kotlin flavor
2. **Remember to escape special characters**:
   - `?` must be `\?`
   - `.` must be `\.`
3. **Check your match condition** - the text must contain the "Match Contains" string first

### Changes not being applied?

1. **Verify rule is enabled** (checkbox should be checked)
2. **Check the "Match Contains" field** - it must match the actual text
3. **Test the regex pattern** - invalid patterns are silently skipped
4. **Copy fresh text** - the service monitors new clipboard changes

## Performance Considerations

- The service uses coroutines to prevent blocking the main thread
- Anti-loop detection prevents infinite clipboard modifications
- History is limited to 100 recent entries (configurable)
- Rules are applied in order; stop after first match or continue for all

## Security & Privacy

- ✅ No internet connection required
- ✅ No analytics or tracking
- ✅ All data stored locally on device
- ✅ No permissions to read/write files outside app directory
- ✅ Accessibility Service limited to clipboard monitoring

## Building for Release

1. **Update version in `app/build.gradle.kts`**:
   ```kotlin
   versionCode = 2
   versionName = "1.1.0"
   ```

2. **Generate signed APK**:
   - Build > Generate Signed APK/Bundle
   - Follow the wizard to create/select keystore

3. **Or use Gradle command**:
   ```bash
   ./gradlew assembleRelease
   ```

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) for details.

## Changelog

### Version 1.0.0
- Initial release
- Basic rule management
- Clipboard monitoring
- History tracking
- Material Design 3 UI

## Support

For bugs or feature requests, please open an issue on [GitHub Issues](https://github.com/yourusername/clipboard-rules/issues).

---

**Made with ❤️ for cleaner URLs**
# Clipboard-Rule-Manager
