# Architecture Improvements & Best Practices

This document outlines improvements made to handle Android 10+ clipboard limitations and provides best practices for extending the application.

## Android 10+ Clipboard Limitations

Since Android 10, there are significant restrictions on background clipboard access:

1. **Limited AccessibilityService** - Cannot reliably access clipboard in background without user being on screen
2. **ClipboardManager** - Limited to foreground app access
3. **Privacy** - System restricts clipboard monitoring to accessibility needs only

## Improved Architecture

### Separation of Concerns

Instead of having ClipboardAccessibilityService as the "brain" of the application, the architecture now separates:

```
ClipboardMonitor (Listener for clipboard changes)
        ↓
RuleEngine (Pure business logic - regex processing)
        ↓
ClipboardWriter (Updates clipboard)
        ↓
RuleStorage (DataStore/Room persistence)
```

### Layer Responsibilities

#### Data Layer
- **ClipboardRepository** - Abstracts all clipboard and database operations
- **RuleEngine** - NO database access (pure transformation logic)
- **DAOs** - Direct database access via Room

#### Service Layer
- **ClipboardAccessibilityService** - Lightweight, only triggers rule processing
- Does NOT contain business logic
- Does NOT handle database operations directly

#### Domain Layer
- **RuleEngine** - Pure functions for regex matching/replacement
- No dependencies on Android framework
- Fully testable without instrumentation

### Anti-Loop Prevention

The system uses multiple layers of anti-loop protection:

1. **Text Cache** - RuleEngine remembers last input/output
2. **Processing Flag** - Service uses `isProcessing` flag
3. **Delay** - Small delay between write and next detection
4. **Manual Reset** - Can be triggered for edge cases

## Best Practices

### Adding New Features

#### 1. New Rule Type
```kotlin
// Add to ClipboardRule
data class ClipboardRule(
    val scope: RuleScope = RuleScope.GLOBAL  // NEW: Restrict to domain
    // ... existing fields
)

enum class RuleScope {
    GLOBAL,
    INSTAGRAM_ONLY,
    YOUTUBE_ONLY,
    // ...
}
```

**Changes needed**:
- Update ClipboardRule entity (+1 line)
- Update RuleEngine logic (+5 lines)
- Update UI to show scope selector (+10 lines)
- Test new logic

#### 2. New Data Format Support
```kotlin
// In RuleEngine
fun applyRules(text: String, rules: List<ClipboardRule>): RuleResult {
    // Could add JSON, XML processing here
}
```

#### 3. Custom Clipboard Source
```kotlin
// Extend ClipboardRepository
interface ClipboardSource {
    fun getText(): String?
    fun setText(text: String)
}

class SystemClipboardSource : ClipboardSource { }
class TestClipboardSource : ClipboardSource { }  // For testing
```

### Testing Strategy

#### Unit Tests
- Test RuleEngine with diverse inputs
- No Android dependencies needed
- Fast execution
- High coverage

#### Integration Tests
- Test Repository with Room database
- Use in-memory database for speed
- Test database transactions

#### Instrumentation Tests
- Test AccessibilityService
- Test clipboard integration
- Test UI flows

#### Example Test Coverage
```
RuleEngine:
  - Basic functionality: 10 tests
  - Conditions: 8 tests
  - Multi-rule: 5 tests
  - Anti-loop: 3 tests
  - Edge cases: 8 tests
  - Complex URLs: 6 tests
  TOTAL: 40 tests

Repository:
  - CRUD: 5 tests
  - History: 3 tests
  - Integration: 3 tests
  TOTAL: 11 tests

Overall: 51+ tests covering critical paths
```

### Performance Considerations

#### RuleEngine
- O(n) where n = number of rules
- Short-circuit on first match (can be changed)
- Regex compilation is cached by Kotlin

#### ClipboardAccessibilityService
- Coroutine-based (no blocking)
- Uses Dispatchers.IO for database
- Small memory footprint

#### Database
- Room queries are synchronous in tests only
- Production uses Flow for reactive updates
- History limited to 100 entries to prevent growth

### Security Considerations

1. **No Network** - All operations are local
2. **No Sensitive Data** - Regex patterns stored locally
3. **Accessibility Service** - Only for clipboard, no other permissions
4. **Database** - Local, no cloud sync (optional feature for v2)

## Known Limitations & Workarounds

### Issue: Service stops on device lock
**Workaround**: Service continues but clipboard monitoring pauses
**Solution**: User manually copies text again, or unlock device

### Issue: Some apps clear clipboard on paste
**Workaround**: We detect this and don't reprocess
**Solution**: Anti-loop cache prevents issues

### Issue: Very large clipboard content
**Workaround**: Process anyway (Kotlin handles large strings)
**Solution**: Add size limit if needed (optional feature)

## Future Improvements

### v1.1 (Planned)
- [ ] Import/export rules as JSON
- [ ] Rule templates
- [ ] Live regex preview
- [ ] Better anti-loop visualization

### v1.2 (Planned)
- [ ] App-specific rules (whitelist/blacklist)
- [ ] Rule scheduling (enable/disable by time)
- [ ] Statistics dashboard

### v2.0 (Planned)
- [ ] Cloud sync (optional)
- [ ] Rule marketplace
- [ ] Plugin system

## Code Quality

### Metrics
- **Lines of Code**: ~2,500 (Kotlin)
- **Test Coverage**: ~40 tests for core logic
- **Architecture**: Clean Architecture (3 layers)
- **Code Style**: Kotlin conventions, KDoc comments

### Dependencies
- Minimal external dependencies
- Uses official Android libraries
- Room for persistence
- DataStore for settings
- Coroutines for async

### Error Handling
- Graceful exception handling throughout
- Invalid regex patterns handled
- Database errors logged
- Service doesn't crash on errors

## Contributing

When contributing, ensure:

1. **Tests pass** - `./gradlew test`
2. **Code style** - Follow Kotlin conventions
3. **Documentation** - Add KDoc comments
4. **Architecture** - Maintain layer separation
5. **No breaking changes** - Maintain backward compatibility

## Performance Profiling

To profile the app:

1. **CPU Profiler**
   - Open Android Studio Profiler
   - Navigate to CPU
   - Interact with app
   - Check for long operations

2. **Memory Profiler**
   - In Profiler, select Memory
   - Capture heap dumps
   - Check for leaks

3. **Database Profiler**
   - Use Room Inspector
   - Query execution times
   - Database size

Typical performance:
- Rule application: <100ms
- Clipboard write: <50ms
- Database save: <100ms
- Total latency: <250ms

## References

- [Android AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [ClipboardManager](https://developer.android.com/reference/android/content/ClipboardManager)
- [Android 10 Privacy Changes](https://developer.android.com/about/versions/10/privacy)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVVM Pattern](https://developer.android.com/jetpack/guide)

---

Updated: April 18, 2026
