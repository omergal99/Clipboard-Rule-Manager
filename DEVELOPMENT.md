# Development Guide - Clipboard Rule Manager

This document is for developers who want to understand, modify, or extend the Clipboard Rule Manager application.

## Architecture Overview

### Clean Architecture Pattern

The application follows Clean Architecture with three main layers:

```
┌─────────────────────────────────────┐
│   PRESENTATION LAYER                │
│ (UI, ViewModels, Composables)       │
├─────────────────────────────────────┤
│   DOMAIN LAYER                      │
│ (Business Logic, Use Cases)         │
├─────────────────────────────────────┤
│   DATA LAYER                        │
│ (Repositories, DAOs, Models)        │
└─────────────────────────────────────┘
```

### Data Flow

```
AccessibilityService (detects clipboard change)
         ↓
ClipboardRepository.getClipboardText()
         ↓
RuleEngine.applyRules()
         ↓
ClipboardRepository.setClipboardText()
         ↓
ClipboardRepository.insertHistory()
         ↓
Database
```

## Project Dependencies

### Core Android
- `androidx.core:core-ktx` - Kotlin extensions for core Android
- `androidx.appcompat:appcompat` - Backward compatibility
- `androidx.lifecycle` - ViewModels, LiveData, Coroutines

### UI
- `androidx.compose.*` - Jetpack Compose framework
- `androidx.activity:activity-compose` - Compose integration

### Data Persistence
- `androidx.room:room-*` - SQL database
- `androidx.datastore:datastore-preferences` - Key-value storage

### Concurrency
- `kotlinx.coroutines` - Asynchronous programming

## Key Classes Explained

### 1. Data Models

#### `ClipboardRule.kt`
```kotlin
@Entity(tableName = "clipboard_rules")
data class ClipboardRule(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val matchContains: String,  // Condition to apply rule
    val regex: String,           // Pattern to match
    val replacement: String,     // Replacement text
    val enabled: Boolean,        // Is this rule active?
    val createdAt: Long,
    val updatedAt: Long
)
```

#### `ClipboardHistory.kt`
Records all clipboard modifications for auditing:
```kotlin
@Entity(tableName = "clipboard_history")
data class ClipboardHistory(
    val originalText: String,
    val modifiedText: String,
    val ruleApplied: String?,   // Which rule was applied
    val timestamp: Long
)
```

### 2. Database Layer

#### Room Database Integration
- `AppDatabase` - Main database class with DAOs
- `ClipboardRuleDao` - CRUD operations for rules
- `ClipboardHistoryDao` - Logging operations

**Key Features**:
- Singleton pattern for database instance
- Coroutine support via Flow<>
- Migration support (version handling)

#### Repository Pattern
`ClipboardRepository` abstracts database and clipboard operations:
```kotlin
class ClipboardRepository {
    fun getAllRules(): Flow<List<ClipboardRule>>
    suspend fun insertRule(rule: ClipboardRule)
    fun getClipboardText(): String?
    fun setClipboardText(text: String)
}
```

### 3. Business Logic

#### `RuleEngine.kt`
Core processing engine:
- Applies rules in sequence
- Anti-loop prevention using text cache
- Handles regex compilation errors gracefully

**Algorithm**:
1. Check if text matches "matchContains" condition
2. Apply regex pattern
3. Replace matched text with replacement string
4. Return result with metadata

**Anti-Loop Prevention**:
- Caches last processed input/output
- Skips processing if output becomes input again
- Prevents infinite clipboard updates

### 4. Background Service

#### `ClipboardAccessibilityService.kt`
Runs constantly in background:
- Listens for `TYPE_CLIPBOARD_CHANGED` events
- Processes clipboard text through RuleEngine
- Updates clipboard with cleaned text
- Records history

**Key Implementation Details**:
- Uses Coroutines for non-blocking operations
- `isProcessing` flag prevents recursive calls
- Catches exceptions to avoid crashes
- Runs on Dispatchers.IO thread

### 5. UI Layer

#### ViewModels
Manage state and communicate with repository:
- `RulesViewModel` - Rule list management
- `HistoryViewModel` - History display

#### Screens (Compose)
- `MainScreen` - Tab navigation
- `RulesScreen` - List and manage rules
- `HistoryScreen` - View clipboard modifications
- `AddEditRuleDialog` - Create/edit rules

#### UI State Management
Uses Compose State and Coroutines Flow:
```kotlin
val rules: StateFlow<List<ClipboardRule>> = 
    repository.getAllRules().stateIn(...)
```

## Development Workflow

### Setting Up Development Environment

1. **Install Dependencies**:
   ```bash
   ./gradlew dependencies
   ```

2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on Device**:
   ```bash
   ./gradlew installDebug
   ```

4. **Run Tests**:
   ```bash
   ./gradlew test
   ```

### Code Organization

**Kotlin Package Structure**:
```
com.clipboard.rulemanager
├── data
│   ├── dao               # Database access objects
│   ├── database          # Room database definition
│   ├── model             # Data entities
│   ├── preferences       # Settings storage
│   └── repository        # Data abstraction layer
├── domain
│   └── ...               # Business logic (Rule Engine)
├── presentation
│   ├── ui
│   │   ├── screens       # Compose screens
│   │   └── theme         # UI theme
│   ├── viewmodel         # State management
│   └── MainActivity.kt
└── service
    └── ...               # Accessibility Service
```

## Adding New Features

### Example: Add a Settings Screen

1. **Create new screen**:
   ```kotlin
   @Composable
   fun SettingsScreen() {
       // Your UI here
   }
   ```

2. **Update MainScreen**:
   ```kotlin
   val tabs = listOf("Rules", "History", "Settings")
   // Add case in HorizontalPager
   ```

