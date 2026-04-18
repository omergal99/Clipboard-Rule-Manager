package com.clipboard.rulemanager.domain

import com.clipboard.rulemanager.data.model.ClipboardRule

/**
 * Engine for applying clipboard rules to text.
 * Handles regex matching and replacement, with anti-loop prevention.
 */
class RuleEngine {
    private var lastProcessedText: String? = null
    private var lastProcessedResult: String? = null

    data class RuleResult(
        val modified: Boolean,
        val originalText: String,
        val modifiedText: String,
        val appliedRule: ClipboardRule?
    )

    /**
     * Applies rules to clipboard text
     * @param text The clipboard text to process
     * @param rules List of enabled rules to apply
     * @return RuleResult containing the result and applied rule
     */
    fun applyRules(text: String, rules: List<ClipboardRule>): RuleResult {
        // Anti-loop prevention: if we just processed this exact text, skip
        if (text == lastProcessedText && text == lastProcessedResult) {
            return RuleResult(
                modified = false,
                originalText = text,
                modifiedText = text,
                appliedRule = null
            )
        }

        var result = text
        var appliedRule: ClipboardRule? = null

        // Apply rules in order
        for (rule in rules) {
            if (!rule.enabled) continue

            // Check if text contains the match condition
            if (rule.matchContains.isNotEmpty() && !result.contains(rule.matchContains, ignoreCase = true)) {
                continue
            }

            // Try to apply regex replacement
            try {
                val newResult = result.replace(Regex(rule.regex), rule.replacement)
                if (newResult != result) {
                    result = newResult
                    appliedRule = rule
                    // Continue applying other rules if text was modified
                }
            } catch (e: Exception) {
                // Invalid regex, skip this rule
                continue
            }
        }

        // Update cache for anti-loop detection
        lastProcessedText = text
        lastProcessedResult = result

        return RuleResult(
            modified = result != text,
            originalText = text,
            modifiedText = result,
            appliedRule = appliedRule
        )
    }

    /**
     * Resets the anti-loop cache
     */
    fun reset() {
        lastProcessedText = null
        lastProcessedResult = null
    }
}
