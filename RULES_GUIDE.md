# Clipboard Rule Manager - Preset Rules Guide

This document contains pre-configured rules for common use cases. You can copy these rules into your app.

## Social Media Rules

### Instagram - Remove Sharing Metadata

**Purpose**: Remove `igsh` parameter that reveals how the link was shared

```
Rule Name: Instagram - Remove Sharing Metadata
Match Contains: instagram.com
Regex Pattern: \?igsh=.*$
Replacement: (leave empty)
```

**Input**: `https://www.instagram.com/reel/DUOGuoeCnkN/?igsh=azF2ZGhkbzNueTUx`
**Output**: `https://www.instagram.com/reel/DUOGuoeCnkN/`

### Instagram - Keep Only Reel ID

**Purpose**: Extract only the essential reel/post information

```
Rule Name: Instagram - Keep Only Reel ID
Match Contains: instagram.com
Regex Pattern: ^(https://www\.instagram\.com/[^/]+/[^/]+/).*$
Replacement: $1
```

### Twitter/X - Remove Tracking Parameters

**Purpose**: Remove tracking parameters like `s`, `t`, etc.

```
Rule Name: Twitter - Clean URL
Match Contains: twitter.com
Regex Pattern: \?s=[^&]*&*
Replacement: (leave empty)
```

### Twitter/X - Full Clean (multiple params)

```
Rule Name: Twitter - Full Clean
Match Contains: twitter.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

### TikTok - Remove Tracking

```
Rule Name: TikTok - Clean URL
Match Contains: tiktok.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

### Facebook - Remove Query Parameters

```
Rule Name: Facebook - Clean URL
Match Contains: facebook.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

## URL Shortener Rules

### Bit.ly - Expand (Extract redirect target)

```
Rule Name: Bitly - Mark as Short
Match Contains: bit.ly
Regex Pattern: ^
Replacement: [SHORT-URL] 
// This just marks short URLs - actual expansion requires server API
```

### Remove UTM Parameters

```
Rule Name: Remove UTM Parameters
Match Contains: (leave empty - applies to all)
Regex Pattern: [?&]utm_[^&]*
Replacement: (leave empty)
```

**Input**: `https://example.com/page?utm_source=twitter&utm_medium=social&param=value`
**Output**: `https://example.com/page?param=value`

## YouTube Rules

### YouTube - Extract Video ID Only

```
Rule Name: YouTube - Video ID Only
Match Contains: youtube.com
Regex Pattern: ^.*v=([a-zA-Z0-9_-]+).*$
Replacement: https://youtu.be/$1
```

### YouTube - Remove All Parameters

```
Rule Name: YouTube - Clean URL
Match Contains: youtube.com
Regex Pattern: (&[a-z_]*=[^&]*)
Replacement: (leave empty)
```

**Input**: `https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10&list=xyz&index=1`
**Output**: `https://www.youtube.com/watch?v=dQw4w9WgXcQ`

### YouTube - Remove List Parameter

```
Rule Name: YouTube - Remove Playlist
Match Contains: youtube.com
Regex Pattern: &(list|index)=[^&]*
Replacement: (leave empty)
```

## Amazon Rules

### Amazon - Remove Affiliate Tags

```
Rule Name: Amazon - Remove Affiliate
Match Contains: amazon.com
Regex Pattern: (\?|&)(tag|ref-link-code)=[^&]*
Replacement: (leave empty)
```

**Input**: `https://www.amazon.com/product?tag=myaffiliate-20&ref-link-code=xyz`
**Output**: `https://www.amazon.com/product`

## AliExpress Rules

### AliExpress - Clean URL

```
Rule Name: AliExpress - Clean URL
Match Contains: aliexpress.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

## GitHub Rules

### GitHub - Remove Query Parameters

```
Rule Name: GitHub - Clean URL
Match Contains: github.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

## E-commerce Rules

### Shopify Stores - Remove Analytics

```
Rule Name: Shopify - Remove Analytics
Match Contains: (leave empty)
Regex Pattern: (\?|&)(utm_|fbclid|gclid)=[^&]*
Replacement: (leave empty)
```

## News/Blog Rules

### Medium - Remove Query Params

