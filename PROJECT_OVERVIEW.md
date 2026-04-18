# Project Overview - Clipboard Rule Manager

## 📱 What You Have

A complete, production-ready Android application that intelligently monitors your clipboard and automatically cleans URLs by applying custom regex rules.

**Key Benefit**: Copy messy Instagram links with tracking codes, and they'll automatically be cleaned before you paste them anywhere else!

## 📁 Project Structure

```
clipboard-rules/
├── 📄 README.md                    # Full documentation
├── 📄 QUICKSTART.md               # Get started in 5 minutes
├── 📄 RULES_GUIDE.md              # Preset rules and examples
├── 📄 DEVELOPMENT.md              # Developer guide
├── 📄 CHANGELOG.md                # Version history
├── 📄 LICENSE                     # MIT License
├── 📄 .gitignore                  # Git ignore rules
├── 📄 gradle.properties           # Gradle configuration
├── 📄 local.properties.example    # SDK path template
├── 📄 default-rules.json          # Preset rules in JSON
│
├── 🔵 build.gradle.kts            # Root build config
├── 🔵 settings.gradle.kts         # Project settings
│
└── 📦 app/
    ├── 🔵 build.gradle.kts        # App build configuration
    ├── 📄 proguard-rules.pro      # ProGuard rules
    │
    └── src/main/
        ├── 📄 AndroidManifest.xml # App manifest
        │
        ├── kotlin/...
        │   └── com/clipboard/rulemanager/
        │       ├── 📁 data/
        │       │   ├── dao/
        │       │   │   ├── ClipboardRuleDao.kt
        │       │   │   └── ClipboardHistoryDao.kt
        │       │   ├── database/
        │       │   │   └── AppDatabase.kt
        │       │   ├── model/
        │       │   │   ├── ClipboardRule.kt
        │       │   │   └── ClipboardHistory.kt
        │       │   ├── preferences/
        │       │   │   └── ServicePreferences.kt
        │       │   └── repository/
        │       │       └── ClipboardRepository.kt
        │       ├── 📁 domain/
        │       │   └── RuleEngine.kt
        │       ├── 📁 presentation/
        │       │   ├── MainActivity.kt
        │       │   ├── ui/
        │       │   │   ├── theme/
        │       │   │   │   └── Theme.kt
        │       │   │   └── screens/
        │       │   │       ├── MainScreen.kt
        │       │   │       ├── RulesScreen.kt
        │       │   │       ├── HistoryScreen.kt
        │       │   │       └── AddEditRuleDialog.kt
        │       │   └── viewmodel/
        │       │       ├── RulesViewModel.kt
        │       │       └── HistoryViewModel.kt
        │       └── 📁 service/
        │           └── ClipboardAccessibilityService.kt
        │
        └── res/
            ├── xml/
            │   └── accessibility_service_config.xml
            └── values/
                ├── strings.xml
                └── themes.xml
```

## 🎯 How It Works

```
1. User copies Instagram URL to clipboard
   ↓
2. AccessibilityService detects clipboard change
   ↓
3. RuleEngine checks against enabled rules
   ↓
4. Finds matching rule: "Match: instagram.com", "Regex: \?igsh=.*$"
   ↓
5. Removes the tracking parameter
   ↓
6. Writes cleaned URL back to clipboard
   ↓
7. Records the change in History
   ↓
8. User pastes clean URL anywhere ✨
```

## 🚀 Quick Start

### 1. Build
```bash
cd /home/omer/DEV/ai-start/clipboard-rules
./gradlew assembleDebug
```

### 2. Install
```bash
./gradlew installDebug
```

### 3. Enable Service
- Open app
- Click "Enable Service in Accessibility Settings"
- Go to Settings > Accessibility > Accessibility Services
- Enable "Clipboard Rule Manager"

### 4. Add Your First Rule
- Rules tab > Add New Rule
- Name: "Instagram Cleaner"
- Match Contains: "instagram.com"
- Regex: `\?igsh=.*$`
- Replacement: (empty)
- Save

