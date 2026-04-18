# Quick Start Guide 🚀

Get up and running with Clipboard Rule Manager in 5 minutes!

## Installation (2 minutes)

### Step 1: Build the App
```bash
cd clipboard-rules
./gradlew assembleDebug
```

### Step 2: Install on Device/Emulator
```bash
./gradlew installDebug
```

Or use Android Studio:
- Connect device via USB (or start emulator)
- Click "Run" > "Run 'app'"

## First-Time Setup (2 minutes)

### Step 1: Launch the App
Open "Clipboard Rule Manager" from your app drawer

### Step 2: Enable the Accessibility Service
1. Click **"Enable Service in Accessibility Settings"**
2. You'll be taken to Settings
3. Navigate to **Accessibility** > **Accessibility Services**
4. Find **"Clipboard Rule Manager"**
5. Toggle it **ON**
6. Grant permissions if prompted
7. Return to the app

### Step 3: Add Your First Rule

**Goal**: Clean Instagram URLs by removing `?igsh=...`

1. Go to **Rules** tab
2. Click **"Add New Rule"**
3. Fill in:
   - **Rule Name**: `Instagram Cleaner`
   - **Match Contains**: `instagram.com`
   - **Regex Pattern**: `\?igsh=.*$`
   - **Replacement**: (leave empty)
   - **Enabled**: ✓ (checked)
4. Click **Save**

## Test It! (1 minute)

### Copy an Instagram Link

1. Open Instagram (or use this example URL):
   ```
   https://www.instagram.com/reel/DUOGuoeCnkN/?igsh=azF2ZGhkbzNueTUx
   ```

2. Copy the URL to your clipboard

3. Go back to Clipboard Rule Manager app

4. Check **History** tab

5. You should see:
   - **Original**: `https://www.instagram.com/reel/DUOGuoeCnkN/?igsh=azF2ZGhkbzNueTUx`
   - **Modified**: `https://www.instagram.com/reel/DUOGuoeCnkN/`
   - **Rule Applied**: `Instagram Cleaner`

### Paste the URL

Paste the URL anywhere (messaging app, browser, etc.) - it will be **without the tracking code**! ✨

## Add More Rules (Optional)

### YouTube - Remove Parameters

1. **Add New Rule**
   - **Rule Name**: `YouTube Clean`
   - **Match Contains**: `youtube.com`
   - **Regex Pattern**: `\?.*$`
   - **Replacement**: (empty)
   - **Enabled**: ✓

2. Test with: `https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10&list=abc`

### Twitter/X - Remove Parameters

1. **Add New Rule**
   - **Rule Name**: `Twitter Clean`
   - **Match Contains**: `twitter.com`
   - **Regex Pattern**: `\?.*$`
   - **Replacement**: (empty)
   - **Enabled**: ✓

### Remove All Query Parameters (Global)

1. **Add New Rule**
   - **Rule Name**: `Remove URLs Query Strings`
   - **Match Contains**: (empty - applies to all)
   - **Regex Pattern**: `\?.*$`
   - **Replacement**: (empty)
   - **Enabled**: ✓

> ⚠️ **Warning**: This removes query parameters from ALL URLs. Some might need them!

## Keyboard Shortcuts (Coming Soon)

- (None yet - planned for v1.1)

## Tips & Tricks

### 💡 Disable a Rule Temporarily

Don't delete it! Just uncheck the **Enabled** checkbox to disable it.

### 💡 Test Regex Online

Before adding a complex regex:
1. Go to [regex101.com](https://regex101.com)
2. Select **Kotlin** flavor
3. Test your pattern against real URLs

### 💡 Check History

The **History** tab shows:
- What text was modified
- When it was modified
- Which rule was applied

Good for debugging why something didn't work!

### 💡 Order Matters

Rules are applied in the order they appear. If Rule A matches and modifies text, Rule B will see the modified text (not the original).

## Troubleshooting

### "Service not working"
- [ ] Is the accessibility service enabled? Check Settings > Accessibility
- [ ] Is the app still open/running? (Some Android versions close background apps)
- [ ] Try restarting your device

### "Rule not applying"
- [ ] Is the rule **Enabled** (checkbox checked)?
- [ ] Is the **Match Contains** field present in the URL? (Case-insensitive check)
- [ ] Is the **Regex Pattern** valid? Test it on [regex101.com](https://regex101.com)

### "Getting wrong results"
- [ ] Check the regex pattern - it might be too broad
- [ ] Test at [regex101.com](https://regex101.com)
- [ ] Check the order of rules
- [ ] Review History tab to see what's happening

## What Happens Behind the Scenes

1. You copy text to clipboard
2. Accessibility Service detects the change
3. Gets the clipboard text
4. Checks each enabled rule in order
5. If text contains **Match Contains** string AND matches **Regex Pattern**
6. Replaces matched portion with **Replacement** text
7. Writes cleaned text back to clipboard
8. Logs the change to History

All of this happens in about **100-200ms** ⚡

## Next Steps

- Read [README.md](README.md) for full documentation
- Check [RULES_GUIDE.md](RULES_GUIDE.md) for more preset rules
- See [DEVELOPMENT.md](DEVELOPMENT.md) to contribute

## Need Help?

- 📖 Read the [README.md](README.md)
- 🔍 Search the [RULES_GUIDE.md](RULES_GUIDE.md) for examples
- 💻 Check [DEVELOPMENT.md](DEVELOPMENT.md) for technical details

---

**That's it! You're now cleaning URLs automatically! 🎉**

Happy cleaner browsing! ✨
