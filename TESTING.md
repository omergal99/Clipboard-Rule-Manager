# Testing Guide - Clipboard Rule Manager

Comprehensive guide for running, writing, and understanding tests in this project.

## Quick Start

### Run All Tests
```bash
# Unit tests
./gradlew test

# Instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest

# Everything
./gradlew test connectedAndroidTest
```

### Run Specific Test
```bash
# Single test class
./gradlew test -k RuleEngineTest

# Single test method
./gradlew test -k "testInstagramUrlCleaned"
```

### Run With Coverage
```bash
./gradlew test jacocoTestReport
```

Coverage report: `app/build/reports/jacoco/test/html/index.html`

---

## Unit Tests - RuleEngine

Located in: `app/src/test/kotlin/com/clipboard/rulemanager/domain/RuleEngineTest.kt`

### Test Structure

**40+ Test Cases** organized in categories:

#### 1. Basic Functionality (5 tests)
Tests core regex replacement operations:
- Plain text remains unchanged
- Instagram URL cleaning
- Query string removal
- YouTube video ID extraction
- Basic pattern matching

```kotlin
@Test
fun testInstagramUrlCleaned() {
    val rule = ClipboardRule(
        name = "Instagram Cleaner",
        matchContains = "instagram.com",
        regex = "\\?igsh=.*$",
        replacement = ""
    )
    
    val input = "https://www.instagram.com/reel/DUOGuoeCnkN/?igsh=azF2ZGhkbzNueTUx"
    val result = ruleEngine.applyRules(input, listOf(rule))
    
    assertTrue(result.modified)
    assertEquals("https://www.instagram.com/reel/DUOGuoeCnkN/", result.modifiedText)
}
```

#### 2. Condition Matching (3 tests)
Tests the `matchContains` condition:
- Condition must be present in text
- Case-insensitive matching
- Empty condition applies to all

#### 3. Multi-Rule Processing (4 tests)
Tests multiple rules applied in sequence:
- Multiple rules applied
- Disabled rules skipped
- First matching rule returned
- Rules applied in order

#### 4. Anti-Loop Prevention (3 tests)
Tests infinite loop prevention:
- Same input not reprocessed
- Different input resets cache
- Reset mechanism works

#### 5. Invalid Input Handling (1 test)
Tests error handling:
- Invalid regex handled gracefully
- No crash, no modification

#### 6. Complex URL Patterns (4 tests)
Real-world URL cleaning:
- Amazon affiliate removal
- UTM parameter removal
- Reddit tracking removal
- Multi-parameter patterns

#### 7. Edge Cases (6 tests)
Tests boundary conditions:
- Empty strings
- Very long URLs
- URLs in middle of text
- URL preservation
- Reset mechanism

### Running Unit Tests

```bash
# Run all RuleEngine tests
./gradlew test --tests RuleEngineTest

# Run specific test
./gradlew test --tests RuleEngineTest.testInstagramUrlCleaned

# With verbose output
./gradlew test --tests RuleEngineTest -i
```

### Test Output Example

```
RuleEngineTest.testInstagramUrlCleaned PASSED (125ms)
RuleEngineTest.testQueryStringRemoved PASSED (85ms)
RuleEngineTest.testAntiLoopPrevention PASSED (95ms)
...
BUILD SUCCESSFUL
```

---

## Integration Tests - Repository

Located in: `app/src/androidTest/kotlin/com/clipboard/rulemanager/data/ClipboardRepositoryIntegrationTest.kt`

### Test Structure

**11 Test Cases** covering database operations:

#### 1. CRUD Operations (4 tests)
- Insert and retrieve rule
- Update rule
- Delete rule
- Get enabled rules only

#### 2. History Management (3 tests)
- Insert history entry
- History limited to 100 entries
- Clear history

#### 3. Integration Scenarios (3 tests)
- Complete clipboard workflow
- Multiple rules and history
- Rule toggling (enable/disable)

### Running Integration Tests

```bash
# Run all integration tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific class
./gradlew connectedAndroidTest --tests ClipboardRepositoryIntegrationTest

# Run on specific device
adb devices  # List devices
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.notAnnotation=androidx.test.filters.FlakyTest
```

### Test Database

Tests use in-memory database:
```kotlin
database = Room.inMemoryDatabaseBuilder(
    context,
    AppDatabase::class.java
).allowMainThreadQueries().build()
```

**Advantages**:
- Fast execution (no disk I/O)
- Isolated from real database
- Each test gets fresh database
- Clean test state

---

## Writing New Tests

### Test Template - Unit Test

```kotlin
@Test
fun testFeatureName() {
    // ARRANGE
    val rule = ClipboardRule(
        name = "Test",
        matchContains = "example.com",
        regex = "test_pattern",
        replacement = ""
    )
    
    // ACT
    val result = ruleEngine.applyRules("https://example.com?test=1", listOf(rule))
    
    // ASSERT
    assertTrue(result.modified)
    assertEquals("expected_output", result.modifiedText)
}
```

### Test Template - Integration Test

```kotlin
@Test
fun testDatabaseOperation() = runBlocking {
    // ARRANGE
    val rule = ClipboardRule(/* ... */)
    
    // ACT
    repository.insertRule(rule)
    val retrieved = repository.getAllRules().first()
    
    // ASSERT
    assertEquals(1, retrieved.size)
}
```

### Best Practices

1. **One assertion per test** (generally)
2. **Descriptive test names** - What, When, Then
3. **Use AAA pattern** - Arrange, Act, Assert
4. **Test behavior not implementation** - What not how
5. **Test edge cases** - Empty, null, extreme values