```
Rule Name: Medium - Clean URL
Match Contains: medium.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

### Reddit - Remove Share Metadata

```
Rule Name: Reddit - Clean URL
Match Contains: reddit.com
Regex Pattern: \?[a-z_]*=[^&]*$
Replacement: (leave empty)
```

## Generic Rules

### Remove All Query Parameters (All URLs)

```
Rule Name: Remove All Query Strings
Match Contains: (leave empty)
Regex Pattern: \?.*$
Replacement: (leave empty)
```

### Remove All Fragments

```
Rule Name: Remove Fragments
Match Contains: (leave empty)
Regex Pattern: #.*$
Replacement: (leave empty)
```

### Remove www. Prefix

```
Rule Name: Remove www prefix
Match Contains: (leave empty)
Regex Pattern: ^(https?://)?www\.
Replacement: $1
```

### Convert HTTP to HTTPS

```
Rule Name: Force HTTPS
Match Contains: (leave empty)
Regex Pattern: ^http://
Replacement: https://
```

## Apple Services Rules

### App Store - Remove Query Params

```
Rule Name: App Store - Clean
Match Contains: apps.apple.com
Regex Pattern: \?.*$
Replacement: (leave empty)
```

## Google Services Rules

### Google Drive - Remove Share Params

```
Rule Name: Google Drive - Clean
Match Contains: drive.google.com
Regex Pattern: [?#].*$
Replacement: (leave empty)
```

### Google Photos - Keep Only Album ID

```
Rule Name: Google Photos - Album ID
Match Contains: photos.app.goo.gl
Regex Pattern: \?.*$
Replacement: (leave empty)
```

## Notion Rules

### Notion - Keep Only Database ID

```
Rule Name: Notion - Database ID
Match Contains: notion.so
Regex Pattern: \?.*$
Replacement: (leave empty)
```

## Custom Rules Template

Use this template to create your own rules:

```
Rule Name: [Descriptive name]
Match Contains: [domain.com or empty for all]
Regex Pattern: [Your regex here]
Replacement: [Replacement text or empty to remove]
Enabled: ✓
```

## Regex Testing Tips

1. **Test everything at regex101.com**:
   - Select "Kotlin" flavor
   - Test with real URLs before adding

2. **Common mistakes**:
   - Forgetting to escape: `.` → `\.`
   - Forgetting to escape: `?` → `\?`
   - Using `/` as delimiter (not needed in Kotlin regex)
   - Forgetting `$` to match end of string

3. **Debugging**:
   - Start simple: `\?.*$` to remove everything after `?`
   - Add complexity: `\?param=.*(&|$)` for specific param
   - Use groups: `^(.*)\?.*$` to keep before `?`, then use `$1` as replacement

## Performance Tips

1. **Order matters**: Put more specific rules before general ones
2. **Match Contains**: Always use it to avoid unnecessary regex processing on every URL
3. **Simpler patterns**: `\?.*$` is faster than complex patterns
4. **Disable unused rules**: Toggle off rules you don't currently use

## Common Issues

### Rule not triggering?
- Check "Match Contains" matches the actual URL
- Example: if Match Contains is "example.com" but URL is "www.example.com", it won't match

### Getting unwanted results?
- Your regex is too broad
- Example: `.*=.*` matches everything
- Be more specific: `&utm_source=[^&]*`

### Crashes or errors?
- Look at HistoryScreen to see what failed
- Invalid regex patterns are silently skipped
- Check Android logcat for error messages

## Advanced Patterns

### Conditional Replacement

Use capture groups `()` and backreferences `$1`, `$2`, etc.:

```
Pattern: ^(https?://).*@([^/]*)(.*)$
Replacement: $1$2$3
```
This removes any basic auth credentials while keeping protocol and domain.

### Multiple Parameter Removal

```
Pattern: [?&](utm_[a-z_]*|fbclid|gclid|ref)=[^&]*
Replacement: (leave empty)
```
This removes multiple known tracking parameters.

### URL Canonicalization

```
Pattern: ^(https?://)(www\.)?(.*)$
Replacement: $1$3
```
This removes www from all URLs.

---

**Pro Tip**: You can enable/disable rules individually to see which one is affecting your URLs! Use the History tab to check what's being modified.