### 5. Test
- Copy Instagram URL to clipboard
- Check History to see it was cleaned!

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| [README.md](README.md) | Complete feature documentation, setup, troubleshooting |
| [QUICKSTART.md](QUICKSTART.md) | Get started in 5 minutes with step-by-step guide |
| [RULES_GUIDE.md](RULES_GUIDE.md) | 30+ preset rules for common websites and use cases |
| [DEVELOPMENT.md](DEVELOPMENT.md) | Architecture, code structure, and development workflow |
| [CHANGELOG.md](CHANGELOG.md) | Version history and planned features |

## 🏗️ Architecture

**Clean Architecture with 3 Layers**:

### Data Layer
- Room Database for persistence
- DAOs for database access
- Repository pattern for abstraction
- DataStore for settings

### Domain Layer
- RuleEngine: Core processing logic
- Regex matching and replacement
- Anti-loop prevention

### Presentation Layer
- Material Design 3 UI (Jetpack Compose)
- ViewModels for state management
- 4 screens: Main, Rules, History, and AddEditDialog

### Service Layer
- AccessibilityService: Background monitoring
- Non-blocking coroutines
- Error handling

## 🔧 Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Kotlin | 1.9.0 | Modern Android development |
| Android SDK | 34 | Target SDK |
| Jetpack Compose | 1.5.4 | Modern UI framework |
| Room | 2.6.1 | Local database |
| DataStore | 1.0.0 | Settings storage |
| Coroutines | 1.7.3 | Async operations |
| Material Design 3 | 1.1.2 | UI components |

## ✨ Key Features

✅ **Background Monitoring** - Runs as AccessibilityService
✅ **Rule-Based Cleaning** - Regex patterns with conditions
✅ **Easy Rule Management** - Add, edit, enable/disable rules
✅ **History Tracking** - See all modifications
✅ **Anti-Loop Prevention** - Prevents infinite updates
✅ **Material Design 3** - Modern, clean interface
✅ **Persistent Storage** - Rules saved locally
✅ **Zero Tracking** - No analytics, no internet

## 📦 What's Included

✅ Complete source code (well-organized, production-ready)
✅ Android Manifest with proper permissions
✅ Room Database setup with DAOs
✅ Jetpack Compose UI components
✅ Material Design 3 theme
✅ Gradle build configuration
✅ ProGuard rules
✅ Comprehensive documentation
✅ Preset rules (30+ examples)
✅ MIT License

## 🎮 Custom Rules Example

### Instagram Cleaner
```
Name: Instagram URL Cleaner
Match Contains: instagram.com
Regex: \?igsh=.*$
Replacement: (empty)
```
**Result**: 
- Before: `https://www.instagram.com/reel/DUOGuoeCnkN/?igsh=azF2ZGhkbzNueTUx`
- After: `https://www.instagram.com/reel/DUOGuoeCnkN/`

### YouTube Cleaner
```
Name: YouTube Clean
Match Contains: youtube.com
Regex: \?.*$
Replacement: (empty)
```
**Result**:
- Before: `https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10&list=xyz`
- After: `https://www.youtube.com/watch`

## 🔐 Security & Privacy

✅ No internet connection
✅ No cloud storage
✅ No analytics tracking
✅ All data local only
✅ No ads
✅ Open source (MIT License)
✅ Accessibility Service limited to clipboard monitoring only

## 📋 What's Next?

1. **Build** - `./gradlew assembleDebug`
2. **Install** - `./gradlew installDebug`
3. **Enable Service** - Follow in-app instructions
4. **Add Rules** - Choose from 30+ presets or create custom ones
5. **Enjoy** - Automatic URL cleaning!

## 🐛 Troubleshooting

**Service not working?**
- Check Settings > Accessibility > is "Clipboard Rule Manager" enabled?
- Restart your device
- Close and reopen the app

**Rule not applying?**
- Is the rule enabled (checkbox checked)?
- Does the URL contain the "Match Contains" text?
- Is the regex pattern valid?

**More help?**
- See README.md for detailed troubleshooting
- Check DEVELOPMENT.md for technical details
- Review RULES_GUIDE.md for rule examples

## 📞 Support

- 📖 **Documentation**: See README.md
- 🎓 **Quick Start**: See QUICKSTART.md
- 📚 **Rules**: See RULES_GUIDE.md
- 👨‍💻 **Development**: See DEVELOPMENT.md

---

**🎉 Enjoy automatic URL cleaning!**

Your clipboard just got smarter.