---

## Test Coverage

### Current Coverage

| Component | Coverage | Tests |
|-----------|----------|-------|
| RuleEngine | ~95% | 40+ |
| ClipboardRepository | ~80% | 11 |
| Models | ~100% | 0 (data classes) |
| DAOs | ~70% | 11 |
| **Total** | ~87% | **62+** |

### Improving Coverage

Add tests to:
- Exception handling
- Database integrity constraints
- UI ViewModel logic (future)
- AccessibilityService lifecycle

### Viewing Coverage

```bash
./gradlew test jacocoTestReport
open app/build/reports/jacoco/test/html/index.html
```

---

## CI/CD Integration

### Git Pre-commit Hook

Prevent commits with failing tests:

```bash
#!/bin/bash
# .git/hooks/pre-commit

set -e

echo "Running unit tests..."
./gradlew test

if [ $? -ne 0 ]; then
  echo "Unit tests failed. Aborting commit."
  exit 1
fi

echo "Tests passed. Committing..."
```

### GitHub Actions Example

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - run: ./gradlew test
      - run: ./gradlew build
```

---

## Debugging Tests

### Print Test Output

```kotlin
@Test
fun testWithDebugOutput() {
    val result = ruleEngine.applyRules(input, rules)
    println("Input: $input")
    println("Output: ${result.modifiedText}")
    println("Modified: ${result.modified}")
    assertTrue(result.modified)
}
```

Run with:
```bash
./gradlew test --tests TestName -i -s
```

### Android Studio Debugging

1. Click **Gutter** (line number area)
2. Select **Run** > **Run with Debugger**
3. Use breakpoints and watches
4. Step through execution

### Logcat for Instrumentation Tests

```bash
adb logcat | grep "RuleEngine"
```

---

## Test Scenarios - Real World

### Scenario 1: Instagram URL Cleaning

**Input**: `https://www.instagram.com/reel/ABC123/?igsh=sharetoken123`

**Expected**: `https://www.instagram.com/reel/ABC123/`

**Test**:
```kotlin
@Test
fun testInstagramScenario() {
    val rule = ClipboardRule(/* Instagram rule */)
    val result = ruleEngine.applyRules(instagramUrl, listOf(rule))
    assertEquals("https://www.instagram.com/reel/ABC123/", result.modifiedText)
}
```

### Scenario 2: Multiple Parameters

**Input**: `https://example.com?utm_source=twitter&utm_medium=social&param=keep`

**Expected**: `https://example.com?param=keep` (UTM removed, other kept)

**Test**:
```kotlin
@Test
fun testUTMRemovalPreservesOther() {
    val rule = ClipboardRule(/* UTM rule */)
    val result = ruleEngine.applyRules(urlWithParams, listOf(rule))
    assertTrue(result.modifiedText.contains("param=keep"))
    assertFalse(result.modifiedText.contains("utm_"))
}
```

### Scenario 3: Anti-Loop Protection

**Input**: Same URL twice

**Expected**: First processed, second skipped (anti-loop)

**Test**:
```kotlin
@Test
fun testAntiLoopProtection() {
    val rule = ClipboardRule(/* Test rule */)
    
    // First call
    val result1 = ruleEngine.applyRules(url, listOf(rule))
    assertTrue(result1.modified)
    
    // Second call - same input
    val result2 = ruleEngine.applyRules(url, listOf(rule))
    assertFalse(result2.modified)  // Anti-loop prevents reprocessing
}
```

---

## Performance Testing

### Measuring Execution Time

```kotlin
@Test
fun testPerformance() {
    val startTime = System.currentTimeMillis()
    
    repeat(1000) {
        ruleEngine.applyRules(testUrl, rules)
    }
    
    val duration = System.currentTimeMillis() - startTime
    println("1000 operations took ${duration}ms")
    assertTrue(duration < 5000)  // Should complete in < 5 seconds
}
```

**Expected Performance**:
- Single rule application: <5ms
- 10 rules: <20ms
- 100 URLs processed: <500ms

---

## Troubleshooting

### Test Failure: "No AndroidJUnit4 Runner"

```bash
# Missing test dependency
./gradlew androidDependencies | grep junit
```

Add to `build.gradle.kts`:
```kotlin
androidTestImplementation("androidx.test.ext:junit:1.1.5")
```

### Test Failure: "Database locked"

Use `allowMainThreadQueries()` for in-memory database tests:
```kotlin
Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
    .allowMainThreadQueries()
    .build()
```

### Test Failure: "Timeout"

Increase timeout or check for infinite loops:
```kotlin
@Test(timeout = 10000)  // 10 seconds
fun testWithTimeout() {
    // ...
}
```

---

## Best Practices Summary

✅ **DO**:
- Write tests for core business logic
- Use descriptive test names
- Test edge cases and error conditions
- Keep tests independent
- Use AAA pattern (Arrange, Act, Assert)
- Run tests before committing
- Maintain high coverage for critical paths

❌ **DON'T**:
- Write tests for trivial code
- Test implementation details
- Have tests depend on each other
- Use sleep() for synchronization
- Ignore flaky tests
- Skip testing error handling

---

## References

- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Android Testing Guide](https://developer.android.com/training/testing)
- [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)
- [Kotlin Coroutines Testing](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)

---

**Last Updated**: April 18, 2026

For questions or issues with tests, see ARCHITECTURE.md or DEVELOPMENT.md
