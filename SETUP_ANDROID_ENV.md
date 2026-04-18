# Android Environment Setup Guide

Complete setup instructions for compiling and testing the Clipboard Rule Manager project.

## Prerequisites

You need:
- **Java 17 or higher** (for Android Gradle Plugin 8.x)
- **Android SDK 34** (target) and SDK 28+ (minimum)
- **Android Build Tools 34.x**
- **Gradle 8.1** (via wrapper)
- Optional: **Android Emulator** or physical device (for integration tests)

## Installation Steps

### 1. Install Java 17+

**Ubuntu/Debian:**
```bash
sudo apt-get install openjdk-17-jdk-headless
java -version  # Verify installation
```

**macOS (Homebrew):**
```bash
brew install openjdk@17
/opt/homebrew/opt/openjdk@17/bin/java -version
```

**Windows:**
Download from [Oracle JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 2. Install Android SDK

**Option A: Via Android Studio (Recommended)**
1. Download [Android Studio](https://developer.android.com/studio)
2. Install and complete the setup wizard
3. SDK Manager automatically installs required packages

**Option B: Via Command Line Tools**

```bash
# Download SDK Tools (Linux/Mac example)
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip

# Setup SDK location
export ANDROID_SDK_ROOT=$HOME/.android/sdk
mkdir -p $ANDROID_SDK_ROOT/cmdline-tools/latest
mv cmdline-tools/* $ANDROID_SDK_ROOT/cmdline-tools/latest/

# Accept licenses and install packages
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --licenses
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "platforms;android-34"
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "build-tools;34.0.0"
```

**Option C: Via Package Managers**

Fedora/RHEL:
```bash
sudo dnf install android-sdk
```

### 3. Set Environment Variables

**Linux/macOS** - Add to `~/.bashrc` or `~/.zshrc`:
```bash
export ANDROID_SDK_ROOT=$HOME/.android/sdk
export ANDROID_HOME=$ANDROID_SDK_ROOT
export PATH=$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$PATH
export PATH=$ANDROID_SDK_ROOT/platform-tools:$PATH
```

**Windows** - Set environment variables:
```
ANDROID_SDK_ROOT=C:\Users\YourUsername\AppData\Local\Android\Sdk
ANDROID_HOME=%ANDROID_SDK_ROOT%
PATH=%ANDROID_SDK_ROOT%\cmdline-tools\latest\bin;%PATH%
```

### 4. Verify Installation

```bash
# Check Java
java -version  # Should show 17 or higher

# Check SDK
adb version    # Android Debug Bridge
sdkmanager --list_installed
```

Should output:
- SDK Platform Android 34
- Android SDK Build-Tools 34.0.0+

## Building the Project

### 1. Clone and Setup
```bash
git clone <repo-url>
cd clipboard-rules
./gradlew --version  # Verify Gradle 8.1
```

### 2. Build Debug APK
```bash
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### 3. Build Release APK  
```bash
# First create keystore (if not exists)
keytool -genkey -v -keystore release.keystore -alias clipboard-rules \
  -keyalg RSA -keysize 2048 -validity 10000

# Build release
./gradlew assembleRelease
```

## Running Tests

### Unit Tests (No Device Required)

**Run locally:**
```bash
# All tests
./gradlew test

# Single test class
./gradlew test -k RuleEngineTest

# Single test method
./gradlew test -k "testInstagramUrlCleaned"

# With verbose output
./gradlew test -i
```

**With coverage report:**
```bash
./gradlew test jacocoTestReport
open app/build/reports/jacoco/test/html/index.html
```

### Instrumentation Tests (Device Required)

**Setup device/emulator:**
```bash
# List connected devices
adb devices

# Start emulator (if using emulator)
emulator -avd Pixel_6_API_34
```

**Run tests:**
```bash
# All instrumentation tests
./gradlew connectedAndroidTest

# Single test class
./gradlew connectedAndroidTest -k ClipboardRepositoryIntegrationTest

# Run on specific device
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.device=emulator-5554
```

### End-to-End Manual Testing

```bash
# Build and install debug APK
./gradlew installDebug

# Launch app
adb shell am start -n com.clipboard.rulemanager/.MainActivity

# View logs
adb logcat | grep "RuleEngine\|ClipboardMonitor"
```

## Troubleshooting

### "SDK location not found"
```bash
# Create local.properties file in project root
echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties

# Or set ANDROID_HOME
export ANDROID_HOME=$HOME/.android/sdk
```

### "Java: Incompatible version"
```bash
# Verify Java version
java -version  # Should be 17+

# Set JAVA_HOME if needed
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### Gradle Daemon Issues
```bash
./gradlew --stop
rm -rf .gradle
./gradlew clean
```

### ADB Device Not Found
```bash
# List devices
adb devices

# If empty, check connection
adb kill-server
adb start-server
adb devices

# Or via emulator
emulator -list-avds  # List available emulators
emulator -avd Pixel_6_API_34  # Start emulator
```

## Continuous Integration

### GitHub Actions Configuration

Create `.github/workflows/test.yml`:

```yaml
name: Android Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'adopt'
      
      - name: Run unit tests
        run: ./gradlew test
      
      - name: Build APK
        run: ./gradlew assembleDebug
      
      - name: Upload build artifacts
        uses: actions/upload-artifact@v3
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

### GitLab CI Configuration

Create `.gitlab-ci.yml`:

```yaml
stages:
  - build
  - test

build:
  stage: build
  image: android:latest
  script:
    - ./gradlew assembleDebug

unit_tests:
  stage: test
  image: android:latest
  script:
    - ./gradlew test
```

## IDE Setup

### Android Studio

1. Open project: File > Open > select project root
2. Android Studio auto-detects SDK
3. If not: Settings > System Settings > Android SDK
4. Run tests: Right-click test class > Run 'TestName'

### VS Code

1. Install extensions:
   - Gradle for Java (Microsoft)
   - Kotlin Language
   - Android Configuration

2. Edit `.vscode/settings.json`:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/usr/lib/jvm/java-17-openjdk-amd64"
    }
  ]
}
```

3. Run tests: Command Palette > Gradle: Run Test

### IntelliJ IDEA

1. Open project
2. Configure Project Structure: SDK Location > Android SDK
3. Right-click test file > Run 'TestName'

## Performance Optimization

### Build Speed

**Local build cache:**
```bash
# Gradle properties already has caching enabled
# To use Gradle Build Cache server:
echo "org.gradle.cache.local.store.type=directory" >> gradle.properties
```

**Parallel builds:**
```bash
./gradlew test --parallel
```

**Incremental compilation:**
```bash
# Already enabled in build.gradle.kts
# Recompiles only changed code
```

### Test Execution Speed

**Run only changed tests:**
```bash
./gradlew test --no-rebuild
```

**Skip tests during build:**
```bash
./gradlew assembleDebug -x test
```

**Use test filters:**
```bash
# Run only fast tests (< 1 second)
./gradlew test -k "Not(slowTest)"
```

## Verification Checklist

- [ ] Java 17+ installed (`java -version`)
- [ ] Android SDK 34 installed (`sdkmanager --list_installed`)
- [ ] `ANDROID_HOME` environment variable set
- [ ] `local.properties` file exists with sdk.dir
- [ ] Gradle wrapper executable (`ls -la gradlew`)
- [ ] Unit tests pass (`./gradlew test`)
- [ ] Project builds (`./gradlew assembleDebug`)
- [ ] Debug APK created (`ls app/build/outputs/apk/debug/`)

## Getting Help

- [Android Developers Docs](https://developer.android.com)
- [Gradle Build Tool Guide](https://gradle.org/guides/)
- [Android Studio Setup Guide](https://developer.android.com/studio)
- Project docs: See [README.md](README.md), [TESTING.md](TESTING.md)

---

**Last Updated:** April 18, 2026

For project-specific questions, see [ARCHITECTURE.md](ARCHITECTURE.md)
