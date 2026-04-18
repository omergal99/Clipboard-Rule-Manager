package com.clipboard.rulemanager.data

import android.content.ClipboardManager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clipboard.rulemanager.data.database.AppDatabase
import com.clipboard.rulemanager.data.model.ClipboardHistory
import com.clipboard.rulemanager.data.model.ClipboardRule
import com.clipboard.rulemanager.data.repository.ClipboardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for ClipboardRepository
 * Tests database operations and clipboard access
 */
@RunWith(AndroidJUnit4::class)
class ClipboardRepositoryIntegrationTest {
    
    private lateinit var database: AppDatabase
    private lateinit var repository: ClipboardRepository
    
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = ClipboardRepository(
            context,
            database.clipboardRuleDao(),
            database.clipboardHistoryDao()
        )
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    // ============= RULE CRUD TESTS =============
    
    @Test
    fun testInsertAndRetrieveRule() = runBlocking {
        val rule = ClipboardRule(
            name = "Test Rule",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = ""
        )
        
        repository.insertRule(rule)
        
        val rules = repository.getAllRules().first()
        assertTrue(rules.isNotEmpty())
        assertEquals("Test Rule", rules[0].name)
    }
    
    @Test
    fun testUpdateRule() = runBlocking {
        val rule = ClipboardRule(
            name = "Original Name",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val id = repository.insertRule(rule).toInt()
        
        // Update the rule
        val updatedRule = rule.copy(
            id = id,
            name = "Updated Name"
        )
        repository.updateRule(updatedRule)
        
        val retrieved = repository.getRuleById(id)
        assertEquals("Updated Name", retrieved?.name)
    }
    
    @Test
    fun testDeleteRule() = runBlocking {
        val rule = ClipboardRule(
            name = "To Delete",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = ""
        )
        
        repository.insertRule(rule)
        val rules1 = repository.getAllRules().first()
        assertTrue(rules1.isNotEmpty())
        
        // Delete
        repository.deleteRule(rules1[0])
        val rules2 = repository.getAllRules().first()
        assertTrue(rules2.isEmpty())
    }
    
    @Test
    fun testGetEnabledRulesOnly() = runBlocking {
        val rule1 = ClipboardRule(
            name = "Enabled",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = "",
            enabled = true
        )
        val rule2 = ClipboardRule(
            name = "Disabled",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = "",
            enabled = false
        )
        
        repository.insertRule(rule1)
        repository.insertRule(rule2)
        
        val enabledRules = repository.getEnabledRules().first()
        assertEquals(1, enabledRules.size)
        assertEquals("Enabled", enabledRules[0].name)
    }
    
    // ============= HISTORY TESTS =============
    
    @Test
    fun testInsertHistoryEntry() = runBlocking {
        val history = ClipboardHistory(
            originalText = "https://example.com?a=1",
            modifiedText = "https://example.com",
            ruleApplied = "Remove Query String"
        )
        
        repository.insertHistory(history)
        
        val entries = repository.getRecentHistory().first()
        assertTrue(entries.isNotEmpty())
        assertEquals("https://example.com", entries[0].modifiedText)
    }
    
    @Test
    fun testHistoryLimitedTo100Entries() = runBlocking {
        // Insert more than 100 entries
        repeat(150) { i ->
            val history = ClipboardHistory(
                originalText = "text$i",
                modifiedText = "modified$i",
                ruleApplied = "Rule"
            )
            repository.insertHistory(history)
        }
        
        val entries = repository.getRecentHistory().first()
        // Should return only the most recent entries
        assertTrue(entries.size <= 100)
    }
    
    @Test
    fun testClearHistory() = runBlocking {
        // Add entries
        repeat(5) { i ->
            repository.insertHistory(
                ClipboardHistory(
                    originalText = "text$i",
                    modifiedText = "modified$i"
                )
            )
        }
        
        val beforeClear = repository.getRecentHistory().first()
        assertTrue(beforeClear.isNotEmpty())
        
        // Clear
        repository.clearHistory()
        
        val afterClear = repository.getRecentHistory().first()
        assertTrue(afterClear.isEmpty())
    }
    
    // ============= INTEGRATION SCENARIOS =============
    
    @Test
    fun testCompleteClipboardWorkflow() = runBlocking {
        // 1. Insert a rule
        val rule = ClipboardRule(
            name = "Instagram Cleaner",
            matchContains = "instagram.com",
            regex = "\\?igsh=.*$",
            replacement = "",
            enabled = true
        )
        repository.insertRule(rule)
        
        // 2. Verify rule was stored
        val rules = repository.getEnabledRules().first()
        assertEquals(1, rules.size)
        
        // 3. Simulate processing
        val history = ClipboardHistory(
            originalText = "https://www.instagram.com/reel/abc/?igsh=123",
            modifiedText = "https://www.instagram.com/reel/abc/",
            ruleApplied = "Instagram Cleaner"
        )
        repository.insertHistory(history)
        
        // 4. Verify history
        val entries = repository.getRecentHistory().first()
        assertEquals(1, entries.size)
        assertEquals("Instagram Cleaner", entries[0].ruleApplied)
    }
    
    @Test
    fun testMultipleRulesAndHistory() = runBlocking {
        // Add multiple rules
        repeat(3) { i ->
            repository.insertRule(
                ClipboardRule(
                    name = "Rule $i",
                    matchContains = "domain$i.com",
                    regex = "\\?.*$",
                    replacement = ""
                )
            )
        }
        
        // Add history for each
        repeat(3) { i ->
            repository.insertHistory(
                ClipboardHistory(
                    originalText = "https://domain$i.com?a=1",
                    modifiedText = "https://domain$i.com",
                    ruleApplied = "Rule $i"
                )
            )
        }
        
        val rules = repository.getAllRules().first()
        val history = repository.getRecentHistory().first()
        
        assertEquals(3, rules.size)
        assertEquals(3, history.size)
    }
    
    @Test
    fun testRuleToggling() = runBlocking {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = "",
            enabled = false
        )
        
        repository.insertRule(rule)
        
        var enabledRules = repository.getEnabledRules().first()
        assertTrue(enabledRules.isEmpty())
        
        // Enable it
        val ruleToUpdate = repository.getAllRules().first()[0]
        repository.updateRule(ruleToUpdate.copy(enabled = true))
        
        enabledRules = repository.getEnabledRules().first()
        assertEquals(1, enabledRules.size)
    }
}
