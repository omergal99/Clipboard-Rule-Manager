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

2. **Setup Gradle Wrapper** (if gradle-wrapper.jar is missing):
   ```bash
   # If you have Gradle installed
   gradle wrapper
   
   # Otherwise, use Android Studio to open the project
   # Android Studio will automatically download and set up Gradle
   ```

3. **Open in Android Studio** (Recommended):
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `clipboard-rules` folder
   - Let Android Studio sync and download dependencies

4. **Build the project** (from command line):
   ```bash
   chmod +x gradlew        # Make script executable
   ./gradlew build
   ```

5. **Run on device or emulator**:
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

## GitHub Release Management

The project includes a modular CI/CD pipeline that automatically:

- **📋 Tests**: Runs unit tests on every push
- **🏗️ Builds**: Creates release APKs automatically
- **🚀 Releases**: Publishes GitHub Releases with APK artifacts on version tags

### Quick Release Steps

1. **Update version** in `buildConfig.gradle.kts`
2. **Commit changes** to main
3. **Create git tag**: `git tag v1.0.X`
4. **Push to GitHub**: `git push origin main && git push origin v1.0.X`
5. **GitHub Actions** automatically creates a release with the APK

For complete release instructions and signing setup, see [RELEASE.md](RELEASE.md).

### Download APK

Users can download the latest release APK from:
- **GitHub Releases**: https://github.com/YOUR_USERNAME/clipboard-rules/releases
- **GitHub Actions Artifacts**: https://github.com/YOUR_USERNAME/clipboard-rules/actions (for development builds)

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Build & Release

### Build Modes

**Debug APK** (development only):
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK** (distribution):
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

For detailed release instructions, signing setup, and version management, see [RELEASE.md](RELEASE.md).

## Testing

The project includes comprehensive unit and integration tests to ensure code quality.

### Running Tests

**Unit Tests** (Test RuleEngine with various regex patterns):
```bash
./gradlew test
```

**Integration Tests** (Test database and repository operations):
```bash
./gradlew connectedAndroidTest
```

Or in Android Studio:
- Right-click the test file
- Select "Run Tests" or "Run with Coverage"

### Test Coverage

The test suite covers:
- **RuleEngine Tests** (~40 test cases):
  - Basic functionality (plain text, URL cleaning, query string removal)
  - Condition matching (matchContains, case insensitivity)
  - Multi-rule scenarios
  - Anti-loop prevention
  - Invalid regex handling
  - Complex URL patterns (Amazon, YouTube, Reddit, etc.)
  - Edge cases (empty strings, very long URLs, etc.)

- **Repository Integration Tests**:
  - CRUD operations for rules
  - History tracking
  - Database transactions
  - Rule filtering (enabled/disabled)
  - Multi-rule scenarios

### Key Test Scenarios

| Test | Purpose | Result |
|------|---------|--------|
| Plain text unchanged | Text without matching rules | No modification |
| Instagram URL cleaned | Remove `?igsh=...` | URL cleaned ✅ |
| No infinite loop | Same text processed twice | Prevented ✅ |
| Rule toggle | Enable/disable works | Live toggle ✅ |
| Multi-rule process | Multiple rules applied | All applied ✅ |

### Running With Coverage

To generate test coverage reports:
```bash
./gradlew test jacocoTestReport
```

Coverage reports will be generated in: `app/build/reports/jacoco/`

## Local Development on Ubuntu

### Quick Setup

1. **Install Java 17**:
   ```bash
   sudo apt-get install openjdk-17-jdk-headless gradle
   java -version  # Should show version 17
   ```

2. **Install Android SDK**:
   ```bash
   mkdir -p ~/Android/Sdk/cmdline-tools
   wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
   unzip commandlinetools-linux-11076708_latest.zip -d ~/Android/Sdk/cmdline-tools/tmp
   mv ~/Android/Sdk/cmdline-tools/tmp/cmdline-tools ~/Android/Sdk/cmdline-tools/latest
   
   export ANDROID_HOME=~/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
   
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   yes | sdkmanager --licenses
   ```

3. **Setup Project**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/clipboard-rules.git
   cd clipboard-rules
   echo "sdk.dir=$HOME/Android/Sdk" > local.properties
   chmod +x gradlew
   ```

4. **Build & Test**:
   ```bash
   ./gradlew test          # Run unit tests
   ./gradlew assembleDebug # Build debug APK
   ./gradlew assembleRelease # Build release APK (unsigned)
   ```

   **Output paths**:
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release.apk`

For complete release and signing instructions, see [RELEASE.md](RELEASE.md).

### Run on Emulator

1. **Create Virtual Device** (Android Studio):
   ```
   Tools → Device Manager → Create Device
   Select: Pixel 6, Android 14 (API 34)
   ```

   Or use command line:
   ```bash
   sdkmanager "system-images;android-34;default;x86_64"
   avdmanager create avd -n "Pixel_6_API_34" \
     -k "system-images;android-34;default;x86_64" \
     -d "Pixel 6"
   ```

2. **Start Emulator**:
   ```bash
   emulator -avd Pixel_6_API_34 &
   ```

3. **Install & Run**:
   ```bash
   adb devices  # Verify connection
   ./gradlew installDebug
   adb shell am start -n com.clipboard.rulemanager/.MainActivity
   ```

## Continuous Integration with GitHub Actions

The project is **fully GitHub Actions enabled** with a modular, production-ready CI/CD pipeline.

### Workflow Overview

| Job | Trigger | Purpose |
|-----|---------|---------|
| **test** | Every push | Runs unit tests |
| **build** | Every push (after tests) | Builds release APK |
| **release** | Version tags (v*) | Creates GitHub Release |

### Pipeline Features

✅ **Automated Testing**: Runs 20+ unit tests on every push
✅ **Release Builds**: Creates optimized APKs with ProGuard minification
✅ **Signed Releases**: Supports optional keystore signing via GitHub Secrets
✅ **Versioning**: Automatic version tagging and release notes
✅ **Artifact Management**: APK downloads from Actions tab

### Setup CI/CD

1. **Push to GitHub**:
   ```bash
   git add .
   git commit -m "Initial commit: Clipboard Rule Manager"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/clipboard-rules.git
   git push -u origin main
   ```

2. **Monitor Builds**:
   - Go to: `https://github.com/YOUR_USERNAME/clipboard-rules/actions`
   - Watch workflow execute automatically
   - Download APK from artifacts

3. **For Signed Releases** (optional):
   - Follow "GitHub Actions Secrets" section in [RELEASE.md](RELEASE.md)
   - Configure 4 secrets in GitHub repo settings
   - Version tags will automatically trigger signed releases

### Download Build Artifacts

**For Development** (Debug builds):
- Actions tab → Latest commit workflow → debug-apk artifact

**For Distribution** (Release builds):
- Actions tab → Latest commit workflow → release-apk artifact
- OR GitHub Releases tab → Download APK from latest version

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