3. **Create SettingsViewModel**:
   ```kotlin
   class SettingsViewModel(repository: ClipboardRepository) 
   ```

### Example: Add Rule Validation

1. **Extend RuleEngine**:
   ```kotlin
   fun validateRule(rule: ClipboardRule): ValidationResult {
       if (rule.name.isEmpty()) return ValidationResult.InvalidName
       // ... more validation
   }
   ```

2. **Use in dialog**:
   ```kotlin
   val validation = ruleEngine.validateRule(rule)
   if (validation.isValid) {
       saveRule(rule)
   }
   ```

### Example: Add Rule Export/Import

1. **Create JsonSerializer** (using kotlinx.serialization):
   ```kotlin
   fun exportRules(rules: List<ClipboardRule>): String {
       return Json.encodeToString(rules)
   }
   ```

2. **Add UI button**:
   ```kotlin
   Button(onClick = { exportRules() }) {
       Text("Export Rules")
   }
   ```

## Testing

### Unit Tests

Test the RuleEngine:
```kotlin
@Test
fun testInstagramRuleRemovesIgsh() {
    val rule = ClipboardRule(
        name = "Instagram",
        matchContains = "instagram.com",
        regex = "\\?igsh=.*$",
        replacement = ""
    )
    
    val engine = RuleEngine()
    val input = "https://instagram.com/reel/123/?igsh=abc"
    val result = engine.applyRules(input, listOf(rule))
    
    assertEquals("https://instagram.com/reel/123/", result.modifiedText)
    assertTrue(result.modified)
}
```

### Integration Tests

Test database operations:
```kotlin
@Test
fun testRuleInsertion() = runBlocking {
    val rule = ClipboardRule(name = "Test", ...)
    ruleDao.insertRule(rule)
    
    val retrieved = ruleDao.getRuleById(1)
    assertEquals(rule.name, retrieved?.name)
}
```

### UI Tests

Test Compose components with Espresso/Compose testing libraries.

## Performance Optimization

### Coroutines Best Practices

- Always use appropriate dispatcher:
  ```kotlin
  viewModelScope.launch(Dispatchers.IO) { /* DB operations */ }
  ```

- Use `Flow` for reactive updates:
  ```kotlin
  val rules: Flow<List<ClipboardRule>> = ruleDao.getAllRules()
  ```

### Database Optimization

- Use indices for frequently queried columns:
  ```kotlin
  @Entity(indices = [Index("matchContains")])
  data class ClipboardRule(...)
  ```

- Limit history queries:
  ```kotlin
  @Query("SELECT * FROM clipboard_history ORDER BY timestamp DESC LIMIT 100")
  ```

### Memory Management

- Anti-loop cache prevents excessive processing
- History limited to 100 entries
- Unused rules can be enabled/disabled

## Debugging

### View Logs

```bash
adb logcat com.clipboard.rulemanager:V
```

### Check Accessibility Service Status

```bash
adb shell settings get secure enabled_accessibility_services
```

### Database Inspection

Use Android Studio's Database Inspector:
1. View > Tool Windows > Database Inspector
2. Connect your device
3. Browse `clipboard_rules_db`

### Test Rules

Use the History tab to see:
- Original clipboard text
- Modified text
- Applied rule name
- Timestamp

## Common Issues & Solutions

### Accessibility Service Not Working

**Problem**: Service doesn't process clipboard changes.

**Solutions**:
1. Verify service is enabled: `Settings > Accessibility`
2. Check manifest has permission: `BIND_ACCESSIBILITY_SERVICE`
3. Restart device
4. Check logcat for errors

### Infinite Loop in Rule Processing

**Problem**: Clipboard updated endlessly.

**Solutions**:
1. RuleEngine has anti-loop cache
2. Check `lastProcessedText` logic if modified
3. Add logging to track processing

### Room Database Not Persisting

**Problem**: Rules lost after app restart.

**Solutions**:
1. Verify database file exists: `/data/data/com.clipboard.rulemanager/databases/`
2. Check database creation in `AppDatabase.getInstance()`
3. Verify @Entity and @Dao decorators

### UI Not Updating

**Problem**: Rule changes don't appear immediately.

**Solutions**:
1. Verify ViewModel uses `stateIn()` correctly
2. Check Flow collection in Compose
3. Verify Repository returns Flow, not single value

## Build Variants

### Debug Variant
- Full logging
- No obfuscation
- Debuggable

### Release Variant
- ProGuard obfuscation enabled
- Logging removed
- Optimized

Build release:
```bash
./gradlew assembleRelease
```

## Code Style

### Kotlin Conventions

- Use `camelCase` for variables and functions
- Use `PascalCase` for classes and interfaces
- Use `UPPER_CASE` for constants
- Max line length: 120 characters
- Use meaningful variable names

### Compose Guidelines

- Stateless composables when possible
- Pass lambdas for callbacks
- Use proper spacing and padding
- Follow Material Design 3

## Documentation

### Inline Comments

Only comment complex logic:
```kotlin
// Prevent reprocessing: if we just processed this text, skip
if (text == lastProcessedText && text == lastProcessedResult) {
    return RuleResult(modified = false, ...)
}
```

### Function Documentation

Use KDoc for public functions:
```kotlin
/**
 * Applies rules to clipboard text
 * @param text The clipboard text to process
 * @param rules List of enabled rules to apply
 * @return RuleResult containing the result and applied rule
 */
fun applyRules(text: String, rules: List<ClipboardRule>): RuleResult
```

## Resources

- [Android Developer Documentation](https://developer.android.com)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)

---

**Happy coding! 🚀**
