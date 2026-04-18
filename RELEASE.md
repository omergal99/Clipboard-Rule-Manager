# Release Guide - Clipboard Rule Manager

Complete guide for building, signing, and releasing the Clipboard Rule Manager app.

## Table of Contents
1. [Local Development Build](#local-development-build)
2. [Local Release Build](#local-release-build)
3. [Signing Configuration](#signing-configuration)
4. [GitHub Release Management](#github-release-management)
5. [Versioning](#versioning)

---

## Local Development Build

### Quick Debug Build

Build and test locally:

```bash
# Set environment
export ANDROID_HOME=/home/omer/Android/Sdk
cd /path/to/clipboard-rules

# Run tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Install and Run on Emulator

```bash
# Verify emulator is running
adb devices

# Install debug APK
./gradlew installDebug

# Launch app
adb shell am start -n com.clipboard.rulemanager/.MainActivity
```

---

## Local Release Build

### Build Release APK (Unsigned)

For a quick release build **without** signing:

```bash
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

**Note**: Unsigned APKs can be installed on emulators or devices for testing only. They cannot be uploaded to Google Play Store.

### Build Release APK (Signed)

For a **signed release APK** that can be distributed:

#### Step 1: Create a Keystore (First Time Only)

```bash
# Create signing directory
mkdir -p ~/.android

# Generate a new keystore (RSA, 10000-day validity)
keytool -genkey -v -keystore ~/.android/release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias clipboard-release \
  -storepass your-store-password \
  -keypass your-key-password \
  -dname "CN=Your Name,O=Your Organization,C=US"
```

You'll be prompted for:
- **Store Password**: Password to the keystore file
- **Key Password**: Password for the specific key
- **CN** (Common Name): Your name or app name
- **O** (Organization): Your organization
- **C** (Country): Country code (e.g., US)

**Keep these passwords safe!** You'll need them to build signed releases.

#### Step 2: Build Signed Release APK

Set environment variables and build:

```bash
export SIGNING_STORE_PASSWORD="your-store-password"
export SIGNING_KEY_ALIAS="clipboard-release"
export SIGNING_KEY_PASSWORD="your-key-password"

./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk` (signed and ready to distribute)

#### Verify Signature (Optional)

```bash
jarsigner -verify app/build/outputs/apk/release/app-release.apk
```

---

## Signing Configuration

### How Signing Works

The build system looks for signing credentials in this order:

1. **Environment Variables** (for CI/CD):
   - `SIGNING_STORE_PASSWORD` - Keystore password
   - `SIGNING_KEY_ALIAS` - Key identifier
   - `SIGNING_KEY_PASSWORD` - Key password
   - `SIGNING_KEYSTORE_PATH` - Path to keystore file (optional)

2. **Default File** (for local builds):
   - `~/.android/release.keystore` (standard location)

3. **Fallback**:
   - If no signing configuration is found, release APK is built **unsigned**

### For Local Development

Create your keystore once (see Step 1 above), then use environment variables or create a local config file:

**Option A: Use Environment Variables**

```bash
# Add to ~/.bashrc or ~/.zshrc for persistence
export SIGNING_STORE_PASSWORD="your-password"
export SIGNING_KEY_ALIAS="clipboard-release"
export SIGNING_KEY_PASSWORD="your-key-password"
```

**Option B: Create a Gradle Properties File**

Create `.gradle/gradle.properties` in your home directory:

```properties
SIGNING_STORE_PASSWORD=your-password
SIGNING_KEY_ALIAS=clipboard-release
SIGNING_KEY_PASSWORD=your-key-password
```

---

## GitHub Release Management

### Automatic Release on Git Tags

Every version tag you push will trigger:
1. ✅ Unit tests run
2. ✅ Release APK builds
3. ✅ GitHub Release created with APK attached

### Create a New Release

#### Step 1: Update Version Number

Edit `app/build.gradle.kts` - update the `AppVersions` object near the top:

```kotlin
object AppVersions {
    const val versionCode = 2          // Increment this
    const val versionName = "1.1.0"    // Update this
    // ... rest of config
}
```

#### Step 2: Commit Changes

```bash
git add app/build.gradle.kts
git commit -m "Release v1.1.0: Feature description"
```

#### Step 3: Create Git Tag and Push

```bash
# Create annotated tag (recommended)
git tag -a v1.1.0 -m "Version 1.1.0 - Feature description"

# Push to GitHub
git push origin main
git push origin v1.1.0
```

#### Step 4: Monitor Release

- Go to: `https://github.com/YOUR_USERNAME/clipboard-rules/actions`
- Watch workflow execute automatically
- Download APK from the created GitHub Release

---

## Versioning Strategy

### Version Format

Follow [Semantic Versioning](https://semver.org/):

```
MAJOR.MINOR.PATCH
1.0.0
├─ MAJOR: Major feature or incompatible changes
├─ MINOR: New features (backward compatible)
└─ PATCH: Bug fixes only
```

### Version Code vs Version Name

- **versionCode**: Internal integer (must increase with each build)
- **versionName**: User-visible version string

### Update Workflow

1. Increment `versionCode` by 1 (always)
2. Update `versionName` following semantic versioning
3. Commit to `main` branch
4. Create Git tag: `versionName` prefixed with `v` (e.g., `v1.0.1`)
5. Push tag to GitHub
6. GitHub Actions automatically triggers release workflow

### Example Release Sequence

```bash
# Feature added → Minor version
# Edit: app/build.gradle.kts AppVersions object:
  versionCode: 1 → 2
  versionName: "1.0.0" → "1.1.0"
git tag v1.1.0

# Regular patch
# Edit: app/build.gradle.kts AppVersions object:
  versionCode: 2 → 3
  versionName: "1.1.0" → "1.1.1"
git tag v1.1.1

# Major rewrite
# Edit: app/build.gradle.kts AppVersions object:
  versionCode: 3 → 4
  versionName: "1.1.1" → "2.0.0"
git tag v2.0.0
```

---

## GitHub Actions Secrets for Signed Releases

To enable **signed releases** in GitHub Actions, configure these secrets:

### Setup Signing Secrets

1. **Create local keystore** (see "Local Release Build" above)
2. **Encode keystore as Base64**:

   ```bash
   base64 -w 0 ~/.android/release.keystore > keystore-base64.txt
   cat keystore-base64.txt
   ```

3. **Go to GitHub**:
   - Repository Settings → Secrets and variables → Actions
   - Add these secrets:
     - `SIGNING_KEYSTORE_BASE64` - Output from step 2
     - `SIGNING_STORE_PASSWORD` - Your keystore password
     - `SIGNING_KEY_ALIAS` - Your key alias (e.g., `clipboard-release`)
     - `SIGNING_KEY_PASSWORD` - Your key password

4. **GitHub Actions will**:
   - Decode the keystore
   - Use it to sign releases
   - Delete temporary keystore file (for security)

### Security Notes

- Secrets are **encrypted** and only available during workflow execution
- Never commit `release.keystore` to Git
- Add `**/*.jks` and `**/*.keystore` to `.gitignore` (already done)
- Rotate passwords annually for security

---

## File Locations Summary

```
clipboard-rules/
├── app/build.gradle.kts             # ← Version number (AppVersions object) - EDIT THIS
├── app/
│   ├── proguard-rules.pro           # ← ProGuard minification rules (optimizes release APK)
│   └── build/outputs/apk/
│       ├── debug/
│       │   └── app-debug.apk        # ← Debug builds (development only)
│       └── release/
│           └── app-release.apk      # ← Release builds (what you distribute) ⭐
├── .github/workflows/
│   └── android.yml                  # ← CI/CD pipeline (modular: test, build, release jobs)
├── RELEASE.md                       # ← You are here
└── ~/.android/release.keystore      # ← Your signing keystore (not in repo, local only)
```

**Key Files to Remember:**
- 📝 **Version Control**: `app/build.gradle.kts` (see `AppVersions` object)
- 🔨 **Build Configuration**: `app/build.gradle.kts` (see `signingConfigs`)
- 🚀 **CI/CD Pipeline**: `.github/workflows/android.yml` (modular jobs)
- 📦 **Output APK**: `app/build/outputs/apk/release/app-release.apk`
- 🔑 **Signing Key**: `~/.android/release.keystore` (local machine, git-ignored)

---

## Troubleshooting

### "Keystore not found" Error

```
Error: Keystore file does not exist:
/home/omer/.android/release.keystore
```

**Solution**: Create the keystore using Step 1 in "Local Release Build" section.

### "Invalid keystore format" Error

**Solution**: Ensure the keystore was created correctly:

```bash
keytool -list -v -keystore ~/.android/release.keystore \
  -storepass your-password
```

### "Keystore alias not found" Error

**Solution**: Check your key alias matches what you specified when creating the keystore:

```bash
keytool -list -keystore ~/.android/release.keystore
```

### Build Success but APK Not Signed

**Solution**: Verify environment variables are set:

```bash
echo $SIGNING_STORE_PASSWORD
echo $SIGNING_KEY_ALIAS
echo $SIGNING_KEY_PASSWORD
```

---

## Quick Reference Commands

```bash
# Local dev: Compile and test
./gradlew assembleDebug

# Local release: Build unsigned release APK
./gradlew assembleRelease

# Local release: Build signed release APK
SIGNING_STORE_PASSWORD=pass \
SIGNING_KEY_ALIAS=alias \
SIGNING_KEY_PASSWORD=keypass \
./gradlew assembleRelease

# Clean everything
./gradlew clean

# Run tests
./gradlew test

# View available tasks
./gradlew tasks
```

---

## Support

For issues or questions:
1. Check `.github/workflows/android.yml` for CI/CD configuration
2. Review `app/build.gradle.kts` for build settings
3. Check `buildConfig.gradle.kts` for version information
4. Open GitHub Issues for bugs or feature requests
