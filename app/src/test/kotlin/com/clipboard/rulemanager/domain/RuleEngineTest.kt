package com.clipboard.rulemanager.domain

import com.clipboard.rulemanager.data.model.ClipboardRule
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for RuleEngine - the core business logic
 * Tests regex matching, replacement, and anti-loop prevention
 */
class RuleEngineTest {
    
    private lateinit var ruleEngine: RuleEngine
    
    @Before
    fun setUp() {
        ruleEngine = RuleEngine()
    }
    
    // ============= BASIC FUNCTIONALITY TESTS =============
    
    @Test
    fun testPlainTextUnmodified() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "instagram.com",
            regex = "\\?igsh=.*$",
            replacement = ""
        )
        
        val result = ruleEngine.applyRules("Hello World", listOf(rule))
        
        assertFalse(result.modified)
        assertEquals("Hello World", result.modifiedText)
    }
    
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
    
    @Test
    fun testQueryStringRemoved() {
        val rule = ClipboardRule(
            name = "Remove Query String",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val input = "https://example.com/page?param1=value1&param2=value2"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertTrue(result.modified)
        assertEquals("https://example.com/page", result.modifiedText)
    }
    
    @Test
    fun testYoutubeVideoIdExtraction() {
        val rule = ClipboardRule(
            name = "YouTube Video ID",
            matchContains = "youtube.com",
            regex = "^.*v=([a-zA-Z0-9_-]+).*$",
            replacement = "https://youtu.be/$1"
        )
        
        val input = "https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10&list=xyz"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertTrue(result.modified)
        assertEquals("https://youtu.be/dQw4w9WgXcQ", result.modifiedText)
    }
    
    // ============= CONDITION MATCHING TESTS =============
    
    @Test
    fun testMatchContainsCondition() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "instagram.com",
            regex = "\\?igsh=.*$",
            replacement = ""
        )
        
        // URL with instagram.com - should match
        val result1 = ruleEngine.applyRules(
            "https://www.instagram.com/reel/abc/?igsh=123",
            listOf(rule)
        )
        assertTrue(result1.modified)
        
        // URL without instagram.com - should NOT match
        val result2 = ruleEngine.applyRules(
            "https://www.facebook.com/page/?igsh=123",
            listOf(rule)
        )
        assertFalse(result2.modified)
    }
    
    @Test
    fun testMatchContainsCaseInsensitive() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "INSTAGRAM.COM",
            regex = "\\?igsh=.*$",
            replacement = ""
        )
        
        val input = "https://www.instagram.com/reel/abc/?igsh=123"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        // Should match despite case difference
        assertTrue(result.modified)
    }
    
    @Test
    fun testEmptyMatchContainsAppliesToAll() {
        val rule = ClipboardRule(
            name = "Remove Query String",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        // Should apply to any URL
        val result1 = ruleEngine.applyRules("https://example.com?a=1", listOf(rule))
        val result2 = ruleEngine.applyRules("https://google.com?q=test", listOf(rule))
        
        assertTrue(result1.modified)
        assertTrue(result2.modified)
    }
    
    // ============= MULTI-RULE TESTS =============
    
    @Test
    fun testMultipleRulesApplied() {
        val rule1 = ClipboardRule(
            name = "Remove Query String",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        val rule2 = ClipboardRule(
            name = "Remove Trailing Slash",
            matchContains = "",
            regex = "/$",
            replacement = ""
        )
        
        val input = "https://example.com/page/?param=value/"
        val result = ruleEngine.applyRules(input, listOf(rule1, rule2))
        
        // First rule removes ?..., second removes trailing /
        assertEquals("https://example.com/page", result.modifiedText)
    }
    
    @Test
    fun testDisabledRuleNotApplied() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*$",
            replacement = "",
            enabled = false
        )
        
        val input = "https://example.com?param=value"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertFalse(result.modified)
        assertEquals(input, result.modifiedText)
    }
    
    @Test
    fun testFirstMatchingRuleReturned() {
        val rule1 = ClipboardRule(
            id = 1,
            name = "Rule 1",
            matchContains = "example.com",
            regex = "\\?.*$",
            replacement = ""
        )
        val rule2 = ClipboardRule(
            id = 2,
            name = "Rule 2",
            matchContains = "example.com",
            regex = "$",
            replacement = "_modified"
        )
        
        val input = "https://example.com/page?param=value"
        val result = ruleEngine.applyRules(input, listOf(rule1, rule2))
        
        // Rule 1 should be applied and returned
        assertEquals("Rule 1", result.appliedRule?.name)
    }
    
    // ============= ANTI-LOOP TESTS =============
    
    @Test
    fun testAntiLoopPrevention() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val input = "https://example.com?a=1"
        
        // First call - should modify
        val result1 = ruleEngine.applyRules(input, listOf(rule))
        assertTrue(result1.modified)
        
        // Second call with same input - should not modify (anti-loop)
        val result2 = ruleEngine.applyRules(input, listOf(rule))
        assertFalse(result2.modified)
    }
    
    @Test
    fun testAntiLoopResetAfterDifferentInput() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val input1 = "https://example.com?a=1"
        val input2 = "https://example.com?b=2"
        
        // Process first URL
        val result1 = ruleEngine.applyRules(input1, listOf(rule))
        assertTrue(result1.modified)
        
        // Process different URL - should process normally
        val result2 = ruleEngine.applyRules(input2, listOf(rule))
        assertTrue(result2.modified)
    }
    
    // ============= INVALID REGEX TESTS =============
    
    @Test
    fun testInvalidRegexHandledGracefully() {
        val rule = ClipboardRule(
            name = "Invalid Rule",
            matchContains = "",
            regex = "[invalid(regex",  // Invalid regex
            replacement = ""
        )
        
        val input = "https://example.com?a=1"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        // Should not crash, should not modify
        assertFalse(result.modified)
        assertEquals(input, result.modifiedText)
    }
    
    // ============= COMPLEX URL TESTS =============
    
    @Test
    fun testAmazonAffiliateCleanup() {
        val rule = ClipboardRule(
            name = "Amazon Affiliate",
            matchContains = "amazon.com",
            regex = "(\\?|&)(tag|ref-link-code)=[^&]*",
            replacement = ""
        )
        
        val input = "https://www.amazon.com/product?tag=myaffiliate-20&ref-link-code=xyz"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertTrue(result.modified)
        // Tags should be removed
        assertTrue(!result.modifiedText.contains("tag="))
        assertTrue(!result.modifiedText.contains("ref-link-code="))
    }
    
    @Test
    fun testUTMParameterRemoval() {
        val rule = ClipboardRule(
            name = "Remove UTM",
            matchContains = "",
            regex = "[?&]utm_[^&]*",
            replacement = ""
        )
        
        val input = "https://example.com/page?utm_source=twitter&utm_medium=social&param=keep"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertTrue(result.modified)
        assertTrue(!result.modifiedText.contains("utm_"))
        assertTrue(result.modifiedText.contains("param=keep"))
    }
    
    @Test
    fun testRedditTrackingRemoval() {
        val rule = ClipboardRule(
            name = "Reddit Clean",
            matchContains = "reddit.com",
            regex = "\\?[a-z_]*=[^&]*$",
            replacement = ""
        )
        
        val input = "https://www.reddit.com/r/funny/comments/abc123?share=xyz"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertTrue(result.modified)
        assertEquals("https://www.reddit.com/r/funny/comments/abc123", result.modifiedText)
    }
    
    // ============= EDGE CASES =============
    
    @Test
    fun testEmptyString() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val result = ruleEngine.applyRules("", listOf(rule))
        
        assertFalse(result.modified)
        assertEquals("", result.modifiedText)
    }
    
    @Test
    fun testVeryLongUrl() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val longUrl = "https://example.com/very/long/path/to/page?" +
                "param1=value1&param2=value2&param3=value3&param4=value4&param5=value5"
        
        val result = ruleEngine.applyRules(longUrl, listOf(rule))
        
        assertTrue(result.modified)
        assertEquals("https://example.com/very/long/path/to/page", result.modifiedText)
    }
    
    @Test
    fun testUrlInMiddleOfText() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*(?=\\s|$)",
            replacement = ""
        )
        
        val input = "Check this: https://example.com?a=1 out"
        val result = ruleEngine.applyRules(input, listOf(rule))
        
        assertTrue(result.modified)
        assertTrue(result.modifiedText.contains("https://example.com"))
        assertFalse(result.modifiedText.contains("?a=1"))
    }
    
    @Test
    fun testResetEngine() {
        val rule = ClipboardRule(
            name = "Test",
            matchContains = "",
            regex = "\\?.*$",
            replacement = ""
        )
        
        val input = "https://example.com?a=1"
        
        // First call
        val result1 = ruleEngine.applyRules(input, listOf(rule))
        assertTrue(result1.modified)
        
        // Reset
        ruleEngine.reset()
        
        // Same input should now be processed again
        val result2 = ruleEngine.applyRules(input, listOf(rule))
        assertTrue(result2.modified)
    }
}
